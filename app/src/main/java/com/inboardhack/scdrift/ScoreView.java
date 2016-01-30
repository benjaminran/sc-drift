package com.inboardhack.scdrift;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by benjaminran on 1/29/16.
 */
public class ScoreView extends RelativeLayout {

    private Score score;
    private TextView scoreView;
    private TextView scoreLabel;

    private Slide currentSlide;

    public ScoreView(Context context) {
        super(context);
        init(context);
    }

    public ScoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScoreView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setGravity(CENTER_IN_PARENT);
        score = new Score();
        scoreView = new TextView(context);
        scoreView.setTextSize(TypedValue.COMPLEX_UNIT_IN, 1);
        scoreLabel = new TextView(context);
        scoreLabel.setText("Score:");
        //update(); // TODO
        addView(scoreView);
        addView(scoreLabel);
    }

    public void updateSpeed(double speed) {
        scoreView.setText(""+speed);
    }

    public void update() {
        double[] acceleration = null;
        double[] velocity = null;
        double[] rotation = null;
        double speed = 0;
        boolean isSliding = Utils.isSliding(acceleration, velocity, speed);
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
        }
        scoreView.setText(""+currentSlide.getScore());
    }
}
