package io.redistrict.Election;

import java.util.Map;

public class ElectionData {
    ElectionType electionType;
    Map<Party,Integer> electionResults;

    public ElectionType getElectionType() {
        return electionType;
    }

    public void setElectionType(ElectionType electionType) {
        this.electionType = electionType;
    }
}
