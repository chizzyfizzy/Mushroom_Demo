package com.ppem.psu.mushroomdemo4;

import java.util.Date;

/**
 * Created by Mitchell on 6/19/2017.
 */

public class Count {
    private long countId;
    private String countName;
    private long countNumber;
    private long countDate;

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

    @Override
    public String toString(){
        return countName;
    }


}
