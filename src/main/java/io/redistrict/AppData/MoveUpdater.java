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

    public void addToUpdates(Move move){
        updates.add(new MoveUpdate(move.getSrcDistrictID(),move.getDstDistrictID(),move.getPrecinct().getGeoID10()));
    }
    public void removeFromBack(){
        if(updates.size() ==0)
            return;
        MoveUpdate toBeRemoved = updates.get(updates.size()-1);
        updates.remove(toBeRemoved);
    }
    public void removeFromFront(){
        if(updates.size()==0)
            return;
        updates.remove(0);

    }
}
