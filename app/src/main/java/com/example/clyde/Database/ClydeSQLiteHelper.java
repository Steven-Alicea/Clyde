package com.example.clyde.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ClydeSQLiteHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "clyde.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USERS                 = "users";
    private static final String USER_ID                     = "user_id";
    private static final String COLUMN_LAST_NAME            = "last_name";
    private static final String COLUMN_FIRST_NAME           = "first_name";
    private static final String COLUMN_MIDDLE_NAME          = "middle_name";
    private static final String COLUMN_EMAIL                = "email";
    private static final String COLUMN_PASSWORD             = "password";
    private static final String COLUMN_PHONE_NUMBER         = "phone_number";
    private static final String COLUMN_LAST_KNOWN_LATITUDE  = "last_known_latitude";
    private static final String COLUMN_LAST_KNOWN_LONGITUDE = "last_known_longitude";

    private static final String TABLE_HISTORICAL_SITES      = "historical_sites";
    private static final String HISTORICAL_SITE_ID          = "historical_site_id";
    private static final String COLUMN_SITE_NAME            = "site_name";
    private static final String COLUMN_LATITUDE             = "latitude";
    private static final String COLUMN_LONGITUDE            = "longitude";
    private static final String COLUMN_NUMBER_OF_VISITS     = "number_of_visits";

    private static final String TABLE_USERS_CREATE = "create table " + TABLE_USERS
            + "( "
            + USER_ID + " integer primary key autoincrement, "
            + COLUMN_LAST_NAME + " text not null, "
            + COLUMN_FIRST_NAME + " text not null, "
            + COLUMN_MIDDLE_NAME + " text, "
            + COLUMN_EMAIL + " text not null, "
            + COLUMN_PASSWORD + " text not null, "
            + COLUMN_PHONE_NUMBER + " text, "
            + COLUMN_LAST_KNOWN_LATITUDE + " long, "
            + COLUMN_LAST_KNOWN_LONGITUDE + " long "
            + ");";

    private static final String TABLE_HISTORICAL_SITES_CREATE = "create table " + TABLE_HISTORICAL_SITES
            + "( "
            + HISTORICAL_SITE_ID + " integer primary key autoincrement, "
            + COLUMN_SITE_NAME + " text not null, "
            + COLUMN_LATITUDE + " double not null, "
            + COLUMN_LONGITUDE + " double not null, "
            + COLUMN_NUMBER_OF_VISITS + " integer not null "
            + ");";

    ClydeSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TABLE_USERS_CREATE);
        database.execSQL(TABLE_HISTORICAL_SITES_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String warningMessage = String.format("Upgrading database from V%d to V%d, which will destroy all old data", oldVersion, newVersion);
        Log.w(ClydeSQLiteHelper.class.getName(), warningMessage);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORICAL_SITES);
        onCreate(sqLiteDatabase);
    }
}
