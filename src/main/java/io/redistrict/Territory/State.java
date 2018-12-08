package io.redistrict.Territory;

import io.redistrict.Algorithm.Algorithm;
import io.redistrict.Election.ElectionData;
import io.redistrict.Election.Party;

import java.util.*;

public class State {
    private int population;
    private String stateName;
    private Map<Integer, District> districts;
    private Map<Integer, ElectionData> districtVoteResults;
    private Map<Party, Integer> stateElectionResult;
    private Stack<Move> moves;
    private Map<String, Precinct> allPrecincts;
    private Set<String> unassignedPrecinctIds;
    private Map<District, Float> popScores = new LinkedHashMap<>();

    public State(State state){
        //if u want districts set it urself
        this.stateName = state.getStateName();
        this.allPrecincts= new HashMap<>(state.getAllPrecincts());
    }
    public State(String name, Map<String,Precinct> allPrecincts){
        this.stateName=name;
        this.allPrecincts = allPrecincts;
        this.population = calculateStatePopulation();
        districts = new LinkedHashMap<>();
    }

    public int calculateStatePopulation(){
        int total =0;
       Iterator<Precinct> precinctIterator = allPrecincts.values().iterator();
        while(precinctIterator.hasNext()){
            total +=precinctIterator.next().getPopulation();
        }
        return total;
    }

    public int getNumDistrict(){
        return districts.size();
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
    public void undoLastMove(Move move){
        Precinct modifiedPrecinct = move.getPrecinct();
        modifiedPrecinct.setParentDistrictID(move.getSrcDistrictID());
        District srcDistrict = districts.get(move.getSrcDistrictID());
        District dstDistrict = districts.get(move.getDstDistrictID());
        dstDistrict.removePrecinct(modifiedPrecinct);
        srcDistrict.addPrecinct(modifiedPrecinct);
    }
    public District getRandomDistrict(){
        int numDistricts = districts.size();
        Random rand = new Random();
        int n = rand.nextInt(numDistricts) + 0;
        return districts.get(n);
    }
    public void addToMoveStack(Move move){
        moves.add(move);
    }
    public void addToDistrictList(District district){
        districts.put(district.getDistrictId(), district);
    }
    public float calculateIdealPop(){
        return (float)population / districts.size();
    }
    public void updatePopScores(District source, District dest, float score1, float score2){
        popScores.put(source, score1);
        popScores.put(dest, score2);
    }
    public double getDistrictScore(District d){
        return 0;
    }
    public void updateDistrictScore(float score, District dest){

    }
    public void resetUnassignedPrecinctIds(){
        unassignedPrecinctIds = new LinkedHashSet<>(allPrecincts.keySet());
    }
    public Map<Integer, District> getDistricts() {
        return districts;
    }
    public void setDistricts(Map<Integer, District> districts) {
        this.districts = districts;
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
        District destDist = districts.get(destDistId);
        removeFromUnassignedIds(precinct.getGeoID10());

        destDist.addPrecinct(precinct);
        destDist.updateBorderPrecinctsForRg(unassignedPrecinctIds);
        updatePopulationEqualityMeasure(move);

    }
    public void updatePopulationEqualityMeasure(Move m) {
        float score1;
        float score2;
        float idealPop = calculateIdealPop();
        int srcDistrictID = m.getSrcDistrictID();
        int destDistrictID = m.getDstDistrictID();
        District src = districts.get(srcDistrictID);
        District dest = districts.get(destDistrictID);
        if(src != null) {
            score1 = src.calculatePopEqualScore(idealPop);
            popScores.put(src,score1);
        }
        if(dest != null){
            score2 = dest.calculatePopEqualScore(idealPop);
            popScores.put(dest,score2);
        }

        //updatePopScores(src, dest, score1, score2);
    }

    public void assignAllUnassignedPrecincts(int districtId){
        District d = districts.get(districtId);

        for(String id : unassignedPrecinctIds){
            Precinct unassignedPrecinct=  allPrecincts.get(id);
            d.addPrecinct(unassignedPrecinct);

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
}
