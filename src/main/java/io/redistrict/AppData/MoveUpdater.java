package io.redistrict.AppData;


import io.redistrict.Territory.Move;

import java.util.LinkedList;
import java.util.List;

public class MoveUpdater {
    String state ;
    Score oldScore;
    Score newScore;

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

    public Score getOldScore() {
        return oldScore;
    }

    public void setOldScore(Score oldScore) {
        this.oldScore = oldScore;
    }

    public Score getNewScore() {
        return newScore;
    }

    public void setNewScore(Score newScore) {
        this.newScore = newScore;
    }
}
