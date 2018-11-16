//package io.redistrict.backend;
//
//import java.util.*;
//
//public class District {
//
//    private int districtId;
//    private int population;
//    private Map<Integer, Precinct> precincts;
//    private Map<Integer, ElectionData> precinctVoteResults;
//    private Map<Party, Integer> electionResult;
//    private Set<Precinct> borderPrecinct;
//
//    public District(int districtId, int population){
//        //Need more than just these two fields in constructor
//    }
//
//    /**
//     * Iterates through electionResults and gets Party with highest votes
//     * @return Winning party of the district
//     */
//    public Party getWinningParty(){
//        HashMap<Party, Integer> partyResults = new HashMap<Party, Integer>();
//        Set set = electionResult.entrySet();
//        Iterator iterator = set.iterator();
//        while(iterator.hasNext()){
//            Map.Entry entry = (Map.Entry)iterator.next();
//            if(!partyResults.containsKey(entry.getKey())){
//                //Does not contain the current party in question
//                partyResults.put((Party)entry.getKey(), entry.getValue());
//            }
//            else{
//                //The party is already in our hashmap. Need to update value
//                partyResults.put(entry.getKey(), partyResults.get(entry.getKey()) + (Integer)entry.getValue();
//            }
//        }
//        Party winningParty = null;
//        int max = 0;
//        Set results = partyResults.entrySet();
//        Iterator loop = results.iterator();
//        while(loop.hasNext()){
//            Map.Entry entry = (Map.Entry)iterator.next();
//            if((Integer)entry.getValue() > max){
//                max = (Integer) entry.getValue();
//                winningParty = (Party)entry.getKey();
//            }
//        }
//        return winningParty;
//    }
//
//    /**
//     * Returns a map of <Precincts, ElectionData> in the current district
//     * @return Map of Precinct Vote Results (PrecinctID, ElectionData)
//     */
//    public Map<Integer, ElectionData> getPrecinctVoteResults(){
//        return precinctVoteResults;
//    }
//
//    /**
//     * Adds the new precinct to the current District
//     * @param precinct
//     */
//    public void addPrecinct(Precinct precinct){
//        precincts.put(precinct.getPrecinctId(), precinct);
//        population += precinct.getPopulation();
//        //Need to add the new precinct's election vote results into this districts precinvt vote results hash
//        //Change new border precincts. Add new precincts data into election Result
//    }
//
//    /**
//     * Removes the precinct from our current District
//     * @param precinct
//     */
//    public void removePrecinct(Precinct precinct){
//        precincts.remove(precinct);
//        population -= precinct.getPopulation();
//        //Remove the precincts election vote results from hash.
//        //Change border precincts again. Remove precincts data from election Result
//    }
//
//    /**
//     * Returns the population of the district
//     * @return population
//     */
//    public int getPopulation(){
//        return population;
//    }
//
//    /**
//     * Returns a map of the Partys and the votes of the current District
//     * @return Map of the Partys and number of Votes each party got
//     */
//    public Map<Party, Integer> getElectionResult(){
//        return electionResult;
//    }
//
//    /**
//     * Returns a Collection of all Precincts neighboring the District
//     * @return Collection of neighbor Precincts
//     */
//    public Collection<Precinct> getAdjPrecs(){
//        //return a list of all the adjacent neighbors?
//    }
//
//    /**
//     * Creates a new District with only one precinct, the seed
//     * @param seed
//     * @return new District
//     */
//    public District makeSeedDistrict(Precinct seed){
//        //Call constructor with seed's info to make a new district. return it
//    }
//
//    /**
//     * Returns a random precinct from the District's set of precincts
//     * @return random precinct
//     *
//     */
//    public Precinct getRandomPrecinct(){
//        //Return a random precinct
//    }
//
//    /**
//     * Removes the adjacent neighbor from the District
//     * @param precinct
//     * @return whether the removal was successful or not.
//     */
//    public boolean removeAdjPrec(Precinct precinct){
//
//    }
//
//}
