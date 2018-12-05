package io.redistrict.Algorithm;

import java.util.Map;

public class ObjectiveFunction {
    private Map<MeasuresEnum, Float> scores;
    private Map<MeasuresEnum, Float> weights;
    private double objVal;

    public ObjectiveFunction(Map<MeasuresEnum, Float> scores, Map<MeasuresEnum, Float> weights) {
        this.scores = scores;
        this.weights = weights;
    }

    public Map<MeasuresEnum, Float> getScores() {
        return scores;
    }

    public void setScores(Map<MeasuresEnum, Float> scores) {
        this.scores = scores;
    }

    public Map<MeasuresEnum, Float> getWeights() {
        return weights;
    }

    public void setWeights(Map<MeasuresEnum, Float> weights) {
        this.weights = weights;
    }

    public double calculateObjectiveFunction() {
        for (Float f1 : scores.values()) {
            for (Float f2 : weights.values())
                objVal += f1 * f2;
        }
        return objVal;
    }
}