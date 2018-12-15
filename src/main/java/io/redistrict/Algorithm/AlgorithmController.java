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
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

@RestController
public class AlgorithmController {
    private Algorithm alg;
    private AlgorithmData data;

    @PostMapping(value = "/startAlgorithm")
    public String startAlgorithm( String stateName) {
        System.out.println(stateName);
        alg = new Algorithm();
        data = new AlgorithmData();
        alg.setData(data);
        Gson gson = new Gson();
        System.out.println("The algObj is " + stateName);
//        ObjectMapper mapper = new ObjectMapper();
//        Map<String, String> map = new HashMap<String, String>();
//        try {
//            map = mapper.readValue(algObj, new TypeReference<Map<String, String>>() {});
//            System.out.println(map);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        String stateName;
//        if (map.get("state").equals("colorado"))
//            stateName = "CO";
//        else if (map.get("state").equals("kansas"))
//            stateName = "MO";
//        else stateName = "KA";
        State state = AppData.getState(stateName);
        data.setWorkingState(state);
//        if (map.get("algorithm").equals("sa"))
//            data.setType(AlgorithmType.SA);
//        else
//            data.setType(AlgorithmType.RG);
//        data.getWeights().setCompactness(Integer.parseInt(map.get("compactness")));
//        data.getWeights().setPopulationEquality(Integer.parseInt(map.get("populationEquality")));
        System.out.println(data.toString());
        System.out.println("we are in startAlgorithm");
        District.loadDefaultProperties();
        state.initPopScores();
        System.out.println(state.getDefaultDistrict().size());
//        JSONObject item = new JSONObject(map);
//        item.put("successful", "We are starting the algorithm now...");
        AppData.setCurrentAlgorithm(alg);
        data.setType(AlgorithmType.SA);
        return "We are starting algorithm now...";
    }
    @PostMapping(value = "/continueAlgorithm")
    @ResponseBody
    public MoveUpdater continueAlgorithm(@RequestBody String countObj) {
        ObjectiveFunctionCalculator calculator = new ObjectiveFunctionCalculator();
        calculator.setWeights(data.getWeights());
        Stack<Move> moves = alg.run10SA();
        MoveUpdater updater = new MoveUpdater();
//        System.out.println(countObj);
//        JSONObject item = new JSONObject();
//        int num = (int)(Math.random()*10+1);
//        item.put("successful", num);
        if (moves.isEmpty())
            System.out.println("moves is empty");
        for (Move m : moves) {
//            System.out.println(m);
            MoveUpdate update = new MoveUpdate(m.getSrcDistrictID(), m.getDstDistrictID(), m.getPrecinct().getGeoID10());
            updater.getUpdates().add(update);
        }
//        System.out.println("\n\n");
//        for (MoveUpdate m : updater.getUpdates()) {
//            System.out.println(m);
//        }
//        ADD moves.clear() and updater.getupdates().clear() if run10SA() returns the moveStack associated with
//        state bc it is never cleared. This clears it because it is pointing to the same state moveStack
        moves.clear();

        //double currentScore = calculator.getStateObjectiveFunction(alg.getData().getWorkingState(),AlgorithmType.SA);
        //updater.setCurrentScore(currentScore);
        return updater;
    }

    @PostMapping(value = "/setWeights")
    public String setWeights( @RequestBody AlgorithmWeights wts) {
        alg = new Algorithm();
        data = new AlgorithmData();
        alg.setData(data);

        wts.setEfficencyGap(wts.getEfficencyGap()/100);
        wts.setPartisanFairness(wts.getPartisanFairness()/100);
        wts.setPopulationEquality(wts.getPopulationEquality()/100);
        wts.setCompactness(wts.getCompactness()/100);
        data.setWeights(wts);

        State state = AppData.getState(wts.getStateAbbrv().toUpperCase());
        state.initPopScores();

        data.setWorkingState(state);
        AppData.setCurrentAlgorithm(alg);
        data.setType(AlgorithmType.SA);

        System.out.println(data);
        JSONObject item = new JSONObject();
        item.put("success", "Weights successfully set");
        ObjectiveFunctionCalculator calculator = new ObjectiveFunctionCalculator();
        calculator.setWeights(data.getWeights());
        System.out.println("ORIGINAL SCORE IS:" + calculator.getStateObjectiveFunction(state,AlgorithmType.SA));
//        return "Weights successfully set";
//        can't return String because we specify in Ajax call that a json is expected to be returned
        return item.toString();
    }
}
