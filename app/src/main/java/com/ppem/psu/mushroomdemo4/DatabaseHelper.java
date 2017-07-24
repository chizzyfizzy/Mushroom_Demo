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
    public static final int DATABASE_VERSION = 30;

    //Farm Table
    public static final String TABLE_NAME_FARMS = "Farms";
    public static final String FARM_ID = "_id";
    public static final String FARM_NAME = "farmName";
    public static final String FARM_DESCRIPTION = "farmDescr";
    //Create Farms Table Statement
    public static final String CREATE_TABLE_FARMS = "CREATE TABLE " + TABLE_NAME_FARMS +
            " ( " + FARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + FARM_NAME + " TEXT NOT NULL, "
            + FARM_DESCRIPTION + " TEXT ); ";


    //Plants Table
    public static final String TABLE_NAME_PLANTS = "Plants";
    public static final String PLANT_ID = "_id";
    public static final String PLANT_NAME = "plantName";
    public static final String PLANT_LABEL = "plantLabel";
    public static final String FK_PLANT_FARM = "farmId";
    public static List<String> selectedCells = new ArrayList<>();
    //Create Plants Table Statement
    public static final String CREATE_TABLE_PLANTS = "CREATE TABLE " + TABLE_NAME_PLANTS +
            " ( " + PLANT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
            + PLANT_NAME + " TEXT NOT NULL, "
            + PLANT_LABEL + " TEXT,"
            + FK_PLANT_FARM + " INTEGER NOT NULL, "
            + " FOREIGN KEY (" + FK_PLANT_FARM + ") REFERENCES " + TABLE_NAME_FARMS + " (" + FARM_ID + ")"
            + "); ";


    //Rooms Table
    public static final String TABLE_NAME_ROOMS = "Rooms";
    public static final String ROOM_ID = "_id";
    public static final String ROOM_NAME = "roomName";
    public static final String ROOM_LABEL = "roomLabel";
    public static final String FK_PLANT_ID = "plantID";
    //Create Rooms Table Statement
    public static final String CREATE_TABLE_ROOMS = " CREATE TABLE " + TABLE_NAME_ROOMS +
            " ( " + ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
            + ROOM_NAME + " TEXT NOT NULL, "
            +ROOM_LABEL + " TEXT, "
            + FK_PLANT_ID + " INTEGER NOT NULL, "
            + " FOREIGN KEY (" + FK_PLANT_ID + ") REFERENCES " + TABLE_NAME_PLANTS + " (" + PLANT_ID + ")"
            + ");";



    //Counts Table
    public static final String TABLE_NAME_COUNTS = "Counts";
    public static final String COUNT_ID = "_id";
    public static final String COUNT_NAME = "countName";
    public static final String COUNT_NUMBER = "countNum";
    public static final String COUNT_CREATED_AT = "countDate";
    public static final String COUNT_CHART_BOOLEAN = "countChart";
    public static final String FK_COUNT_ROOM = "roomId";
    //Create Counts Table Statement
    public static final String CREATE_TABLE_COUNTS = " CREATE TABLE " + TABLE_NAME_COUNTS + " ( "
            + COUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
            + COUNT_NAME + " TEXT NOT NULL, "
            + COUNT_NUMBER + " INTEGER , "
            //+ COUNT_CREATED_AT + " INTEGER , "
            + COUNT_CHART_BOOLEAN + " INTEGER ,"
            + FK_COUNT_ROOM + " INTEGER , "
            + " FOREIGN KEY (" + FK_COUNT_ROOM + ") REFERENCES " + TABLE_NAME_ROOMS + " (" + ROOM_ID+")"
            + ");";



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
            + CHART_COL_NUM + " INTEGER NOT NULL, "
            + CHART_ROW_NUM + " INTEGER NOT NULL, "
            + CHART_BED_PEAK + " INTEGER , "
            + FK_CHART_ROOM + " INTEGER NOT NULL, "
            + " FOREIGN KEY (" + FK_CHART_ROOM + ") REFERENCES " + TABLE_NAME_ROOMS + " (" + ROOM_ID + ")"
            + ");";


    //Cell / Squares Table
    public static final String TABLE_NAME_CELLS = "Cells";
    public static final String CELL_ID = "_id";
    public static final String CELL_BED = "cellBed";
    public static final String CELL_COLUMN = "cellColumn";
    public static final String CELL_ROW = "cellRow";
    public static final String FK_CELL_ROOM = "roomId";
    //Create Cell Table Statement
    private static final String CREATE_TABLE_CELLS = "CREATE TABLE " + TABLE_NAME_CELLS + " ( "
            + CELL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CELL_BED + " STRING NOT NULL, "
            + CELL_COLUMN + " INTEGER NOT NULL, "
            + CELL_ROW + " INTEGER NOT NULL, "
            + FK_CELL_ROOM + " INTEGER NOT NULL, "
            + " FOREIGN KEY (" + FK_CHART_ROOM + ") REFERENCES " + TABLE_NAME_ROOMS + " (" + ROOM_ID + ")"
            + ");";

    //Counts & Cells Table
    public static final String TABLE_NAME_COUNT_CELLS = "CountCells";
    public static final String COUNT_CELL_ID = "countCellId";
    public static final String PR_COUNT_ID = "countId";
    public static final String PR_CELL_ID = "cellId";
    public static final String FK_ROOM_COUNT_CELL = "roomId";
    //Create Count & Cells Table
    private static final String CREATE_TABLE_COUNT_CELLS = "CREATE TABLE " + TABLE_NAME_COUNT_CELLS + " ( "
            + COUNT_CELL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PR_COUNT_ID + " INTEGER NOT NULL, "
            + PR_CELL_ID + " INTEGER NOT NULL, "
            + FK_ROOM_COUNT_CELL +  " INTEGER NOT NULL, "
            + " FOREIGN KEY (" + PR_COUNT_ID + ") REFERENCES " + TABLE_NAME_COUNTS + " (" + COUNT_ID + ")"
            + " FOREIGN KEY (" + PR_CELL_ID + ") REFERENCES " + TABLE_NAME_CELLS + " (" + CELL_ID + "), "
            + " FOREIGN KEy (" + FK_ROOM_COUNT_CELL + ") REFERENCES " + TABLE_NAME_ROOMS + " (" + ROOM_ID + ") "
            + ");";

    //TODO Add a Count Weekly Total Table? Just add Weekly variable to Count Table (After adding in date functionality)?
    //TODO Add a cell/square Table? Prob not because if exporting to excel then would just print gridview cells with for loops again. Unless



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_FARMS);
        database.execSQL(CREATE_TABLE_PLANTS);
        database.execSQL(CREATE_TABLE_ROOMS);
        database.execSQL(CREATE_TABLE_CHARTS);
        database.execSQL(CREATE_TABLE_COUNTS);
        database.execSQL(CREATE_TABLE_CELLS);
        database.execSQL(CREATE_TABLE_COUNT_CELLS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_FARMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_PLANTS );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ROOMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CHARTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_COUNTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CELLS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_COUNT_CELLS);
        onCreate(db);
    }


}
