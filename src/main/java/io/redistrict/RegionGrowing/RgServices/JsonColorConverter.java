package io.redistrict.RegionGrowing.RgServices;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.util.Collection;


public class JsonColorConverter {
    public static JSONObject convertToJson(Color c){
        JSONObject resultObj = new JSONObject();
        resultObj.put("red",c.getRed());
        resultObj.put("green",c.getGreen());
        resultObj.put("blue",c.getBlue());
        return resultObj;
    }
    public static JSONObject convertToJson(Collection<Color> colors){
        JSONObject resultObj = new JSONObject();
        JSONArray colorsJsonArr = new JSONArray();

        colors.forEach(c ->{
            JSONObject element = convertToJson(c);
            colorsJsonArr.put(element);
        });
        resultObj.put("Colors",colorsJsonArr);
        return resultObj;
    }
}
