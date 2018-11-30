package io.redistrict.RegionGrowing.RgController;

import io.redistrict.RegionGrowing.RgUtilities.ColorRandomizer;
import io.redistrict.RegionGrowing.RgUtilities.RgSeedSelector;
import io.redistrict.Territory.Precinct;
import io.redistrict.Utils.JsonColorConverter;
import io.redistrict.Utils.NeighborsPropertiesLoader;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.*;
import java.util.Set;

@Controller
@RequestMapping("/rg")
public class RegionGrowingController {

    @RequestMapping(value = "/pickrgseed", method = RequestMethod.GET)
    @ResponseBody
    public String assignSeedDistrict(String stateName , String numOfSeed){
        int seedNum = Integer.parseInt(numOfSeed);
        Set<Precinct> precinctSet = NeighborsPropertiesLoader.getPrecincts("src/main/resources/MO_Neighbors.properties");
        Set<Color> colorSet = ColorRandomizer.pickRandomColors(seedNum);
        Set<Precinct> seeds = RgSeedSelector.pickRandomSeeds(precinctSet,seedNum);
        JSONObject initSeedJson = JsonColorConverter.precinctColorToJson(colorSet,seeds);

        if(initSeedJson !=null ){
            return initSeedJson.toString();}

        return "{}";

    }
}
