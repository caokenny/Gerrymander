package io.redistrict.Territory;

import io.redistrict.Election.ElectionData;
import io.redistrict.Territory.District;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Precinct {

    private int population;
    private int precinctId;
    private String name;
    private String geoID10;
    private List<ElectionData> electionData;
    private List<Precinct> neighbors;
    private List<Precinct> unassignedNeighbors;
    private int parentDistrictID;
    private Set<Precinct> borderPrecincts;
    private boolean isBorder;


    public Precinct(int population){
        this.population = population;
        this.precinctId = precinctId;
    }
    public Precinct(String geoID10, String name, int population) {
        this.name = name;
        this.population = population;
        this.geoID10 = geoID10;
    }
    public Precinct(String geoID10){
        this.geoID10 = geoID10;
    }

    public List<Precinct> getNeighbors(){
        return neighbors;
    }
    public int getPopulation(){
        return population;
    }
    public void setParentDistrictID(int id){
        parentDistrictID = id;
    }

    public int getParentDistrictID() {
        return parentDistrictID;
    }
    public Precinct getRandomNeighbor(){
        int i = neighbors.size();
        Random rand = new Random();
        int n = rand.nextInt(i) - 1;
        return neighbors.get(n);
    }
    public String getGeoID10() {
        return geoID10;
    }

    public void setGeoID10(String geoID10) {
        this.geoID10 = geoID10;
    }

    @Override
    public String toString() {
        return "name= " + name
                +"\ngeoid10= " + geoID10
                +"\npopulation= "+population;
    }

    public void setNeighbors(List<Precinct> neighbors) {
        this.neighbors = neighbors;
    }
    public boolean isBorder(){
        return isBorder;
    }
    public void setIsBorder(boolean isBorder){
        this.isBorder = isBorder;
    }
    public List<ElectionData> getElectionData(){
        return electionData;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setElectionData(List<ElectionData> electionData) {
        this.electionData = electionData;
    }

    public Set<Precinct> getBorderPrecincts() {
        return borderPrecincts;
    }

    public void setBorderPrecincts(Set<Precinct> borderPrecincts) {
        this.borderPrecincts = borderPrecincts;
    }

    public void setBorder(boolean border) {
        isBorder = border;
    }
}
