package io.redistrict.Utils;


import io.redistrict.Territory.Precinct;
import io.redistrict.Territory.State;
import io.redistrict.Territory.StateEnum;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;


public class StateLoader {

    private static Properties properties = new Properties();


    public static Map<String, State> loadAllStates(StateEnum [] arr){
         Map<String,State> stateMap = new LinkedHashMap<>();

         for(StateEnum stateEnum : arr) {
             String stateName = stateEnum.toString();
             Map<String, Precinct> allPrecincts = loadPrecincts(stateName);
             State currentState = new State(stateName, allPrecincts);
             //TODO make default districts
             stateMap.put(stateName, currentState);
         }
        return stateMap;
    }


    public static Map<String,Precinct> loadPrecincts(String stateName){

        String filePath = properties.getProperty(stateName);
        Map<String,Precinct> precinctMap =null;

        try {
            FileReader fileReader = new FileReader(filePath);
            JSONArray precinctArray = (JSONArray) new JSONParser().parse(fileReader);
            precinctMap = makePrecinctSet(precinctArray);
            NeighborsLoader.loadNeighbors(precinctMap,stateName); //set all the nieghbors for each precinct in the map

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
            String geoId10 = getStringValue(precinctJsObj.get(properties.getProperty("GEOID10")));
            String name = getStringValue(precinctJsObj.get(properties.getProperty("NAME10")));
            precinctMap.put(geoId10,new Precinct(idNum++,geoId10,name,population.intValue()));
        }

        return precinctMap;
    }

    public static Properties getProperties() {
        return properties;
    }

    public static void setProperties(Properties properties) {
        StateLoader.properties = properties;
    }

    public static void loadDefaultProperties(){

        try {
            InputStream precinctLoaderFile = new FileInputStream("src/main/resources/state_loader.properties");
            properties.load(precinctLoaderFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String getStringValue(Object obj){
        return obj instanceof Long? Long.toString((Long)obj) : (String)obj;
    }

}



