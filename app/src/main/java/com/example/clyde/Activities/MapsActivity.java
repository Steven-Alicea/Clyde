package com.example.clyde.Activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clyde.Database.ClydeDataSource;
import com.example.clyde.GeofenceService.GeofenceTransitionService;
import com.example.clyde.HistoricalSite.HistoricalSite;
import com.example.clyde.R;
import com.example.clyde.Utils.PermissionUtils;
import com.example.clyde.Utils.PreferenceUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final float GEOFENCE_RADIUS = 100.0f;
    private final int GEOFENCE_REQ_CODE = 0;
    private boolean mPermissionDenied = false;
    private boolean mode;
    private ArrayList<HistoricalSite> historicalSiteList;
    private Circle geoFenceLimits;
    private ClydeDataSource dataSource;
    private GoogleMap map;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest locationRequest;
    private Marker userMarker, clydeGeoFenceMarker;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String userEmail;
    private TextView textLat, textLong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        historicalSiteList = getIntent().getParcelableArrayListExtra("list");
        mode = getIntent().getBooleanExtra("mode", mode);
        this.dataSource = new ClydeDataSource(this);
        openDatabase();
        bindControls();
        initGMaps();
        createGoogleApi();
    }

    private void bindControls() {
        userEmail = PreferenceUtils.getEmail(this);
        textLat   = findViewById(R.id.lat);
        textLong  = findViewById(R.id.lon);
    }

    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        googleApiClient = new GoogleApiClient.Builder( this )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();
        historicalSiteList.clear();
        clearGeofence();
    }

    @Override
    protected void onDestroy() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.getItem(2).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItemEndTour:
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                historicalSiteList.clear();
                clearGeofence();
                break;
            case R.id.menuItemHelp:
                Intent email = new Intent(Intent.ACTION_SENDTO);
                email.setData(Uri.parse("mailto:steven.m.alicea@gmail.com, MrHilario@sandiego.gov, gsantiago1618@gmail.com, kyd940716@gmail.com"));
                email.putExtra(Intent.EXTRA_SUBJECT, "Clyde Support ");
                email.putExtra(Intent.EXTRA_TEXT, "Hello Team Clyde,");
                startActivity(email);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }
        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            mPermissionDenied = false;
            getLastKnownLocation();
        } else {
            mPermissionDenied = true;
        }
    }

    private void initGMaps(){
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.getTitle();
        playAudioDescription(marker.getTitle());
    return false;
    }

    private void playAudioDescription(String geofence) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        if (geofence.contains("Gaslamp Quarter")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.gaslamp_quarter);
        } else if (geofence.contains("Hotel Del Coronado")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.hotel_del_coronado);
        } else if (geofence.contains("National University Kerney Campus")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.national_university);
        } else if (geofence.contains("Natural History Museum")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.natural_history_museum);
        } else if (geofence.contains("Saint Francis Chapel")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.saint_francis_chapel);
        } else if (geofence.contains("Spreckels Organ Pavilion")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.spreckels_organ_pavilion);
        } else if (geofence.contains("Star of India")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.star_of_india);
        } else if (geofence.contains("Timken Museum of Art")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.timken_museum);
        } else if (geofence.contains("USS Midway")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.uss_midway);
        } else if (geofence.contains("Santiago's House")) {
            mediaPlayer = MediaPlayer.create(this, R.raw.national_university);
        }
        mediaPlayer.start();

    }

    private void startLocationUpdates(){
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)
                .setFastestInterval(2000);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        markerForUserLocation(new LatLng(location.getLatitude(), location.getLongitude()));
        this.dataSource.updateUserLocation(userEmail, location.getLatitude(), location.getLongitude());
        textLat.setText("Lat: " + location.getLatitude());
        textLong.setText("Long: " + location.getLongitude());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected()");
        getLastKnownLocation();
        startGeofence(historicalSiteList);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    // Get last known location
    private void getLastKnownLocation() {
        lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastLocation != null)
            startLocationUpdates();
        else {
            Log.w(TAG, "No location retrieved yet");
            startLocationUpdates();
        }
    }

    private void markerForUserLocation(LatLng latLng) {
        String title = "User Location (" + latLng.latitude + ", " + latLng.longitude + ")";
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(title);
        if (map!=null) {
            if (userMarker != null)
                userMarker.remove();
            userMarker = map.addMarker(markerOptions);
            float zoom = 15f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            map.animateCamera(cameraUpdate);
        }
    }

    private void markerForGeofence(HistoricalSite historicalSite, int number) {
        String title = null;
        LatLng latLng = new LatLng(historicalSite.getLatitude(), historicalSite.getLongitude());
        if (mode)
            title = ++number + ". " + historicalSite.getName();
        else if (!mode)
            title = historicalSite.getName();
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title(title);
        clydeGeoFenceMarker = map.addMarker(markerOptions);
    }

    private void startGeofence(ArrayList<HistoricalSite> historicalSiteList) {
        Log.i(TAG, "startGeofence()");
        if( historicalSiteList.isEmpty() ) {
            Log.e(TAG, "historicalSiteList is empty");
        } else {
            for (int i = 0; i < historicalSiteList.size(); i++) {
                HistoricalSite historicalSite = historicalSiteList.get(i);
                markerForGeofence(historicalSite, i);
                drawGeofence();
                Geofence geofence = createGeofenceHistoricalSite(historicalSite, GEOFENCE_RADIUS);
                GeofencingRequest geofenceRequest = createGeofenceRequest(geofence);
                addGeofenceRequest(geofenceRequest);
            }
        }
    }

    private Geofence createGeofenceHistoricalSite(HistoricalSite historicalSite, float radius) {
        Log.d(TAG, "createGeofenceHistoricalSite");
        return new Geofence.Builder()
                .setRequestId(historicalSite.getName())
                .setCircularRegion( historicalSite.getLatitude(), historicalSite.getLongitude(), radius)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .build();
    }

    private GeofencingRequest createGeofenceRequest(Geofence geofence) {
        Log.d(TAG, "createGeofenceRequest");
        return new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();
    }

    private PendingIntent geoFencePendingIntent;
    private PendingIntent createGeofencePendingIntent() {
        if (geoFencePendingIntent != null)
            return geoFencePendingIntent;
        Intent intent = new Intent(this, GeofenceTransitionService.class);
        return PendingIntent.getService(this, GEOFENCE_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT );
    }

    private void addGeofenceRequest(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");
        LocationServices.GeofencingApi.addGeofences(googleApiClient, request, createGeofencePendingIntent()).setResultCallback(null);
    }

    private void drawGeofence() {
        CircleOptions circleOptions = new CircleOptions()
                .center(clydeGeoFenceMarker.getPosition())
                .strokeColor(Color.argb(50, 70,70,70))
                .fillColor(Color.argb(100, 150,150,150))
                .radius(GEOFENCE_RADIUS);
        geoFenceLimits = map.addCircle(circleOptions);
    }

    private void clearGeofence() {
        Log.d(TAG, "clearGeofence()");
        for (int i = 0; i < historicalSiteList.size(); i++) {
            LocationServices.GeofencingApi.removeGeofences(googleApiClient, createGeofencePendingIntent());
        }
        map.clear();
    }
}