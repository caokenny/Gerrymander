package io.redistrict.RegionGrowing.RgController;

import io.redistrict.Algorithm.Algorithm;
import io.redistrict.Algorithm.AlgorithmWeights;
import io.redistrict.AppData.AppData;
import io.redistrict.RegionGrowing.RgUtilities.ColorRandomizer;
import io.redistrict.RegionGrowing.RgUtilities.RgSeedSelector;
import io.redistrict.Territory.District;
import io.redistrict.Territory.Precinct;
import io.redistrict.Territory.State;
import io.redistrict.Utils.JsonColorConverter;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

import java.util.Collection;
import java.util.Set;

@Controller
@RequestMapping("/rg")
public class RegionGrowingController {

    @RequestMapping(value = "/pickrgseed", method = RequestMethod.GET)
    @ResponseBody
    public String assignSeedDistrict(String stateName , int seedNum){
        System.out.println("Picking RG Seeds");
//        int seedNum = Integer.parseInt(numOfSeed);
        State state = AppData.getState(stateName);
        Collection<Precinct> precinctSet = state.getAllPrecincts().values();
        Set<Color> colorSet = ColorRandomizer.pickRandomColors(seedNum);
        Set<Precinct> seeds = RgSeedSelector.pickRandomSeeds(precinctSet,seedNum);
        Set<District> seedDistricts = District.makeSeedDistricts(seeds);

        JSONObject initSeedJson = JsonColorConverter.districtColorToJson(colorSet,seedDistricts);
        return initSeedJson.toString();

    }

    @PostMapping(value = "/startRg")
    @ResponseBody
    public void startRg(@RequestBody AlgorithmWeights entry) {
        Algorithm algorithm = new Algorithm();
        AppData.setCurrentAlgorithm(algorithm);
    }
}
