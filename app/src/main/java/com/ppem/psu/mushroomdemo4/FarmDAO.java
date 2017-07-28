package com.ppem.psu.mushroomdemo4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitchell on 7/18/2017.
 */

public class FarmDAO {
    //DB Fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {DatabaseHelper.FARM_ID, DatabaseHelper.FARM_NAME, DatabaseHelper.FARM_DESCRIPTION};

    public FarmDAO (Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Farm createFarm(String newFarmName, String newFarmDesc){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.FARM_NAME, newFarmName);
        values.put(DatabaseHelper.FARM_DESCRIPTION, newFarmDesc);

        long insertId = database.insert(DatabaseHelper.TABLE_NAME_FARMS, null, values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_FARMS, allColumns, DatabaseHelper.FARM_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Farm newFarm = cursorToFarm(cursor);
        cursor.close();
        return newFarm;
    }

    public void deleteFarm(Farm farm) {
        long id = farm.getFarmId();
        System.out.println("Farm deleted with id: " +id);
        database.delete(DatabaseHelper.TABLE_NAME_FARMS, DatabaseHelper.FARM_ID + " = " + id, null);
    }

    public List<Farm> getAllFarms() {
        List<Farm> farmList = new ArrayList<Farm>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_FARMS, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Farm farm = cursorToFarm(cursor);
            farmList.add(farm);
            cursor.moveToNext();
        }
        cursor.close();
        return farmList;
    }

    public Farm getSpecificFarm(long id){
        dbHelper.getReadableDatabase();
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_FARMS, allColumns, DatabaseHelper.FARM_ID + " = " + id, null, null, null,null);
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            Farm farm = cursorToFarm(cursor);
            return farm;
        }
        else{ return null;}
    }

    private Farm cursorToFarm(Cursor cursor) {
        Farm farm = new Farm();
        farm.setFarmId(cursor.getLong(0));
        farm.setFarmName(cursor.getString(1));
        farm.setFarmDescription(cursor.getString(2));
        System.out.println("Setting Farm " + cursor.getString(1) + " " + cursor.getString(2));
        return farm;
    }

    public void deleteAllFarms(){
        System.out.println("All Plants Deleted");
        database.delete(DatabaseHelper.TABLE_NAME_FARMS, null, null);
    }
}
