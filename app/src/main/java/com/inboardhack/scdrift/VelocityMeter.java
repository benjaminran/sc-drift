package com.inboardhack.scdrift;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

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

    public Location currentLocation;
    public Location previousLocation;

    private double currentAltitude;
    private double currentTime;
    private double previousAltitude;
    private double previousTime;

    public VelocityMeter(DataService dataService) {
        observers = new ArrayList<Observer>();
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
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener, dataService.getDataThreadLooper());
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

    public boolean locationHasAll() {
        if(currentLocation==null) {
            Log.d("scd", "currentLocation is null");
            return false;
        }
        return currentLocation.hasAltitude() && currentLocation.hasSpeed() && currentLocation.hasBearing();
    }

    @Override
    public void onLocationChanged(Location location) {
        previousLocation = currentLocation;
        currentLocation = location;
        // update with new location
        Log.w("scd", "hasSpeed: "+location.hasSpeed()+"; hasBearing: "+location.hasBearing()+"; hasAltitude: "+location.hasAltitude());
        if(location.hasSpeed()) speed = location.getSpeed();
        if(location.hasBearing()) bearing = location.getBearing();
        if(location.hasAltitude()) {
            previousAltitude = currentAltitude;
            previousTime = currentTime;
            currentAltitude = location.getAltitude();
            currentTime = location.getTime();
            da = currentAltitude = previousAltitude;
            dt = (currentTime - previousTime)/1000.0;
        }
        Log.d("scd", "dt = "+dt);
        for(final Observer observer : observers) {
            dataService.getUiHandler().post(new Runnable() {
                @Override
                public void run() {
                    observer.observeUpdate(this);
                }
            });
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
