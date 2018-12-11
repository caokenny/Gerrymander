package io.redistrict.Algorithm;

import io.redistrict.Territory.Precinct;
import io.redistrict.Territory.State;

import java.util.Set;

public class AlgorithmData {
    private  Set<Precinct> startingSeeds;
    private AlgorithmWeights weights;
    private AlgorithmType type;
    private State workingState;

    public Set<Precinct> getStartingSeeds() {
        return startingSeeds;
    }

    public void setStartingSeeds(Set<Precinct> startingSeeds) {
        this.startingSeeds = startingSeeds;
    }

    public AlgorithmWeights getWeights() {
        return weights;
    }

    public void setWeights(AlgorithmWeights weights) {
        this.weights = weights;
    }

    public AlgorithmType getType() {
        return type;
    }

    public void setType(AlgorithmType type) {
        this.type = type;
    }

    public State getWorkingState() {
        return workingState;
    }

    public void setWorkingState(State workingState) {
        this.workingState = workingState;
    }

    @Override
    public String toString() {
        return "AlgorithmData{" +
                "startingSeeds=" + startingSeeds +
                ", weights=" + weights +
                ", type=" + type +
                ", workingState=" + workingState +
                '}';
    }
}
