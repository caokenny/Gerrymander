package io.redistrict.Algorithm;

import com.google.gson.Gson;
import io.redistrict.AppData.AppData;
import org.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AlgorithmController {
    @PostMapping(value = "/startAlgorithm")
    @ResponseBody
    public String startAlgorithm( @RequestBody String algObj) {
        Algorithm alg = new Algorithm();
        AlgorithmData data = new AlgorithmData();
        Gson gson = new Gson();

        data.setType(AlgorithmType.SA);
        System.out.println("we are in startAlgorithm");
        JSONObject item = new JSONObject();
        item.put("successful", "success");
        AppData.setCurrentAlgorithm(alg);
        return item.toString();
    }
    @PostMapping(value = "/continueAlgorithm")
    @ResponseBody
    public String continueAlgorithm(@RequestBody String countObj) {
        System.out.println(countObj);
        JSONObject item = new JSONObject();
        int num = (int)(Math.random()*10+1);
        item.put("successful", num);
        return item.toString();
    }
}
