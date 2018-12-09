package io.redistrict.Utils;

import io.redistrict.Algorithm.Algorithm;
import io.redistrict.Algorithm.AlgorithmData;
import io.redistrict.Algorithm.AlgorithmType;
import io.redistrict.AppData.AppData;
import io.redistrict.AppData.MoveUpdater;
import io.redistrict.RegionGrowing.RgUtilities.RgSeedSelector;
import io.redistrict.Territory.District;
import io.redistrict.Territory.Precinct;

import io.redistrict.Territory.State;
import io.redistrict.Territory.StateEnum;


import java.util.*;

public class precinctLoaderTest {
    public static void main(String [] args) {

        NeighborsLoader.loadDefaultProperties();
        StateLoader.loadDefaultProperties();
        AppData.setStateMap(StateLoader.loadAllStates(StateEnum.values()));

        //setup
        State mo = AppData.getState("CO");
        Set<Precinct> seeds = RgSeedSelector.pickRandomSeeds(mo.getAllPrecincts().values(),3);
        Map<Integer,District> seedDistricts = District.makeSeedDistricts(seeds);
        mo.setDistricts(seedDistricts);
        mo.resetUnassignedPrecinctIds();

        Algorithm algorithm = new Algorithm();
        AlgorithmData data = new AlgorithmData();
        data.setWorkingState(mo);
        data.setStartingSeeds(seeds);
        data.setType(AlgorithmType.RG);
        algorithm.setData(data);
        AppData.setCurrentAlgorithm(algorithm);

        //Start
        Algorithm currentAlgorithm = AppData.getCurrentAlgorithm();
        if(currentAlgorithm == null){
            System.out.println("ALGORITHM IS NULL");
        }
        // set weight variable to current algorithm
        //currentAlgorithm.getData().setWeights(weights);

        Set<String> unassignedPrecinctIds = currentAlgorithm.getData().getWorkingState().getUnassignedPrecinctIds();
        while (!unassignedPrecinctIds.isEmpty()) {
            MoveUpdater updater = currentAlgorithm.do10RgIteration();
            System.out.println(updater.getUpdates().size());
        }

    }

}
