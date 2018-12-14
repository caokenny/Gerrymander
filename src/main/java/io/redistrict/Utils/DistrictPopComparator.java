package io.redistrict.Utils;

import io.redistrict.Territory.District;

import java.util.Comparator;

public class DistrictPopComparator implements Comparator<District> {
    @Override
    public int compare(District o1, District o2) {
        return o2.getPopulation()-o1.getPopulation();
    }
}
