package io.redistrict.Territory;

import io.redistrict.Election.ElectionData;
import io.redistrict.Election.Party;

import java.util.LinkedHashMap;
import java.util.Map;

public class State {
    private int population;
    private String stateName;
    private Map<Integer, District> districts;
    private Map<Integer, ElectionData> districtVoteResults;
    private Map<Party, Integer> stateElectionResult;
//    private Stack<Move> moves;
    private Map<String, Precinct> allPrecincts;
    private Map<Integer, Precinct> unassignedPrecincts;
//    private AlgorithmType type;
    private Map<District, Float> popScores;


    public State(String name, Map<String,Precinct> allPrecincts){
        this.stateName=name;
        this.allPrecincts = allPrecincts;
        districts = new LinkedHashMap<>();
    }

    /**
     * Returns the number of districts in the State
     * @return numbero of districts
     */
//    public int getNumDistrict(){
//
//    }

    /**
     * Returns the state name
     * @return state name
     */
    public String getStateName(){
        return stateName;
    }

    /**
     * Returns the state after it was simulated and modified
     * @return modified State
     */
    public State getSimulatedState(){
        return this;
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
//    public void updatePrecinct(Move move){
//
//    }
//    public void undoLastMove(){
//
//    }
//    public District getRandomDistrict(){
//
//    }
//    public void addToMoveStack(Move move){
//
//    }
    public void addToDistrictList(District district){

    }
//    public void executeMove(Move move){
//
//    }
//    public float calculateIdealPop(){
//
//    }
//    public float updatePopScores(District source, District dest, float score1, float score2){
//
//    }
//    public int getDistrictScore(District d){
//
//    }
    public void updateDistrictScore(float score, District dest){

    }

}
