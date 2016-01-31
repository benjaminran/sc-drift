package com.inboardhack.scdrift;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Handler;

import java.util.ArrayList;

/**
 * Created by benjaminran on 1/30/16.
 */
public class BluetoothBridge implements SensorEventListener, Runnable {

    private ArrayList<Observer> observers;

    private static BluetoothBridge instance = null;
    private Handler mHandler;
    private SensorManager mSensorManager;
    private Sensor accelerometer, gyroscope, gravity, orientation, linearAccelerometer;

    private double[] accelerometerData, rotationData, gravityData, linearAccelerometerData;

    private BluetoothBridge(Context context, Handler mHandler) {
        this.mHandler = mHandler;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        orientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        linearAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        accelerometerData = new double[3];
        linearAccelerometerData = new double[3];
        rotationData = new double[9];

        gravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        gravityData = new double[3];
    }

    public void registerObserver(Observer o) {
        if(observers==null) observers = new ArrayList<>();
        observers.add(o);
    }

    // must be called first
    public static BluetoothBridge getInstance(StartActivity activity, Handler mHandler) {
        new FindBoard(activity).execute();
        instance = new BluetoothBridge(activity, mHandler);
        return instance;
    }

    public static BluetoothBridge getInstance() {
        return instance;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor==accelerometer) {
            accelerometerData[0] = event.values[0];
            accelerometerData[1] = event.values[1] * -1;
            accelerometerData[2] = event.values[2];
        }
        else if(event.sensor==gyroscope) {
            rotationData[3] = event.values[0];
            rotationData[4] = event.values[1] * -1;
            rotationData[5] = event.values[2];
        }
        else if(event.sensor==gravity) {
            gravityData[0] = event.values[0];
            gravityData[1] = event.values[1] * -1;
            gravityData[2] = event.values[2];
        }
        else if(event.sensor==orientation) {
            rotationData[0] = event.values[0];
            rotationData[1] = event.values[1]*-1;
            rotationData[2] = event.values[2];
        }
        else if(event.sensor==linearAccelerometer) {
            linearAccelerometerData[0] = event.values[0];
            linearAccelerometerData[1] = event.values[1] * -1;
            linearAccelerometerData[2] = event.values[2];
        }
    }

    public double[] getRealAccel() { return linearAccelerometerData; }
    public double[] getWorldAccel() { return accelerometerData; }
    public double[] getGravity() { return gravityData; }
    public double[] getRotationData() { return rotationData; }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    protected void onResume() {
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, orientation, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, linearAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void run() {
        update();
        mHandler.post(this);//, UPDATE_PERIOD_MS);
    }

    private void update() {
        if(observers==null) return;
        for(Observer o : observers)
            o.observeUpdate(this);
    }
}
