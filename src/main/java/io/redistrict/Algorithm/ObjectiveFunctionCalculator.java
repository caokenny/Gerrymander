package io.redistrict.Algorithm;

import io.redistrict.Territory.District;
import io.redistrict.Territory.State;

public class ObjectiveFunctionCalculator {
    AlgorithmWeights weights;

    public AlgorithmWeights getWeights() {
        return weights;
    }

    public void setWeights(AlgorithmWeights weights) {
        this.weights = weights;
    }

    public double getDistrictObjectiveFunction(State state, District distict , AlgorithmType type){
        double perimeter = distict.getPerimeter(distict.getAllDPrecincts());
        double area = distict.getArea(distict.getAllDPrecincts());
        float ideaPop = state.calculateIdealPop(type);

        double popScore =(double) distict.calculatePopEqualScore(ideaPop);
        double compactness = distict.calculatePolsbyPopper(area,perimeter);

        double weightedPopScore = weights.getPopulationEquality()*popScore;
        double weightedCompactness = weights.getCompactness()*compactness;

        return weightedCompactness+weightedPopScore;
    }

    public double getStateObjectFunction(State state,AlgorithmType type){
        //TODO
        return 0.0;
    }
}
