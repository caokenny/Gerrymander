package io.redistrict.RegionGrowing.RgController;

import io.redistrict.Algorithm.Algorithm;
import io.redistrict.Algorithm.AlgorithmEntry;
import io.redistrict.Algorithm.AlgorithmManager;
import io.redistrict.AppData.AppData;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Controller
@RequestMapping("/rg")
public class RegionGrowingController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RequestMapping(value = "/pickrgseed", method = RequestMethod.GET)
    @ResponseBody
    public String assignRandomSeedDistrict(/*String stateName , String numOfSeed*/){
        System.out.println("Picking RG Seeds");
        int seedNum = 3;//Integer.parseInt(numOfSeed);
        State state = AppData.getState("MO"/*stateName*/);
        Collection<Precinct> precinctSet = state.getAllPrecincts().values();
        Set<Color> colorSet = ColorRandomizer.pickRandomColors(seedNum);
        Set<Precinct> seeds = RgSeedSelector.pickRandomSeeds(precinctSet,seedNum);
        Set<District> seedDistricts = District.makeSeedDistricts(seeds);
        AlgorithmManager.setCurrentSeeds(seeds);

        JSONObject initSeedJson = JsonColorConverter.districtColorToJson(colorSet,seedDistricts);

        return initSeedJson.toString();

    }

    @PostMapping(value = "/startRg")
    @ResponseBody
    public void startRg(@RequestBody AlgorithmEntry entry) {
        Algorithm rgAlg = new Algorithm();
        AlgorithmManager.setEntry(entry);
        AlgorithmManager.setCurrentAlgorithm(rgAlg);

        rgAlg.startRg(AlgorithmManager.getCurrentSeeds(), entry.getStateAbbrv());

    }
}
