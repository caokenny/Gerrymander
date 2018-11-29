package io.redistrict.Utils;

import io.redistrict.AppData.AppData;
import io.redistrict.Territory.StateEnum;

public class precinctLoaderTest {
    public static void main(String [] args) {

        NeighborsLoader.loadDefaultProperties();
        StateLoader.loadDefaultProperties();
        AppData.setStateMap(StateLoader.loadAllStates(StateEnum.values()));
        //System.out.println(new AppData().toString());
    }

}
