package io.redistrict.Utils;

import io.redistrict.Territory.District;
import io.redistrict.Territory.Precinct;

import java.util.Map;

public class StateData {
    private Map<String, Precinct> allPrecinct;
    private Map<Integer, District> defaultDistricts;

    public Map<String, Precinct> getAllPrecinct() {
        return allPrecinct;
    }

    public void setAllPrecinct(Map<String, Precinct> allPrecinct) {
        this.allPrecinct = allPrecinct;
    }

    public Map<Integer, District> getDefaultDistricts() {
        return defaultDistricts;
    }

    public void setDefaultDistricts(Map<Integer, District> defaultDistricts) {
        this.defaultDistricts = defaultDistricts;
    }
}
