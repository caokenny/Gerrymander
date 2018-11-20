package io.redistrict.Utils;


import io.redistrict.Territory.Precinct;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class NeighborsPropertiesLoader {
    public static Set<Precinct> getPrecincts(String filePath){
        InputStream inputFile;
        Properties prop = new Properties();
        Set<Precinct> resultSet = new HashSet<>();

        try {
            inputFile = new FileInputStream(filePath);
            prop.load(inputFile);
            Set<String> pID = prop.stringPropertyNames();
            pID.forEach(id -> resultSet.add(new Precinct(id)));
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return resultSet;
    }

}
