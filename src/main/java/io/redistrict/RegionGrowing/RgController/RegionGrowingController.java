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
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.*;
import java.util.Set;

@Controller
@RequestMapping("/rg")
public class RegionGrowingController {

    @RequestMapping("/pickrgseed")
    @ResponseBody
    public String assignSeedDistrict(/*String stateName , */@RequestParam("seeds") String seedsRequest){
        int numOfSeed = Integer.parseInt(seedsRequest);
        Set<Precinct> precinctSet = NeighborsPropertiesLoader.getPrecincts("src/main/resources/MO_Neighbors.properties");
        Set<Color> colorSet = ColorRandomizer.pickRandomColors(numOfSeed);
        Set<Precinct> seeds = RgSeedSelector.pickRandomSeeds(precinctSet,numOfSeed);
        JSONObject initSeedJson = JsonColorConverter.precinctColorToJson(colorSet,seeds);
        return initSeedJson.toString();

    }
}
