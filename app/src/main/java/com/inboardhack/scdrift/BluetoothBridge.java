package com.inboardhack.scdrift;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import co.lujun.lmbluetoothsdk.BluetoothController;
import co.lujun.lmbluetoothsdk.base.BluetoothListener;
import co.lujun.lmbluetoothsdk.base.State;

/**
 * Created by benjaminran on 1/30/16.
 */
public class BluetoothBridge implements SensorEventListener, Runnable {



    private List<String> mList;


    private static final int ENABLE_BT = 3;

    public static BluetoothClass.Device device = null;
    private BluetoothAdapter mBluetoothAdapter;

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

        setUpBluetooth(context);

        onResume();
    }

    private void setUpBluetooth(final Context context) {
        BluetoothController mBTController = BluetoothController.getInstance().build(context);
        mBTController.setAppUuid(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        mBTController.setBluetoothListener(new BluetoothListener() {

            @Override
            public void onActionStateChanged(int preState, int state) {
                Log.d("scd", "bluetooth service state:" + state);
                if (state == State.STATE_CONNECTED) {
                    //Intent intent = new Intent(ClassicBluetoothActivity.this, ChatActivity.class);
                    //startActivityForResult(intent, 4);
                }
            }

            @Override
            public void onActionDiscoveryStateChanged(String discoveryState) {
                // Callback when local Bluetooth adapter discovery process state changed.
                if (discoveryState.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                    Toast.makeText(context, "scanning!", Toast.LENGTH_SHORT).show();
                } else if (discoveryState.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                    Toast.makeText(context, "scan finished!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onActionScanModeChanged(int preScanMode, int scanMode) {
                // Callback when the current scan mode changed.
                Log.d("scd", "preScanMode:" + preScanMode + ", scanMode:" + scanMode);
            }

            @Override
            public void onBluetoothServiceStateChanged(int state) {
                // Callback when the connection state changed.
                Log.d("scd", "bluetooth service state:" + state);
                if (state == State.STATE_CONNECTED) {
                    //Intent intent = new Intent(context, ChatActivity.class);
                    //startActivityForResult(intent, 4);
                }
            }

            @Override
            public void onActionDeviceFound(BluetoothDevice device) {
                // Callback when found device.
                mList.add(device.getName() + "@" + device.getAddress());

            }

            @Override
            public void onReadData(final BluetoothDevice device, final byte[] data) {
                // Callback when remote device send data to current device.
                processData(device, data);
            }
        });
        mBTController.startAsServer();
    }

    private void processData(final BluetoothDevice device, final byte[] data) {
        Log.d("scd", data.toString());
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
        /*else if(event.sensor==accelerometer) {
            linearAccelerometerData[0] = event.values[0];
            linearAccelerometerData[1] = event.values[1] * -1;
            linearAccelerometerData[2] = event.values[2];
        }*/
    }

    public double[] getRealAccel() { return accelerometerData; }
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
