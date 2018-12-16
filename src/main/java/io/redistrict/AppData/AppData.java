package io.redistrict.AppData;

import io.redistrict.Algorithm.Algorithm;
import io.redistrict.Algorithm.AlgorithmType;
import io.redistrict.Election.VoteData;
import io.redistrict.Territory.District;
import io.redistrict.Territory.Precinct;
import io.redistrict.Territory.State;
import io.redistrict.Utils.StateLoader;
import org.springframework.security.core.parameters.P;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AppData {

    /** this is only temporary! will be removed later with stateMap only **/
    private static Map<String, Precinct> precinctMap;
    private static Map<String, State> stateMap;
    private static Algorithm currentAlgorithm;

    public static Map<String, Precinct> getPrecinctMap() {
        return precinctMap;
    }

    public static void setPrecinctMap(Map<String, Precinct> precinctMap) {
        AppData.precinctMap = precinctMap;
    }

    public static Map<String, State> getStateMap() {
        return stateMap;
    }

    public static void setStateMap(Map<String, State> stateMap) {
        AppData.stateMap = stateMap;
    }

    public String toString(){
        String result = "";
        for(String stateName : stateMap.keySet()){
            result += "State: "+ stateName
                    +" Precinct size: "
                    +stateMap.get(stateName).getAllPrecincts().size()+"\n";
        }
        return result;
    }

    public static State getState(String stateName){
        //return new State(stateMap.get(stateName.toUpperCase()));
        return getStateCopy(stateName.toUpperCase());
    }

    public static Algorithm getCurrentAlgorithm() {
        return currentAlgorithm;
    }

    public static void setCurrentAlgorithm(Algorithm currentAlgorithm) {
        AppData.currentAlgorithm = currentAlgorithm;
    }

    public static State getStateCopy(String stateName){
        State originalState = stateMap.get(stateName.toUpperCase());

        Map<String,Precinct> originalPrecinctMap = originalState.getAllPrecincts();
        Map<String,Precinct> precinctMapCopy = makePrecinctCopy(originalPrecinctMap);
        assignNeighbors(originalPrecinctMap, precinctMapCopy);
        Map<Integer,District> defaultDistrictCopy = StateLoader.loadDefaultDistricts(precinctMapCopy);

        State stateCopy = new State(stateName.toUpperCase(), precinctMapCopy);
        stateCopy.setDefaultDistrict(defaultDistrictCopy);
        stateCopy.setTotalVotes();
        return stateCopy;
    }
    private static Map<String,Precinct> makePrecinctCopy(Map<String, Precinct> originalPrecinctMap){
        Map<String,Precinct> precinctMapCopy = new LinkedHashMap<>();

        for(String geoid : originalPrecinctMap.keySet()){
            Precinct originalPrecinct = originalPrecinctMap.get(geoid);
            Precinct precinctCopy = copyPrecinct(originalPrecinct);

            if(precinctMapCopy.get(geoid)== null) { precinctMapCopy.put(geoid,precinctCopy); }
            else{ throw new NullPointerException("ADDING A PRECINCT WITH A GEOID THAT ALREADY EXIST"); }
        }
        return precinctMapCopy;
    }

    private static Precinct copyPrecinct (Precinct originalPrecinct)
    {
        //STILL NEED TO MAKE NEIGHBORSLIST
        int population = originalPrecinct.getPopulation();
        String name = originalPrecinct.getName();
        String geoid10 = originalPrecinct.getGeoID10();
        int parentDistrictId = originalPrecinct.getParentDistrictID();
        boolean isBorder = originalPrecinct.isBorder();
        String geoJsonString = originalPrecinct.getGeoJsonString();
        VoteData voteData = new VoteData();
        voteData.setDemVotes(originalPrecinct.getVoteData().getDemVotes());
        voteData.setRepVotes(originalPrecinct.getVoteData().getRepVotes());

        Precinct precinctCopy = new Precinct(population);
        precinctCopy.setName(name);
        precinctCopy.setGeoID10(geoid10);
        precinctCopy.setVoteData(voteData);
        precinctCopy.setParentDistrictID(parentDistrictId);
        precinctCopy.setIsBorder(isBorder);
        precinctCopy.setGeoJsonString(geoJsonString);

        return precinctCopy;
    }
    private static void assignNeighbors(Map<String, Precinct> originalMap , Map<String, Precinct> copyMap){

        for (Precinct precinct : originalMap.values()){
            String geoId = precinct.getGeoID10();
            Precinct precinctCopy = copyMap.get(geoId);

            if(precinctCopy == null){ throw new NullPointerException("THIS PRECINCT DOESN'T EXIST IN COPY");}

            List<Precinct> originalNeighborsList = precinct.getNeighbors();
            List<Precinct> neighborsListCopy = new ArrayList<>();

            for (Precinct originalNeighbor : originalNeighborsList){
                Precinct neighborCopy = copyMap.get(originalNeighbor.getGeoID10());
                if(neighborCopy== null){throw new NullPointerException("THIS PRECINCT DOESN'T EXIST IN COPY");}
                neighborsListCopy.add(neighborCopy);
            }
            precinctCopy.setNeighbors(neighborsListCopy);
        }

    }
}

