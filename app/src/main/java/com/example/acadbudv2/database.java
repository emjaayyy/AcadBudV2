package com.example.acadbudv2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AcadBudV2.db";
    private static final int DATABASE_VERSION = 1;

    public database (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create your database tables and define their structure here.
        String createTableSQL = "CREATE TABLE IF NOT EXISTS your_table_name ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT);";
        db.execSQL(createTableSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database schema upgrades here if needed.
    }
}
