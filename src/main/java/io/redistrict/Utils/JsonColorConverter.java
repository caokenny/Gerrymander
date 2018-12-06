package io.redistrict.Utils;

import io.redistrict.Territory.District;
import io.redistrict.Territory.Precinct;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.util.Collection;
import java.util.Iterator;


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
    public static JSONObject precinctColorToJson(Collection<Color> colors, Collection<Precinct> precincts){
        if(colors.size() != precincts.size())
            return null;

        JSONObject colorJson = convertToJson(colors);
        JSONArray colorsArrJson = colorJson.getJSONArray("Colors");
        Iterator<Precinct> precinctIterator =  precincts.iterator();

        for(int index = 0 ; precinctIterator.hasNext();index ++){
          JSONObject colorArrElement = colorsArrJson.getJSONObject(index);
          colorArrElement.put("GEOID10",precinctIterator.next().getGeoID10());
        }
        return colorJson;
    }

    public static JSONObject districtColorToJson(Collection<Color> colors, Collection<District> districts){
        if(colors.size() != districts.size())
            return null;

        JSONObject colorJson = convertToJson(colors);
        JSONArray colorsArrJson = colorJson.getJSONArray("Colors");
        Iterator<District> districtIterator =  districts.iterator();

        for(int index = 0 ; districtIterator.hasNext();index ++){
            JSONObject colorArrElement = colorsArrJson.getJSONObject(index);
            District currentDistrict = districtIterator.next();
            colorArrElement.put("DistrictId",currentDistrict.getDistrictId());
            colorArrElement.put("GEOID10",currentDistrict.getSeedPrecinctId());
        }
        return colorJson;
    }

}
