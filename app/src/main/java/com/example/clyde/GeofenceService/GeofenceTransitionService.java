package com.example.clyde.GeofenceService;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.media.MediaPlayer;
import android.util.Log;
import android.widget.Toast;

import com.example.clyde.Database.ClydeDataSource;
import com.example.clyde.R;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.ArrayList;
import java.util.List;

public class GeofenceTransitionService extends IntentService {

    private static final String TAG = GeofenceTransitionService.class.getSimpleName();
    private ClydeDataSource dataSource;
    private MediaPlayer mediaPlayer;

    @Override
    public void onDestroy() {
        closeDatabase();
        super.onDestroy();
    }

    private boolean openDatabase() {
        try {
            this.dataSource.open();
            return true;
        }
        catch (SQLiteException ex) {
            Toast.makeText(this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void closeDatabase() {
        try {
            this.dataSource.close();
        }
        catch (SQLiteException ex) {
            Toast.makeText(this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public GeofenceTransitionService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.dataSource = new ClydeDataSource(this);
        openDatabase();
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMsg = getErrorString(geofencingEvent.getErrorCode() );
            Log.e( TAG, errorMsg );
            return;
        }
        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String geofenceTransitionDetails = getGeofenceTrasitionDetails(geoFenceTransition, triggeringGeofences );
            if (geofenceTransitionDetails == "Entering"){
                System.out.println("ENTERING: " + geofencingEvent.getTriggeringGeofences().get(0).getRequestId());
                playWelcomeBeep();
                dataSource.updateVisitToHistoricalSite(geofencingEvent.getTriggeringGeofences().get(0).getRequestId());
            }
            else if (geofenceTransitionDetails == "Exiting") {
                //todo REMOVE GEOFENCE
            }
        }
    }

    private void playWelcomeBeep() {
        mediaPlayer = MediaPlayer.create(this, R.raw.welcome);
        mediaPlayer.start();
    }

    private String getGeofenceTrasitionDetails(int geoFenceTransition, List<Geofence> triggeringGeofences) {
        ArrayList<String> triggeringGeofencesList = new ArrayList<>();
        for (Geofence geofence : triggeringGeofences)
            triggeringGeofencesList.add(geofence.getRequestId()) ;
        String status = null;
        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER)
            status = "Entering";
        else if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT)
            status = "Exiting";
        return status;
    }

    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }
}
