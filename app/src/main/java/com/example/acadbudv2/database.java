package com.example.acadbudv2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "AcadBudV2.db";
    private static final int DATABASE_VERSION = 1;

    public database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create your database tables and define their structure here.
        String createTableSQL = "CREATE TABLE IF NOT EXISTS your_table_name ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "lrn TEXT, "
                + "name TEXT, "
                + "email TEXT, "
                + "password TEXT);";
        db.execSQL(createTableSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database schema upgrades here if needed.
    }

    public List<users> getUsers() {
        List<users> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                "your_table_name",  // Replace with your actual table name
                new String[] { "id", "lrn", "name", "email", "password" },
                null,
                null,
                null,
                null,
                null
        );

        int idColumnIndex = cursor.getColumnIndex("id");
        int lrnColumnIndex = cursor.getColumnIndex("lrn");
        int nameColumnIndex = cursor.getColumnIndex("name");
        int emailColumnIndex = cursor.getColumnIndex("email");
        int passwordColumnIndex = cursor.getColumnIndex("password");

        while (cursor.moveToNext()) {
            users user = new users();
            user.setId(cursor.getInt(idColumnIndex));
            user.setLrn(cursor.getString(lrnColumnIndex));
            user.setName(cursor.getString(nameColumnIndex));
            user.setEmail(cursor.getString(emailColumnIndex));
            user.setPassword(cursor.getString(passwordColumnIndex));
            userList.add(user);
        }

        cursor.close();
        db.close();

        return userList;
    }
}
