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

public class MainActivity extends AppCompatActivity implements Observer {

    private DataServiceConnection serviceConnection;
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
        serviceConnection = new DataServiceConnection(new Handler(getMainLooper()));
        serviceConnection.registerObserver(this);
        bindService(intent, serviceConnection, 0);
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
    public void notify(double velocity, double bearing, double altitude) {}

    @Override
    public void notifyUpdated() {
        dataService = serviceConnection.dataService;
        dataService.getVelocityMeter().registerForLocationIfNeeded(this);
        dataService.getVelocityMeter().registerObserver(scoreView);
    }
}
