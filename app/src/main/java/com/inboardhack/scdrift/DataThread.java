package com.inboardhack.scdrift;

import android.content.Context;

/**
 * Created by benjaminran on 1/29/16.
 */
public class DataThread extends Thread {

    private DataService dataService;

    private VelocityMeter velocityMeter;

    public DataThread(Context context, DataService dataService) {
        this.dataService = dataService;
        velocityMeter = new VelocityMeter(context);
    }

    public void run() {

    }
}
