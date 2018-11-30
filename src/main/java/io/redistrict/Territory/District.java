package io.redistrict.Territory;

import io.redistrict.Election.ElectionData;
import io.redistrict.Election.Party;

import java.util.*;

public class District {

    private int districtId;
    private int population;
    private Map<String, Precinct> allDPrecincts;
    private Map<Integer, ElectionData> precinctVoteResults;
    private Map<Party, Integer> electionResult;
    private Set<Precinct> borderPrecincts;
    private int numOfNeighbors;

    public District(int districtId,Precinct startPrecinct){
        this.districtId = districtId;
        this.population= startPrecinct.getPopulation();
        this.allDPrecincts = new LinkedHashMap<>();
        this.borderPrecincts = new LinkedHashSet<>();

        allDPrecincts.put(startPrecinct.getGeoID10(),startPrecinct);
        borderPrecincts.add(startPrecinct);
        this.numOfNeighbors = startPrecinct.getNeighborIds().size();
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    /**
     * Returns a map of <Precincts, ElectionData> in the current district
     * @return Map of Precinct Vote Results (PrecinctID, ElectionData)
     */
    public Map<Integer, ElectionData> getPrecinctVoteResults(){
        return precinctVoteResults;
    }

    /**
     * Adds the new precinct to the current District
     * @param precinct
     */
    public void addPrecinct(Precinct precinct){
        allDPrecincts.put(precinct.getGeoID10(), precinct);
        population += precinct.getPopulation();

    }

    /**
     * Removes the precinct from our current District
     * @param precinct
     */
    public void removePrecinct(Precinct precinct){
        allDPrecincts.remove(precinct);
        population -= precinct.getPopulation();
        //Remove the allDPrecincts election vote results from hash.
        //Change border allDPrecincts again. Remove allDPrecincts data from election Result
    }

    /**
     * Returns the population of the district
     * @return population
     */
    public int getPopulation(){
        return population;
    }

    /**
     * Returns a map of the Partys and the votes of the current District
     * @return Map of the Partys and number of Votes each party got
     */
    public Map<Party, Integer> getElectionResult(){
        return electionResult;
    }


    public void updateBorderPrecincts(Set<String> unassignedPrecinctIds)
    {
        borderPrecincts.clear();

        for(String precinctId: allDPrecincts.keySet()){
            Precinct currentPrecinct = allDPrecincts.get(precinctId);
            if(isBorder(currentPrecinct,unassignedPrecinctIds))
            {
                borderPrecincts.add(currentPrecinct);
            }
        }
    }

    private boolean isBorder(Precinct precinct, Set<String> unassignedPrecinctIds){

        Set<String> neighborIds = precinct.getNeighborIds();

        for(String id : neighborIds)
        {
            if (unassignedPrecinctIds.contains(id))
            {
                return true;
            }
        }
        return false;
    }

    public Set<Precinct> getBorderPrecincts() {
        return borderPrecincts;
    }

    public void setBorderPrecincts(Set<Precinct> borderPrecincts) {
        this.borderPrecincts = borderPrecincts;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public int getNumOfNeighbors() {
        return numOfNeighbors;
    }

    public void setNumOfNeighbors(int numOfNeighbors) {
        this.numOfNeighbors = numOfNeighbors;
    }

    public Map<String, Precinct> getAllDPrecincts() {
        return allDPrecincts;
    }

    public void setAllDPrecincts(Map<String, Precinct> allDPrecincts) {
        this.allDPrecincts = allDPrecincts;
    }
}

