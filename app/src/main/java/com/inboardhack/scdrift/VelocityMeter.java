package com.inboardhack.scdrift;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by benjaminran on 1/29/16.
 */
public class VelocityMeter implements LocationListener {

    private DataService dataService;

    private LocationManager locationManager;
    private ArrayList<Observer> observers;

    private double lastAltitude;
    private double lastTime;

    public VelocityMeter(DataService dataService) {
        observers = new ArrayList<Observer>();
        lastAltitude = 0;
        lastTime = System.currentTimeMillis();
        this.dataService = dataService;
    }

    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void onLocationChanged(Location location) {
        // update with new location
        if(!location.hasSpeed() || !location.hasBearing() || !location.hasAltitude())
            Log.w("scd", "hasSpeed: "+location.hasSpeed()+"; hasBearing: "+location.hasBearing()+"; hasAltitude: "+location.hasAltitude());
        final double speed = location.getSpeed();
        final double bearing = location.getBearing();
        final double altitude = location.getAltitude();
        long time = location.getTime();
        Log.d("scd", "dt = "+(time-lastTime));
        Log.d("scd", "current thread: "+Thread.currentThread());
        for(final Observer observer : observers) {
            dataService.getUiHandler().post(new Runnable() {
                @Override
                public void run() {
                    observer.notify(speed, bearing, altitude);
                }
            });
        }

        lastAltitude = altitude;
        lastTime = time;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
