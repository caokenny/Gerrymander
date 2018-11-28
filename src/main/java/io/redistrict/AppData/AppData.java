package io.redistrict.AppData;

import io.redistrict.Territory.Precinct;
import io.redistrict.Territory.State;

import java.util.Map;

public class AppData {

    /** this is only temporary! will be removed later with stateMap only **/
    private static Map<String, Precinct> precinctMap;
    private static Map<String, State> stateMap;

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
}

