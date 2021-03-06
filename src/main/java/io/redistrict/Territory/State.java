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

    private Stack<Move> moves = new Stack<Move>();
    private Map<String, Precinct> allPrecincts;
    private Set<String> unassignedPrecinctIds;
    private Map<District, Float> popScores = new LinkedHashMap<>();
    private Map<Integer,District> defaultDistrict;
    private int totalVotes; // these values have to be long becuase of overflow!
    private int totalDemVotes;
    private int totalRepVotes;

    public State(){}

    public State(State state){
        //if u want rgdistricts set it urself
        this.stateName = state.getStateName();
        this.allPrecincts= new LinkedHashMap<>(state.getAllPrecincts());
        this.population=state.getPopulation();
        rgdistricts = new LinkedHashMap<>();
        this.allPrecincts= new LinkedHashMap<>(state.getAllPrecincts());
        // THIS MIGHT BE REMOVED LATER
        this.defaultDistrict = new LinkedHashMap<>(state.getDefaultDistrict());
        this.totalVotes=0;
        this.totalDemVotes=0;
        this.totalRepVotes=0;
        setTotalVotes();
    }
    public void setTotalVotes(){
        int pop = 0;
        int over=0;
        for(Precinct p : allPrecincts.values()){
            VoteData data = p.getVoteData();
            pop += p.getPopulation();
            if(data.getDemVotes()<0 || data.getRepVotes()<0)
            {
                throw new NullPointerException("-NEGATIVE VOTES");
            }
            if(p.getPopulation() < data.getRepVotes()+data.getDemVotes())
            {
                over++;
            }
            totalDemVotes += data.getDemVotes();
            totalRepVotes += data.getRepVotes();
            int total = data.getDemVotes() + data.getRepVotes();
            totalVotes+= total;
        }
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

    public double calculatePartisanBias(Map<Integer, District> districts){
        long numDemSeats = 0;
        int numDistrict = districts.size();
        double demPercentage = ((double)(totalDemVotes)) / totalVotes;
        for(District d : districts.values()){
            VoteData data = d.getVoteResult();
            if(data.getDemVotes() > data.getRepVotes()){
                numDemSeats++;
            }
        }
        //This is in terms of Democratic party.
        //So if answer returned is 0.02, then there is a 2% partisan bias TOWARDS the Democratic party.
        //If answer is -0.02, then there is a 2% partisan bias AGAINST the Democratic party
        double percentDemSeatsWon = ((double)numDemSeats) / numDistrict;
        return 1-Math.abs(percentDemSeatsWon - demPercentage);
    }
    public double calculateEfficiencyGap(Map<Integer,District> districts){
        int totalDemWastedVotes = 0;
        int totalRepWastedVotes = 0;
        int total = 0;
        for(District d : districts.values()){
            //Find out what is votes needed to win.
            VoteData data = d.getVoteResult();
            total +=  (data.getDemVotes() + data.getRepVotes());

            int districtTotalVotes = data.getDemVotes()+data.getRepVotes();
            int votesNeedToWin = (districtTotalVotes / 2) + 1;
            if(data.getDemVotes() > data.getRepVotes()){
                //Dem party wins. all of reps is wasted
                totalRepWastedVotes += data.getRepVotes();
                totalDemWastedVotes = totalDemWastedVotes + (data.getDemVotes() - votesNeedToWin);
            }
            else{
                totalDemWastedVotes += data.getDemVotes();
                totalRepWastedVotes = totalRepWastedVotes + (data.getRepVotes() - votesNeedToWin);
            }
        }
        double demNetWastedVotes = totalDemWastedVotes - totalRepWastedVotes;
        double efficiencyGap = 1-Math.abs(demNetWastedVotes / total);
        //If efficiency gap is 0.2, then that means it is 20% in FAVOR of Republicans. +20% Republicans
        //If efficiency gap is -0.2, then that means it is 20% in FAVOR of Democrats. +20% Democrats.
        return efficiencyGap;
    }
    public String getStateName(){
        return stateName;
    }

    public boolean acceptBadMove(double oldScore, double newScore,double acceptConstant){
        double exponent = Math.abs(newScore - oldScore) / acceptConstant;
        double acceptProb = Math.pow(Math.E, exponent); // smaller = better
        Random rand = new Random();
        int randomNum = rand.nextInt(100) / 100;
        if(randomNum > acceptProb)
            return true;
        return false;
    }
    public Map<String, Precinct> getAllPrecincts() {
        return allPrecincts;
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
        dstDistrict.removePrecinct(modifiedPrecinct,AlgorithmType.SA);
        srcDistrict.addPrecinct(modifiedPrecinct,AlgorithmType.SA);
        updatePopulationEqualityMeasure(move,AlgorithmType.SA);
    }

    public District getRandomDistrictSA(){
        int numDistricts = defaultDistrict.size();
//        Random rand = new Random();
//        int n = rand.nextInt(numDistricts) + 0;
        // when n == 0 it returns null because it is a Map with values starting from 1
        int n = (int)(Math.random() * numDistricts) + 1;
        return defaultDistrict.get(n);
    }
    public District getLowestPopDistrictSA(){
        District lowestDistrict = defaultDistrict.get(1);
        double lowestPop = lowestDistrict.getPopulation();
        for(District district :defaultDistrict.values())
        {
            if(district.getPopulation()<lowestPop){
                lowestDistrict = district;
                lowestPop= district.getPopulation();
            }
        }
        return lowestDistrict;
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

    public Map<District, Float> getPopScores() {
        return popScores;
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

    public void executeSaMove(Move move){
        Precinct precinct =move.getPrecinct();
        int destDistId = move.getDstDistrictID();
        int srcDistId = move.getSrcDistrictID();
        District destDist = defaultDistrict.get(destDistId);
        District srcDist = defaultDistrict.get(srcDistId);

        destDist.addPrecinct(precinct,AlgorithmType.SA);
        srcDist.removePrecinct(precinct,AlgorithmType.SA);
        precinct.setParentDistrictID(destDistId);
        updatePopulationEqualityMeasure(move,AlgorithmType.SA);
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
            score1 = src.calculatePopEqualScore(idealPop);
            popScores.put(src,score1);
            score2 = dest.calculatePopEqualScore(idealPop);
            popScores.put(dest,score2);
        }
        else {
            src = rgdistricts.get(srcDistrictID);
            dest = rgdistricts.get(destDistrictID);
            score1 = (float)dest.calcuateRgPopScore(idealPop);
            popScores.put(dest,score1);
        }
    }

    public void assignAllUnassignedPrecincts(int districtId){
        District d = rgdistricts.get(districtId);

        for(String id : unassignedPrecinctIds){
            Precinct unassignedPrecinct=  allPrecincts.get(id);
            d.addPrecinct(unassignedPrecinct,AlgorithmType.RG);

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
    public float getTotalPopScore() {
        float totalPopScore = 0;
        for (Float f : popScores.values())
            totalPopScore += f;
        return totalPopScore;
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

    public double getAverageStatePopScore(AlgorithmType type){

        Map<Integer,District> districtMap;
        float idealPop = calculateIdealPop(type);
        double avgPopScore ;
        double total= 0;

        if(type == AlgorithmType.SA){ districtMap = getDefaultDistrict(); }
        else{ districtMap = rgdistricts;}

        for(District district : districtMap.values()){

            if(type==AlgorithmType.SA){
                total += district.calculatePopEqualScore(idealPop);
            }
            else{
                total += district.calcuateRgPopScore(idealPop);
            }
        }
        avgPopScore = total/districtMap.size();
        return  avgPopScore;
    }


    public int getTotalVotes() {
        return totalVotes;
    }

    public int getTotalDemVotes() {
        return totalDemVotes;
    }

    public int getTotalRepVotes() {
        return totalRepVotes;
    }
}
