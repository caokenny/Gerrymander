package io.redistrict.Utils;

import io.redistrict.Territory.District;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Map;

public class JsonConverter {
    public static JSONObject seedDistrictToJson(Map<Integer, District> districtMap){
        JSONObject seedJson = new JSONObject();
        JSONArray districtArray = new JSONArray();
        for(int districtId : districtMap.keySet()){
            String seedPrecinctId = districtMap.get(districtId).getSeedPrecinctId();
            JSONObject districtJson = new JSONObject();

            districtJson.put("districtID",districtId);
            districtJson.put("precinctGeoId",seedPrecinctId);
            districtArray.add(districtJson);
        }
        seedJson.put("seeds", districtArray);
        return seedJson;
    }
}
