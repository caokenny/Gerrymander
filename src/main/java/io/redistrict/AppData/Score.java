package io.redistrict.AppData;

public class Score {
    double PopScore;
    double CompactnessScore;
    double efficencyGapScore;
    double partisanScore;
    double stateObjScore;

    public double getPopScore() {
        return PopScore;
    }

    public void setPopScore(double popScore) {
        PopScore = popScore;
    }

    public double getCompactnessScore() {
        return CompactnessScore;
    }

    public void setCompactnessScore(double compactnessScore) {
        CompactnessScore = compactnessScore;
    }

    public double getEfficencyGapScore() {
        return efficencyGapScore;
    }

    public void setEfficencyGapScore(double efficencyGapScore) {
        this.efficencyGapScore = efficencyGapScore;
    }

    public double getPartisanScore() {
        return partisanScore;
    }

    public void setPartisanScore(double partisanScore) {
        this.partisanScore = partisanScore;
    }

    public double getStateObjScore() {
        return stateObjScore;
    }

    public void setStateObjScore(double stateObjScore) {
        this.stateObjScore = stateObjScore;
    }

    @Override
    public String toString() {
        return "Score{" +
                "PopScore=" + PopScore +
                ", CompactnessScore=" + CompactnessScore +
                ", efficencyGapScore=" + efficencyGapScore +
                ", partisanScore=" + partisanScore +
                ", stateObjScore=" + stateObjScore +
                '}';
    }
}
