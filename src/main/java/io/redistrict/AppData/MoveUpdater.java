package io.redistrict.AppData;


import io.redistrict.Territory.Move;

import java.util.LinkedList;
import java.util.List;

public class MoveUpdater {
    String state ;
    double currentScore;

    private List<MoveUpdate> updates = new LinkedList<>();

    public List<MoveUpdate> getUpdates() {
        return updates;
    }

    public void setUpdates(List<MoveUpdate> updates) {
        this.updates = updates;
    }

    public String toString(){
        return updates.toString();
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCurrentScore(double currentScore) {
        this.currentScore = currentScore;
    }

    public double getCurrentScore() {
        return currentScore;
    }
}
