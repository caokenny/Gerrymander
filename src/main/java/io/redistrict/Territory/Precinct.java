package io.redistrict.Territory;

import io.redistrict.Election.ElectionData;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Precinct {

    private int population;
    //private int precinctId;
    private String precinctId;
    private List<ElectionData> electionData;
    private List<Precinct> neighbors;
    private int parentDistrictID;
    private Set<Precinct> borderPrecincts;
    private boolean isBorder;

    public Precinct(int population, String precinctId){
        this.population = population;
        this.precinctId = precinctId;
    }
    /*public Precinct(int precinctId){
        this.precinctId = precinctId;
    }*/
    public Precinct(String precinctId){
        this.precinctId = precinctId;
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

    public Precinct getRandomNeighbor(){
        int i = neighbors.size();
        Random rand = new Random();
        int n = rand.nextInt(i) - 1;
        return neighbors.get(n);
    }
    public String getPrecinctId(){
        return precinctId;
    }

    @Override
    public String toString() {
        return "Precinct id= "+precinctId;
    }
}
