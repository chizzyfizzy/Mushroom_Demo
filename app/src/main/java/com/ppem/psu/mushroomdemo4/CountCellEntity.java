package com.ppem.psu.mushroomdemo4;

import java.util.ArrayList;

/**
 * Created by Mitchell on 7/21/2017.
 */

public class CountCellEntity {
    private long countCellId;
    private Count count;
    private Cell cell;
    private ArrayList<CountCellEntity> ccList;

    public CountCellEntity(){

    }

    public CountCellEntity(Cell cell, Count count){
        this.cell = cell;
        this.count = count;
    }

    public long getCountCellId() {
        return countCellId;
    }

    public void setCountCellId(long countCellId) {
        this.countCellId = countCellId;
    }

    public Count getCount() {
        return count;
    }

    public void setCount(Count count) {
        this.count = count;
    }

    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }

    public ArrayList<CountCellEntity> getCcList() {
        return ccList;
    }

    public void setCcList(ArrayList<CountCellEntity> ccList) {
        this.ccList = ccList;
    }

    public void addCountCellToList(CountCellEntity cc){
        ccList.add(cc);
    }
}
