package com.ppem.psu.mushroomdemo4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitchell on 7/13/2017.
 */

public class CellDAO {
    //DB Fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {DatabaseHelper.CELL_ID, DatabaseHelper.CELL_COLUMN, DatabaseHelper.CELL_ROW};

    public CellDAO (Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Cell createCell (String cellBed, int cellColumn, int cellRow, long roomId){
        ContentValues values = new ContentValues();
       // values.put(DatabaseHelper.CELL_BED, cellBed);
        values.put(DatabaseHelper.CELL_COLUMN, cellColumn);
        values.put(DatabaseHelper.CELL_ROW, cellRow);
        values.put(DatabaseHelper.FK_CELL_BED, roomId);
        long insertId = database.insert(DatabaseHelper.TABLE_NAME_CELLS, null, values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_CELLS, allColumns, DatabaseHelper.CELL_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Cell newCell = cursorToCell(cursor);
        cursor.close();
        return newCell;
    }

    public Cell createCellForBed(int cellCol, int cellRow, long bedId){
        ContentValues values=  new ContentValues();
        values.put(DatabaseHelper.CELL_COLUMN, cellCol);
        values.put(DatabaseHelper.CELL_ROW, cellRow);
        values.put(DatabaseHelper.FK_CELL_BED, bedId);
        long insertId = database.insert(DatabaseHelper.TABLE_NAME_CELLS, null, values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_CELLS, allColumns, DatabaseHelper.CELL_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Cell newCell = cursorToCell(cursor);
        cursor.close();
        return newCell;
    }

    public void deleteCell(Cell cell) {
        long id = cell.getCellId();
        System.out.println("Cell deleted with id: " +id);
        database.delete(DatabaseHelper.TABLE_NAME_CELLS, DatabaseHelper.CELL_ID + " = " + id, null);
    }

    public List<Cell> getAllCellsForRoom(long roomId) {
        List<Cell> cellList = new ArrayList<Cell>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_CELLS, allColumns, DatabaseHelper.FK_CELL_BED + " = " + roomId, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Cell cell = cursorToCell(cursor);
            cellList.add(cell);
            cursor.moveToNext();
        }
        cursor.close();
        return cellList;
    }

    public List<Cell> getCellsForBed(long bedId){
        List<Cell> cellList = new ArrayList<Cell>();

        try {
            Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_CELLS, allColumns, DatabaseHelper.FK_CELL_BED + " = " + bedId, null, null, null, null);


        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            Cell cell = cursorToCell(cursor);
            cellList.add(cell);
            cursor.moveToNext();
        }
        cursor.close();
        } catch(SQLException e){
            e.printStackTrace();
        }
        return cellList;
    }

    public List<Cell> getAllCells(){
        List<Cell> cellList = new ArrayList<Cell>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_CELLS, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Cell cell = cursorToCell(cursor);
            cellList.add(cell);
            cursor.moveToNext();
        }
        cursor.close();
        return cellList;
    }

    public void deleteCellsForBed(long bedId){
        database.delete(DatabaseHelper.TABLE_NAME_CELLS, DatabaseHelper.FK_CELL_BED + " = " + bedId, null);

    }



    private Cell cursorToCell(Cursor cursor) {
        Cell cell = new Cell();
        cell.setCellId(cursor.getLong(0));
        cell.setCellColumn(cursor.getInt(1));
        cell.setCellRow(cursor.getInt(2));
        return cell;
    }

}
