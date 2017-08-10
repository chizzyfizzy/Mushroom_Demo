package com.ppem.psu.mushroomdemo4.DatabaseControllers;

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
    public static final int DATABASE_VERSION = 43;

    //Farm Table
    public static final String TABLE_NAME_FARMS = "Farms";
    public static final String FARM_ID = "f_id";
    public static final String FARM_NAME = "farmName";
    public static final String FARM_DESCRIPTION = "farmDescr";
    //Create Farms Table Statement
    public static final String CREATE_TABLE_FARMS = "CREATE TABLE " + TABLE_NAME_FARMS +
            " ( " + FARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + FARM_NAME + " TEXT NOT NULL, "
            + FARM_DESCRIPTION + " TEXT ); ";


    //Plants Table
    public static final String TABLE_NAME_PLANTS = "Plants";
    public static final String PLANT_ID = "p_id";
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
    public static final String ROOM_ID = "r_id";
    public static final String ROOM_NAME = "roomName";
    public static final String ROOM_LABEL = "roomLabel";
    public static final String ROOM_COMMENT = "roomComment";
    public static final String ROOM_LAST_EDIT = "lastEdit";
    public static final String FK_PLANT_ID = "plantID";
    //Create Rooms Table Statement
    public static final String CREATE_TABLE_ROOMS = " CREATE TABLE " + TABLE_NAME_ROOMS +
            " ( " + ROOM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
            + ROOM_NAME + " TEXT NOT NULL, "
            + ROOM_LABEL + " TEXT, "
            + ROOM_COMMENT + " TEXT, "
            + ROOM_LAST_EDIT + " TEXT, "
            + FK_PLANT_ID + " INTEGER NOT NULL, "
            + " FOREIGN KEY (" + FK_PLANT_ID + ") REFERENCES " + TABLE_NAME_PLANTS + " (" + PLANT_ID + ")"
            + ");";



    //Counts Table
    public static final String TABLE_NAME_COUNTS = "Counts";
    public static final String COUNT_ID = "co_id";
    public static final String COUNT_NAME = "countName";
    public static final String COUNT_NUMBER = "countNum";
    public static final String COUNT_DATE = "countDate";
    public static final String COUNT_CHART_BOOLEAN = "countChart";
    public static final String FK_COUNT_ROOM = "roomId";
    //Create Counts Table Statement
    public static final String CREATE_TABLE_COUNTS = " CREATE TABLE " + TABLE_NAME_COUNTS + " ( "
            + COUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
            + COUNT_NAME + " TEXT NOT NULL, "
            + COUNT_NUMBER + " INTEGER , "
            + COUNT_DATE + " INTEGER , "
            + COUNT_CHART_BOOLEAN + " INTEGER ,"
            + FK_COUNT_ROOM + " INTEGER , "
            + " FOREIGN KEY (" + FK_COUNT_ROOM + ") REFERENCES " + TABLE_NAME_ROOMS + " (" + ROOM_ID+")"
            + ");";


    //Bed Table
    public static final String TABLE_NAME_BEDS = "Beds";
    public static final String BED_ID = "b_id";
    public static final String BED_NAME = "bedName";
    public static final String BED_LEVELS = "bedLevels";
    public static final String BED_SQUARES = "bedSquares";
    public static final String FK_BED_ROOM = "roomID";
    //Create Beds Table Statement
    private static final String CREATE_TABLE_BEDS = "CREATE TABLE " + TABLE_NAME_BEDS + " ( "
            + BED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BED_NAME + " TEXT NOT NULL, "
            + BED_LEVELS + " INTEGER NOT NULL, "
            + BED_SQUARES + " INTEGER NOT NULL, "
            + FK_BED_ROOM + " INTEGER NOT NULL, "
            + " FOREIGN KEY (" + FK_BED_ROOM + ") REFERENCES " + TABLE_NAME_ROOMS + " (" + ROOM_ID + ")"
            + ");";

    //Cell or Squares Table
    public static final String TABLE_NAME_CELLS = "Cells";
    public static final String CELL_ID = "ce_id";
    public static final String CELL_COLUMN = "cellColumn";
    public static final String CELL_ROW = "cellRow";
    public static final String FK_CELL_BED = "bedId";
    //Create Cell Table Statement
    private static final String CREATE_TABLE_CELLS = "CREATE TABLE " + TABLE_NAME_CELLS + " ( "
            + CELL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CELL_COLUMN + " INTEGER NOT NULL, "
            + CELL_ROW + " INTEGER NOT NULL, "
            + FK_CELL_BED + " INTEGER NOT NULL, "
            + " FOREIGN KEY (" + FK_CELL_BED + ") REFERENCES " + TABLE_NAME_BEDS + " (" + BED_ID + ")"
            + ");";

    //Counts & Cells Table
    public static final String TABLE_NAME_COUNT_CELLS = "CountCells";
    public static final String COUNT_CELL_ID = "cc_Id";
    public static final String PR_COUNT_ID = "countId";
    public static final String PR_CELL_ID = "cellId";
    public static final String FK_BED_COUNT_CELL = "bedId";
    //Create Count & Cells Table
    private static final String CREATE_TABLE_COUNT_CELLS = "CREATE TABLE " + TABLE_NAME_COUNT_CELLS + " ( "
            + COUNT_CELL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PR_COUNT_ID + " INTEGER NOT NULL, "
            + PR_CELL_ID + " INTEGER NOT NULL, "
            + FK_BED_COUNT_CELL +  " INTEGER NOT NULL, "
            + " FOREIGN KEY (" + PR_COUNT_ID + ") REFERENCES " + TABLE_NAME_COUNTS + " (" + COUNT_ID + ")"
            + " FOREIGN KEY (" + PR_CELL_ID + ") REFERENCES " + TABLE_NAME_CELLS + " (" + CELL_ID + "), "
            + " FOREIGN KEY (" + FK_BED_COUNT_CELL + ") REFERENCES " + TABLE_NAME_BEDS + " (" + BED_ID + ") "
            + ");";

    //TODO Some way for weekly counts



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_FARMS);
        database.execSQL(CREATE_TABLE_PLANTS);
        database.execSQL(CREATE_TABLE_ROOMS);
        database.execSQL(CREATE_TABLE_BEDS);
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
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_BEDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_COUNTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CELLS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_COUNT_CELLS);
        onCreate(db);
    }


}
