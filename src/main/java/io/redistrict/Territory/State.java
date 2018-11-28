package io.redistrict.Territory;

import io.redistrict.Election.ElectionData;
import io.redistrict.Election.Party;

import java.util.Map;
import java.util.Random;
import java.util.Stack;

public class State {
    private int population;
    private String stateName;
    private Map<Integer, District> districts;
    private Map<Integer, ElectionData> districtVoteResults;
    private Map<Party, Integer> stateElectionResult;
    private Stack<Move> moves;
    private Map<Integer, Precinct> allPrecincts;
    private Map<Integer, Precinct> unassignedPrecincts;
//    private AlgorithmType type;
    private Map<District, Float> popScores;
    private int badMoves = 0;

    /**
     * Returns the number of districts in the State
     * @return numbero of districts
     */
    public int getNumDistrict(){
        return districts.size();
    }

    /**
     * Returns the state name
     * @return state name
     */
    public String getStateName(){
        return stateName;
    }
    public State getSimulatedState(){
        while(badMoves < 25){
            District district = getRandomDistrict();
            int oldScore = getDistrictScore(district);
            Move move = district.modifyDistrict();
            int newScore = getDistrictScore(district);
            if(newScore > oldScore){
                addToMoveStack(move);
            }
            else{
                badMoves++;
                undoLastMove(move);
            }
        }
        return this;
    }
    /**
     * Returns the state after it was simulated and modified
     * @return modified State
     */
    public Map<Integer, Precinct> getAllPrecincts() {
        return allPrecincts;
    }
//    public Map<Party, Integer> getStateVoteResults(){
//        return stateElectionResult;
//    }
//    public Map<Integer, ElectionData> getDistrictVoteResult(){
//        return districtVoteResults;
//    }
    public int getPopulation(){
        return population;
    }
    public void updatePrecinct(Move move){

    }
    public void undoLastMove(Move move){
        Precinct p = move.getPrecinct();
        p.setParentDistrict(move.getSrcDistrict());
        p.setParentDistrictId(move.getSrcDistrict().getDistrictId());
    }
    public District getRandomDistrict(){
        int numDistricts = districts.size();
        Random rand = new Random();
        int n = rand.nextInt(numDistricts) + 0;
        return districts.get(n);
    }
    public void addToMoveStack(Move move){

    }
    public void addToDistrictList(District district){

    }
    public void executeMove(Move move){

    }
    public float calculateIdealPop(){
        return 0;
    }
    public float updatePopScores(District source, District dest, float score1, float score2){
        return 0;
    }
    public int getDistrictScore(District d){
        return 0;
    }
    public void updateDistrictScore(float score, District dest){

    }

}
