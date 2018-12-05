package io.redistrict.Algorithm;

import io.redistrict.Territory.Precinct;

import java.util.Set;

public class AlgorithmManager {
    private static Set<Precinct> currentSeeds;
    private static AlgorithmEntry entry;
    private static Algorithm currentAlgorithm;

    public static Set<Precinct> getCurrentSeeds() {
        return currentSeeds;
    }

    public static void setCurrentSeeds(Set<Precinct> currentSeeds) {
        AlgorithmManager.currentSeeds = currentSeeds;
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
}
