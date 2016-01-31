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

    public DataService dataService;
    private DataServiceConnection serviceConnection;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.start_toolbar);
        setSupportActionBar(toolbar);

        button = (Button) findViewById(R.id.start_button);
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
        if(dataService==null) {
            Toast.makeText(this, "Please wait, data service still initializing", Toast.LENGTH_LONG).show();
        }

        if(notMoving()) {
            Board.getInstance(dataService);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void observeUpdate(Object origin) { // onServiceBound
        dataService = serviceConnection.dataService;
        BluetoothBridge.getInstance(this, dataService.dataThread.mHandler).run();
    }
}
