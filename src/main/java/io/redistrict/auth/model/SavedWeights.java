package io.redistrict.auth.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "SavedWeights")
public class SavedWeights {

    private Integer id;
    private Integer compactness;
    private Integer population;
    private Integer partisanFairness;
    private Integer efficiencyGap;


    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCompactness() {
        return compactness;
    }

    public void setCompactness(Integer compactness) {
        this.compactness = compactness;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public Integer getPartisanFairness() {
        return partisanFairness;
    }

    public void setPartisanFairness(Integer partisanFairness) {
        this.partisanFairness = partisanFairness;
    }

    public Integer getEfficiencyGap() {
        return efficiencyGap;
    }

    public void setEfficiencyGap(Integer efficiencyGap) {
        this.efficiencyGap = efficiencyGap;
    }

}
