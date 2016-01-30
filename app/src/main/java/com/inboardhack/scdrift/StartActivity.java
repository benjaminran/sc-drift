package com.inboardhack.scdrift;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class StartActivity extends AppCompatActivity implements View.OnClickListener, Observer {

    private int stage;

    private BluetoothBridge bridge;
    private DataService dataService;
    private DataServiceConnection serviceConnection;

    private Button button;
    private TextView directions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.start_toolbar);
        setSupportActionBar(toolbar);

        bridge = BluetoothBridge.getInstance(this);

        stage = 1;

        button = (Button) findViewById(R.id.start_button);
        directions = (TextView) findViewById(R.id.start_directions);
        button.setOnClickListener(this);

        serviceConnection = new DataServiceConnection(new Handler(getMainLooper()));
        serviceConnection.registerObserver(this);
        Intent intent = new Intent(this, DataService.class);
        startService(intent);
        bindService(intent, serviceConnection, 0);
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



    private boolean notMoving() {
        return true; // TODO: confirm that not moving
    }

    @Override
    public void onClick(View v) {
        if(stage==1) {
            if(notMoving()) {

                button.setText("Finish");
                directions.setText("Ride your board straight forward then click the button again and you're done.");
                stage++;
            }
        }
        else if(stage==2) {
            // finish callibration
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void notify(double velocity, double bearing, double altitude) {}

    @Override
    public void notifyUpdated() {
        dataService = serviceConnection.dataService;
        dataService.getVelocityMeter().registerForLocationIfNeeded(this);
    }
}
