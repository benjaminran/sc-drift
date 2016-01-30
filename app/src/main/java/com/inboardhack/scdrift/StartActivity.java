package com.inboardhack.scdrift;

import android.content.Intent;
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

public class StartActivity extends AppCompatActivity implements View.OnClickListener, Observer {

    private int stage;

    private Board board;


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
    protected void onResume() {
        super.onResume();
        if(dataService!=null) dataService.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(dataService!=null) dataService.onPause();
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
        if(dataService==null) {
            Toast.makeText(this, "Please wait, data service still initializing", Toast.LENGTH_LONG).show();
        }
        if(stage==1) {
            if(notMoving() && dataService!=null) {
                board = Board.getInstance(dataService);
                button.setText("Finish");
                directions.setText("Ride your board straight forward then click the button again and you're done.");
                stage++;
            }
        }
        else if(stage==2) {
            // TODO: finish callibration
            board.calibrate(dataService.getVelocityMeter().bearing);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void observeUpdate(Object origin) { // onServiceBound
        dataService = serviceConnection.dataService;
        dataService.bridge = BluetoothBridge.getInstance(this, dataService.dataThread.mHandler);
        dataService.bridge.run();
        dataService.getVelocityMeter().registerForLocationIfNeeded(this);
    }
}
