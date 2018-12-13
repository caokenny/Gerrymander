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
import sun.dc.pr.PRError;

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
        int maxIteration = (state.getAllPrecincts().size()/100);
        ObjectiveFunctionCalculator calculator = new ObjectiveFunctionCalculator();
        calculator.setWeights(data.getWeights());

        while (!state.getUnassignedPrecinctIds().isEmpty() && iterationsDone <10){
            // IF ONLY 1 DISTRICT THEN WE ASSIGN ALL TO IT
            if(rgDistricts.size() == 1){
                District district = rgDistricts.values().iterator().next();
                System.out.println("LAST MOVE, rgdistrictId: "+district.getDistrictId() + " num of precinct to be assigned on last move: "+ state.getUnassignedPrecinctIds().size());
                MoveUpdater updater = assignAll(district,state);
                System.out.println("state current score score is: "+ calculator.getStateObjectiveFunction(state,AlgorithmType.RG));
                return updater;
            }
            //ELSE
            District rgDistrict = getLowestPopDistrict(rgDistricts);

            if(rgDistrict.getBorderRgPrecincts().size() == 0) {
                rgDistricts.remove(rgDistrict.getDistrictId());
                continue;
            }
            Precinct rgPrecinct = selectRgAdditionPrecinct(rgDistrict,state);
            //List<Precinct> rgPrecinct = selectRgAdditionPrecincts(rgDistrict,state);

            if(rgPrecinct == null){
                rgDistrict.updateBorderPrecinctsForRg(state.getUnassignedPrecinctIds()); // only update borderprecincts when old neighbors are gone
                iterationsDone++;
                continue;
            }

//            List<MoveUpdate> newUpdates=executeListOfMoves(rgPrecinct,state,rgDistrict);
//            updates.addAll(newUpdates);
//            iterationsDone+= newUpdates.size();

            Move move = new Move(rgPrecinct,-1,rgDistrict.getDistrictId());
            state.executeRgMove(move);

            state.removeFromUnassignedIds(rgPrecinct.getGeoID10());

            updates.add(new MoveUpdate(move.getSrcDistrictID(),move.getDstDistrictID(),move.getPrecinct().getGeoID10()));

//            System.out.println("district: "+rgDistrict.getDistrictId() +" num of border precincts: " + rgDistrict.getBorderRgPrecincts().size());
//            System.out.println("num of unassigned precincts left: "+ state.getUnassignedPrecinctIds().size());
//            System.out.println("num of rgDistrictsLeft: "+rgDistricts.size());
//            System.out.println("______________________________________________________");
            System.out.println("num of unassigned left: "+state.getUnassignedPrecinctIds().size());
            iterationsDone++;
        }
        if(state.getUnassignedPrecinctIds().isEmpty())
        {
            System.out.println("state current score score is: "+ calculator.getStateObjectiveFunction(state,AlgorithmType.RG));
        }
        MoveUpdater updater = new MoveUpdater();
        updater.setUpdates(updates);
        return updater;
    }


    public List<MoveUpdate> executeListOfMoves(List<Precinct> additionPrecincts, State state,District rgdistrict){
        List<MoveUpdate> moveUpdates = new ArrayList<>();
        for(Precinct precinct : additionPrecincts){
            Move move = new Move(precinct,-1,rgdistrict.getDistrictId());
            state.executeRgMove(move);
            state.removeFromUnassignedIds(precinct.getGeoID10());
            moveUpdates.add(new MoveUpdate(move.getSrcDistrictID(),move.getDstDistrictID(),move.getPrecinct().getGeoID10()));
        }
        return  moveUpdates;
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

        boolean improved = false;
        int maxNumOfPossible =(int)(unassignedNeighbors.size()*.5);
        int tried = 0;

        Precinct resultCanidate = PrecinctSelector.selectRandomPrecinct(unassignedNeighbors);
        double bestScore = getTempDistrictScore(state,district, resultCanidate);

        while(tried<=maxNumOfPossible){
            Precinct potentialPrecinct =  PrecinctSelector.selectRandomPrecinct(unassignedNeighbors);
            double newScore = getTempDistrictScore(state,district,potentialPrecinct);
            if(newScore >bestScore){ // GET BEST PRECINCT THAT IMPROVES SCORE
                bestScore= newScore;
                resultCanidate= potentialPrecinct;
            }
            tried++;
        }
        return resultCanidate;
    }

    private double getCurrentDistrictScore(State state, District district){
        double popScoreWeight = data.getWeights().getPopulationEquality();
        double popScore = district.calcuateRgPopScore(state.calculateIdealPop(AlgorithmType.RG));
        return popScore*popScoreWeight;
    }

    private double getTempDistrictScore(State state,District district , Precinct precinct){
        double popScoreWeight = data.getWeights().getPopulationEquality();
        district.addPrecinct(precinct,AlgorithmType.RG);
        double popScore = district.calcuateRgPopScore(state.calculateIdealPop(AlgorithmType.RG));
        district.removePrecinct(precinct,AlgorithmType.RG);
        return popScore*popScoreWeight;
    }


    private List<Precinct> selectRgAdditionPrecincts(District district,State state){
        List<Precinct> resultList = new ArrayList<>();
        List<Precinct> borderPrecincts = district.getBorderRgPrecincts();//state.getUnassignedPrecinctIds());
        List<Precinct> unassignedNeighbors = new ArrayList<>(NeighborFinder.findUnassignedNeighbors
                (state.getAllPrecincts(),state.getUnassignedPrecinctIds(),borderPrecincts));
        if(unassignedNeighbors.size()== 0){return null;}

        if(unassignedNeighbors.size()==1)
            return unassignedNeighbors;

        if(unassignedNeighbors.size() <=10){
            Collections.shuffle(unassignedNeighbors);
            resultList.add(unassignedNeighbors.get(0));
            return resultList;
        }
        else {
            ObjectiveFunctionCalculator objectiveFunctionCalculator = new ObjectiveFunctionCalculator();
            objectiveFunctionCalculator.setWeights(data.getWeights());
            double currentObjValue = objectiveFunctionCalculator.getStateObjectiveFunction(state, AlgorithmType.RG);
            List<Precinct> randomNeighbors = getRandomNeighbors(unassignedNeighbors,7);
            double newScore = objectiveFunctionCalculator.getTempPrecinctsAdditionScore(state,district,randomNeighbors,AlgorithmType.RG);
            if(newScore>currentObjValue){
                return randomNeighbors;
            }
        }
        return getRandomNeighbors(unassignedNeighbors,3);
    }


//    private Precinct selectRgAdditionPrecinct(District district, State state) {
//        List<Precinct> borderPrecincts = district.getBorderRgPrecincts();
//        Set<Precinct> unassignedNeighbors = NeighborFinder.findUnassignedNeighbors
//                (state.getAllPrecincts(),state.getUnassignedPrecinctIds(),borderPrecincts);
//        if(unassignedNeighbors.size()== 0){return null;}
//        ObjectiveFunctionCalculator objectiveFunctionCalculator = new ObjectiveFunctionCalculator();
//        objectiveFunctionCalculator.setWeights(data.getWeights());
//        double currentObjValue = objectiveFunctionCalculator.getDistrictObjectiveFunction(state, district, AlgorithmType.RG);
//
//        if(unassignedNeighbors.size()==1)
//            return unassignedNeighbors.iterator().next(); // only 1
//
//        boolean improved = false;
//        int maxNumOfPossible =(int)(unassignedNeighbors.size()*.1);
//        int possibleTried = 0;
//        while (!improved) {
//            Precinct possibleAddition = PrecinctSelector.selectRandomPrecinct(unassignedNeighbors);
//            double newObjValue = objectiveFunctionCalculator.getTempPrecinctAdditionScore(state, district,possibleAddition,AlgorithmType.RG);
//            if(newObjValue>=currentObjValue){
//                return possibleAddition;
//            }
//            possibleTried++;
//            if(possibleTried == maxNumOfPossible)
//                improved = true;
//        }
//
//        return PrecinctSelector.selectRandomPrecinct(unassignedNeighbors);
//    }
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
            srcDistrict.removePrecinct(modifiedPrecinct,AlgorithmType.SA);
            dstDistrict.addPrecinct(modifiedPrecinct,AlgorithmType.SA);
            int distNewPop = d.getPopulation();
            double newScore = s.getDistrictScore(d);
            if(distOldPop > distNewPop){
                s.addToMoveStack(move);
            }
            else{
                badMoves++; // THIS MIGHT NEED TO BE SWAPPED TO SOMEWHERE ELSE(UNDER S.UNDOLASTMove())
                boolean acceptBadMove = s.acceptBadMove(oldScore, newScore, accecptanceConstant);
                if(acceptBadMove){
                    s.addToMoveStack(move);
                }
                else
                    s.undoLastMove(move);
                accecptanceConstant *= constantMultiplier;
            }
            count++;
        }
        return s.getMoves();
    }
    private List<Precinct> getRandomNeighbors (List<Precinct> unassignedNeighbors , int num){

        if(num > unassignedNeighbors.size())
            throw new IndexOutOfBoundsException("num size greater than neighbor size");

        Collections.shuffle(unassignedNeighbors);
        List<Precinct> randomPrecincts = new ArrayList<>();
        for (int x = 0 ; x<num; x++){
            randomPrecincts.add(unassignedNeighbors.get(x));
        }
        return randomPrecincts;
    }
}
