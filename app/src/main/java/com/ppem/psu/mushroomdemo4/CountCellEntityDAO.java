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
    private String[] allColumns = {DatabaseHelper.PR_COUNT_ID, DatabaseHelper.PR_CELL_ID, DatabaseHelper.FK_BED_COUNT_CELL};

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
        values.put(DatabaseHelper.FK_BED_COUNT_CELL, roomId);
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
                                                             + DatabaseHelper.FK_BED_COUNT_CELL + " = " + roomId, null);
    }

    public List<CountCellEntity> getAllCountCellsBed(long bedId) {
        List<CountCellEntity> countCellList = new ArrayList<CountCellEntity>();
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_COUNT_CELLS, allColumns, DatabaseHelper.FK_BED_COUNT_CELL + " = " + bedId, null, null, null, null);
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
        cursor.close();
        return countCellList;
    }

    public List<Count> getDistinctCountsForCell(long cellId){
        List<Count> countList = new ArrayList<Count>();
        String[] countColumns  = {DatabaseHelper.COUNT_ID, DatabaseHelper.COUNT_NAME, DatabaseHelper.COUNT_NUMBER, DatabaseHelper.COUNT_CHART_BOOLEAN};
        String selectQuery = "SELECT * FROM " + DatabaseHelper.TABLE_NAME_COUNTS + " co, " + DatabaseHelper.TABLE_NAME_CELLS +" ce, "
                + DatabaseHelper.TABLE_NAME_COUNT_CELLS + " cc WHERE"
                + " ce." + DatabaseHelper.CELL_ID + " = " + cellId
                + " AND ce." + DatabaseHelper.CELL_ID + " = cc." + DatabaseHelper.PR_CELL_ID
                + " AND " + "co." + DatabaseHelper.COUNT_ID + " = cc." + DatabaseHelper.PR_COUNT_ID
                + " GROUP BY " + DatabaseHelper.COUNT_ID;
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Count count = cursorToCount(cursor);
            countList.add(count);
            cursor.moveToNext();
        }
        cursor.close();
        return countList;
    }



    private CountCellEntity cursorToCountCell(Cursor cursor) {
        CountCellEntity countCell = new CountCellEntity();
        if(cursor.getCount() > 0) {
            countCell.setCountCellId(cursor.getLong(0));
            countCell.setCountId(cursor.getLong(1));
            countCell.setCellId(cursor.getLong(2));
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
