package com.ppem.psu.mushroomdemo4.Models;

import java.util.Date;

/**
 * Created by Mitchell on 6/19/2017.
 */

public class Count {
    private long countId;
    private String countName;
    private int countNumber;
    private boolean inChart;
    private Date countDate;

    public Count(){
        this.countId = getCountId();
        this.countName = getCountName();
        this.countNumber = getCountNumber();
        this.inChart = isInChart();
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

    public int getCountNumber() {
        return countNumber;
    }

    public void setCountNumber(int countNumber) {
        this.countNumber = countNumber;
    }

    public Date getCountDate() {
        return countDate;
    }

    public void setCountDate(Date countDate) {
        this.countDate = countDate;
    }

    public boolean isInChart() {
        return inChart;
    }

    public void setInChart(boolean inChart) {
        this.inChart = inChart;
    }


    @Override
    public String toString(){
        return countName;
    }


}
