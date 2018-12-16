package io.redistrict.Algorithm;

import io.redistrict.AppData.AppData;
import io.redistrict.AppData.MoveUpdate;
import io.redistrict.AppData.MoveUpdater;
import io.redistrict.AppData.Score;
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
    private int movesDone= 0;
    private int maxMoves = 1000;
    private Score oldScore;
    private boolean isDone= false;

    public MoveUpdater do10RgIteration(){
        State state = data.getWorkingState();
        Map<Integer,District> rgDistricts= new LinkedHashMap<>(state.getRgdistricts());
        List<MoveUpdate> updates = new ArrayList<>();
        int iterationsDone = 0;
        int maxIteration = (state.getAllPrecincts().size()/100);
        MoveUpdater updater = new MoveUpdater();

        ObjectiveFunctionCalculator calculator = new ObjectiveFunctionCalculator();
        calculator.setWeights(data.getWeights());
        if(oldScore == null)
        {
            oldScore = calculator.getDefaultStateScoreRG(state);
            updater.setOldScore(oldScore);
            System.out.println("original score: "+ oldScore);
        }

        while (!state.getUnassignedPrecinctIds().isEmpty() && iterationsDone <maxIteration){
            // IF ONLY 1 DISTRICT THEN WE ASSIGN ALL TO IT
            District rgDistrict ;
            if(rgDistricts.size() == 1){
                rgDistrict = rgDistricts.values().iterator().next();
                rgDistrict.updateBorderPrecinctsForRg(state.getUnassignedPrecinctIds());
                System.out.println("LAST MOVE, rgdistrictId: "+rgDistrict.getDistrictId() + " num of precincts left: "+ state.getUnassignedPrecinctIds().size());
            }
            //ELSE
            else {
                if (data.getWeights().isVariance()) {
                    rgDistrict = getRandomDistrict(rgDistricts);
                } else {
                    rgDistrict = getLowestPopDistrict(rgDistricts);
                }
            }

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
        if(isDone==true || updates.isEmpty())
        {
            if(oldScore ==null){
                throw new NullPointerException("OLD SCORE IS NULL !");
            }

            Score newScore = calculator.getStateObjectiveFunction(state,AlgorithmType.RG);
            System.out.println("state current score score is: "+ newScore);
            updater.setNewScore(newScore);
            updater.setOldScore(oldScore);
        }
        if(state.getUnassignedPrecinctIds().isEmpty()){
            isDone=true;
        }
        updater.setUpdates(updates);
        System.out.println(updater);
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
        if(unassignedNeighbors.size()==1) {
            district.updateBorderPrecinctsForRg(state.getUnassignedPrecinctIds());
            return unassignedNeighbors.iterator().next();
        }

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

    private double getCurrentStateScore(State state,AlgorithmType type){
        double popScoreWeight = data.getWeights().getPopulationEquality();
        double partianFairnessWeight = data.getWeights().getPartisanFairness();
        double efficiencyGapWeight = data.getWeights().getEfficencyGap();
        double totalWeights = popScoreWeight+partianFairnessWeight+efficiencyGapWeight;
        if(totalWeights == 0){return 0;}
        double popScore = state.getAverageStatePopScore(AlgorithmType.SA);
        double efficiencyGapScore;
        double partisanFairness;
        Map<Integer, District> districtMap;

        if(type ==AlgorithmType.SA){ districtMap = state.getDefaultDistrict();}
        else{ districtMap = state.getRgdistricts();}

        partisanFairness = state.calculatePartisanBias(districtMap);
        efficiencyGapScore = state.calculateEfficiencyGap(districtMap);
        double weightScore = ((popScore*popScoreWeight) + (efficiencyGapScore*efficiencyGapWeight) + (partianFairnessWeight*partisanFairness))/totalWeights;
        weightScore = (Math.round(weightScore*1000))/1000.00;
        return weightScore;
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
        State s = data.getWorkingState();
        int count = 0;

        double compactnessWeight = data.getWeights().getCompactness();
        double efficencyWeight = data.getWeights().getEfficencyGap();
        double populationWeight = data.getWeights().getPopulationEquality();
        double partisanWeight = data.getWeights().getPartisanFairness();
        double weightTotal = compactnessWeight+efficencyWeight+populationWeight+partisanWeight;
        double compactnessAcceptanceRate = compactnessWeight/weightTotal;
        double originalScore = getCurrentStateScore(s,AlgorithmType.SA);

        while(movesDone < maxMoves && count < 50){

            District additionDistrict;// = s.getRandomDistrictSA();
            //The following below will be used when variance is set
            if(data.getWeights().isVariance()) {additionDistrict = s.getRandomDistrictSA();}
            else{additionDistrict = s.getLowestPopDistrictSA();}

            double oldStateScore = getCurrentStateScore(s, AlgorithmType.SA);
            double newScore;
            double compactnessRandomizer = Math.random();
            Precinct candidate;

            if(compactnessRandomizer <= compactnessAcceptanceRate)
            {
                    candidate = getRandomCompactedCanidate(additionDistrict);
            }
            else{
                    candidate = getRandomCanidate(additionDistrict);
            }
            if(candidate.getParentDistrictID() == additionDistrict.getDistrictId()){ throw new NullPointerException(" YOU SELECTED A PRECINCT IN ADDITION DISTRICT");}

            Move move = new Move(candidate,candidate.getParentDistrictID(),additionDistrict.getDistrictId());
            s.executeSaMove(move);
            newScore = getCurrentStateScore(s,AlgorithmType.SA);
            if (newScore >= oldStateScore)
            {
                s.addToMoveStack(move);
            }
            else {
                s.undoLastMove(move);
            }
            count++;
            movesDone++;
        }
        if(s.getMoves().empty() || movesDone >maxMoves){
//            ObjectiveFunctionCalculator calculator = new ObjectiveFunctionCalculator();
//            calculator.setWeights(data.getWeights());
//            System.out.println("state score = " + calculator.getStateObjectiveFunction(s,AlgorithmType.SA));
        }
        else {
            System.out.println("Original score:" + originalScore);
            System.out.println("new Score: " + getCurrentStateScore(s, AlgorithmType.SA));
        }
        return s.getMoves();
    }

    public District getRandomDistrict(Map<Integer,District> rgDistricts){
        List<District> districts = new ArrayList<>(rgDistricts.values());
        int random = (int)(Math.random()*rgDistricts.size());
        return districts.get(random);
    }


    public List<Precinct> getCompactedAdditions (District district, double idealCompactness){
        List<Precinct> borderPrecincts = district.getBorderSaPrecincts();
        List<Precinct> compactedAdditions = new ArrayList<>();

        for(Precinct precinct : borderPrecincts){
            List<Precinct> neighbors = precinct.getNeighbors();
            for (Precinct neighbor : neighbors){
                if(neighbor.getParentDistrictID() !=district.getDistrictId())
                {
                    if(isCompacted(precinct,district,idealCompactness)){compactedAdditions.add(neighbor);}
                }
            }
        }
        if (compactedAdditions.size() == 0){return null;}
        return compactedAdditions;
    }

    //precinct var is the precinct you going to add to this district var(true only if it is compact if you add the precinct to that neighbor)
    public boolean isCompacted(Precinct precinct, District district, double idealRatio){


        List<Precinct> neighbors = precinct.getNeighbors();
        int nonParentNeighbors = 0;
        int parentNeighbors = 0;
        int additionDistrictId = district.getDistrictId();

        for (Precinct neighbor : neighbors){
            if(neighbor.getParentDistrictID() == additionDistrictId){ parentNeighbors++;}
            else{nonParentNeighbors++;}
        }

        double total = nonParentNeighbors+parentNeighbors;
//        System.out.println("ratio: "+ ((double)(parentNeighbors))/total);
        if (((double)(parentNeighbors))/total >= idealRatio) { return  true;}
        else{return false;}
    }

    public Precinct getRandomCanidate(District district)
    {
        Precinct randomBorderPrecinct = PrecinctSelector.selectRandomPrecinct(district.getBorderSaPrecincts());
        List<Precinct> possibleCanidates = district.possiblePrecinctAdditionsSa(randomBorderPrecinct);
        return PrecinctSelector.selectRandomPrecinct(possibleCanidates);
    }

    public Precinct getRandomCompactedCanidate(District additionDistrict)
    {
        boolean compactnessMet= false;
        double idealCompactness = .9;
        List<Precinct> compactedCanidates;
        while (!compactnessMet){
            compactedCanidates = getCompactedAdditions(additionDistrict,idealCompactness);
            if(compactedCanidates !=null)
            {
                compactnessMet = true;
                return PrecinctSelector.selectRandomPrecinct(compactedCanidates);
            }
            else{idealCompactness -= .05;}
        }
        return  null;  // should never get here
    }

    public int getMovesDone() {
        return movesDone;
    }

    public void setMovesDone(int movesDone) {
        this.movesDone = movesDone;
    }

    public int getMaxMoves() {
        return maxMoves;
    }

    public void setMaxMoves(int maxMoves) {
        this.maxMoves = maxMoves;
    }

    public Score getOldScore() {
        return oldScore;
    }

    public void setOldScore(Score oldScore) {
        this.oldScore = oldScore;
    }
}
