package io.redistrict.Territory;

import io.redistrict.Election.ElectionData;
import io.redistrict.Election.Party;

import java.util.*;

public class State {
    private int population;
    private String stateName;
    private Map<Integer, District> districts;
    private Map<Integer, ElectionData> districtVoteResults;
    private Map<Party, Integer> stateElectionResult;
//    private Stack<Move> moves;
    private Map<String, Precinct> allPrecincts;
    private Set<String> unassignedPrecinctIds;
    private Map<District, Float> popScores;

    public State(State state){
        this.stateName = state.getStateName();
        this.allPrecincts= new HashMap<>(state.getAllPrecincts());
    }

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

    public void addToDistrictList(District district){

    }

    public void updateDistrictScore(float score, District dest){

    }

    /**
     * set unassigned precinct to allPrecinct. Used in RG.
     */
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
        District destDist = move.getDestDistrict();
        District srcDist = move.getSrcDistrict();

        removeFromUnassigned(precinct.getGeoID10());

        if(srcDist == null){
            districts.get(destDist.getDistrictId()).addPrecinct(precinct);
        }

        districts.get(destDist.getDistrictId()).updateBorderPrecincts(unassignedPrecinctIds);
    }

}
