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
    private List<Precinct> borderPrecincts;
    private int numOfNeighbors;
    private String seedPrecinctId;

    private static Properties properties = new Properties();
    public District(int districtId,Precinct startPrecinct){
        this.districtId = districtId;
        this.population= startPrecinct.getPopulation();
        this.allDPrecincts = new LinkedHashMap<>();
        this.borderPrecincts = new ArrayList<>();
        allDPrecincts.put(startPrecinct.getGeoID10(),startPrecinct);
        borderPrecincts.add(startPrecinct);
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
        if(isBorderPrecinct(precinct)) {
            precinct.setIsBorder(true);
            borderPrecincts.add(precinct);
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
            borderPrecincts.remove(precinct);
        }
    }

    public int getPopulation(){
        return population;
    }

    public Map<Party, Integer> getElectionResult(){
        return electionResult;
    }

    public void updateBorderPrecincts(Set<String> unassignedPrecinctIds) {
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

    public List<Precinct> getBorderPrecincts() {
        return borderPrecincts;
    }

    public void setBorderPrecincts(List<Precinct> borderPrecincts) {
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
    public boolean isBorderPrecinct(Precinct precinct){
        List<Precinct> neighbors = precinct.getNeighbors();
        for(Precinct p : neighbors){
            if(p.getParentDistrictID() != precinct.getParentDistrictID())
                return true;
        }
        return false;
    }
    public Precinct getRandomPrecinct(){
        //Return a random precinct
        int numPrecincts = borderPrecincts.size();
        Random rand = new Random();
        int n = rand.nextInt(numPrecincts) + 0;
        return borderPrecincts.get(n);
    }
    public Move modifyDistrict(){
        Precinct precinct = getRandomPrecinct();
        Precinct pNeighbor = precinct.getRandomNeighbor();
        while(!pNeighbor.isBorder())
            pNeighbor = precinct.getRandomNeighbor();
        Move move = new Move(pNeighbor, pNeighbor.getParentDistrictID(), precinct.getParentDistrictID());
        return move;
    }
    public int getDistrictID(){
        return districtId;
    }
}

