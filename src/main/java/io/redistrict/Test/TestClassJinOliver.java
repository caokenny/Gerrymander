package io.redistrict.Test;

import io.redistrict.Algorithm.Algorithm;
import io.redistrict.Algorithm.AlgorithmData;
import io.redistrict.Algorithm.AlgorithmType;
import io.redistrict.Algorithm.AlgorithmWeights;
import io.redistrict.AppData.AppData;
import io.redistrict.Territory.State;
import io.redistrict.Territory.StateEnum;
import io.redistrict.Utils.NeighborsLoader;
import io.redistrict.Utils.StateLoader;

public class TestClassJinOliver {
    public static void main (String [] args){
        NeighborsLoader.loadDefaultProperties();
        StateLoader.loadDefaultProperties();
        Algorithm.loadDefaultProperties();
        AppData.setStateMap(StateLoader.loadAllStates(StateEnum.values()));

        Algorithm algorithm = new Algorithm();
        AlgorithmData data = new AlgorithmData();
        data.setType(AlgorithmType.SA);
        algorithm.setAlgorithmData(data);

        //IF YOU HAVE WEIGHTS YOU WANT TO USE TO TEST. USE AlgorithmWeights setters to set them.
        //WEIGHTS ARE ALL EMPTY RN
        AlgorithmWeights weights = new AlgorithmWeights();
        data.setWeights(weights);

        State state = AppData.getState("ENTER YOUR STATE NAME HERE");
        data.setWorkingState(state);

        //RUN ALG AFTER THIS (ex- algorithm.run10sa()) and check if it ran correctly

    }
}
