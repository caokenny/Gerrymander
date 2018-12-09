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
    private Stack<Move> moves = new Stack<Move>();
    private Map<String, Precinct> allPrecincts;
    private Map<String, Precinct> unassignedPrecincts;
    private Set<String> unassignedPrecinctIds;
    private Map<District, Float> popScores = new HashMap<>();
    private Map<Integer,District> defaultDistrict;


    public State(State state){
        this.stateName = state.getStateName();
        this.allPrecincts= new LinkedHashMap<>(state.getAllPrecincts());
        this.defaultDistrict = new LinkedHashMap<>(state.getDefaultDistrict());
        this.population= state.getPopulation();
        districts = new LinkedHashMap<>();
    }
    public State(String name, Map<String,Precinct> allPrecincts){
        this.stateName=name;
        this.allPrecincts = allPrecincts;
        districts = new LinkedHashMap<>();
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
        srcDistrict.addPrecinct(modifiedPrecinct);
    }
    public District getRandomDistrict(){
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
    public void addToDistrictList(District district){
        districts.put(district.getDistrictID(), district);
    }
    public float calculateIdealPop(){
        return (float)population / districts.size();
    }
    public void updatePopScores(District source, District dest, float score1, float score2){
        popScores.put(source, score1);
        popScores.put(dest, score2);
    }
    public double getDistrictScore(District d){
        return popScores.get(d);
    }
    public void updateDistrictScore(float score, District dest){
        popScores.put(dest, score);
    }
    public void initPopScores() {
        float idealPop = (float)population / defaultDistrict.size();
        System.out.println("ideal pop: " + idealPop);
        for (Integer i : defaultDistrict.keySet()) {
            District d = defaultDistrict.get(i);
            popScores.put(d, d.calculatePopEqualScore(idealPop));
        }
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

    public void removeFromUnassigned(Set<String> removalIds){
        removalIds.forEach(id-> unassignedPrecinctIds.remove(id));
    }

    public void removeFromUnassigned(String id){
        unassignedPrecinctIds.remove(id);
    }

    public void executeMove(Move move){
        Precinct precinct = move.getPrecinct();
        int destDistId = move.getDstDistrictID();
        int srcDistId = move.getSrcDistrictID();
        District srcDist = districts.get(srcDistId);
        District destDist = districts.get(destDistId);
        removeFromUnassigned(precinct.getGeoID10());
        if(srcDist != null){
            districts.get(srcDistId).removePrecinct(precinct);
        }

        districts.get(destDistId).addPrecinct(precinct);
        districts.get(destDistId).updateBorderPrecincts(unassignedPrecinctIds);
        updatePopulationEqualityMeasure(move);

    }
    public void updatePopulationEqualityMeasure(Move m) {
        float idealPop = calculateIdealPop();
        int srcDistrictID = m.getSrcDistrictID();
        int destDistrictID = m.getDstDistrictID();
        District src = districts.get(srcDistrictID);
        District dest = districts.get(destDistrictID);
        float score1 = src.calculatePopEqualScore(idealPop);
        float score2 = dest.calculatePopEqualScore(idealPop);
        updatePopScores(src, dest, score1, score2);
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
}
