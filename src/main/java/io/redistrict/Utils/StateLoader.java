package io.redistrict.Utils;


import io.redistrict.Territory.District;
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
             StateData stateData = loadStateData(stateName);
             State currentState = new State(stateName, stateData.getAllPrecinct());
             currentState.setDefaultDistrict(stateData.getDefaultDistricts());
             stateMap.put(stateName, currentState);
         }
        return stateMap;
    }

    public static Map<Integer, District> loadDefaultDistricts(Map<String , Precinct> allPrecinct){
        Map<Integer,District> defaultDistrict = new LinkedHashMap<>();

        for(Precinct precinct : allPrecinct.values()){
            int parentId = precinct.getParentDistrictID();
            if(defaultDistrict.get(parentId) == null){
                District newDistrict = new District(parentId,precinct);
                defaultDistrict.put(parentId,newDistrict);
            }
            else{
                defaultDistrict.get(parentId).addPrecinct(precinct);
            }
        }
        return defaultDistrict;
    }


    public static StateData loadStateData(String stateName){

        String filePath = properties.getProperty(stateName);
        Map<String,Precinct> precinctMap =null;
        StateData stateData = new StateData();

        try {
            FileReader fileReader = new FileReader(filePath);
            JSONArray precinctArray = (JSONArray) new JSONParser().parse(fileReader);
            precinctMap = getPrecinctData(precinctArray);
            NeighborsLoader.loadNeighbors(precinctMap,stateName);
            Map<Integer,District> defaultDistrict = loadDefaultDistricts(precinctMap);
            stateData.setDefaultDistricts(defaultDistrict);
            stateData.setAllPrecinct(precinctMap);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return stateData;
    }

    /*
    get map of GEOID10 to Precinct from JSONArray
     */
    private static Map<String, Precinct> getPrecinctData(JSONArray jsonArray)
    {
        Map<String, Precinct> precinctMap = new HashMap<>();

        for(Object obj : jsonArray){
            JSONObject precinctJsObj = (JSONObject) obj;
            Long population = (Long) precinctJsObj.get(properties.getProperty("POPULATION"));
            String geoId10 = getStringValue(precinctJsObj.get(properties.getProperty("GEOID10")));
            String name = getStringValue(precinctJsObj.get(properties.getProperty("NAME10")));
            Long districtId = (Long) precinctJsObj.get(properties.get("DISTRICTID"));
            //String geoJsonString = (String) precinctJsObj.get(properties.get("GEOJSON"));
            JSONObject geoJson =(JSONObject) precinctJsObj.get(properties.get("GEOJSON"));
            String geoJsonString= geoJson.toJSONString();

            Precinct precinct = new Precinct(geoId10,name,population.intValue(), geoJsonString);
            precinct.setParentDistrictID(districtId.intValue());

            precinctMap.put(geoId10,precinct);
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



