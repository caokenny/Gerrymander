package io.redistrict.Algorithm;

import io.redistrict.AppData.Score;
import io.redistrict.Election.VoteData;
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


    public Score getStateObjectiveFunction(State state, AlgorithmType type){

        double compactnessWeight = weights.getCompactness();
        double popWeight = weights.getPopulationEquality();
        double partisanWeight = weights.getPartisanFairness();
        double effWeight =weights.getEfficencyGap();

        Map<Integer,District> districtMap;
        List<Double> popscoreList = new ArrayList();
        List<Double> compactnessScoreList = new ArrayList<>();
        double normalizedCompactnessScore = .45;

        if(type==AlgorithmType.RG){districtMap = state.getRgdistricts();}
        else{districtMap=state.getDefaultDistrict();}
//        System.out.println("IDEAL POP OF STATE IS: "+ state.calculateIdealPop(type));
        for(District d : districtMap.values()){
            double compactnessScore;
            float ideaPop = state.calculateIdealPop(type);
            double popScore;

            //calculating compactness score
            double area = d.getArea(d.getAllDPrecincts());
            double perimeter = d.getPerimeter(d.getAllDPrecincts());
            compactnessScore = d.calculatePolsbyPopper(area, perimeter)/normalizedCompactnessScore;


            // calculating popScore
            if(type== AlgorithmType.SA){
                popScore = d.calculatePopEqualScore(ideaPop);
            }
            else{
                popScore = d.calcuateRgPopScore(ideaPop);
            }
//            System.out.println("district: "+ d.getDistrictId()+" has population of: "+ d.getPopulation() +" with pop score: "+popScore);
//            System.out.println("district: "+ d.getDistrictId()+" compactnessScore: "+ compactnessScore);
            popscoreList.add(popScore);
            compactnessScoreList.add(compactnessScore);
        }
        double popScoreAvg = getAverageScore(popscoreList);
        double compactnessScoreAvg = getAverageScore(compactnessScoreList);
        double efficencyScore= state.calculateEfficiencyGap(districtMap);
        double partisanScore = state.calculatePartisanBias(districtMap);

        Score score = new Score();
        score.setCompactnessScore(compactnessScoreAvg);
        score.setEfficencyGapScore(efficencyScore);
        score.setPopScore(popScoreAvg);
        score.setPartisanScore(partisanScore);

//        System.out.println("___________________________________");
//        System.out.println("District vote data");
//        System.out.println("Total percentage of dem votes:" + (double)state.getTotalDemVotes()/state.getTotalVotes());
        int demSeats=0;
        for(District district : districtMap.values())
        {
            VoteData data = district.getVoteResult();
//            System.out.println("District: "+district.getDistrictId() + " rep votes: "+data.getRepVotes() + " dem votes: "+ data.getDemVotes());
            if(data.getDemVotes() > data.getRepVotes()){
             demSeats++;
            }
        }
//        System.out.println("Total dem seats won: " +demSeats);
//        System.out.println("Total rep sears won:" + (districtMap.size()-demSeats));
//        System.out.println("_____________________________________");
//        System.out.println("state level score, popScoreAvg: "+ popScoreAvg+ " compactnessScoreAvg: "+ compactnessScoreAvg+
//                " efficencyScore: "+ efficencyScore + " partisanScore: "+partisanScore);
        double weightTotal = partisanWeight+compactnessWeight+effWeight+popWeight;
        double objValue = (compactnessWeight*compactnessScoreAvg)+(popScoreAvg*popWeight)+ (partisanScore* partisanWeight) + (efficencyScore*effWeight);
        double normalizedObjScore = objValue/weightTotal; // normalized score
        score.setStateObjScore(normalizedObjScore);
        return score;
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
