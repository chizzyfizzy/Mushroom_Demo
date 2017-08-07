package com.ppem.psu.mushroomdemo4.DatabaseControllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import com.ppem.psu.mushroomdemo4.Models.Count;
import com.ppem.psu.mushroomdemo4.Models.Room;

import java.util.ArrayList;
import java.util.List;

import static java.util.Locale.getDefault;

/**
 * Created by Mitchell on 6/19/2017.
 */

public class CountsDAO {
    //DB Fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {DatabaseHelper.COUNT_ID, DatabaseHelper.COUNT_NAME, DatabaseHelper.COUNT_NUMBER, DatabaseHelper.COUNT_CHART_BOOLEAN};

    public CountsDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Count createCount(Count count, Long roomId){
        int chartBool = (count.isInChart()) ? 1:0;
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COUNT_NAME, count.getCountName());
        values.put(DatabaseHelper.COUNT_CHART_BOOLEAN, chartBool);
       // values.put(DatabaseHelper.COUNT_CREATED_AT, System.currentTimeMillis());
        values.put(DatabaseHelper.FK_COUNT_ROOM, roomId);
        long insertId = database.insert(DatabaseHelper.TABLE_NAME_COUNTS, null, values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_COUNTS, allColumns, DatabaseHelper.COUNT_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Count newCount = cursorToCount(cursor);
        cursor.close();
        return newCount;
    }

    public boolean createCountAllRooms(Count count){
        int chartBool = (count.isInChart()) ? 1:0;
        boolean success;
        try {
            //Get room list to iterate through and get the id of each.
            List<Room> roomList = new ArrayList<Room>();
            String[] allRoomColumns = {DatabaseHelper.ROOM_ID, DatabaseHelper.ROOM_NAME, DatabaseHelper.ROOM_LABEL};
            Cursor roomCursor = database.query(DatabaseHelper.TABLE_NAME_ROOMS, allRoomColumns, null, null, null, null, null);
            roomCursor.moveToFirst();
            while (!roomCursor.isAfterLast()) {
                Room room = cursorToRoom(roomCursor);
                roomList.add(room);
                roomCursor.moveToNext();
            }
            //Iterate through each room in roomList and add new count with passed name. Also gives corresponding FK_ROOM_ID for each count
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COUNT_NAME, count.getCountName());
            values.put(DatabaseHelper.COUNT_CHART_BOOLEAN, chartBool);
           // values.put(DatabaseHelper.COUNT_CREATED_AT, System.currentTimeMillis());
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

    public List<Count> getChartCounts (long roomId) {
        List<Count> countList = new ArrayList<Count>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_COUNTS, allColumns, DatabaseHelper.FK_COUNT_ROOM + " = " + roomId +
                                        " AND " + DatabaseHelper.COUNT_CHART_BOOLEAN + " = " + 1, null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
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

    public boolean updateCountValue(long countId, int countNum){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COUNT_NUMBER, countNum);
        database.update(DatabaseHelper.TABLE_NAME_COUNTS, values, DatabaseHelper.COUNT_ID + " = " + countId, null);
        return true;
    }


    public void resetCounts(){
        System.out.println("Resetting Count Data");
        database.delete(DatabaseHelper.TABLE_NAME_COUNTS, DatabaseHelper.COUNT_NUMBER,null);
    }

    public void updateCountSetting(String cName, boolean inChart, long cId){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COUNT_NAME, cName);
        values.put(DatabaseHelper.COUNT_CHART_BOOLEAN, (inChart) ? 1:0);
        database.update(DatabaseHelper.TABLE_NAME_COUNTS, values, DatabaseHelper.COUNT_ID + " = " + cId, null);
    }

    public void updateCountWithName(String oldName, String newName, boolean inChart){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COUNT_NAME, newName);
        values.put(DatabaseHelper.COUNT_CHART_BOOLEAN, (inChart) ? 1:0);
        database.update(DatabaseHelper.TABLE_NAME_COUNTS, values, DatabaseHelper.COUNT_NAME + " = '" + oldName + "'", null);
    }

    public void deleteCountWithName(Count count) {
        database.delete(DatabaseHelper.TABLE_NAME_COUNTS, DatabaseHelper.COUNT_NAME + " = '" + count.getCountName() + "'", null);
    }

    public List<Count> getDistinctCounts(){
        List<Count> countList = new ArrayList<Count>();
        Cursor cursor = database.query(true, DatabaseHelper.TABLE_NAME_COUNTS, allColumns, DatabaseHelper.FK_COUNT_ROOM + " is null ", null, DatabaseHelper.COUNT_NAME, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Count count = cursorToCount(cursor);
            countList.add(count);
            cursor.moveToNext();
        }
        cursor.close();
        return countList;
    }

    public void createDefaultCount(String name, boolean inChart){
        int chartBool = (inChart) ? 1:0;
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COUNT_NAME, name);
        values.put(DatabaseHelper.COUNT_CHART_BOOLEAN, chartBool);
        database.insert(DatabaseHelper.TABLE_NAME_COUNTS, null, values);
    }



    private Count cursorToCount(Cursor cursor) {
        Count count = new Count();
        count.setCountId(cursor.getLong(0));
        count.setCountName(cursor.getString(1));
        count.setCountNumber(cursor.getInt(2));
        count.setInChart(cursor.getInt(3) == 1);
//        count.setCountDate(cursor.getInt(3));
        return count;
    }

    private Room cursorToRoom(Cursor cursor) {
        Room room = new Room();
        room.setRoomId(cursor.getLong(0));
        room.setRoomName(cursor.getString(1));
        return room;
    }



}
