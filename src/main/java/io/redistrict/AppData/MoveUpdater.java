package io.redistrict.AppData;


import io.redistrict.Territory.Move;

import java.util.LinkedList;
import java.util.List;

public class MoveUpdater {
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
}
