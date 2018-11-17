package io.redistrict.RegionGrowing.RgController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RegionGrowingController {

    @RequestMapping(value ="/pickRgSeed", method = RequestMethod.POST)
    @ResponseBody
    public String assignSeedDistrict(String stateName, int numOfSeed){

        return null;
    }
}
