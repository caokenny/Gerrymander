package io.redistrict.Territory;

public class Move {
    private Precinct precinct;
    private District srcDistrict;
    private District destDistrict;

    public Move(Precinct precinct, District srcDistrict, District destDistrict) {
        this.precinct = precinct;
        this.srcDistrict = srcDistrict;
        this.destDistrict = destDistrict;
    }

    public Precinct getPrecinct() {
        return precinct;
    }

    public void setPrecinct(Precinct precinct) {
        this.precinct = precinct;
    }

    public District getSrcDistrict() {
        return srcDistrict;
    }

    public void setSrcDistrict(District srcDistrict) {
        this.srcDistrict = srcDistrict;
    }

    public District getDestDistrict() {
        return destDistrict;
    }

    public void setDestDistrict(District destDistrict) {
        this.destDistrict = destDistrict;
    }
}
