package com.ppem.psu.mushroomdemo4;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mitchell on 6/19/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "farm.db";

    //Increment for every change in database structure.
    public static final int DATABASE_VERSION = 21;


    //Plants Table
    public static final String TABLE_NAME_PLANTS = "Plants";
    public static final String PLANT_ID = "_id";
    public static final String PLANT_NAME = "plantName";
    public static final String PLANT_LABEL = "plantLabel";
    public static List<String> selectedCells = new ArrayList<>();
    //Create Plants Table Statement
    public static final String CREATE_TABLE_PLANTS = "CREATE TABLE " + TABLE_NAME_PLANTS +
            " ( " + PLANT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
            + PLANT_NAME + " TEXT , "
            + PLANT_LABEL + " TEXT ); ";


    //Rooms Table
    public static final String TABLE_NAME_ROOMS = "Rooms";
    public static final String ROOM_ID = "_id";
    public static final String ROOM_NAME = "roomName";
    public static final String FK_PLANT_ID = "plantID";
    //Create Rooms Table Statement
    public static final String CREATE_TABLE_ROOMS = " CREATE TABLE " + TABLE_NAME_ROOMS +
            " ( " + ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
            + ROOM_NAME + " TEXT , "
            + FK_PLANT_ID + " INTEGER, "
            + " FOREIGN KEY (" + FK_PLANT_ID + ") REFERENCES " + TABLE_NAME_PLANTS + " (" + PLANT_ID + "));";



    //Counts Table
    public static final String TABLE_NAME_COUNTS = "Counts";
    public static final String COUNT_ID = "_id";
    public static final String COUNT_NAME = "countName";
    public static final String COUNT_NUMBER = "countNum";
    public static final String COUNT_CREATED_AT = "countDate";
    public static final String FK_COUNT_ROOM = "roomId";
    //Create Counts Table Statement
    public static final String CREATE_TABLE_COUNTS = " CREATE TABLE " + TABLE_NAME_COUNTS
            + " ( " + COUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
            + COUNT_NAME + " TEXT, "
            + COUNT_NUMBER + " INTEGER, "
            + COUNT_CREATED_AT + " INTEGER , "
            + FK_COUNT_ROOM + " INTEGER, "
            + " FOREIGN KEY (" + FK_COUNT_ROOM + ") REFERENCES " + TABLE_NAME_ROOMS + " (" + ROOM_ID+"));";



    //Charts Table
    public static final String TABLE_NAME_CHARTS = "Charts";
    public static final String CHART_ID = "_id";
    public static final String CHART_COL_NUM = "colNum";
    public static final String CHART_ROW_NUM = "rowNum";
    public static final String CHART_BED_PEAK= "bedPeak";
    public static final String FK_CHART_ROOM = "roomID";
    //Create Charts Table Statement
    private static final String CREATE_TABLE_CHARTS = "CREATE TABLE " + TABLE_NAME_CHARTS + " ( "
            + CHART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CHART_COL_NUM + " INTEGER , "
            + CHART_ROW_NUM + " INTEGER, "
            + CHART_BED_PEAK + " BOOLEAN , "
            + FK_CHART_ROOM + " INTEGER, "
            + " FOREIGN KEY (" + FK_CHART_ROOM + ") REFERENCES " + TABLE_NAME_ROOMS + " (" + ROOM_ID + "));";

    //TODO Add a Count Weekly Total Table? Just add Weekly variable to Count Table?
    //TODO Add a cell/square Table?



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_PLANTS);
        database.execSQL(CREATE_TABLE_ROOMS);
        database.execSQL(CREATE_TABLE_CHARTS);
        database.execSQL(CREATE_TABLE_COUNTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PLANTS );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ROOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CHARTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_COUNTS);
        onCreate(db);
    }


}
