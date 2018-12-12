package io.redistrict.Territory;

import io.redistrict.Territory.District;

public class Move {
    private Precinct precinct;
    private int srcDistrictID;
    private int dstDistrictID;
    public Move(Precinct precinct, int srcDistrictID, int dstDistrictID){
        this.precinct = precinct;
        this.srcDistrictID = srcDistrictID;
        this.dstDistrictID = dstDistrictID;
    }
    public Precinct getPrecinct(){
        return precinct;
    }
    public int getSrcDistrictID(){
        return srcDistrictID;
    }
    public int getDstDistrictID(){
        return dstDistrictID;
    }

    @Override
    public String toString() {
        return "Move{" +
                "precinct=" + precinct +
                ", srcDistrictID=" + srcDistrictID +
                ", dstDistrictID=" + dstDistrictID +
                '}';
    }
}
