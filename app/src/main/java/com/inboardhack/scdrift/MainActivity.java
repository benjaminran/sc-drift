package com.inboardhack.scdrift;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private DataService dataService;

    private ScoreView scoreView;
    private ScoreViewUpdater scoreViewUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initUi();
        scoreViewUpdater = new ScoreViewUpdater(new Handler(), scoreView);
        scoreViewUpdater.run();
        Intent intent = new Intent(this, DataService.class);
        startService(intent);
        bindService(intent, this, 0);
    }

    private void registerForLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = dataService.getVelocityMeter();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener, dataService.getDataThreadLooper());
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener, dataService.getDataThreadLooper());
    }

    private void initUi() {
        scoreView = (ScoreView) findViewById(R.id.score_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d("scd", "DataService connected");
        dataService = ((DataService.DataServiceBinder) service).getService();
        dataService.setUiHandler(new Handler(getMainLooper()));
        registerForLocation();
        dataService.getVelocityMeter().registerObserver(scoreView);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d("scd", "DataService disconnected");
    }
}
