package io.redistrict.Utils;


import io.redistrict.Territory.Precinct;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.*;


public class NeighborsLoader {

    private static Properties properties = new Properties();



    public static boolean loadNeighbors(Map<String,Precinct> precinctMap, String stateName){
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(properties.getProperty(stateName));
            JSONObject neighborJSObj =  (JSONObject) new JSONParser().parse(fileReader);

            for(Object o : neighborJSObj.keySet()){
                List <Precinct> neighbors = new ArrayList<>();
                String key = (String)o;
                JSONArray neighborValues =  (JSONArray) neighborJSObj.get(key);
                Precinct keyPrecinct = precinctMap.get(key);

                neighborValues.forEach(n-> neighbors.add( precinctMap.get((String)n)));
                //keyPrecinct.setNeighborIds(neighbors);
                keyPrecinct.setNeighbors(neighbors);
            }
            return true;
        }catch (IOException | ParseException e){
            e.printStackTrace();
        }
        return false;
    }

    public static Properties getProperties() {
        return properties;
    }

    public static void setProperties(Properties properties) {
        NeighborsLoader.properties = properties;
    }

    public static void loadDefaultProperties(){
        InputStream nStream = NeighborsLoader.class.getClassLoader().getResourceAsStream("neighbors.properties");
        try{
            properties.load(nStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
