package io.redistrict.Utils;

import io.redistrict.Territory.Precinct;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class NeighborFinder {

    public static Set<Precinct> findUnassignedNeighbors(Map<String,
            Precinct> allPrecincts, Set<String> unassignedIds, Set<Precinct> keys)
    {

        Set<Precinct> unassignedNeighbors = new LinkedHashSet<>();

        for(Precinct precinct : keys)
        {
            Set<String> neighborIds = precinct.getNeighborIds();
            for(String id : neighborIds)
            {
                if(unassignedIds.contains(id)) { unassignedNeighbors.add(allPrecincts.get(id)); }
            }
        }

        return unassignedNeighbors;
    }
}
