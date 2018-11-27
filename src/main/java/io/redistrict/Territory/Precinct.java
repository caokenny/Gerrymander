package io.redistrict.backend;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Precinct {

    private int population;
    private int precinctId;
    private String geoID10;
    private List<ElectionData> electionData;
    private List<Precinct> neighbors;
    private int parentDistrictID;
    private Set<Precinct> borderPrecincts;
    private boolean isBorder;
    private District parentDistrict;

    public Precinct(int population, int precinctId){
        this.population = population;
        this.precinctId = precinctId;
    }
    public Precinct(int precinctId){
        this.precinctId = precinctId;
    }
    public Precinct(String geoID10){
        this.geoID10 = geoID10;
    }

    public Collection<Precinct> getNeighbors(){
        return neighbors;
    }
    public int getPopulation(){
        return population;
    }
    public void setParentDistrict(int id){
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

    public String getGeoID10() {
        return geoID10;
    }

    public void setGeoID10(String geoID10) {
        this.geoID10 = geoID10;
    }

    @Override
    public String toString() {
        return "Precinct id= "+precinctId;
    }
}
