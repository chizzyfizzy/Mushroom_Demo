package com.ppem.psu.mushroomdemo4;

/**
 * Created by Mitchell on 7/13/2017.
 */

public class Cell {
    private long cellId;
    private String cellBed;
    private int cellColumn;
    private int cellRow;

    public long getCellId() {
        return cellId;
    }

    public void setCellId(long cellId) {
        this.cellId = cellId;
    }

    public String getCellBed() {
        return cellBed;
    }

    public void setCellBed(String cellBed) {
        this.cellBed = cellBed;
    }


    public int getCellColumn() {
        return cellColumn;
    }

    public void setCellColumn(int cellColumn) {
        this.cellColumn = cellColumn;
    }

    public int getCellRow() {
        return cellRow;
    }

    public void setCellRow(int cellRow) {
        this.cellRow = cellRow;
    }
}
