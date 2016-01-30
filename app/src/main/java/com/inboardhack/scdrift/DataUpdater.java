package com.inboardhack.scdrift;

import android.os.Handler;

/**
 * UNUSED; TODO: delete
 */
public class DataUpdater implements Runnable {

    private static final int UPDATE_PERIOD_MS = 500;
    private Handler handler;
    private BluetoothBridge bridge;

    public DataUpdater(Handler handler) {
        super();
        this.handler = handler;
        bridge = BluetoothBridge.getInstance();
    }

    @Override
    public void run() {
        update();
        handler.postDelayed(this, UPDATE_PERIOD_MS);
    }

    private void update() {
        double[] acceleration = null;
        double[] velocity = null;
        double[] rotation = null;
        double speed = 0;

    }
}
