package io.redistrict.Algorithm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.redistrict.AppData.AppData;
import org.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class AlgorithmController {
    private Algorithm alg = new Algorithm();
    private AlgorithmData data = new AlgorithmData();
    private AlgorithmWeights wts = new AlgorithmWeights();

    @PostMapping(value = "/startAlgorithm")
    @ResponseBody
    public String startAlgorithm( @RequestBody String algObj) {
        data.setWeights(wts);
        Gson gson = new Gson();
        System.out.println("The algObj is " + algObj);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = new HashMap<String, String>();
        try {
            map = mapper.readValue(algObj, new TypeReference<Map<String, String>>() {});
            System.out.println(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (map.get("algorithm").equals("sa"))
            data.setType(AlgorithmType.SA);
        else
            data.setType(AlgorithmType.RG);
        data.getWeights().setCompactness(Integer.parseInt(map.get("compactness")));
        data.getWeights().setPopulationEquality(Integer.parseInt(map.get("populationEquality")));
        System.out.println(data.toString());
        System.out.println("we are in startAlgorithm");
        JSONObject item = new JSONObject(map);
        item.put("successful", "We are starting the algorithm now...");
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
