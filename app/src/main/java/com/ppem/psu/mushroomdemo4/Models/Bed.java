package com.ppem.psu.mushroomdemo4.Models;

/**
 * Created by Mitchell on 7/27/2017.
 */

public class Bed {
    private long id;
    private String bedName;
    private int bedLevels;
    private int bedSquares;


    public long getBedId() {
        return id;
    }

    public void setBedId(long id) {
        this.id = id;
    }

    public String getBedName() {
        return bedName;
    }

    public void setBedName(String bedName) {
        this.bedName = bedName;
    }

    public int getBedLevels() {
        return bedLevels;
    }

    public void setBedLevels(int bedLevels) {
        this.bedLevels = bedLevels;
    }

    public int getBedSquares() {
        return bedSquares;
    }


    public void setBedSquares(int bedSquares) {
        this.bedSquares = bedSquares;
    }

}
