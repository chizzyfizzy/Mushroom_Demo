package com.ppem.psu.mushroomdemo4;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Mitchell on 6/19/2017.
 */

//Room Model data
public class Room {
    private long roomId;
    private String roomName;


    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }


    @Override
    public String toString(){
        return roomName;
    }


}
