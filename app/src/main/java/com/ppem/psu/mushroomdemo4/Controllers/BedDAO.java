package com.ppem.psu.mushroomdemo4.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ppem.psu.mushroomdemo4.Models.Bed;
import com.ppem.psu.mushroomdemo4.Models.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitchell on 7/27/2017.
 */

public class BedDAO {
    //DB fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {DatabaseHelper.BED_ID, DatabaseHelper.BED_NAME, DatabaseHelper.BED_LEVELS, DatabaseHelper.BED_SQUARES};

    public BedDAO (Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Bed createBed(String bedName, int bedNum, int squareNum, long roomId){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.BED_NAME, bedName);
        values.put(DatabaseHelper.BED_LEVELS, bedNum);
        values.put(DatabaseHelper.BED_SQUARES, squareNum);
        values.put(DatabaseHelper.FK_BED_ROOM, roomId);
        long insertId = database.insert(DatabaseHelper.TABLE_NAME_BEDS, null, values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_BEDS, allColumns, DatabaseHelper.BED_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Bed newBed = cursorToBed(cursor);
        cursor.close();
        return newBed;
    }


    public boolean createBedsAllRooms(int bedNum, int bedSquaresNum) {
        boolean success;
        try {
            //Get room list to iterate through and get the id of each.
            List<Room> roomList = new ArrayList<Room>();
            String[] allRoomColumns = {DatabaseHelper.ROOM_ID, DatabaseHelper.ROOM_NAME};
            Cursor roomCursor = database.query(DatabaseHelper.TABLE_NAME_BEDS, allRoomColumns, null, null, null, null, null);
            roomCursor.moveToFirst();
            while (!roomCursor.isAfterLast()) {
                Room room = cursorToRoom(roomCursor);
                roomList.add(room);
                roomCursor.moveToNext();
            }

            //Iterate through each room in roomList and add new chart with passed name. Also gives corresponding FK_ROOM_ID for each count
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.CHART_COL_NUM, bedNum);
            values.put(DatabaseHelper.CHART_ROW_NUM, bedSquaresNum);
            for (int i = 0; i < roomList.size(); i++) {
                long roomId = roomList.get(i).getRoomId();
                values.put(DatabaseHelper.FK_BED_ROOM, roomId);
                long insertId = database.insert(DatabaseHelper.TABLE_NAME_BEDS, null, values);
                Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_BEDS, allColumns, DatabaseHelper.BED_ID + " = " + insertId, null, null, null, null);
                cursor.moveToFirst();
                //Count newCount = cursorToCount(cursor);
                cursor.close();
            }
            success = true;
        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }

        return success;
    }


    public void deleteBed(Bed bed) {
        long id = bed.getBedId();
        System.out.println("Bed deleted with id: " + id);
        database.delete(DatabaseHelper.TABLE_NAME_BEDS, DatabaseHelper.BED_ID + " = " + id, null);
    }

    public void deleteAllBeds(){
        database.delete(DatabaseHelper.TABLE_NAME_BEDS,null,null);
    }

    public List<Bed> getAllBeds() {
        List<Bed> bedList = new ArrayList<Bed>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_BEDS, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Bed bed = cursorToBed(cursor);
            bedList.add(bed);
            cursor.moveToNext();
        }
        cursor.close();
        return bedList;
    }

    public List<Bed> getBedsForRoom(long roomId){
        List<Bed> bedList = new ArrayList<Bed>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_BEDS, allColumns, DatabaseHelper.FK_BED_ROOM + " = " + roomId, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Bed bed = cursorToBed(cursor);
            bedList.add(bed);
            cursor.moveToNext();
        }
        return bedList;
    }

    private Bed cursorToBed(Cursor cursor) {
        Bed bed = new Bed();
        if(cursor.getCount() > 0) {
            bed.setBedId(cursor.getLong(0));
            bed.setBedName(cursor.getString(1));
            bed.setBedLevels(cursor.getInt(2));
            bed.setBedSquares(cursor.getInt(3));
        }
        return bed;
    }

    private Room cursorToRoom(Cursor cursor) {
        Room room = new Room();
        room.setRoomId(cursor.getLong(0));
        room.setRoomName(cursor.getString(1));
        return room;
    }
}
