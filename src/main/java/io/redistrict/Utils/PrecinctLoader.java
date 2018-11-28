package io.redistrict.Utils;


import io.redistrict.Territory.Precinct;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;


public class PrecinctLoader {

    private static Properties properties = new Properties();

    public static Map<String,Precinct> loadPrecinct(String stateName){

        String filePath = properties.getProperty(stateName);
        Map<String,Precinct> precinctMap =null;

        try {
            FileReader fileReader = new FileReader(filePath);
            JSONArray precinctArray = (JSONArray) new JSONParser().parse(fileReader);
            precinctMap = makePrecinctSet(precinctArray);
            NeighborsLoader.loadNeighbors(precinctMap,stateName);

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return precinctMap;
    }
    /*
    get map of GEOID10 to Precinct from JSONArray
     */
    private static Map<String,Precinct> makePrecinctSet(JSONArray jsonArray)
    {
        int idNum=1;
        Map<String, Precinct> precinctMap = new HashMap<>();
        for(Object obj : jsonArray){
            JSONObject precinctJsObj = (JSONObject) obj;
            Long population = (Long) precinctJsObj.get(properties.getProperty("POPULATION"));
            String geoId10 = (String)precinctJsObj.get(properties.getProperty("GEOID10"));
            String name = (String)precinctJsObj.get(properties.getProperty("NAME10"));

            precinctMap.put(geoId10,new Precinct(idNum++,geoId10,name,population.intValue()));
        }

        return precinctMap;
    }

    public static Properties getProperties() {
        return properties;
    }

    public static void setProperties(Properties properties) {
        PrecinctLoader.properties = properties;
    }

    public static void loadDefaultProperties(){

        try {
            InputStream precinctLoaderFile = new FileInputStream("src/main/resources/PrecinctLoader.properties");
            properties.load(precinctLoaderFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}



