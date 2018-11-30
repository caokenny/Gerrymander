package io.redistrict.RegionGrowing.RgUtilities;

import io.redistrict.Territory.Precinct;

import java.util.*;
import java.util.stream.IntStream;

public class RgSeedSelector {
    public static Set<Precinct> pickRandomSeeds(Collection<Precinct> precincts, int numOfSeeds){
        List<Precinct> precinctLst = new ArrayList<>();
        Set<Precinct> seeds = new HashSet<>();

        precinctLst.addAll(precincts);
        Collections.shuffle(precinctLst);
        IntStream.range(0,numOfSeeds).forEach(nbr -> seeds.add(precinctLst.get(nbr)));

        return seeds;
    }
}
