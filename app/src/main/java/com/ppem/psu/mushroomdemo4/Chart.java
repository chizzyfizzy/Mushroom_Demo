package com.ppem.psu.mushroomdemo4;

/**
 * Created by Mitchell on 6/29/2017.
 */

public class Chart {
    private long id;
    private boolean bedPeak;
    private int colNum;
    private int rowNum;


    public long getChartId() {
        return id;
    }

    public void setChartId(long id) {
        this.id = id;
    }

    public boolean getBedPeak() {
        return bedPeak;
    }

    public void setBedPeak(boolean bedPeak) {
        this.bedPeak = bedPeak;
    }

    public int getColNum() {
        return colNum;
    }

    public void setColNum(int colNum) {
        this.colNum = colNum;
    }

    public int getRowNum() {
        return rowNum;
    }


    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }


}
