package com.ppem.psu.mushroomdemo4.Models;

/**
 * Created by Mitchell on 6/19/2017.
 */

//Room Model data
public class Room {
    private long roomId;
    private String roomName;
    private String roomLabel;
    private String roomComment;
    private String lastEdit;

    public Room(){
        this.roomId = getRoomId();
        this.roomName = getRoomName();
        this.lastEdit = getLastEdit();
    }

    public Room(String name, String label){
        this.roomName = name;
        this.roomLabel = label;
    }

    public Room(Long rId, String name, String label){
        this.roomId = rId;
        this.roomName = name;
        this.roomLabel = label;
    }


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

    public String getRoomLabel() {
        return roomLabel;
    }

    public void setRoomLabel(String roomLabel) {
        this.roomLabel = roomLabel;
    }

    public String getRoomComment() {
        return roomComment;
    }

    public void setRoomComment(String roomComment) {
        this.roomComment = roomComment;
    }

    public String getLastEdit() {
        return lastEdit;
    }

    public void setLastEdit(String lastEdit) {
        this.lastEdit = lastEdit;
    }

    @Override
    public String toString(){
        return roomName;
    }


}
