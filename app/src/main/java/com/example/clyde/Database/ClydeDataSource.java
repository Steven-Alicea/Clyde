package com.example.clyde.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.clyde.HistoricalSite.HistoricalSite;
import com.example.clyde.User.User;

import java.util.LinkedList;
import java.util.List;

public class ClydeDataSource {

    private ClydeSQLiteHelper clydeSQLiteHelper;
    private SQLiteDatabase database;

    public ClydeDataSource(Context context) {
        this.clydeSQLiteHelper = new ClydeSQLiteHelper(context);
        this.database = null;
    }

    public void open() throws SQLiteException {
        this.database = this.clydeSQLiteHelper.getWritableDatabase();
    }

    public void close() {
        this.database.close();
        this.clydeSQLiteHelper.close();
    }

    public void insertUser(User user) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("last_name", user.getLastName());
        contentValues.put("first_name", user.getFirstName());
        contentValues.put("middle_name", user.getMiddleName());
        contentValues.put("phone_number", user.getPhoneNumber());
        contentValues.put("email", user.getEmail());
        contentValues.put("password", user.getPassword());
        contentValues.put("last_known_latitude", user.getLatitude());
        contentValues.put("last_known_longitude", user.getLongitude());
        database.insert("users", null, contentValues);
    }

    public boolean getUserEmail(String email) {
        Cursor cursor = database.rawQuery("select email from " + "users" + " where " + "email" + " =  ?", new String[] {email});
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        else {
            cursor.close();
            return false;
        }
    }

    public boolean getUserEmailAndPassword(String email, String password) {
        Cursor cursor = database.rawQuery("select * from users where email = ? and password = ?", new String[] {email, password});
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        else {
            cursor.close();
            return false;
        }
    }

    public int getNumberOfVisitsForHistoricalSite(String name) {
        Cursor cursor = database.rawQuery("select number_of_visits from historical_sites where site_name = ?", new String[] {name});
        if (cursor != null)
            cursor.moveToFirst();
        int numberOfVisits = cursor.getInt(0);
        return numberOfVisits;
    }

    public void updateUserLocation( String email, double latitude, double longitude) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("last_known_latitude", latitude);
        contentValues.put("last_known_longitude", longitude);
        database.update("users", contentValues, "email = ?", new String[] {email});
        System.out.println("ClydeDataSource.updateUserLocation() : " + latitude + ", " + longitude);
    }

    public void updateVisitToHistoricalSite(String historicalSite) {
        int visitsIncremented = getNumberOfVisitsForHistoricalSite(historicalSite) + 1;
        ContentValues contentValues = new ContentValues();
        contentValues.put("number_of_visits", visitsIncremented);
        database.update("historical_sites", contentValues, "site_name = ?", new String [] {historicalSite});
        System.out.println(historicalSite + " : VISITS INCREMENTED = " + getNumberOfVisitsForHistoricalSite(historicalSite));
    }

    public void insertHistoricalSite(String name, double latitude, double longitude, int numberOfVisits) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("site_name", name);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        contentValues.put("number_of_visits", numberOfVisits);
        database.insert("historical_sites", null, contentValues);
        System.out.println("insertHistoricalSite() (SITE, VISITS) " + name + " , " + numberOfVisits);
    }

    public List<HistoricalSite> getAllHistoricalSites() {
        String columnNames[] = {"historical_site_id", "site_name", "latitude", "longitude", "number_of_visits"};
        List<HistoricalSite> historicalSiteList = new LinkedList<HistoricalSite>();
        Cursor cursor = database.query("historical_sites", columnNames, null, null, null, null, "number_of_visits" + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int columnNameIndex = 0;
            int id = cursor.getInt(columnNameIndex++);
            String name = cursor.getString(columnNameIndex++);
            double latitude = cursor.getDouble(columnNameIndex++);
            double longitude = cursor.getDouble(columnNameIndex++);
            int numberOfVisits = cursor.getInt(columnNameIndex++);
            HistoricalSite historicalSite = new HistoricalSite(id, name, latitude, longitude, numberOfVisits);
            historicalSiteList.add(historicalSite);
            cursor.moveToNext();
        }
        if (cursor.getCount() > 0) {
            cursor.close();
            return  historicalSiteList;
        }
        else {
            insertHistoricalSite("National University Kearney Campus", 32.808793, -117.149083, 87);
            insertHistoricalSite("Saint Francis Chapel", 32.7312448, -117.1523714, 10);
            insertHistoricalSite("Timken Museum of Art", 32.7319304, -117.1500066,34);
            insertHistoricalSite("Spreckels Organ Pavilion", 32.7293545, -117.1503352, 12);
            insertHistoricalSite("Natural History Museum", 32.7323223, -117.1495527, 67);
            insertHistoricalSite("USS Midway", 32.7139995, -117.174993, 86);
            insertHistoricalSite("Gaslamp Quarter", 32.7111876, -117.1646042, 89);
            insertHistoricalSite("Star of India", 32.7203428, -117.175755, 88);
            insertHistoricalSite("Hotel Del Coronado", 32.6808631, -117.1804441, 57);
            getAllHistoricalSites();
            }
            cursor.close();
        return historicalSiteList;
    }
}
