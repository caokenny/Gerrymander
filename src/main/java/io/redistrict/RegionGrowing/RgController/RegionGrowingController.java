package io.redistrict.RegionGrowing.RgController;

import io.redistrict.Algorithm.Algorithm;
import io.redistrict.Algorithm.AlgorithmData;
import io.redistrict.Algorithm.AlgorithmType;
import io.redistrict.Algorithm.AlgorithmWeights;
import io.redistrict.AppData.AppData;
import io.redistrict.AppData.MoveUpdater;
import io.redistrict.RegionGrowing.RgUtilities.ColorRandomizer;
import io.redistrict.RegionGrowing.RgUtilities.RgSeedSelector;
import io.redistrict.Territory.District;
import io.redistrict.Territory.Precinct;
import io.redistrict.Territory.State;
import io.redistrict.Utils.JsonColorConverter;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

import java.util.Collection;
import java.util.Set;

@Controller
@RequestMapping("/rg")
public class RegionGrowingController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RequestMapping(value = "/pickrgseed", method = RequestMethod.GET)
    @ResponseBody
    public String assignSeedDistrict(String stateName , int seedNum){

        State state = AppData.getState(stateName);
        Collection<Precinct> precinctSet = state.getAllPrecincts().values();
        Set<Color> colorSet = ColorRandomizer.pickRandomColors(seedNum);
        Set<Precinct> seeds = RgSeedSelector.pickRandomSeeds(precinctSet,seedNum);
        Set<District> seedDistricts = District.makeSeedDistricts(seeds);

        //SET TO APP DATA SO WE CAN KEEP TRACK OF CURRENT ALGO
        Algorithm rgGrowingAlg = new Algorithm();
        AlgorithmData  rgData = new AlgorithmData();
        rgData.setWorkingState(state);
        rgData.setStartingSeeds(seeds);
        rgData.setType(AlgorithmType.RG);
        rgGrowingAlg.setData(rgData);
        AppData.setCurrentAlgorithm(rgGrowingAlg);

        JSONObject initSeedJson = JsonColorConverter.districtColorToJson(colorSet,seedDistricts);

        return initSeedJson.toString();

    }

    @PostMapping(value = "/startRg")
    @ResponseBody
    public MoveUpdater startRg(@RequestBody AlgorithmWeights weights) {
        Algorithm currentAlgorithm = AppData.getCurrentAlgorithm();
        if(currentAlgorithm == null){
            return null;
        }
        // set weight variable to current algorithm
        currentAlgorithm.getData().setWeights(weights);

        MoveUpdater updater = currentAlgorithm.do10RgIteration();
        return updater;
    }
}
