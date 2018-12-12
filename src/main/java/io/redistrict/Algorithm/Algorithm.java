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
        Map<Integer,District> rgDistricts= new LinkedHashMap<>(state.getRgdistricts());
        List<MoveUpdate> updates = new ArrayList<>();
        int iterationsDone = 0;

        while (!state.getUnassignedPrecinctIds().isEmpty() && iterationsDone <10){
            // IF ONLY 1 DISTRICT THEN WE ASSIGN ALL TO IT
            if(rgDistricts.size() == 1){
                District district = rgDistricts.values().iterator().next();
                System.out.println("LAST MOVE, rgdistrictId: "+district.getDistrictId() + " num of precinct to be assigned on last move: "+ state.getUnassignedPrecinctIds().size());
                MoveUpdater updater = assignAll(district,state);
                return updater;
            }
            //ELSE
            District rgDistrict = getLowestPopDistrict(rgDistricts);
            rgDistrict.updateBorderPrecinctsForRg(state.getUnassignedPrecinctIds());
            if(rgDistrict.getBorderRgPrecincts().size() == 0/*rgDistrict.getNumOfUnassignedNeighbors() == 0*/) {
                rgDistricts.remove(rgDistrict.getDistrictId());
                continue;
            }
            Precinct rgPrecinct = selectRgAdditionPrecinct(rgDistrict,state);

            Move move = new Move(rgPrecinct,-1,rgDistrict.getDistrictId());
            state.executeRgMove(move);

            state.removeFromUnassignedIds(rgPrecinct.getGeoID10());
            //  rgDistrict.updateBorderPrecinctsForRg(state.getUnassignedPrecinctIds());

            updates.add(new MoveUpdate(move.getSrcDistrictID(),move.getDstDistrictID(),move.getPrecinct().getGeoID10()));

            System.out.println("district: "+rgDistrict.getDistrictId() +" num of border precincts: " + rgDistrict.getBorderRgPrecincts().size());
            System.out.println("num of unassigned precincts left: "+ state.getUnassignedPrecinctIds().size());
            System.out.println("num of rgDistrictsLeft: "+rgDistricts.size());
            System.out.println("______________________________________________________");
            iterationsDone++;
        }

        MoveUpdater updater = new MoveUpdater();
        updater.setUpdates(updates);
        return updater;
    }



    public State startRg(Set<Precinct> seeds, String stateName){
        State state = makeRgState(seeds,stateName);
        Map<Integer,District> possibleDistricts = new LinkedHashMap<>(state.getRgdistricts());
        while (!state.getUnassignedPrecinctIds().isEmpty()) {
            District rgDistrict = getLowestPopDistrict(possibleDistricts);
            Precinct rgPrecinct = selectRgAdditionPrecinct(rgDistrict, state);
            state.removeFromUnassignedIds(rgPrecinct.getGeoID10());
            Move move = new Move(rgPrecinct,-1,rgDistrict.getDistrictId());
            state.executeRgMove(move);
            if(rgDistrict.getNumOfUnassignedNeighbors()== 0){
                possibleDistricts.remove(rgDistrict.getDistrictId());
            }
        }
        return state;
    }

    private State makeRgState(Set<Precinct> seeds , String stateName){
        State state = AppData.getState(stateName.toUpperCase());
        Map<Integer,District> seedDistricts = makeSeedDistricts(seeds);
        Set<String> allPrecinctIds = state.getAllPrecincts().keySet();
        Set<String> initUnassignedPrecincts = getInitUnassignedPrecinctIds(seeds,allPrecinctIds);
        state.setRgdistricts(seedDistricts);
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
        List<Precinct> borderPrecincts = district.getBorderRgPrecincts(state.getUnassignedPrecinctIds());
        Set<Precinct> unassignedNeighbors = NeighborFinder.findUnassignedNeighbors
                (state.getAllPrecincts(),state.getUnassignedPrecinctIds(),borderPrecincts);
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
    public void setData(AlgorithmData data) {
        this.data = data;
    }
    private MoveUpdater assignAll(District loneDistrict , State state){
        List<MoveUpdate> updates = new ArrayList<>();
        for(String precinctId : state.getUnassignedPrecinctIds()){
            updates.add(new MoveUpdate(-1,loneDistrict.getDistrictId(),precinctId));
        }
        state.assignAllUnassignedPrecincts(loneDistrict.getDistrictId());
        MoveUpdater updater = new MoveUpdater();
        updater.setUpdates(updates);
        return  updater;
    }
    public Stack<Move> run10SA(){
        int badMoves = 0;
        int count = 0;
        State s = data.getWorkingState();
        int max_bad_move = Integer.parseInt(properties.getProperty("max_bad_moves"));
        double accecptanceConstant = Double.parseDouble(properties.getProperty("acceptance_constant"));
        double constantMultiplier = Double.parseDouble(properties.getProperty("constant_multiplier"));

        while(badMoves < max_bad_move && count < 10){
            District d = s.getLowestPopScoreDistrict();
            int distOldPop = d.getPopulation();
            double oldScore = s.getDistrictScore(d);
            Move move = d.moveLargestBorderPrec();
            Precinct modifiedPrecinct = move.getPrecinct();
            modifiedPrecinct.setParentDistrictID(move.getDstDistrictID());
            District srcDistrict = s.getDefaultDistrict().get(move.getSrcDistrictID());
            District dstDistrict =s.getDefaultDistrict().get(move.getDstDistrictID());
            srcDistrict.removePrecinct(modifiedPrecinct);
            dstDistrict.addPrecinct(modifiedPrecinct,AlgorithmType.SA);
            int distNewPop = d.getPopulation();
//            double newScore = s.getDistrictScore(d);
            s.updatePopulationEqualityMeasure(move, data.getType());
            double newScore = s.getDistrictScore(d);
            if(newScore > oldScore){
                s.addToMoveStack(move);
            }
            else{
                boolean acceptBadMove = s.acceptBadMove(oldScore, newScore, accecptanceConstant);
                if(acceptBadMove){
                    s.addToMoveStack(move);
                }
                else {
                    s.undoLastMove(move);
                    badMoves++; // THIS MIGHT NEED TO BE SWAPPED TO SOMEWHERE ELSE(UNDER S.UNDOLASTMove())
                    accecptanceConstant *= constantMultiplier;
                }
            }
            count++;
        }
        return s.getMoves();
    }
}
