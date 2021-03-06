package io.redistrict.Territory;

import io.redistrict.Election.ElectionData;
import io.redistrict.Election.Party;
import io.redistrict.Election.VoteData;
import io.redistrict.Territory.District;
import org.locationtech.jts.geom.Geometry;
import org.wololo.geojson.Feature;
import org.wololo.geojson.GeoJSONFactory;
import org.wololo.jts2geojson.GeoJSONReader;

import java.util.*;

public class Precinct {

    private int population;
    private String name;
    private String geoID10;
    private VoteData voteData;
    private List<Precinct> neighbors;
    private int parentDistrictID;
    private boolean isBorder;
    String geoJsonString;


    public Precinct(int population){
        this.population = population;
    }
    public Precinct(String geoID10, String name, int population, String geoJsonString) {
        this.name = name;
        this.population = population;
        this.geoID10 = geoID10;
        this.geoJsonString = geoJsonString;
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

    public Precinct getRandomNeighbor() {
        int i = neighbors.size();
        int n = (int)(Math.random() * i);
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

    public void setPopulation(int population) {
        this.population = population;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getArea(){
        Feature feature = (Feature) GeoJSONFactory.create(geoJsonString);
        GeoJSONReader reader = new GeoJSONReader();
        Geometry precinctGeometry = reader.read(feature.getGeometry());
        return precinctGeometry.getArea();
    }
    public double getPerimeter(){
        Feature feature = (Feature) GeoJSONFactory.create(geoJsonString);
        GeoJSONReader reader = new GeoJSONReader();
        Geometry precinctGeometry = reader.read(feature.getGeometry());
        return precinctGeometry.getLength();
    }

    public String getGeoJsonString() {
        return geoJsonString;
    }

    public void setGeoJsonString(String geoJsonString) {
        this.geoJsonString = geoJsonString;
    }

    public VoteData getVoteData() {
        return voteData;
    }

    public void setVoteData(VoteData voteData) {
        this.voteData = voteData;
    }
}
