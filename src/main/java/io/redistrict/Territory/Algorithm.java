package io.redistrict.Algorithm;

import io.redistrict.AppData.AppData;
import io.redistrict.Territory.District;
import io.redistrict.Territory.Move;
import io.redistrict.Territory.Precinct;
import io.redistrict.Territory.State;
import io.redistrict.Utils.NeighborFinder;
import io.redistrict.Utils.PrecinctSelector;

import java.util.*;

public class Algorithm {
    int badMoves = 0;
    public State startRg(Set<Precinct> seeds, String stateName){
        State state = makeRgState(seeds,stateName);
        Map<Integer,District> possibleDistricts = new LinkedHashMap<>(state.getDistricts());
        while (!state.getUnassignedPrecinctIds().isEmpty()) {
            District rgDistrict = selectRgDistrict(possibleDistricts);
            Precinct rgPrecinct = selectRgAdditionPrecinct(rgDistrict, state);
            state.removeFromUnassigned(rgPrecinct.getGeoID10());
            Move move = new Move(rgPrecinct,-1,rgDistrict.getDistrictId());
            state.executeMove(move);
            if(rgDistrict.getNumOfNeighbors()== 0){
                possibleDistricts.remove(rgDistrict.getDistrictId());
            }
        }
        return state;
    }
    public State getSimulatedState(){
        State state = AppData.getState(stateName.toUpperCase());
        while(badMoves < 25){
            District district = state.getRandomDistrict();
            double oldScore = state.getDistrictScore(district);
            Move move = district.modifyDistrict();
            Precinct modifiedPrecinct = move.getPrecinct();
            modifiedPrecinct.setParentDistrictId(move.getDstDistrictID());
            District srcDistrict = state.getDistricts.get(move.getSrcDistrictID());
//            (make getter in State class)
            District dstDistrict =state.getDistricts.get(move.getDstDistrictID());
            srcDistrict.removePrecinct(modifiedPrecinct);
            dstDistrict.addPrecinct(modifiedPrecinct);
            double newScore = state.getDistrictScore(district);
            if(newScore > oldScore){
                state.addToMoveStack(move);
            }
            else{
                badMoves++;
                boolean acceptBadMove = state.acceptBadMove(oldScore, newScore);
                if(acceptBadMove){
                    state.addToMoveStack(move);
                }
                else
                    state.undoLastMove(move);
            }
        }
        return state;
    }
    private State makeRgState(Set<Precinct> seeds , String stateName){
        State state = AppData.getState(stateName.toUpperCase());
        Map<Integer,District> seedDistricts = makeSeedDistricts(seeds);
        Set<String> allPrecinctIds = state.getAllPrecincts().keySet();
        Set<String> initUnassignedPrecincts = getInitUnassignedPrecinctIds(seeds,allPrecinctIds);
        state.setDistricts(seedDistricts);
        state.setUnassignedPrecinctIds(initUnassignedPrecincts);
        return state;

    }

    private Map<Integer, District> makeSeedDistricts(Set<Precinct> seeds){
        int startDistrictId=1;
        Map<Integer,District> seedDistricts = new LinkedHashMap<>();
        for(Precinct precinct : seeds){
            seedDistricts.put(startDistrictId,new District(startDistrictId,precinct));
            startDistrictId++;
        }
        return  seedDistricts;
    }

    private Set<String> getInitUnassignedPrecinctIds(Set<Precinct> exclusions,Set<String> allPrecinctsIds){
        Set<String> initUnAssignedPIds = new LinkedHashSet<>(allPrecinctsIds);
        exclusions.forEach(precinct -> initUnAssignedPIds.remove(precinct.getGeoID10()));
        return initUnAssignedPIds;
    }

    private District selectRgDistrict(Map<Integer,District> districtMap) {
        int minPop = -1;
        int minPopDId = -1;
        for(int districtId : districtMap.keySet()) {
            int currentDistrictPop = districtMap.get(districtId).getPopulation();
            if(currentDistrictPop< minPop || minPop == -1) {
                minPopDId = districtId;
                minPop=currentDistrictPop;
            }
        }
        if(minPopDId==-1){ return null;}
        return districtMap.get(minPopDId);
    }

    private Precinct selectRgAdditionPrecinct(District district, State state) {
        List<Precinct> borderPrecincts = district.getBorderPrecincts();
        Set<Precinct> unassignedNeighbors = NeighborFinder.findUnassignedNeighbors
                (state.getAllPrecincts(),state.getUnassignedPrecinctIds(),borderPrecincts);
        district.setNumOfNeighbors(unassignedNeighbors.size()-1);
        return PrecinctSelector.selectRandomPrecinct(unassignedNeighbors);
    }
}
