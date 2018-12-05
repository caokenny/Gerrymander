package io.redistrict.Algorithm;

public class AlgorithmEntry {
    private double compactness;
    private double populationEquality;
    private double partisanFairness;
    private double efficencyGap;
    private String algorithm;
    private String stateAbbrv; // THIS HAS TO BE ALL CAPS

    public double getCompactness() {
        return compactness;
    }

    public void setCompactness(double compactness) {
        this.compactness = compactness;
    }

    public double getPopulationEquality() {
        return populationEquality;
    }

    public void setPopulationEquality(double populationEquality) {
        this.populationEquality = populationEquality;
    }

    public double getPartisanFairness() {
        return partisanFairness;
    }

    public void setPartisanFairness(double partisanFairness) {
        this.partisanFairness = partisanFairness;
    }

    public double getEfficencyGap() {
        return efficencyGap;
    }

    public void setEfficencyGap(double efficencyGap) {
        this.efficencyGap = efficencyGap;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }
    public String toString(){
        return "this is an algorithm entry with compactness of:" +compactness;
    }

    public String getStateAbbrv() {
        return stateAbbrv;
    }

    public void setStateAbbrv(String stateAbbrv) {
        this.stateAbbrv = stateAbbrv;
    }
}
