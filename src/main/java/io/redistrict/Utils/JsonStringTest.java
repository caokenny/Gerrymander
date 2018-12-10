package io.redistrict.Utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class JsonStringTest {
    public static void main (String [] args){
        String path = "/Users/JackieChen/IdeaProjects/Gerrymander/src/main/resources/CO1.JSON";
        try {
            FileReader reader = new FileReader(path);
            JSONArray dataArray =(JSONArray) new JSONParser().parse(reader);
            JSONObject jsobject = (JSONObject)dataArray.get(1);
            String geojson = (String)jsobject.get("GEOJSONSTRING");
            System.out.println(geojson);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
