package com.ppem.psu.mushroomdemo4.Controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.ppem.psu.mushroomdemo4.Models.Chart;
import com.ppem.psu.mushroomdemo4.Models.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitchell on 6/29/2017.
 */

public class ChartDAO {
    //DB fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {DatabaseHelper.CHART_ID, DatabaseHelper.CHART_COL_NUM, DatabaseHelper.CHART_ROW_NUM, DatabaseHelper.CHART_BED_PEAK};

    public ChartDAO (Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Chart createChart(int bedNum, int bedPeak, int rowNum, long roomId){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CHART_COL_NUM, bedNum);
        values.put(DatabaseHelper.CHART_ROW_NUM, rowNum);
        values.put(DatabaseHelper.CHART_BED_PEAK, bedPeak);
        values.put(DatabaseHelper.FK_CHART_ROOM, roomId);
        long insertId = database.insert(DatabaseHelper.TABLE_NAME_CHARTS, DatabaseHelper.FK_CHART_ROOM + " = " + roomId, values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_CHARTS, allColumns, DatabaseHelper.CHART_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Chart newChart = cursorToChart(cursor);
        cursor.close();
        return newChart;
    }


    public boolean createChartAllRooms(int bedNum, boolean bedPeak, int rowNum) {
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

            //Iterate through each room in roomList and add new chart with passed name. Also gives corresponding FK_ROOM_ID for each count
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.CHART_COL_NUM, bedNum);
            values.put(DatabaseHelper.CHART_ROW_NUM, rowNum);
            values.put(DatabaseHelper.CHART_BED_PEAK, bedPeak);
            for (int i = 0; i < roomList.size(); i++) {
                long roomId = roomList.get(i).getRoomId();
                values.put(DatabaseHelper.FK_CHART_ROOM, roomId);
                long insertId = database.insert(DatabaseHelper.TABLE_NAME_CHARTS, null, values);
                Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_CHARTS, allColumns, DatabaseHelper.CHART_ID + " = " + insertId, null, null, null, null);
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


    public void deleteChart(Chart chart) {
        long id = chart.getChartId();
        System.out.println("Comment deleted with id: " + id);
        database.delete(DatabaseHelper.TABLE_NAME_CHARTS, DatabaseHelper.CHART_ID + " = " + id, null);
    }

    public void deleteAllCharts(){
        database.delete(DatabaseHelper.TABLE_NAME_CHARTS,null,null);
    }

    public List<Chart> getAllCharts() {
        List<Chart> chartList = new ArrayList<Chart>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_CHARTS, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Chart chart = cursorToChart(cursor);
            chartList.add(chart);
            cursor.moveToNext();
        }
        cursor.close();
        return chartList;
    }

    public Chart getChartForRoom(long roomId){
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_CHARTS, allColumns, DatabaseHelper.FK_CHART_ROOM + " = " + roomId, null, null, null, null);
        cursor.moveToFirst();
        Chart chart = cursorToChart(cursor);
        cursor.close();
        return chart;
    }

    private Chart cursorToChart(Cursor cursor) {
        Chart chart = new Chart();
        if(cursor.getCount() > 0) {
            chart.setChartId(cursor.getLong(0));
            chart.setColNum(cursor.getInt(1));
            chart.setRowNum(cursor.getInt(2));
            chart.setBedPeak(cursor.getInt(3));
        }
        return chart;
    }

    private Room cursorToRoom(Cursor cursor) {
        Room room = new Room();
        room.setRoomId(cursor.getLong(0));
        room.setRoomName(cursor.getString(1));
        return room;
    }
}
