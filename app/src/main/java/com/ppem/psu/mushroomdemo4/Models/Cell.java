package com.ppem.psu.mushroomdemo4.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitchell on 7/13/2017.
 */

public class Cell {
    private long cellId;
    private String cellBed;
    private int cellColumn;
    private int cellRow;
    private List<Count> countListInCell = new ArrayList<>();

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

    public List<Count> getCountListInCell() {
        return countListInCell;
    }

    public void setCountListInCell(List<Count> countListInCell) {
        this.countListInCell = countListInCell;
    }

    public void addCountListInCell(Count count){
        this.countListInCell.add(count);
    }

}
