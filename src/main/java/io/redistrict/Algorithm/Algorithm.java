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
    private int badMoves = 0;
    double accecptanceConstant = Double.parseDouble(properties.getProperty("acceptance_constant"));

    public MoveUpdater do10RgIteration(){
        State state = data.getWorkingState();
        Map<Integer,District> rgDistricts= new LinkedHashMap<>(state.getRgdistricts());
        List<MoveUpdate> updates = new ArrayList<>();
        int iterationsDone = 0;
        int maxIteration = (state.getAllPrecincts().size()/100);
        ObjectiveFunctionCalculator calculator = new ObjectiveFunctionCalculator();
        calculator.setWeights(data.getWeights());

        while (!state.getUnassignedPrecinctIds().isEmpty() && iterationsDone <100){
            // IF ONLY 1 DISTRICT THEN WE ASSIGN ALL TO IT
            if(rgDistricts.size() == 1){
                District district = rgDistricts.values().iterator().next();
                System.out.println("LAST MOVE, rgdistrictId: "+district.getDistrictId() + " num of precinct to be assigned on last move: "+ state.getUnassignedPrecinctIds().size());
                MoveUpdater updater = assignAll(district,state);
                return updater;
            }
            //ELSE
            District rgDistrict = getLowestPopDistrict(rgDistricts);

            if(rgDistrict.getBorderRgPrecincts().size() == 0) {
                rgDistricts.remove(rgDistrict.getDistrictId());
                continue;
            }
            Precinct rgPrecinct = selectRgAdditionPrecinct(rgDistrict,state);

            if(rgPrecinct == null){
                rgDistrict.updateBorderPrecinctsForRg(state.getUnassignedPrecinctIds()); // only update borderprecincts when old neighbors are gone
                iterationsDone++;
                continue;
            }

            Move move = new Move(rgPrecinct,-1,rgDistrict.getDistrictId());
            state.executeRgMove(move);

            state.removeFromUnassignedIds(rgPrecinct.getGeoID10());

            MoveUpdate moveUpdate = new MoveUpdate(move.getSrcDistrictID(),move.getDstDistrictID(),move.getPrecinct().getGeoID10());
            updates.add(moveUpdate);

            System.out.println("num of unassigned left: "+state.getUnassignedPrecinctIds().size());
            iterationsDone++;
        }
        MoveUpdater updater = new MoveUpdater();
        if(state.getUnassignedPrecinctIds().isEmpty())
        {
            System.out.println("state current score score is: "+ calculator.getStateObjectiveFunction(state,AlgorithmType.RG));
            updater.setCurrentScore(calculator.getStateObjectiveFunction(state,AlgorithmType.RG));
        }
        updater.setUpdates(updates);
        //updater.setCurrentScore(calculator.getStateObjectiveFunction(state,AlgorithmType.RG));
        return updater;
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

        double compactnessWeight = data.getWeights().getCompactness();
        List<Precinct> borderPrecincts ;

        // SHOW UPDATED BORDERS OR NOT BASED ON COMPACTNESS WEIGHT;
        if(Math.random()>compactnessWeight){
            borderPrecincts = district.getBorderRgPrecincts(state.getUnassignedPrecinctIds());
        }
        else{
            borderPrecincts = district.getBorderRgPrecincts();
        }

        Set<Precinct> unassignedNeighbors = NeighborFinder.findUnassignedNeighbors
                (state.getAllPrecincts(),state.getUnassignedPrecinctIds(),borderPrecincts);

        if(unassignedNeighbors.size()==0){return null;}
        if(unassignedNeighbors.size()==1) {return unassignedNeighbors.iterator().next();}

        int maxNumOfPossible =(int)(unassignedNeighbors.size()*.9);
        int tried = 0;

        Precinct resultCanidate = PrecinctSelector.selectRandomPrecinct(unassignedNeighbors);
        double bestScore = getTempObjectiveScore(state,district, resultCanidate,AlgorithmType.RG);

        while(tried<=maxNumOfPossible){
            Precinct potentialPrecinct =  PrecinctSelector.selectRandomPrecinct(unassignedNeighbors);
            double newScore = getTempObjectiveScore(state,district,potentialPrecinct,AlgorithmType.RG);
            if(newScore >bestScore){ // GET BEST PRECINCT THAT IMPROVES SCORE
                bestScore= newScore;
                resultCanidate= potentialPrecinct;
            }
            tried++;
        }
        return resultCanidate;
    }

    public double getCurrentStateScore(State state,AlgorithmType type){
        double popScoreWeight = data.getWeights().getPopulationEquality();
        double partianFairnessWeight = data.getWeights().getPartisanFairness();
        double efficiencyGapWeight = data.getWeights().getEfficencyGap();
        double popScore = state.getAverageStatePopScore(type);
        double efficiencyGapScore;
        double partisanFairness;
        Map<Integer, District> districtMap;

        if(type ==AlgorithmType.SA){ districtMap = state.getDefaultDistrict();}
        else{ districtMap = state.getRgdistricts();}

        partisanFairness = state.calculatePartisanBias(districtMap);
        efficiencyGapScore = state.calculateEfficiencyGap(districtMap);

        return (popScore*popScoreWeight) + (efficiencyGapScore*efficiencyGapWeight) + (partianFairnessWeight*partisanFairness);
    }

    private double getTempObjectiveScore(State state, District district , Precinct precinct,AlgorithmType type){
        double popScoreWeight = data.getWeights().getPopulationEquality();
        double partianFairnessWeight = data.getWeights().getPartisanFairness();
        double efficiencyGapWeight = data.getWeights().getEfficencyGap();
        double idealPop = state.calculateIdealPop(type);
        double popScore;
        double efficiencyGapScore;
        double partisanFairness;
        Map<Integer, District> districtMap;

        district.addPrecinct(precinct,type); // add the precinct to district to test new score

        if(type == AlgorithmType.SA){
            districtMap = state.getDefaultDistrict();
            popScore= state.getAverageStatePopScore(AlgorithmType.SA);
        }
        else{
            districtMap = state.getRgdistricts();
            popScore = district.calcuateRgPopScore(idealPop);
        }

        partisanFairness = state.calculatePartisanBias(districtMap);
        efficiencyGapScore = state.calculateEfficiencyGap(districtMap);
        district.removePrecinct(precinct,type);

        return (popScore*popScoreWeight) + (efficiencyGapScore*efficiencyGapWeight) + (partianFairnessWeight*partisanFairness);
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
        int count = 0;
        State s = data.getWorkingState();
        int max_bad_move = Integer.parseInt(properties.getProperty("max_bad_moves"));
        double constantMultiplier = Double.parseDouble(properties.getProperty("constant_multiplier"));

        while(badMoves < max_bad_move && count < 10){
//            District d = s.getLowestPopScoreDistrict();
            District d = s.getRandomDistrictSA();
            int distOldPop = d.getPopulation();
            double oldScore = s.getDistrictScore(d);
            double oldtotalPopScore = s.getTotalPopScore();
            double oldObjFuncVal = getCurrentStateScore(s, AlgorithmType.SA);
//            Move move = d.moveLargestBorderPrec();
            Move move = d.moveRandomBorderPrec();
            Precinct modifiedPrecinct = move.getPrecinct();
            modifiedPrecinct.setParentDistrictID(move.getDstDistrictID());
            District srcDistrict = s.getDefaultDistrict().get(move.getSrcDistrictID());
            District dstDistrict =s.getDefaultDistrict().get(move.getDstDistrictID());
            srcDistrict.removePrecinct(modifiedPrecinct,AlgorithmType.SA);
            dstDistrict.addPrecinct(modifiedPrecinct,AlgorithmType.SA);
            int distNewPop = d.getPopulation();
//            double newScore = s.getDistrictScore(d);
            s.updatePopulationEqualityMeasure(move, data.getType());
            double newTotalPopScore = s.getTotalPopScore();
            double newObjFuncVal = getCurrentStateScore(s, AlgorithmType.SA);
            double newScore = s.getDistrictScore(d);
//            if(newScore > oldScore){
//                s.addToMoveStack(move);
//            }
//            with the above if statement a precinct can be traded back and forth between two districts and
//            the newScore > oldScore always
            if(newObjFuncVal > oldObjFuncVal) {
                s.addToMoveStack(move);
                System.out.println("New obj function score is " + newObjFuncVal);
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
