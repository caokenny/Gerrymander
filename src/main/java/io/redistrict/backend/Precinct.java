package io.redistrict.backend;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Precinct {

    private int population;
    private int precinctId;
//    private List<ElectionData> electionData;
    private List<Precinct> neighbors;
    private int parentDistrictID;
    private Set<Precinct> borderPrecincts;
    private boolean isBorder;
    private District parentDistrict;

    public Precinct(int population, int precinctId){
        this.population = population;
        this.precinctId = precinctId;
    }
//    public Map<Party, Integer> getElectionResults(ElectionType eType){
//
//    }
//    public Party getWinningParty(ElectionType eType){
//
//    }
    public Collection<Precinct> getNeighbors(){
        return neighbors;
    }
    public int getPopulation(){
        return population;
    }
    public void setParentDistrictId(int id){
        parentDistrictID = id;
    }

    public int getParentDistrictID() {
        return parentDistrictID;
    }
    public void updatePrecinct(District newDistrict){

    }
    public void setParentDistrict(District district){
        parentDistrict = district;
    }
    public District getParentDistrict(){
        return parentDistrict;
    }
    public Precinct getRandomNeighbor(){
        int i = neighbors.size();
        Random rand = new Random();
        int n = rand.nextInt(i) - 1;
        return neighbors.get(n);
    }
    public int getPrecinctId(){
        return precinctId;
    }
}
