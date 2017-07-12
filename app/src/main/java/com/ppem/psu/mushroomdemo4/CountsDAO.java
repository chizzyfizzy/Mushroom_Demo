package com.ppem.psu.mushroomdemo4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Locale.getDefault;

/**
 * Created by Mitchell on 6/19/2017.
 */

public class CountsDAO {
    //DB Fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {DatabaseHelper.COUNT_ID, DatabaseHelper.COUNT_NAME, DatabaseHelper.COUNT_NUMBER, DatabaseHelper.COUNT_CREATED_AT};

    public CountsDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Count createCount(String newCountName, Long roomId){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COUNT_NAME, newCountName);
        values.put(DatabaseHelper.COUNT_CREATED_AT, System.currentTimeMillis());
        values.put(DatabaseHelper.FK_COUNT_ROOM, roomId);
        long insertId = database.insert(DatabaseHelper.TABLE_NAME_COUNTS, null, values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_COUNTS, allColumns, DatabaseHelper.COUNT_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Count newCount = cursorToCount(cursor);
        cursor.close();
        return newCount;
    }

    public boolean createCountAllRooms(String newCountName){
        boolean success;
        try {
            //Get room list to iterate through and get the id of each.
            List<Room> roomList = new ArrayList<Room>();
            String[] allRoomColumns = {DatabaseHelper.ROOM_ID, DatabaseHelper.ROOM_NAME};
            Cursor roomCursor = database.query(DatabaseHelper.TABLE_NAME_ROOMS, allRoomColumns, null, null, null, null, null);
            roomCursor.moveToFirst();
            while (!roomCursor.isAfterLast()) {
                Room room = cursorToRoom(roomCursor);
                roomList.add(room);
                roomCursor.moveToNext();
            }
            //Iterate through each room in roomList and add new count with passed name. Also gives corresponding FK_ROOM_ID for each count
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COUNT_NAME, newCountName);
            values.put(DatabaseHelper.COUNT_CREATED_AT, System.currentTimeMillis());
            for (int i = 0; i < roomList.size(); i++) {
                long roomId = roomList.get(i).getRoomId();
                values.put(DatabaseHelper.FK_COUNT_ROOM, roomId);
                long insertId = database.insert(DatabaseHelper.TABLE_NAME_COUNTS, null, values);
                Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_COUNTS, allColumns, DatabaseHelper.COUNT_ID + " = " + insertId, null, null, null, null);
                cursor.moveToFirst();
                cursor.close();
            }
            success = true;
        }catch(SQLException e){
            success = false;
            e.printStackTrace();
        }
        return success;
    }

    public void deleteCount(Count count) {
        long id = count.getCountId();
        System.out.println("Count deleted with id: " +id);
        database.delete(DatabaseHelper.TABLE_NAME_COUNTS, DatabaseHelper.COUNT_ID + " = " + id, null);
    }

    public List<Count> getAllCounts() {
        List<Count> countList = new ArrayList<Count>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_COUNTS, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Count count = cursorToCount(cursor);
            countList.add(count);
            cursor.moveToNext();
        }
        cursor.close();
        return countList;
    }


    public List<Count> getAllCountsForRoom (long roomId) {
        List<Count> countList = new ArrayList<Count>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_COUNTS, allColumns, DatabaseHelper.FK_COUNT_ROOM + " = " + roomId, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Count count = cursorToCount(cursor);
            countList.add(count);
            cursor.moveToNext();
        }
        cursor.close();
        return countList;
    }

    public Count getSpecificCount(long id){
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_COUNTS, allColumns, DatabaseHelper.COUNT_ID + " = " + id, null, null, null,null);
        cursor.moveToFirst();
        Count count = cursorToCount(cursor);
        return count;
    }

    public void deleteAllCounts(){
        System.out.println("All Counts Delete");
        database.delete(DatabaseHelper.TABLE_NAME_COUNTS, null, null);
    }
    //TODO Change to insert and add a weekly count
    public boolean updateCounts(long roomId, long countId, int countNum){
        System.out.println("Updating Counts for Room " + roomId);
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COUNT_NUMBER, countNum);
        values.put(DatabaseHelper.COUNT_CREATED_AT, getDateTime());
        database.update(DatabaseHelper.TABLE_NAME_COUNTS, values, DatabaseHelper.COUNT_ID + " = " + countId +
                                                                " AND " + DatabaseHelper.FK_COUNT_ROOM + " = " + roomId, null);
        return true;
    }

    public void resetCounts(){
        System.out.println("Resetting Count Data");
        database.delete(DatabaseHelper.TABLE_NAME_COUNTS, DatabaseHelper.COUNT_NUMBER,null);
    }



    private Count cursorToCount(Cursor cursor) {
        Count count = new Count();
        count.setCountId(cursor.getLong(0));
        count.setCountName(cursor.getString(1));
        count.setCountNumber(cursor.getInt(2));
        count.setCountDate(cursor.getInt(3));
        return count;
    }

    private Room cursorToRoom(Cursor cursor) {
        Room room = new Room();
        room.setRoomId(cursor.getLong(0));
        room.setRoomName(cursor.getString(1));
        return room;
    }

    private String getDateTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }



}
