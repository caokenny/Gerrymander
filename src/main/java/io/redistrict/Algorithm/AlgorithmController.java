package io.redistrict.Algorithm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AlgorithmController {
    @PostMapping(value = "/algorithm")
    @ResponseBody
    public String startAlgorithm(@RequestBody String algObj) {
        System.out.println("we are in startAlgorithm");
        System.out.println(algObj);
        JSONObject item = new JSONObject();
        item.put("successful", "success");
        return item.toString();
    }
}
