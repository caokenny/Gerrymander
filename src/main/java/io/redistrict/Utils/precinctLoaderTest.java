package io.redistrict.Utils;

import io.redistrict.Algorithm.Algorithm;
import io.redistrict.Algorithm.AlgorithmData;
import io.redistrict.Algorithm.AlgorithmType;
import io.redistrict.Algorithm.AlgorithmWeights;
import io.redistrict.AppData.AppData;
import io.redistrict.AppData.MoveUpdater;
import io.redistrict.RegionGrowing.RgUtilities.RgSeedSelector;
import io.redistrict.Territory.District;
import io.redistrict.Territory.Precinct;
import io.redistrict.Territory.State;
import io.redistrict.Territory.StateEnum;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class precinctLoaderTest {
    public static void main(String [] args) {

        Algorithm.loadDefaultProperties();
        District.loadDefaultProperties();
        NeighborsLoader.loadDefaultProperties();
        StateLoader.loadDefaultProperties();
        AppData.setStateMap(StateLoader.loadAllStates(StateEnum.values()));

        //setup
        State mo = AppData.getState("MO");
        State co = AppData.getState("CO");
        State ks = AppData.getState("KS");

        State moCopy = AppData.getStateCopy("MO");
        State coCopy = AppData.getStateCopy("CO");
        State ksCopy = AppData.getStateCopy("KS");

        Set<Precinct> seeds = RgSeedSelector.pickRandomSeeds(moCopy.getAllPrecincts().values(),10);
        Map<Integer,District> seedDistricts = District.makeSeedDistricts(seeds);
        moCopy.setRgdistricts(seedDistricts);
        moCopy.resetUnassignedPrecinctIds();

        Algorithm algorithm = new Algorithm();
        AlgorithmData data = new AlgorithmData();
        data.setWorkingState(moCopy);
        data.setStartingSeeds(seeds);
        data.setType(AlgorithmType.RG);
        algorithm.setData(data);
        AppData.setCurrentAlgorithm(algorithm);

        //Start
        Algorithm currentAlgorithm = AppData.getCurrentAlgorithm();
        if(currentAlgorithm == null){
            System.out.println("ALGORITHM IS NULL");
        }
        AlgorithmWeights weights = new AlgorithmWeights();
        weights.setCompactness(0);
        weights.setPopulationEquality(0);
        weights.setPartisanFairness(0);
        weights.setEfficencyGap(1);
        weights.setVariance(false);
        currentAlgorithm.getData().setWeights(weights);

//
//        //AREA AND PERIMETER TEST
//        Map<String,State> stateMap = AppData.getStateMap();
//        for(String stateName : stateMap.keySet()){
//            State state = stateMap.get(stateName);
//            System.out.println("*************STATE: "+ stateName+"*********************");
//            for(int districtId : state.getDefaultDistrict().keySet()){
//                District district = state.getDefaultDistrict().get(districtId);
//                System.out.println("DistrictId: "+districtId);
//                System.out.println("perimeter: "+district.getPerimeter(district.getAllDPrecincts()));
//                System.out.println("area: "+district.getArea(district.getAllDPrecincts()));
//            }
//        }

//        PRECINCT AREA AND PERIMETER TEST
//        for(String geoid10 : mo.getAllPrecincts().keySet()) {
//            Precinct precinct = mo.getAllPrecincts().get(geoid10);
//            System.out.println("precinct geoid: "+ precinct.getGeoID10() +" area: "+precinct.getArea()+ " perimeter: "+ precinct.getPerimeter());
//        }
//        for(String geoid10 : co.getAllPrecincts().keySet()) {
//            Precinct precinct = co.getAllPrecincts().get(geoid10);
//            System.out.println("precinct geoid: "+ precinct.getGeoID10() +" area: "+precinct.getArea()+ " perimeter: "+ precinct.getPerimeter());
//        }
//        for(String geoid10 : ks.getAllPrecincts().keySet()) {
//            Precinct precinct = ks.getAllPrecincts().get(geoid10);
//            System.out.println("precinct geoid: "+ precinct.getGeoID10() +" area: "+precinct.getArea()+ " perimeter: "+ precinct.getPerimeter());
//        }

//
//        ALGORITHM TEST
        Set<String> unassignedPrecinctIds = currentAlgorithm.getData().getWorkingState().getUnassignedPrecinctIds();
        while (!unassignedPrecinctIds.isEmpty()) {
            MoveUpdater updater = currentAlgorithm.do10RgIteration();
        }



//        District district = new District(1,mo.getAllPrecincts().values().iterator().next());
//        System.out.println("Real area:"+district.getArea(mo.getAllPrecincts()));
//        System.out.println("Real perimeter:"+district.getPerimeter(mo.getAllPrecincts()));

//        Iterator<Precinct> iterator = mo.getAllPrecincts().values().iterator();
//        Precinct testPrecinct = iterator.next();
//        Precinct testPrecinct2 = iterator.next();
//        District district = new District(1,testPrecinct);
//        district.addPrecinct(testPrecinct2,AlgorithmType.SA);
//        System.out.println(district.getArea(district.getAllDPrecincts()));
//        System.out.println(district.getPerimeter(district.getAllDPrecincts()));


    }

}
