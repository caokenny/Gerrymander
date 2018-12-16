package io.redistrict.RegionGrowing.RgController;

import io.redistrict.Algorithm.*;
import io.redistrict.AppData.AppData;
import io.redistrict.AppData.MoveUpdater;
import io.redistrict.AppData.Score;
import io.redistrict.RegionGrowing.RgUtilities.RgSeedSelector;
import io.redistrict.Territory.District;
import io.redistrict.Territory.Precinct;
import io.redistrict.Territory.State;

import io.redistrict.Utils.JsonConverter;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/rg")
public class RegionGrowingController {

    @PostMapping("/pickrgseed")
    @ResponseBody
    public String assignSeedDistrict(String stateName , int seedNum){
        State state = AppData.getState(stateName);
        Collection<Precinct> precinctSet = state.getAllPrecincts().values();
        Set<Precinct> seeds = RgSeedSelector.pickRandomSeeds(precinctSet,seedNum);
        Map<Integer,District> seedDistricts = District.makeSeedDistricts(seeds);
        state.setRgdistricts(seedDistricts);
        state.resetUnassignedPrecinctIds(); // make all unassignPrecinctIds = all precinct ids

        //SET TO APP DATA SO WE CAN KEEP TRACK OF CURRENT ALGO
        Algorithm rgGrowingAlg = new Algorithm();
        AlgorithmData  rgData = new AlgorithmData();
        rgData.setWorkingState(state);
        rgData.setStartingSeeds(seeds);
        rgData.setType(AlgorithmType.RG);
        rgGrowingAlg.setData(rgData);
        AppData.setCurrentAlgorithm(rgGrowingAlg);

        JSONObject initSeedJson = JsonConverter.seedDistrictToJson(seedDistricts);

        return initSeedJson.toString();

    }

    @PostMapping(value = "/do10Rg")
    @ResponseBody
    public MoveUpdater startRg(@RequestBody AlgorithmWeights weights) {
        Algorithm currentAlgorithm = AppData.getCurrentAlgorithm();
        if(currentAlgorithm == null){
            throw  new NullPointerException("ALGORITHM IS NULL");
        }
        // set weight variable to current algorithm
        weights.setCompactness(weights.getCompactness()/100);
        weights.setPopulationEquality(weights.getPopulationEquality()/100);
        weights.setPartisanFairness(weights.getPartisanFairness()/100);
        weights.setEfficencyGap(weights.getEfficencyGap()/100);
        currentAlgorithm.getData().setWeights(weights);
        MoveUpdater updater = currentAlgorithm.do10RgIteration();
        updater.setState(currentAlgorithm.getData().getWorkingState().getStateName());
        return updater;
    }

}
