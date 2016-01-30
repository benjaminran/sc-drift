package com.inboardhack.scdrift;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;

/**
 * Created by benjaminran on 1/29/16.
 */
public class DataService extends Service {

    private Handler uiHandler;
    private DataServiceBinder binder;
    private DataThread dataThread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        handleIntent(intent);
        binder = new DataServiceBinder();
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_NOT_STICKY;
    }

    private void handleIntent(Intent intent) {
        if(dataThread==null) {
            dataThread = new DataThread(this);
            dataThread.start();
        }
    }

    public VelocityMeter getVelocityMeter() { return dataThread.getVelocityMeter(); }
    public Looper getDataThreadLooper() { return dataThread.mHandler.getLooper(); }

    public Handler getUiHandler() { return uiHandler; }
    public void setUiHandler(Handler uiHandler) { this.uiHandler = uiHandler; }

    public class DataServiceBinder extends Binder {
        DataService getService() {
            return DataService.this;
        }
    }
}
