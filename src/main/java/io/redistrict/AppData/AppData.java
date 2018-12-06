package io.redistrict.AppData;

import io.redistrict.Algorithm.Algorithm;
import io.redistrict.Territory.Precinct;
import io.redistrict.Territory.State;

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
        return new State(stateMap.get(stateName.toUpperCase()));
    }

    public static Algorithm getCurrentAlgorithm() {
        return currentAlgorithm;
    }

    public static void setCurrentAlgorithm(Algorithm currentAlgorithm) {
        AppData.currentAlgorithm = currentAlgorithm;
    }
}

