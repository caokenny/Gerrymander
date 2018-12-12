package io.redistrict.Algorithm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.redistrict.AppData.AppData;
import io.redistrict.AppData.MoveUpdate;
import io.redistrict.AppData.MoveUpdater;
import io.redistrict.Territory.District;
import io.redistrict.Territory.Move;
import io.redistrict.Territory.State;
import io.redistrict.Territory.StateEnum;
import io.redistrict.Utils.NeighborsLoader;
import io.redistrict.Utils.StateLoader;
import org.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@Controller
public class AlgorithmController {
    private Algorithm alg;
    private AlgorithmData data;
    private AlgorithmWeights wts;

    @PostMapping(value = "/startAlgorithm")
    @ResponseBody
    public String startAlgorithm( @RequestBody String algObj) {
        alg = new Algorithm();
        data = new AlgorithmData();
        wts = new AlgorithmWeights();
        alg.setData(data);
        NeighborsLoader.loadDefaultProperties();
        StateLoader.loadDefaultProperties();
        Algorithm.loadDefaultProperties();
        AppData.setStateMap(StateLoader.loadAllStates(StateEnum.values()));
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
        String stateName;
        if (map.get("state").equals("colorado"))
            stateName = "CO";
        else if (map.get("state").equals("kansas"))
            stateName = "MO";
        else stateName = "KA";
        State state = AppData.getState(stateName);
        data.setWorkingState(state);
        if (map.get("algorithm").equals("sa"))
            data.setType(AlgorithmType.SA);
        else
            data.setType(AlgorithmType.RG);
        data.getWeights().setCompactness(Integer.parseInt(map.get("compactness")));
        data.getWeights().setPopulationEquality(Integer.parseInt(map.get("populationEquality")));
        System.out.println(data.toString());
        System.out.println("we are in startAlgorithm");
        District.loadDefaultProperties();
        state.initPopScores();
        System.out.println(state.getDefaultDistrict().size());
        JSONObject item = new JSONObject(map);
        item.put("successful", "We are starting the algorithm now...");
        AppData.setCurrentAlgorithm(alg);
        return item.toString();
    }
    @PostMapping(value = "/continueAlgorithm")
    @ResponseBody
    public MoveUpdater continueAlgorithm(@RequestBody String countObj) {
        Stack<Move> moves = alg.run10SA();
        MoveUpdater updater = new MoveUpdater();
//        System.out.println(countObj);
//        JSONObject item = new JSONObject();
//        int num = (int)(Math.random()*10+1);
//        item.put("successful", num);
        if (moves.isEmpty())
            System.out.println("moves is empty");
        for (Move m : moves) {
            System.out.println(m);
            MoveUpdate update = new MoveUpdate(m.getSrcDistrictID(), m.getDstDistrictID(), m.getPrecinct().getGeoID10());
            updater.getUpdates().add(update);
        }
        System.out.println("\n\n");
        for (MoveUpdate m : updater.getUpdates()) {
            System.out.println(m);
        }
        return updater;
    }

}
