package com.ppem.psu.mushroomdemo4.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mitchell on 6/19/2017.
 */

//Model data
public class Plant  {
    private long plantId;
    private String farmName;
    private String plantName;
    private String plantLabel;


    public Plant(){
        this.plantId = getPlantId();
        this.plantName = getPlantName();
        this.plantLabel = getPlantLabel();
    }

    public Plant(Long id, String pName, String pLabel){
        plantId = id;
        plantName = pName;
        plantLabel = pLabel;
    }


    public long getPlantId() {
        return this.plantId;
    }

    public void setPlantId(long id) {
        this.plantId = id;
    }

    public String getFarmName() {
        return this.farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = farmName;
    }

    public String getPlantName() {
        System.out.println("The Plant Getters: " + this.plantLabel + " " + this.plantName);
        return this.plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }

    public String getPlantLabel() {
        return this.plantLabel;
    }

    public void setPlantLabel(String plantLabel) {
        this.plantLabel = plantLabel;
    }



    @Override
    public String toString() {
        return plantName;
    }

;
}
