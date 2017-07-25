package com.ppem.psu.mushroomdemo4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitchell on 6/19/2017.
 */

//Controller of sql data and user interaction with it
public class RoomDAO {
    //DB Fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {DatabaseHelper.ROOM_ID, DatabaseHelper.ROOM_NAME, DatabaseHelper.ROOM_LABEL};

    public RoomDAO (Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Room createRoom (String newRoomName, long plantId){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.ROOM_NAME, newRoomName);
        values.put(DatabaseHelper.FK_PLANT_ID, plantId);
        long insertId = database.insert(DatabaseHelper.TABLE_NAME_ROOMS, null, values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_ROOMS, allColumns, DatabaseHelper.ROOM_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Room newRoom = cursorToRoom(cursor);
        cursor.close();
        return newRoom;
    }


    public void deleteRoom(Room room) {
        long id = room.getRoomId();
        System.out.println("Room deleted with id: " +id);
        database.delete(DatabaseHelper.TABLE_NAME_ROOMS, DatabaseHelper.ROOM_ID + " = " + id, null);
    }

    public List<Room> getAllRoomsForPlant(long plantId) {
        List<Room> roomList = new ArrayList<Room>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_ROOMS, allColumns, DatabaseHelper.FK_PLANT_ID + " = " + plantId, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Room room = cursorToRoom(cursor);
            roomList.add(room);
            cursor.moveToNext();
        }
        cursor.close();
        return roomList;
    }

    public List<Room> getAllRooms(){
        List<Room> roomList = new ArrayList<Room>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_ROOMS, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Room room = cursorToRoom(cursor);
            roomList.add(room);
            cursor.moveToNext();
        }
        cursor.close();
        return roomList;
    }

    public void updateRoom(String rName, String rLabel, long rId, long pId){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.ROOM_NAME, rName);
        values.put(DatabaseHelper.ROOM_LABEL, rLabel);
        database.update(DatabaseHelper.TABLE_NAME_ROOMS, values, DatabaseHelper.ROOM_ID + " = " + rId + " AND "
                                                                + DatabaseHelper.FK_PLANT_ID + " = " + pId, null);
    }


    public void deleteAllRoomsForPlant(long plantId){
        System.out.println("All Rooms Deleted For Plant " + plantId);
        database.delete(DatabaseHelper.TABLE_NAME_ROOMS, DatabaseHelper.FK_PLANT_ID + " = " + plantId, null);
    }

    private Room cursorToRoom(Cursor cursor) {
        Room room = new Room();
        room.setRoomId(cursor.getLong(0));
        room.setRoomName(cursor.getString(1));
        return room;
    }

}
