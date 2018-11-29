package io.redistrict.Territory;

import io.redistrict.Election.ElectionData;
import io.redistrict.Election.Party;

import java.util.*;

public class District {

    private int districtID;
    private int population;
    private Map<String, Precinct> precincts;
    private Map<String, List<ElectionData>> precinctVoteResults;
    private Map<Party, Integer> electionResult;
    private List<Precinct> borderPrecincts;

    public District(int districtID, int population){
        //Need more than just these two fields in constructor
    }
    public Party getWinningParty(){
        HashMap<Party, Integer> partyResults = new HashMap<Party, Integer>();
        Set set = electionResult.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()){
            Map.Entry entry = (Map.Entry)iterator.next();
            if(!partyResults.containsKey(entry.getKey())){
                //Does not contain the current party in question
                partyResults.put((Party)entry.getKey(), (int)entry.getValue());
            }
            else{
                //The party is already in our hashmap. Need to update value
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
    public Map<String, List<ElectionData>> getPrecinctVoteResults(){
        return precinctVoteResults;
    }
    public void addPrecinct(Precinct precinct){
        precinct.setParentDistrictId(districtID);
        precincts.put(precinct.getGeoID10(), precinct);
        population += precinct.getPopulation();
        precinctVoteResults.put(precinct.getGeoID10(), precinct.getElectionData());
        if(isBorderPrecinct(precinct)) {
            precinct.setIsBorder(true);
            borderPrecincts.add(precinct);
        }
        else
            precinct.setIsBorder(false);
    }
    public void removePrecinct(Precinct precinct){
        precinct.setParentDistrictId(-1);
        precincts.remove(precinct);
        population -= precinct.getPopulation();
        precinctVoteResults.remove(precinct.getGeoID10(), precinct.getElectionData());
        if(precinct.isBorder()) {
            borderPrecincts.remove(precinct);
        }
        //Remove the precincts election vote results from hash.
        //Change border precincts again. Remove precincts data from election Result
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
    public void setPopulation(int population){
        this.population = population;
    }
    public int getDistrictID(){
        return districtID;
    }
    public int getPopulation(){
        return population;
    }
    public Map<Party, Integer> getElectionResult(){
        return electionResult;
    }

}
