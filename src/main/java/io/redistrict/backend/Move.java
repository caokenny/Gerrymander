package io.redistrict.backend;

public class Move {
    private String stateName;
    private Precinct precinct;
    private District srcDistrict;
    private District dstDistrict;
    public Move(String stateName, Precinct precinct, District srcDistrict, District dstDistrict){
        this.stateName = stateName;
        this.precinct = precinct;
        this.srcDistrict = srcDistrict;
        this.dstDistrict = dstDistrict;
    }
}
