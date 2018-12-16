package io.redistrict.Territory;

import io.redistrict.Algorithm.AlgorithmType;
import io.redistrict.Election.Party;
import io.redistrict.Election.VoteData;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.wololo.geojson.Feature;
import org.wololo.geojson.GeoJSONFactory;
import org.wololo.jts2geojson.GeoJSONReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class District {

    private int districtId;
    private int population;
    private Map<String, Precinct> allDPrecincts;
    private List<Precinct> borderRgPrecincts; // this is precincts that are within district that have unassigned neighbors
    private List<Precinct> borderSaPrecincts;
    private int numOfUnassignedNeighbors;
    private String seedPrecinctId;
    private static Properties properties = new Properties();
    private VoteData voteData;

    public District(int districtId,Precinct startPrecinct){
        this.districtId = districtId;
        this.population= startPrecinct.getPopulation();
        this.allDPrecincts = new LinkedHashMap<>();
        this.borderRgPrecincts = new ArrayList<>();
        this.borderSaPrecincts = new ArrayList<>();
        allDPrecincts.put(startPrecinct.getGeoID10(),startPrecinct);
        borderRgPrecincts.add(startPrecinct);
        if(startPrecinct.getNeighbors() !=null)
            this.numOfUnassignedNeighbors = startPrecinct.getNeighbors().size();
        this.seedPrecinctId = startPrecinct.getGeoID10();
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public void addPreinctList(List<Precinct> precincts, AlgorithmType type){
        for(Precinct preinct : precincts)
        {
            addPrecinct(preinct,type);
        }
    }

    public void addPrecinct(Precinct precinct, AlgorithmType type){
        precinct.setParentDistrictID(districtId);
        allDPrecincts.put(precinct.getGeoID10(), precinct);
        population += precinct.getPopulation();
        if(type == AlgorithmType.SA) {
            if (isSABorderPrecinct(precinct)) {
                precinct.setIsBorder(true);
                borderSaPrecincts.add(precinct);
            } else
                precinct.setIsBorder(false);
        }
    }

    public void removePrecinctList(List<Precinct> precincts, AlgorithmType type){
        for (Precinct p : precincts){
            removePrecinct(p,type);
        }

    }

    public void removePrecinct(Precinct precinct,AlgorithmType type){
        precinct.setParentDistrictID(-1);
        if(allDPrecincts.remove(precinct.getGeoID10())== null){
            throw new NullPointerException("You are trying to remove precinct number "+ precinct.getGeoID10() +" that doesnt" +
                    "exist in this districts (allDPreinct List)");
        }
//        allDPrecincts.remove(precinct);
//        the above code DID NOT REMOVE THE PRECINCT
        allDPrecincts.remove(precinct.getGeoID10());
        population -= precinct.getPopulation();
        if(type == AlgorithmType.SA) {
            if (precinct.isBorder()) {
                borderSaPrecincts.remove(precinct);
            }
        }
    }

    public int getPopulation(){
        return population;
    }

    public VoteData getVoteResult(){
        int demVotes = 0;
        int repVotes = 0;
        VoteData voteData = new VoteData();
        for(Precinct precinct : allDPrecincts.values()) {
            VoteData votes = precinct.getVoteData();
            demVotes+= votes.getDemVotes();
            repVotes+= votes.getRepVotes();
        }
        voteData.setDemVotes(demVotes);
        voteData.setRepVotes(repVotes);
        this.voteData = voteData;
        return voteData;
    }

    public void updateBorderPrecinctsForRg(Set<String> unassignedPrecinctIds) {
        borderRgPrecincts.clear();
        for(String precinctId: allDPrecincts.keySet()){
            Precinct currentPrecinct = allDPrecincts.get(precinctId);
            if(isRgBorder(currentPrecinct,unassignedPrecinctIds))
            {
                borderRgPrecincts.add(currentPrecinct);
            }
        }
    }

    private boolean isRgBorder(Precinct precinct, Set<String> unassignedPrecinctIds){
        List<Precinct> neighbors = precinct.getNeighbors();
        for(Precinct neighbor : neighbors)
        {
            if (unassignedPrecinctIds.contains(neighbor.getGeoID10()))
            {
                return true;
            }
        }
        return false;
    }

    public List<Precinct> getBorderRgPrecincts() {
        return borderRgPrecincts;
    }

    public List<Precinct> getBorderRgPrecincts(Set<String> unassignedPrecinctIds)
    {
        List<Precinct> bordersPrecincts = new ArrayList<>();
        for(Precinct precinct : allDPrecincts.values()){
            if(isRgBorder(precinct,unassignedPrecinctIds)){
                bordersPrecincts.add(precinct);
            }
        }
        return  bordersPrecincts;
    }

    public void setBorderRgPrecincts(List<Precinct> borderRgPrecincts) {
        this.borderRgPrecincts = borderRgPrecincts;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public int getNumOfUnassignedNeighbors() {
        return numOfUnassignedNeighbors;
    }

    public void setNumOfUnassignedNeighbors(int numOfUnassignedNeighbors) {
        this.numOfUnassignedNeighbors = numOfUnassignedNeighbors;
    }

    public Map<String, Precinct> getAllDPrecincts() {
        return allDPrecincts;
    }

    public void setAllDPrecincts(Map<String, Precinct> allDPrecincts) {
        this.allDPrecincts = allDPrecincts;
    }

    public boolean isSABorderPrecinct(Precinct precinct){
        List<Precinct> neighbors = precinct.getNeighbors();
        for(Precinct p : neighbors){
            if(p.getParentDistrictID() != precinct.getParentDistrictID())
                return true;
        }
        return false;
    }


    public static void loadDefaultProperties(){
        InputStream aStream = District.class.getClassLoader().getResourceAsStream("algorithms.properties");
        try{
            properties.load(aStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double calculateSchwartzberg(double area, double perimeter) {
        // equalAreaRadius = r = sqrt(A/PI)
        double r = Math.sqrt(area/Math.PI);
        // equalAreaPerimeter = C = 2pi * r (Circumference)
        double equalAreaPerimeter = 2 * Math.PI * r;
        // Schwartzberg score = 1 / (Perimeter of district / C )
        double score = 1 / (perimeter/equalAreaPerimeter);
        return score;
    }
    public double calculatePolsbyPopper(double area, double perimeter) {
        // Formula: (4Pi * area) / (Perimeter^2)
        double polsbyPopperScore = (4 * Math.PI * area) / Math.pow(perimeter, 2);
        return polsbyPopperScore;
    }
//
    public double calcuateRgPopScore(double idealPop){
        double devAmount = .05;
        double difference = Math.abs(population - idealPop);
        double lowerBound = idealPop -(idealPop*devAmount);
        double upperBound = idealPop +(idealPop*devAmount);
        if(population<=upperBound && population>= lowerBound){return 1;} //within deviation of 5 %
        else{
            return (1-(difference/idealPop));
        }
    }


    public float calculatePopEqualScore(float idealPop) {
        float score = 1;
        if (population <= idealPop) return score;
        float difference = population - idealPop;
//        System.out.println(properties.getProperty("population_lowThreshold"));
//        float lowerThreshold = Float.parseFloat(properties.getProperty("population_lowThreshold"));
//        float midThreshold = Float.parseFloat(properties.getProperty("population_midThreshold"));
//        float highThreshold = Float.parseFloat(properties.getProperty("population_highThreshold"));
//        float lowPenalty = Float.parseFloat(properties.getProperty("population_lowPenalty"));
//        float midPenalty = Float.parseFloat(properties.getProperty("population_midPenalty"));
//        float highPenalty = Float.parseFloat(properties.getProperty("population_highPenalty"));
//        if (difference <= lowerThreshold) return score - lowPenalty;
//        else if (difference > lowerThreshold && difference <= midThreshold) return score - midPenalty;
//        else return score - highPenalty;
        float dev = Float.parseFloat(properties.getProperty("population_deviation"));
        float penalty = Float.parseFloat(properties.getProperty("population_penalty"));
        float penaltyAmt = penalty;
        float devAmt = dev;
        while (score - penaltyAmt > 0) {
            if (difference > idealPop * devAmt) {
                penaltyAmt += penalty;
                devAmt += dev;
            }
            else {
                score -= penaltyAmt;
                break;
            }
        }
        return score;
    }
    public static Map<Integer, District> makeSeedDistricts(Collection<Precinct> precincts){
        int districtID=1;
        Map<Integer,District> seedDistricts = new LinkedHashMap<>();

        for(Precinct p : precincts){
            District seedDistrict = new District(districtID,p);
            seedDistricts.put(districtID,seedDistrict);
            districtID++;
        }
        return  seedDistricts;
    }
    public double getArea(Map<String, Precinct> map){
        // parse Geometry from Feature

        if(map.size() ==1){return map.values().iterator().next().getArea();}

        ArrayList<Feature> featureList = new ArrayList<>();
        for(Precinct precinct : map.values()){
            Feature feature = (Feature) GeoJSONFactory.create(precinct.getGeoJsonString());
            featureList.add(feature);
        }
        GeoJSONReader reader = new GeoJSONReader();
        ArrayList<Geometry> precinctGeometries = new ArrayList<Geometry>();
        for(Feature feature : featureList){
            Geometry precinctGeometry = reader.read(feature.getGeometry());
            precinctGeometries.add(precinctGeometry);
        }
        GeometryFactory geoFac = new GeometryFactory();
        GeometryCollection geometryCollection = (GeometryCollection) geoFac.buildGeometry(precinctGeometries);
        return geometryCollection.union().getArea();
    }
    public double getPerimeter(Map<String, Precinct> map) {

        if(map.size()==1){ return map.values().iterator().next().getPerimeter();}

        ArrayList<Feature> featureList = new ArrayList<>();
        for (Precinct precinct : map.values()) {
            Feature feature = (Feature) GeoJSONFactory.create(precinct.getGeoJsonString());
            featureList.add(feature);
        }
        GeoJSONReader reader = new GeoJSONReader();
        ArrayList<Geometry> precinctGeometries = new ArrayList<Geometry>();
        for (Feature feature : featureList) {
            Geometry precinctGeometry = reader.read(feature.getGeometry());
            precinctGeometries.add(precinctGeometry);
        }
        GeometryFactory geoFac = new GeometryFactory();
        GeometryCollection geometryCollection = (GeometryCollection) geoFac.buildGeometry(precinctGeometries);
        return geometryCollection.union().getLength();
    }
    public String getSeedPrecinctId() {
        return seedPrecinctId;
    }

    public void setSeedPrecinctId(String seedPrecinctId) {
        this.seedPrecinctId = seedPrecinctId;

    }

    public void updateNumOfUnassignNeighbors(Set<String> unassignIds){

        int unassignNeighborCount = 0;

        for(Precinct p : borderRgPrecincts){
            for(Precinct neighbor : p.getNeighbors()){
                if(unassignIds.contains(neighbor.getGeoID10())){
                    unassignNeighborCount++;
                }
            }
        }
        this.numOfUnassignedNeighbors = unassignNeighborCount;
    }

    public Precinct getLargestBorderPrec() {
        Precinct largest = borderSaPrecincts.get(0);
        for (Precinct p : borderSaPrecincts) {
            if (p.getPopulation() > largest.getPopulation())
                largest = p;
        }
        return largest;
    }

    public Move moveLargestBorderPrec(){
        Precinct p = getLargestBorderPrec();
        Precinct pNeighbor = p.getRandomNeighbor();
        while(!pNeighbor.isBorder() || pNeighbor.getParentDistrictID() == p.getParentDistrictID())
            pNeighbor = p.getRandomNeighbor();
        // Move the largest border precint to a neighboring district
        return new Move(p, p.getParentDistrictID(), pNeighbor.getParentDistrictID());
    }

    public List<Precinct> getBorderSaPrecincts() {
        return borderSaPrecincts;
    }

    public void setBorderSaPrecincts(List<Precinct> borderSaPrecincts) {
        this.borderSaPrecincts = borderSaPrecincts;
    }

}

