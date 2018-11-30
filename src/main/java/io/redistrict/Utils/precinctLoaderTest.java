package io.redistrict.Utils;

import io.redistrict.Algorithm.Algorithm;
import io.redistrict.AppData.AppData;
import io.redistrict.RegionGrowing.RgUtilities.RgSeedSelector;
import io.redistrict.Territory.District;
import io.redistrict.Territory.Precinct;
import io.redistrict.Territory.State;
import io.redistrict.Territory.StateEnum;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class precinctLoaderTest {
    public static void main(String [] args) {

        NeighborsLoader.loadDefaultProperties();
        StateLoader.loadDefaultProperties();
        AppData.setStateMap(StateLoader.loadAllStates(StateEnum.values()));

        Algorithm algorithm = new Algorithm();
        State mo = AppData.getState("MO");
        Set<Precinct> seeds = RgSeedSelector.pickRandomSeeds(mo.getAllPrecincts().values(),3);
        State rgState = algorithm.startRg(seeds, mo.getStateName());
        District district1 = rgState.getDistricts().get(1);
        System.out.println("{ \"geoid\": [");
        for(String pId : district1.getAllDPrecincts().keySet()){
            System.out.println("\"" + pId + "\",");
        }
        System.out.println("]}");

    }

}
