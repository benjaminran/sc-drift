package com.inboardhack.scdrift;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

/**
 * Created by benjaminran on 1/30/16.
 */
public class BluetoothBridge implements Runnable {

    static final int UPDATE_PERIOD_MS = 10;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mmDevice;
    UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    boolean connected = false;
    OutputStream mmOutputStream;
    InputStream mmInputStream;

    private ArrayList<byte[]> data;
    private static final String BT_MAC_ADDRESS = "98:D3:31:FB:20:2B";
    private static BluetoothBridge instance = null;
    private Handler mHandler;
    private double[] realAccel, worldAccel, rotation, gravity;
    private double speed;

    private BluetoothBridge(Handler mHandler) {
        this.mHandler = mHandler;
        realAccel = new double[3];
        worldAccel = new double[3];
        rotation = new double[9];
        gravity = new double[3];
        setUpBluetooth();
    }

    private void setUpBluetooth(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0) {
            for(BluetoothDevice device : pairedDevices) {
                Log.i("scd", device.toString());
            }
        }
        mmDevice = mBluetoothAdapter.getRemoteDevice(BT_MAC_ADDRESS);
        BluetoothSocket mmSocket = null;
        try {
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            connected = true;
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return connected;
    }

    private void processData(final BluetoothDevice device, final byte[] bytes) {
        String data = null;
        try {
            data = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Log.i("scd", "DATA:"+data);
    }

    // must be called first
    public static BluetoothBridge getInstance(StartActivity activity, Handler mHandler) {
        new FindBoard(activity).execute();
        instance = new BluetoothBridge(mHandler);
        return instance;
    }

    public static BluetoothBridge getInstance() {
        return instance;
    }

    public double[] getRealAccel() { return realAccel; }
    public double[] getWorldAccel() { return worldAccel; }
    public double[] getGravity() { return gravity; }
    public double[] getRotationData() { return rotation; }
    public double[] getDirection() { return new double[]{rotation[0],rotation[1],rotation[2]};}

    @Override
    public void run() {
        update();
        mHandler.postDelayed(this, UPDATE_PERIOD_MS);
    }

    private void update() {
        // read bluetooth
        String message = null;
        try { message = Utils.sanitizeInput(mmInputStream);} catch (IOException e) {e.printStackTrace();}
//        Log.d("scd", "MESSAGE:"+message);
        // parse message
        try{parseMessage(message);} catch(NumberFormatException e){e.printStackTrace();}
//        Log.i("scd", "Speed: "+speed);
        // update position
        if(Board.getInstance()==null) return;
        Board.getInstance().updatePosition(worldAccel, realAccel, speed, System.currentTimeMillis());
        Board.getInstance().setRotation(rotation);
        Slide slide = Board.getInstance().newSlide(System.currentTimeMillis());
        if(slide==null && Board.getInstance().getLastSlide()!=null) { // didn't just start sliding
            Board.getInstance().getLastSlide().incrementScore(Board.getInstance().getVelocity(), getDirection(), realAccel);
        }
        else if(slide!=null) { // just started sliding
            slide.incrementScore(Board.getInstance().getVelocity(), getDirection(), realAccel);
        }
        if(Board.getInstance().getLastSlide()!=null && Board.getInstance().getLastSlide().isComplete()) { // just finished sliding
            Slide lastSlide = Board.getInstance().getLastSlide();
            if(!SlideHistory.getInstance().contains(lastSlide)) {
                lastSlide.setEndTime(System.currentTimeMillis());
                SlideHistory.getInstance().add(lastSlide);

            }
        }
    }

    public void parseMessage(String message) throws NumberFormatException {
        if(message==null) return;
        String dataTag = message.substring(0,3);
        if(dataTag.equals("wac")) { // world-oriented accelerometer data (no gravity)
            String[] values = message.substring(4).split(",");
            worldAccel[0] = Double.parseDouble(values[0])/1671.8;
            worldAccel[1] = Double.parseDouble(values[1])/1671.8;
            worldAccel[2] = Double.parseDouble(values[2])/1671.8;//
        }
        else if(dataTag.equals("vel")) { // velocity
            double tmp = Double.parseDouble(message.substring(4));
//            if(tmp<13.5 && tmp>=0.0)
            speed = tmp;
            /*else {
                Log.d("scd", "Speed spiked at "+tmp+"m/s");
            }*/
        }
        else if(dataTag.equals("acc")) { // real acceleration (no gravity)
            String[] values = message.substring(4).split(",");
            realAccel[0] = Double.parseDouble(values[0])/1671.8;
            realAccel[1] = Double.parseDouble(values[1])/1671.8;
            realAccel[2] = Double.parseDouble(values[2])/1671.8;
        }
        else if(dataTag.equals("rot")) { // orientation (roll, pitch, yaw)
            String[] values = message.substring(4).split(",");
            rotation[0] = Double.parseDouble(values[0]);
            rotation[1] = Double.parseDouble(values[1]);
            rotation[2] = Double.parseDouble(values[2]);
        }
        else if(dataTag.equals("gyr")) { // angular velocity (roll, pitch, yaw)
            String[] values = message.substring(4).split(",");
            rotation[3] = Double.parseDouble(values[0]);
            rotation[4] = Double.parseDouble(values[1]);
            rotation[5] = Double.parseDouble(values[2]);
        }
    }
}
