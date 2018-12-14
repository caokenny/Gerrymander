package io.redistrict.Territory;

import io.redistrict.Algorithm.Algorithm;
import io.redistrict.Algorithm.AlgorithmType;
import io.redistrict.Election.ElectionData;
import io.redistrict.Election.Party;
import io.redistrict.Election.VoteData;

import java.util.*;

public class State {
    private int population;
    private String stateName;
    private Map<Integer, District> rgdistricts;
    private Map<Integer, ElectionData> districtVoteResults;
    private Map<Party, Integer> stateElectionResult;
    private Stack<Move> moves = new Stack<Move>();
    private Map<String, Precinct> allPrecincts;
    private Set<String> unassignedPrecinctIds;
    private Map<District, Float> popScores = new LinkedHashMap<>();
    private Map<Integer,District> defaultDistrict;


    public State(State state){
        //if u want rgdistricts set it urself
        this.stateName = state.getStateName();
        this.allPrecincts= new LinkedHashMap<>(state.getAllPrecincts());
        this.population=state.getPopulation();
        rgdistricts = new LinkedHashMap<>();
        this.allPrecincts= new LinkedHashMap<>(state.getAllPrecincts());
        // THIS MIGHT BE REMOVED LATER
        this.defaultDistrict = new LinkedHashMap<>(state.getDefaultDistrict());
    }
    public State(String name, Map<String,Precinct> allPrecincts){
        this.stateName=name;
        this.allPrecincts = allPrecincts;
        this.population = calculateStatePopulation();
        rgdistricts = new LinkedHashMap<>();
    }

    public int calculateStatePopulation(){
        int total =0;
       Iterator<Precinct> precinctIterator = allPrecincts.values().iterator();
        while(precinctIterator.hasNext()){
            total +=precinctIterator.next().getPopulation();
        }
        return total;
    }


    public String getStateName(){
        return stateName;
    }

    public boolean acceptBadMove(double oldScore, double newScore,double acceptConstant){
        double exponent = (newScore - oldScore) / acceptConstant;
        double acceptProb = Math.pow(Math.E, exponent);
        Random rand = new Random();
        int randomNum = rand.nextInt(100) / 100;
        acceptConstant *= 0.8;
        if(randomNum > acceptProb)
            return true;
        return false;
    }
    public Map<String, Precinct> getAllPrecincts() {
        return allPrecincts;
    }
    public Map<Party, Integer> getStateVoteResults(){
        return stateElectionResult;
    }
    public Map<Integer, ElectionData> getDistrictVoteResult(){
        return districtVoteResults;
    }
    public int getPopulation(){
        return population;
    }

    public int getPopulationSA()
    {
        for (Integer i : defaultDistrict.keySet())
            population += defaultDistrict.get(i).getPopulation();
        return population;
    }

    public void undoLastMove(Move move){
        Precinct modifiedPrecinct = move.getPrecinct();
        modifiedPrecinct.setParentDistrictID(move.getSrcDistrictID());
        District srcDistrict = defaultDistrict.get(move.getSrcDistrictID());
        District dstDistrict = defaultDistrict.get(move.getDstDistrictID());
        dstDistrict.removePrecinct(modifiedPrecinct);
        srcDistrict.addPrecinct(modifiedPrecinct,AlgorithmType.SA);
    }

    public District getRandomDistrictSA(){
        int numDistricts = defaultDistrict.size();
//        Random rand = new Random();
//        int n = rand.nextInt(numDistricts) + 0;
        // when n == 0 it returns null because it is a Map with values starting from 1
        int n = (int)(Math.random() * numDistricts) + 1;
        return defaultDistrict.get(n);
    }
    public void addToMoveStack(Move move){
        moves.add(move);
    }

    //works for sa and rg
    public float calculateIdealPop(AlgorithmType type){
        if(type == AlgorithmType.RG)
            return (float)population / rgdistricts.size();
        else
            return (float)population/defaultDistrict.size();
    }

    public double getDistrictScore(District d){
        return popScores.get(d);
    }
    public void updateDistrictScore(float score, District dest){
        popScores.put(dest, score);
    }
    public void initPopScores() {
        float idealPop = (float)population / defaultDistrict.size();
        for (Integer i : defaultDistrict.keySet()) {
            District d = defaultDistrict.get(i);
            popScores.put(d, d.calculatePopEqualScore(idealPop));
        }
    }
    public void resetUnassignedPrecinctIds(){
        unassignedPrecinctIds = new LinkedHashSet<>(allPrecincts.keySet());
    }
    public Map<Integer, District> getRgdistricts() {
        return rgdistricts;
    }
    public void setRgdistricts(Map<Integer, District> rgdistricts) {
        this.rgdistricts = rgdistricts;
    }
    public Set<String> getUnassignedPrecinctIds() {
        return unassignedPrecinctIds;
    }

    public void setUnassignedPrecinctIds(Set<String> unassignedPrecinctIds) {
        this.unassignedPrecinctIds = unassignedPrecinctIds;
    }

    public void removeFromUnassignedIds(Set<String> removalIds){
        removalIds.forEach(id-> unassignedPrecinctIds.remove(id));
    }

    public void removeFromUnassignedIds(String id){
        unassignedPrecinctIds.remove(id);
    }

    public void executeRgMove(Move move){
        Precinct precinct = move.getPrecinct();
        int destDistId = move.getDstDistrictID();
        District destDist = rgdistricts.get(destDistId);
//        removeFromUnassignedIds(precinct.getGeoID10());
        destDist.addPrecinct(precinct,AlgorithmType.RG);
        updatePopulationEqualityMeasure(move, AlgorithmType.RG);
    }

    // works for both SA and RG
    public void updatePopulationEqualityMeasure(Move m, AlgorithmType type) {
        float score1;
        float score2;
        float idealPop = calculateIdealPop(type);
        int srcDistrictID = m.getSrcDistrictID();
        int destDistrictID = m.getDstDistrictID();
        District src;
        District dest;

        if(type == AlgorithmType.SA){
            src = defaultDistrict.get(srcDistrictID);
            dest = defaultDistrict.get(destDistrictID);
        }
        else {
            src = rgdistricts.get(srcDistrictID);
            dest = rgdistricts.get(destDistrictID);
        }

        if(src != null) {
            score1 = src.calculatePopEqualScore(idealPop);
            popScores.put(src,score1);
        }
        if(dest != null){
            score2 = dest.calculatePopEqualScore(idealPop);
            popScores.put(dest,score2);
        }
    }

    public void assignAllUnassignedPrecincts(int districtId){
        District d = rgdistricts.get(districtId);

        for(String id : unassignedPrecinctIds){
            Precinct unassignedPrecinct=  allPrecincts.get(id);
            d.addPrecinct(unassignedPrecinct,AlgorithmType.RG);

            //dont need to update border because all precincts are assigned
            //TODO calculate obj score of state
        }
        unassignedPrecinctIds.clear();
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public Map<Integer, District> getDefaultDistrict() {
        return defaultDistrict;
    }

    public void setDefaultDistrict(Map<Integer, District> defaultDistrict) {
        this.defaultDistrict = defaultDistrict;
    }

    public Stack<Move> getMoves() {
        return moves;
    }

    public District getLowestPopScoreDistrict() {
        District dist = getRandomDistrictSA();
        for (District d : popScores.keySet()) {
            if (popScores.get(d) < popScores.get(dist))
                dist = d;
        }
        return dist;
    }
    public VoteData getStateVoteResult(boolean isDefault)
    {
        VoteData voteData = new VoteData();
        int demVotes= 0;
        int repVotes = 0;
        if(isDefault) {
            for(District district : defaultDistrict.values()){
                VoteData votes = district.getVoteResult();
                demVotes+= votes.getDemVotes();
                repVotes+= votes.getRepVotes();
            }
        }
        else{
            for(District district : rgdistricts.values()){
                VoteData votes = district.getVoteResult();
                demVotes+= votes.getDemVotes();
                repVotes+= votes.getRepVotes();
            }
        }
        voteData.setRepVotes(repVotes);
        voteData.setDemVotes(demVotes);
        return voteData;
    }
}
