package io.redistrict.Utils;

import io.redistrict.Territory.Precinct;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NeighborFinder {

    public static Set<Precinct> findUnassignedNeighbors(Map<String,
            Precinct> allPrecincts, Set<String> unassignedIds, List<Precinct> districtBorderPrecincts)
    {

        Set<Precinct> unassignedNeighbors = new LinkedHashSet<>();

        for(Precinct precinct : districtBorderPrecincts)
        {
            List<Precinct> neighbor = precinct.getNeighbors();
            for(Precinct p : neighbor)
            {
                if(unassignedIds.contains(p.getGeoID10())) { unassignedNeighbors.add(allPrecincts.get(p.getGeoID10())); }
            }
        }

        return unassignedNeighbors;
    }
}
