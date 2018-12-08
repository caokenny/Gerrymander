package io.redistrict.Utils;

import io.redistrict.Algorithm.Algorithm;
import io.redistrict.AppData.AppData;

import io.redistrict.Territory.State;
import io.redistrict.Territory.StateEnum;



public class precinctLoaderTest {
    public static void main(String [] args) {

        NeighborsLoader.loadDefaultProperties();
        StateLoader.loadDefaultProperties();
        Algorithm.loadDefaultProperties();

        AppData.setStateMap(StateLoader.loadAllStates(StateEnum.values()));

        State co = AppData.getState("MO");
        System.out.println(co.getDefaultDistrict().size());

    }

}
