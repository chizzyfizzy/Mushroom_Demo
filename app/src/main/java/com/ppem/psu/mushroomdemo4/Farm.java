package com.ppem.psu.mushroomdemo4;

/**
 * Created by Mitchell on 7/18/2017.
 */

public class Farm {
    private long farmId;
    private String farmName;
    private String farmDescription;

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
