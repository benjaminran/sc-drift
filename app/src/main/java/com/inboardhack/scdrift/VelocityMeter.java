package com.inboardhack.scdrift;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

/**
 * Created by benjaminran on 1/29/16.
 */
public class VelocityMeter implements LocationListener, android.location.LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private DataService dataService;

    private GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    private boolean registeredForLocation;
    private ArrayList<Observer> observers;

    public float speed;
    public float bearing;
    public double da;
    public double dt;

    public Location location;

    private double lastAltitude;
    private double lastTime;

    public VelocityMeter(DataService dataService) {
        observers = new ArrayList<Observer>();
        lastAltitude = 0;
        lastTime = System.currentTimeMillis();
        this.dataService = dataService;
        registeredForLocation = false;
    }

    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    public void registerForLocationIfNeeded2(Activity activity) {
        /*LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = dataService.getVelocityMeter();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener, dataService.getDataThreadLooper());
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener, dataService.getDataThreadLooper());*/
        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public void registerForLocationIfNeeded(Activity activity) {
        if(registeredForLocation) return;
        registeredForLocation = true;
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        android.location.LocationListener locationListener = dataService.getVelocityMeter();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener, dataService.getDataThreadLooper());
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener, dataService.getDataThreadLooper());
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest request = createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, request, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e("scd", "Couldn't connect to Google Play Services");
        throw new NullPointerException("Couldn't connect");
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
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
                    observer.observeUpdate(this);
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
