package io.redistrict.AppData;



public class MoveUpdate {

    int srcDistId;
    int destDistId;
    String precinctId;

    public MoveUpdate(int srcDistId, int destDistId, String precinctId) {
        this.srcDistId = srcDistId;
        this.destDistId = destDistId;
        this.precinctId = precinctId;
    }

    public int getSrcDistId() {
        return srcDistId;
    }

    public void setSrcDistId(int srcDistId) {
        this.srcDistId = srcDistId;
    }

    public int getDestDistId() {
        return destDistId;
    }

    public void setDestDistId(int destDistId) {
        this.destDistId = destDistId;
    }

    public String getPrecinctId() {
        return precinctId;
    }

    public void setPrecinctId(String precinctId) {
        this.precinctId = precinctId;
    }

    public String toString()
    {
        return "precinct: "+precinctId +" move from district: "+srcDistId + " to district: "+destDistId;
    }
}
