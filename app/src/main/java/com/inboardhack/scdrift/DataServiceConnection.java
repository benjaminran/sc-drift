package com.inboardhack.scdrift;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by benjaminran on 1/30/16.
 */
public class DataServiceConnection implements ServiceConnection {

    private ArrayList<Observer> observers;
    Handler mainHandler;
    DataService dataService;

    public DataServiceConnection(Handler mainHandler) {
        this.mainHandler = mainHandler;
        observers = new ArrayList<>();
    }

    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d("scd", "DataService connected");
        dataService = ((DataService.DataServiceBinder) service).getService();
        dataService.setUiHandler(mainHandler);
        for(Observer observer : observers)
            observer.notifyUpdated();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d("scd", "DataService disconnected");
    }
}
