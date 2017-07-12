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
 * Created by Mitchell on 6/19/2017.
 */

//Controller of data in sql and user interactions
public class PlantDAO {
    //DB Fields
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {DatabaseHelper.PLANT_ID, DatabaseHelper.PLANT_NAME, DatabaseHelper.PLANT_LABEL};

    public PlantDAO (Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Plant createPlant(String newPlantName, String newPlantLabel){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.PLANT_NAME, newPlantName);
        values.put(DatabaseHelper.PLANT_LABEL, newPlantLabel);

        long insertId = database.insert(DatabaseHelper.TABLE_NAME_PLANTS, null, values);
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_PLANTS, allColumns, DatabaseHelper.PLANT_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Plant newPlant = cursorToPlant(cursor);
        cursor.close();
        return newPlant;
    }

    public void deletePlant(Plant plant) {
        long id = plant.getPlantId();
        System.out.println("Plant deleted with id: " +id);
        database.delete(DatabaseHelper.TABLE_NAME_PLANTS, DatabaseHelper.PLANT_ID + " = " + id, null);
    }

    public List<Plant> getAllPlants() {
        List<Plant> plantList = new ArrayList<Plant>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_PLANTS, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Plant plant = cursorToPlant(cursor);
            plantList.add(plant);
            cursor.moveToNext();
        }
        cursor.close();
        return plantList;
    }

    public Plant getSpecificPlant(long id){

        dbHelper.getReadableDatabase();
        Cursor cursor = database.query(DatabaseHelper.TABLE_NAME_PLANTS, allColumns, DatabaseHelper.PLANT_ID + " = " + id, null, null, null,null);

        cursor.moveToFirst();
        Plant plant = cursorToPlant(cursor);

        return plant;
    }

    private Plant cursorToPlant(Cursor cursor) {
        Plant plant = new Plant();
        plant.setPlantId(cursor.getLong(0));
        plant.setPlantName(cursor.getString(1));
        plant.setPlantLabel(cursor.getString(2));
        System.out.println("Setting Plant " + cursor.getString(1) +" " +  cursor.getString(2));
        return plant;
    }

    public void deleteAllPlants(){
        System.out.println("All Plants Deleted");
        database.delete(DatabaseHelper.TABLE_NAME_PLANTS, null, null);
        database.delete(DatabaseHelper.TABLE_NAME_ROOMS, null, null);
        database.delete(DatabaseHelper.TABLE_NAME_COUNTS,null,null);
    }


}
