package io.redistrict.Algorithm;

import io.redistrict.Territory.Precinct;

import java.util.Set;

public class AlgorithmManager {
    private static Set<Precinct> startingSeeds;
    private static AlgorithmEntry entry;
    private static String currentState;
    private static Algorithm currentAlgorithm;

    public static Set<Precinct> getStartingSeeds() {
        return startingSeeds;
    }

    public static void setStartingSeeds(Set<Precinct> startingSeeds) {
        AlgorithmManager.startingSeeds = startingSeeds;
    }

    public static AlgorithmEntry getEntry() {
        return entry;
    }

    public static void setEntry(AlgorithmEntry entry) {
        AlgorithmManager.entry = entry;
    }

    public static Algorithm getCurrentAlgorithm() {
        return currentAlgorithm;
    }

    public static void setCurrentAlgorithm(Algorithm currentAlgorithm) {
        AlgorithmManager.currentAlgorithm = currentAlgorithm;
    }

    public static String getCurrentState() {
        return currentState;
    }

    public static void setCurrentState(String currentState) {
        AlgorithmManager.currentState = currentState;
    }
}
