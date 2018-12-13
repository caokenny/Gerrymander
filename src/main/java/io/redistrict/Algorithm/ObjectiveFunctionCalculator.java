package io.redistrict.Algorithm;

import io.redistrict.Territory.District;
import io.redistrict.Territory.Precinct;
import io.redistrict.Territory.State;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ObjectiveFunctionCalculator {
    // EVERYTHING SHOULD WORK FOR SA AND RG****************
    private AlgorithmWeights weights;

    public AlgorithmWeights getWeights() {
        return weights;
    }

    public void setWeights(AlgorithmWeights weights) {
        this.weights = weights;
    }

    public double getTempPrecinctAdditionScore(State state, District district, Precinct additionPrecinct,AlgorithmType type){
        district.addPrecinct(additionPrecinct,type); // add new precinct
        double newObjFunctionScore = getDistrictObjectiveFunction(state,district,type);
        district.removePrecinct(additionPrecinct,type); // remove that new precinct
        return newObjFunctionScore;
    }

    public double getTempPrecinctsAdditionScore(State state, District district, List<Precinct> additionPrecincts, AlgorithmType type){
        district.addPreinctList(additionPrecincts,type);
        double newObjFunctionScore = getStateObjectiveFunction(state,type);
        district.removePrecinctList(additionPrecincts,type);
        return newObjFunctionScore;
    }

    public double getDistrictObjectiveFunction(State state, District distict , AlgorithmType type){
        double perimeter = distict.getPerimeter(distict.getAllDPrecincts());
        double area = distict.getArea(distict.getAllDPrecincts());
        float ideaPop = state.calculateIdealPop(type);
        double popScore;
        double normalizedCompactnessScore = .45;

        double compactness = distict.calculatePolsbyPopper(area, perimeter)/normalizedCompactnessScore;;
        if(type==AlgorithmType.SA) {
            popScore = distict.calculatePopEqualScore(ideaPop);
        }
        else{
            popScore =distict.calcuateRgPopScore(ideaPop);
        }

        double weightedPopScore = weights.getPopulationEquality()*popScore;
        double weightedCompactness = weights.getCompactness()*compactness;

        return weightedCompactness+weightedPopScore;
    }

    public double getStateObjectiveFunction(State state,AlgorithmType type){

        double compactnessWeight = weights.getCompactness();
        double popWeight = weights.getPopulationEquality();
        Map<Integer,District> districtMap;
        List<Double> popscoreList = new ArrayList();
        List<Double> compactnessScoreList = new ArrayList<>();
        double normalizedCompactnessScore = .45;

        if(type==AlgorithmType.RG){districtMap = state.getRgdistricts();}
        else{districtMap=state.getDefaultDistrict();}

        for(District d : districtMap.values()){
            double compactnessScore;
            float ideaPop = state.calculateIdealPop(type);
            double popScore;

            //calculating compactness score
            if(d.getAllDPrecincts().size() <= state.getAllPrecincts().size()*.05) // if there is not enough precincts the compactscore will be 1;
                compactnessScore =1;
            else {
                double area = d.getArea(d.getAllDPrecincts());
                double perimeter = d.getPerimeter(d.getAllDPrecincts());
                compactnessScore = d.calculatePolsbyPopper(area, perimeter)/normalizedCompactnessScore;
            }

            // calculating popScore
            if(type== AlgorithmType.SA){
                popScore = d.calculatePopEqualScore(ideaPop);
            }
            else{
                popScore = d.calcuateRgPopScore(ideaPop);
            }

            System.out.println("district: "+ d.getDistrictId()+" pop score: "+popScore);
            System.out.println("district: "+ d.getDistrictId()+" compactnessScore: "+ compactnessScore);
            popscoreList.add(popScore);
            compactnessScoreList.add(compactnessScore);
        }
        double popScoreAvg = getAverageScore(popscoreList);
        double compactnessScoreAvg = getAverageScore(compactnessScoreList);

        return (compactnessWeight*compactnessScoreAvg)+(popScoreAvg*popWeight);
    }
    private double getAverageScore(List<Double> scores)
    {
        double total =0;
        for(double score : scores){
            total+= score;
        }
        return total/((double)scores.size());
    }
}
