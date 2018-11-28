package io.redistrict.Territory;

import io.redistrict.Territory.District;

public class Move {
    private String stateName;
    private Precinct precinct;
    private District srcDistrict;
    private District dstDistrict;
    public Move(Precinct precinct, District srcDistrict, District dstDistrict){
        this.precinct = precinct;
        this.srcDistrict = srcDistrict;
        this.dstDistrict = dstDistrict;
    }
    public Precinct getPrecinct(){
        return precinct;
    }
    public District getSrcDistrict(){
        return srcDistrict;
    }
    public District getDstDistrict(){
        return dstDistrict;
    }
}
