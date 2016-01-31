package com.inboardhack.scdrift;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by benjaminran on 1/29/16.
 */
public class DataThread extends Thread {

    public Handler mHandler;
    private DataService dataService;

    public DataThread(DataService dataService) {
        super("DataThread");
        this.dataService = dataService;
    }

    public void run() {
        Looper.prepare();

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                // process incoming messages here
            }
        };

        Looper.loop();
    }
}
