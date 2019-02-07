package com.neliry.db_sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBPages  extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "pageDB";
    public static final String TABLE_PAGES = "pagesList";

    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_DATE = "date";
    public static final String KEY_PARENT = "parent";

    public DBPages(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_PAGES + "(" + KEY_ID
                + " integer primary key," + KEY_NAME + " text," + KEY_DATE + " datetime,"+KEY_PARENT+" text" + ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase datab, int oldVersion, int newVersion) {
        datab.execSQL("drop table if exists " + TABLE_PAGES);

        onCreate(datab);

    }
}
