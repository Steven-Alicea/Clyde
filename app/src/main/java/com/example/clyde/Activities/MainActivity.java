package com.example.clyde.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.example.clyde.Adapter.HistoricalSiteAdapter;
import com.example.clyde.Database.ClydeDataSource;
import com.example.clyde.HistoricalSite.HistoricalSite;
import com.example.clyde.HistoricalSite.HistoricalSiteManager;
import com.example.clyde.Interface.InterfaceSiteOperations;
import com.example.clyde.Algorithm.ClydeAlgorithm;
import com.example.clyde.R;
import com.example.clyde.Utils.PreferenceUtils;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements InterfaceSiteOperations {

    private Button start;
    private ClydeDataSource dataSource;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private HistoricalSiteAdapter historicalSiteAdapter;
    private List<HistoricalSite> historicalSiteList, usersHistoricalSiteList;
    private RecyclerView recyclerView;
    private Switch switchMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.dataSource = new ClydeDataSource(this);
        openDatabase();
        bindControls();
        historicalSiteAdapter = new HistoricalSiteAdapter(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(historicalSiteAdapter);
        registerHandlers();
        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setTitle("Clyde");
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        historicalSiteAdapter.notifyDataSetChanged();
        historicalSiteAdapter = new HistoricalSiteAdapter(this);
        recyclerView.setAdapter(historicalSiteAdapter);
        usersHistoricalSiteList.clear();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                usersHistoricalSiteList.add(new HistoricalSite((long) 0.0,"user", location.getLatitude(), location.getLongitude(), 0));
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.getItem(0).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItemHelp:
                Intent email = new Intent(Intent.ACTION_SENDTO);
                email.setData(Uri.parse("mailto:steven.m.alicea@gmail.com, MrHilario@sandiego.gov, gsantiago1618@gmail.com, kyd940716@gmail.com"));
                email.putExtra(Intent.EXTRA_SUBJECT, "Clyde Support ");
                email.putExtra(Intent.EXTRA_TEXT, "Hello Team Clyde,");
                startActivity(email);
                break;
            case R.id.menuItemLogOut:
                PreferenceUtils.saveEmail(null,this);
                PreferenceUtils.savePassword(null, this);
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
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

    private void bindControls() {
        recyclerView = findViewById(R.id.recyclerView);
        start        = findViewById(R.id.btnStart);
        switchMode   = findViewById(R.id.switchMode);
        switchMode.setActivated(false);
        usersHistoricalSiteList     = HistoricalSiteManager.getHistoricalSiteList(getApplicationContext());
    }

    private void registerHandlers() {
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                if (switchMode.isActivated()) {
                    ArrayList<HistoricalSite> sortedArrayList = new ArrayList<>(ClydeAlgorithm.distanceClyde(usersHistoricalSiteList));
                    intent.putParcelableArrayListExtra("list", (ArrayList<? extends Parcelable>) sortedArrayList);
                    intent.putExtra("mode", true);
                }
                else if (!switchMode.isActivated()) {
                    usersHistoricalSiteList.remove(0);
                    intent.putParcelableArrayListExtra("list", (ArrayList<? extends Parcelable>) usersHistoricalSiteList);
                    intent.putExtra("mode", false);
                }
                startActivity(intent);
            }
        });

        switchMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!switchMode.isActivated()) {
                    switchMode.setActivated(true);
                    Toast.makeText(MainActivity.this, "Guided By Clyde On", Toast.LENGTH_SHORT).show();
                }
                else {
                    switchMode.setActivated(false);
                    Toast.makeText(MainActivity.this, "Guided By Clyde Off", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onItemSelected(int selectedItemIndex, View view) {
        CardView cardView  = (CardView) view;
        historicalSiteList = this.dataSource.getAllHistoricalSites();
        if (cardView.isActivated()){
            cardView.setCardBackgroundColor(Color.TRANSPARENT);
            removeHistoricalSite(historicalSiteList.get(selectedItemIndex), usersHistoricalSiteList);
            cardView.setActivated(false);
        }
        else{
            cardView.setCardBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
            usersHistoricalSiteList.add(historicalSiteList.get(selectedItemIndex));
            cardView.setActivated(true);
        }
    }

    @Override
    public List<HistoricalSite> onGetAllHistoricalSites() {
        return this.dataSource.getAllHistoricalSites();
    }

    public List<HistoricalSite> removeHistoricalSite(HistoricalSite historicalSite, List<HistoricalSite> historicalSiteList) {
        for (int i = 0; i < historicalSiteList.size(); i ++) {
            if (historicalSiteList.get(i).getId() == historicalSite.getId()){
                historicalSiteList.remove(historicalSiteList.get(i));
            }
        }
        return historicalSiteList;
    }
}
