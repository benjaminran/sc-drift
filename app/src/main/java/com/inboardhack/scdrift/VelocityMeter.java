package com.inboardhack.scdrift;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
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

    private LocationManager locationManager;
    private ArrayList<Observer> observers;

    public VelocityMeter() {
        observers = new ArrayList<Observer>();
    }

    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void onLocationChanged(Location location) {
        // update with new location
        for(Observer observer : observers) {
            observer.notify(location.getSpeed());
        }
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
