package io.redistrict.Election;

import java.util.Map;

public class ElectionData {
    private ElectionType electionType;
    private Map<Party,Integer> electionResults;

    public Map<Party, Integer> getElectionResults() {
        return electionResults;
    }

    public void setElectionResults(Map<Party, Integer> electionResults) {
        this.electionResults = electionResults;
    }

    public ElectionType getElectionType() {
        return electionType;
    }

    public void setElectionType(ElectionType electionType) {
        this.electionType = electionType;
    }
}
