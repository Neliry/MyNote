package com.neliry.db_sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;

public class DBHelper  extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "editorDB_4";
    public static final String TABLE_CHAPTERS = "chapters";
    public static final String TABLE_PAGES = "pagesList";
    public static final String TABLE_VIEWS = "views";

    public static final String KEY_ID = "_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DATE = "date";
    public static final String KEY_PARENT = "parent";

    public static final String KEY_CONTENT = "content";
    public static final String KEY_WIDTH = "width";
    public static final String KEY_HEIGHT="height";
    public static final String KEY_X ="x";
    public static final String KEY_Y="y";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String CREATE_TABLE_CHAPTERS = "CREATE TABLE " + TABLE_CHAPTERS
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
            + KEY_DATE + " DATETIME," +KEY_PARENT+" TEXT"+ ")";
    private static final String CREATE_TABLE_PAGES = "CREATE TABLE " + TABLE_PAGES
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
            + KEY_DATE + " DATETIME," +KEY_PARENT+" TEXT"+ ")";
    private static final String CREATE_TABLE_VIEWS = "CREATE TABLE " + TABLE_VIEWS
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
            + KEY_CONTENT + " TEXT," +KEY_WIDTH + " INT," +KEY_HEIGHT + " INT," +KEY_X + " INT," +KEY_Y + " INT,"+ KEY_DATE + " TEXT,"  +KEY_PARENT+" TEXT"+ ")";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CHAPTERS);
        db.execSQL(CREATE_TABLE_PAGES);
        db.execSQL(CREATE_TABLE_VIEWS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAPTERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIEWS);
        onCreate(db);

    }
    public void createChapter(String name, String date, String parent) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME,name);
        values.put(KEY_DATE, date);
        values.put(KEY_PARENT, parent);

        // insert row
        db.insert(TABLE_CHAPTERS, null, values);

    }

    public long createPage(String name, String date, String parent) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME,name);
        values.put(KEY_DATE, date);
        values.put(KEY_PARENT, parent);

        // insert row
        long id = db.insert(TABLE_PAGES, null, values);
        return id;
    }

    public long createView(String parentPageId, String date, String content, int height, int width, int x, int y, String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBHelper.KEY_NAME, name);
        values.put(DBHelper.KEY_PARENT, parentPageId);
        values.put(DBHelper.KEY_CONTENT, content);
        values.put(DBHelper.KEY_HEIGHT, height);
        values.put(DBHelper.KEY_WIDTH, width);
        values.put(DBHelper.KEY_X, x);
        values.put(DBHelper.KEY_Y, y);
        values.put(DBHelper.KEY_DATE, date);
        // insert row
        long lastId;
        lastId=db.insert(TABLE_VIEWS, null, values);
        return lastId;
    }
}