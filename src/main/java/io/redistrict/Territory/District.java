package io.redistrict.Territory;

import io.redistrict.Election.ElectionData;
import io.redistrict.Election.Party;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class District {

    private int districtId;
    private int population;
    private Map<String, Precinct> allDPrecincts;
    private Map<String, List<ElectionData>> precinctVoteResults;
    private Map<Party, Integer> electionResult;
    private List<Precinct> borderRgPrecincts; // this is precincts that are within district that have unassigned neighbors
    private int numOfNeighbors;
    private String seedPrecinctId;
    private static Properties properties = new Properties();

    public District(int districtId,Precinct startPrecinct){
        this.districtId = districtId;
        this.population= startPrecinct.getPopulation();
        this.allDPrecincts = new LinkedHashMap<>();
        this.borderRgPrecincts = new ArrayList<>();
        allDPrecincts.put(startPrecinct.getGeoID10(),startPrecinct);
        borderRgPrecincts.add(startPrecinct);
        this.numOfNeighbors = startPrecinct.getNeighbors().size();
        this.seedPrecinctId = startPrecinct.getGeoID10();
    }

    public int getDistrictId() {
        return districtId;
    }

    public void setDistrictId(int districtId) {
        this.districtId = districtId;
    }

    public Map<String, List<ElectionData>> getPrecinctVoteResults(){
        return precinctVoteResults;
    }


    public void addPrecinct(Precinct precinct){
        precinct.setParentDistrictID(districtId);
        allDPrecincts.put(precinct.getGeoID10(), precinct);
        population += precinct.getPopulation();
//        precinctVoteResults.put(precinct.getGeoID10(), precinct.getElectionData());
        if(isSABorderPrecinct(precinct)) {
            precinct.setIsBorder(true);
        }
        else
            precinct.setIsBorder(false);

    }

    public void removePrecinct(Precinct precinct){
        precinct.setParentDistrictID(-1);
        allDPrecincts.remove(precinct);
        population -= precinct.getPopulation();
        precinctVoteResults.remove(precinct.getGeoID10(), precinct.getElectionData());
        if(precinct.isBorder()) {
            borderRgPrecincts.remove(precinct);
        }
    }

    public int getPopulation(){
        return population;
    }

    public Map<Party, Integer> getElectionResult(){
        return electionResult;
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

    public void setBorderRgPrecincts(List<Precinct> borderRgPrecincts) {
        this.borderRgPrecincts = borderRgPrecincts;
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
    public Party getWinningParty(){
        HashMap<Party, Integer> partyResults = new HashMap<Party, Integer>();
        Set set = electionResult.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            if(!partyResults.containsKey(entry.getKey())){
                partyResults.put((Party)entry.getKey(), (int)entry.getValue());
            }
            else{
                partyResults.put((Party)entry.getKey(), partyResults.get(entry.getKey()) + (Integer)entry.getValue());
            }
        }
        Party winningParty = null;
        int max = 0;
        Set results = partyResults.entrySet();
        Iterator loop = results.iterator();
        while(loop.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            if((Integer)entry.getValue() > max){
                max = (Integer) entry.getValue();
                winningParty = (Party)entry.getKey();
            }
        }
        return winningParty;
    }
    public boolean isSABorderPrecinct(Precinct precinct){
        List<Precinct> neighbors = precinct.getNeighbors();
        for(Precinct p : neighbors){
            if(p.getParentDistrictID() != precinct.getParentDistrictID())
                return true;
        }
        return false;
    }
    public Precinct getRandomPrecinct(){
        //Return a random precinct
        int numPrecincts = borderRgPrecincts.size();
        Random rand = new Random();
        int n = rand.nextInt(numPrecincts) + 0;
        return borderRgPrecincts.get(n);
    }
    public Move modifyDistrict(){
        Precinct precinct = getRandomPrecinct();
        Precinct pNeighbor = precinct.getRandomNeighbor();
        while(!pNeighbor.isBorder())
            pNeighbor = precinct.getRandomNeighbor();
        Move move = new Move(pNeighbor, pNeighbor.getParentDistrictID(), precinct.getParentDistrictID());
        return move;
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
    public float calculatePopEqualScore(float idealPop) {
        float score = 1;
        if (population <= idealPop) return score;
        float difference = population - idealPop;
        float lowerThreshold = Float.parseFloat(properties.getProperty("population_lowThreshold"));
        float midThreshold = Float.parseFloat(properties.getProperty("population_midThreshold"));
        float highThreshold = Float.parseFloat(properties.getProperty("population_highThreshold"));
        float lowPenalty = Float.parseFloat(properties.getProperty("population_lowPenalty"));
        float midPenalty = Float.parseFloat(properties.getProperty("population_midPenalty"));
        float highPenalty = Float.parseFloat(properties.getProperty("population_highPenalty"));
        if (difference <= lowerThreshold) return score - lowPenalty;
        else if (difference > lowerThreshold && difference <= midThreshold) return score - midPenalty;
        else return score - highPenalty;
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

    public String getSeedPrecinctId() {
        return seedPrecinctId;
    }

    public void setSeedPrecinctId(String seedPrecinctId) {
        this.seedPrecinctId = seedPrecinctId;
    }
}

