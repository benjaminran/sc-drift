package com.inboardhack.scdrift;

import android.os.Handler;

/**
 * Created by benjaminran on 1/29/16.
 */
public class ScoreViewUpdater implements Runnable {

    private static final int UPDATE_PERIOD_MS = 500;
    private Handler handler;
    private ScoreView scoreView;

    public ScoreViewUpdater(Handler handler, ScoreView scoreView) {
        super();
        this.handler = handler;
        this.scoreView = scoreView;
    }

    @Override
    public void run() {
//        update();
        handler.postDelayed(this, UPDATE_PERIOD_MS);
    }

    private void update() {
        scoreView.update();
    }
}
