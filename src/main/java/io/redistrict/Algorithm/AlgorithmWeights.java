package io.redistrict.Algorithm;

public class AlgorithmWeights {
    private double compactness;
    private double populationEquality;
    private double partisanFairness;
    private double efficencyGap;
    private String algorithm;
    private String stateAbbrv; // THIS HAS TO BE ALL CAPS
    private boolean variance;

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

    public String getStateAbbrv() {
        return stateAbbrv;
    }

    public void setStateAbbrv(String stateAbbrv) {
        this.stateAbbrv = stateAbbrv;
    }

    public boolean isVariance() {
        return variance;
    }

    public void setVariance(boolean variance) {
        this.variance = variance;
    }

    @Override
    public String toString() {
        return "AlgorithmWeights{" +
                "compactness=" + compactness +
                ", populationEquality=" + populationEquality +
                ", partisanFairness=" + partisanFairness +
                ", efficencyGap=" + efficencyGap +
                ", algorithm='" + algorithm + '\'' +
                ", stateAbbrv='" + stateAbbrv + '\'' +
                '}';
    }
}
