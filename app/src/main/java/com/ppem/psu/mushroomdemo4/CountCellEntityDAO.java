package com.ppem.psu.mushroomdemo4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitchell on 7/21/2017.
 */

public class CountCellEntityDAO {
    //DB Fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {DatabaseHelper.PR_COUNT_ID, DatabaseHelper.PR_CELL_ID};

    public CountCellEntityDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public CountCellEntity insertCountCellEntity (CountCellEntity cc, long roomId){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.PR_COUNT_ID, cc.getCount().getCountId());
        values.put(DatabaseHelper.PR_CELL_ID, cc.getCell().getCellId());
        values.put(DatabaseHelper.FK_ROOM_COUNT_CELL, roomId);
        long insertId = database.insert(DatabaseHelper.TABLE_NAME_COUNT_CELLS, null, values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_COUNT_CELLS, allColumns, DatabaseHelper.PR_CELL_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        CountCellEntity newCountCell = cursorToCountCell(cursor);
        cursor.close();
        return cc;
    }


    public void deleteCountCell(long countId, long cellId, long roomId) {
        database.delete(DatabaseHelper.TABLE_NAME_COUNT_CELLS, DatabaseHelper.PR_COUNT_ID + " = " + countId + " AND "
                                                             + DatabaseHelper.PR_CELL_ID + " = " + cellId + " AND "
                                                             + DatabaseHelper.FK_ROOM_COUNT_CELL + " = " + roomId, null);
    }

    public List<CountCellEntity> getAllCountCellsRoom(long roomId) {
        List<CountCellEntity> countCellList = new ArrayList<CountCellEntity>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_COUNT_CELLS, allColumns, DatabaseHelper.FK_ROOM_COUNT_CELL + " = " + roomId, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CountCellEntity countCell = cursorToCountCell(cursor);
            countCellList.add(countCell);
            cursor.moveToNext();
        }
        cursor.close();
        return countCellList;
    }

    public List<Count> getCountsForCell(long cellId){
        List<Count> countCellList = new ArrayList<Count>();
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_NAME_COUNTS + " co, " + DatabaseHelper.TABLE_NAME_CELLS +" ce, " + DatabaseHelper.TABLE_NAME_COUNT_CELLS + " cc WHERE" +
                                        " ce." + DatabaseHelper.CELL_ID + " = " + cellId +
                                                " AND ce." + DatabaseHelper.CELL_ID + " = cc." + DatabaseHelper.PR_CELL_ID + " AND " +
                                                "co." + DatabaseHelper.COUNT_ID + " = cc." + DatabaseHelper.PR_COUNT_ID;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Count count = cursorToCount(cursor);
            countCellList.add(count);
            cursor.moveToNext();
        }

        return countCellList;
    }



    private CountCellEntity cursorToCountCell(Cursor cursor) {
        CountCellEntity countCell = new CountCellEntity();
        if(cursor.getCount() > 0) {
            countCell.setCountCellId(cursor.getLong(0));
        }
        return countCell;
    }

    private Count cursorToCount(Cursor cursor) {
        Count count = new Count();
        count.setCountId(cursor.getLong(0));
        count.setCountName(cursor.getString(1));
        count.setCountNumber(cursor.getInt(2));
        count.setInChart(cursor.getInt(3));
//        count.setCountDate(cursor.getInt(3));
        return count;
    }
}
