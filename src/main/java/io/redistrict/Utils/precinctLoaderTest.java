package io.redistrict.Utils;

import io.redistrict.Territory.Precinct;

import java.util.Map;
import java.util.Set;

public class precinctLoaderTest {
    public static void main(String [] args){
        /*Properties precinctLoaderProp = new Properties();
        InputStream precinctLoaderFile = null;
        try {
            precinctLoaderFile = new FileInputStream("src/main/resources/PrecinctLoader.properties");
            precinctLoaderProp.load(precinctLoaderFile);
            PrecinctLoader.setProperties(precinctLoaderProp);
        }catch (IOException e){
            e.printStackTrace();
        }*/
        NeighborsLoader.loadDefaultProperties();
        PrecinctLoader.loadDefaultProperties();
        Map<String,Precinct> allPrecinct = PrecinctLoader.loadPrecinct("MO");
//        NeighborsLoader.loadNeighbors(allPrecinct,"MO");
//        allPrecinct.forEach((k,v)->System.out.println(k+":"+v.getNeighborIds()));
    }
}
