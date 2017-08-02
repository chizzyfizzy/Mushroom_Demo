package com.ppem.psu.mushroomdemo4.Models;

/**
 * Created by Mitchell on 7/18/2017.
 */

public class Farm {
    private long farmId;
    private String farmName;
    private String farmDescription;

    public Farm(){
        this.farmId = getFarmId();
        this.farmName = getFarmName();
        this.farmDescription = getFarmDescription();
    }

    public Farm(String name, String description){
        this.farmName = name;
        this.farmDescription = description;
    }

    public long getFarmId() {
        return farmId;
    }

    public void setFarmId(long farmId) {
        this.farmId = farmId;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getFarmDescription() {
        return farmDescription;
    }

    public void setFarmDescription(String farmDescription) {
        this.farmDescription = farmDescription;
    }
}
