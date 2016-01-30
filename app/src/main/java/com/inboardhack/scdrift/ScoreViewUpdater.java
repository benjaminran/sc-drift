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
        update();
        handler.postDelayed(this, UPDATE_PERIOD_MS);
    }

    private void update() {
        double[] acceleration = null;
        double[] velocity = null;
        double[] rotation = null;
        double speed = 0;

       /* boolean isSliding = Utils.isSliding(acceleration, velocity, speed);
        if(currentSlide==null && isSliding) {
            currentSlide = new Slide();
        }
        else if(currentSlide!=null && isSliding) {
            currentSlide.incrementScore(velocity, rotation, acceleration);
        }
        else if(currentSlide!=null && !isSliding) {
            SlideHistory.getInstance().add(currentSlide);
        }
        else { // currentSlide==null && !isSliding
            // do nothing
        }*/
//        scoreView.update();
    }
}
