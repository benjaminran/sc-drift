package com.inboardhack.scdrift;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements Observer {

    private DataServiceConnection serviceConnection;
    private DataService dataService;

    private ScoreView scoreView;
    private DataUpdater dataUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initUi();
        /*dataUpdater = new DataUpdater(new Handler(), scoreView);
        dataUpdater.run();*/
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
    public void observeUpdate(Object origin) {
        dataService = serviceConnection.dataService;
        dataService.getVelocityMeter().registerForLocationIfNeeded(this);
        dataService.getVelocityMeter().registerObserver(scoreView);
    }
}
