package com.ppem.psu.mushroomdemo4;

import java.util.Date;

/**
 * Created by Mitchell on 6/19/2017.
 */

public class Count {
    private long countId;
    private String countName;
    private long countNumber;
    private boolean inChart;
    private long countDate;

    public Count(){

    }

    public Count(String countName){
        this.countName = countName;
    }

    public Count(String countName, boolean chartBool){
        this.countName = countName;
        this.inChart = chartBool;
    }

    public long getCountId() {
        return countId;
    }

    public void setCountId(long countId) {
        this.countId = countId;
    }

    public String getCountName() {
        return countName;
    }

    public void setCountName(String countName) {
        this.countName = countName;
    }

    public long getCountNumber() {
        return countNumber;
    }

    public void setCountNumber(long countNumber) {
        this.countNumber = countNumber;
    }

    public long getCountDate() {
        return countDate;
    }

    public void setCountDate(long countDate) {
        this.countDate = countDate;
    }

    public boolean isInChart() {
        return inChart;
    }

    public void setInChart(int inChart) {
        if(inChart == 0) {
            this.inChart = false;
        }
        if(inChart == 1){
            this.inChart = true;
        }
    }

    @Override
    public String toString(){
        return countName;
    }


}
