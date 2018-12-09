package io.redistrict.Algorithm;

import io.redistrict.AppData.AppData;
import io.redistrict.AppData.MoveUpdate;
import io.redistrict.AppData.MoveUpdater;
import io.redistrict.Territory.District;
import io.redistrict.Territory.Move;
import io.redistrict.Territory.Precinct;
import io.redistrict.Territory.State;
import io.redistrict.Utils.NeighborFinder;
import io.redistrict.Utils.PrecinctSelector;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Algorithm {

    private static Properties properties = new Properties();
    private AlgorithmData data = new AlgorithmData();

    public MoveUpdater do10RgIteration(){
        State state = data.getWorkingState();
        Map<Integer,District> rgDistricts= state.getDistricts();
        List<MoveUpdate> updates = new ArrayList<>();
        int iterationsDone = 0;

        while (!state.getUnassignedPrecinctIds().isEmpty() && iterationsDone <10){
            // IF ONLY 1 DISTRICT THEN WE ASSIGN ALL TO IT
            if(rgDistricts.size() == 1){
                District district = rgDistricts.values().iterator().next();
                MoveUpdater updater = assignAll(district,state);
                return updater;
            }
            //ELSE
            District rgDistrict = getLowestPopDistrict(rgDistricts);

            Precinct rgPrecinct = selectRgAdditionPrecinct(rgDistrict,state);
            state.removeFromUnassignedIds(rgPrecinct.getGeoID10());
            Move move = new Move(rgPrecinct,-1,rgDistrict.getDistrictId());
            state.executeRgMove(move);
            updates.add(new MoveUpdate(move.getSrcDistrictID(),move.getDstDistrictID(),move.getPrecinct().getGeoID10()));
            if(rgDistrict.getNumOfNeighbors() == 0) {
                rgDistricts.remove(rgDistrict.getDistrictId());
            }
            System.out.println("num of unassigned precincts left: "+ state.getUnassignedPrecinctIds().size());
            System.out.println("num of rgDistrictsLeft: "+rgDistricts.size());
            iterationsDone++;
        }

        MoveUpdater updater = new MoveUpdater();
        updater.setUpdates(updates);
        return updater;
    }



    public State startRg(Set<Precinct> seeds, String stateName){
        State state = makeRgState(seeds,stateName);
        Map<Integer,District> possibleDistricts = new LinkedHashMap<>(state.getDistricts());
        while (!state.getUnassignedPrecinctIds().isEmpty()) {
            District rgDistrict = getLowestPopDistrict(possibleDistricts);
            Precinct rgPrecinct = selectRgAdditionPrecinct(rgDistrict, state);
            state.removeFromUnassignedIds(rgPrecinct.getGeoID10());
            Move move = new Move(rgPrecinct,-1,rgDistrict.getDistrictId());
            state.executeRgMove(move);
            if(rgDistrict.getNumOfNeighbors()== 0){
                possibleDistricts.remove(rgDistrict.getDistrictId());
            }
        }
        return state;
    }
    public State getSimulatedState(String variant){
        List<MoveUpdate> updates = new ArrayList<>();
        int iterationsDone = 0;
        State state = data.getWorkingState();
        int badMoves = 0;
        int max_bad_move = Integer.parseInt(properties.getProperty("max_bad_moves"));
        double acceptanceConstant = Double.parseDouble(properties.getProperty("acceptance_constant"));
        double constantMultiplier = Double.parseDouble(properties.getProperty("constant_multiplier"));

        while(badMoves < max_bad_move && iterationsDone < 10){
            District district;
            if(variant.equals("random"))
                district = state.getRandomDistrict();
            else{
                district = state.getLowestScoreDistrict();
            }
            double oldScore = state.getDistrictScore(district);
            Move move = district.modifyDistrict();
            Precinct modifiedPrecinct = move.getPrecinct();
            modifiedPrecinct.setParentDistrictID(move.getDstDistrictID());
            District srcDistrict = state.getDistricts().get(move.getSrcDistrictID());
            District dstDistrict =state.getDistricts().get(move.getDstDistrictID());
            srcDistrict.removePrecinct(modifiedPrecinct);
            dstDistrict.addPrecinct(modifiedPrecinct);
            double newScore = state.getDistrictScore(district);
            if(newScore > oldScore){
                state.addToMoveStack(move);
            }
            else{
                boolean acceptBadMove = state.acceptBadMove(oldScore, newScore, acceptanceConstant);
                if(acceptBadMove){
                    state.addToMoveStack(move);
                }
                else {
                    state.undoLastMove(move);
                    badMoves++;
                }
                acceptanceConstant *= constantMultiplier;
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

    private District getLowestPopDistrict(Map<Integer,District> districtMap) {
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
        List<Precinct> borderPrecincts = district.getBorderRgPrecincts();
        Set<Precinct> unassignedNeighbors = NeighborFinder.findUnassignedNeighbors
                (state.getAllPrecincts(),state.getUnassignedPrecinctIds(),borderPrecincts);
        district.setNumOfNeighbors(unassignedNeighbors.size()-1);
        return PrecinctSelector.selectRandomPrecinct(unassignedNeighbors);
    }
    public static void loadDefaultProperties(){
        InputStream aStream = Algorithm.class.getClassLoader().getResourceAsStream("algorithms.properties");
        try{
            properties.load(aStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AlgorithmData getData() {
        return data;
    }

    public MoveUpdater changePrecinct() {
        State state = data.getWorkingState();
        Map<Integer,District> districts= state.getDefaultDistrict();
        List<MoveUpdate> updates = new ArrayList<>();
        District targetDistrict = districts.get(data.getTargetDistrict());
        Precinct selectedPrecinct = state.getAllPrecincts().get(data.getSelectedPrecinct());
        Map<String, Precinct> allPrecincts = targetDistrict.getAllDPrecincts();
        double oldSchwartzbergScore = districts.get(targetDistrict).calculateSchwartzberg(targetDistrict.getArea(allPrecincts), targetDistrict.getPerimeter(allPrecincts));
        double oldPolsbyPopperScore =districts.get(targetDistrict).calculatePolsbyPopper(targetDistrict.getArea(allPrecincts), targetDistrict.getPerimeter(allPrecincts));
        float oldPopulationScore = districts.get(targetDistrict).calculatePopEqualScore(state.calculateIdealPop());

        Move move = new Move(selectedPrecinct,selectedPrecinct.getParentDistrictID(), targetDistrict.getDistrictID());
        state.executeRgMove(move);
        double newSchwartzbergScore = districts.get(targetDistrict).calculateSchwartzberg(targetDistrict.getArea(allPrecincts), targetDistrict.getPerimeter(allPrecincts));
        double newPolsbyPopperScore =districts.get(targetDistrict).calculatePolsbyPopper(targetDistrict.getArea(allPrecincts), targetDistrict.getPerimeter(allPrecincts));
        float newPopulationScore = districts.get(targetDistrict).calculatePopEqualScore(state.calculateIdealPop());
        //Need to somehow pass the scores back to front end
        updates.add(new MoveUpdate(move.getSrcDistrictID(),move.getDstDistrictID(),move.getPrecinct().getGeoID10()));
        MoveUpdater updater = new MoveUpdater();
        updater.setUpdates(updates);
        return updater;
    }
}
