package com.app.marketpal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecentlyViewerDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recently_viewed.db";
    private static final int DATABASE_VERSION = 1;

    // Table name and column names
    public static final String TABLE_RECENTLY_VIEWED = "recently_viewed";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_PRODUCT_ID = "product_id";


    // Create table query
    private static final String CREATE_TABLE_RECENTLY_VIEWED = "CREATE TABLE " +
            TABLE_RECENTLY_VIEWED + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_DATE + " TEXT, " +
            COLUMN_PRODUCT_ID + " INTEGER UNIQUE);";

    public RecentlyViewerDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the recently viewed table
        db.execSQL(CREATE_TABLE_RECENTLY_VIEWED);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }
}

