package io.redistrict.Utils;

import io.redistrict.Territory.Precinct;

import java.util.Collections;
import java.util.Set;

public class PrecinctSelector {
    public static Precinct selectRandomPrecinct(Set<Precinct> options)
    {
        int randomIndex =(int) (Math.random()*(options.size()));
        return (Precinct)options.toArray()[randomIndex];
    }
}
