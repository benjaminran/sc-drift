package com.inboardhack.scdrift;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by benjaminran on 1/29/16.
 */
public class HighScoreView extends RelativeLayout implements Observer {

    private TextView scoreView;
    private TextView scoreLabel;
    private Context context;

    public HighScoreView(Context context) {
        super(context);
        this.context = context;
        SlideHistory.getInstance().registerObserver(this);
        init(context);
    }

    public HighScoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HighScoreView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setGravity(CENTER_IN_PARENT);
        scoreView = new TextView(context);
        LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        scoreView.setLayoutParams(params);
        scoreView.setTypeface(null, Typeface.BOLD_ITALIC);
        scoreView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 82);
        scoreLabel = new TextView(context);
        scoreLabel.setText("High Score:");
        update();
        addView(scoreView);
//        addView(scoreLabel);
    }

    public void update() {
        if(SlideHistory.getInstance()==null || SlideHistory.getInstance().size()==0) return;
        Slide max = SlideHistory.getInstance().get(0);
        for(Slide s : SlideHistory.getInstance()) {
            if(s.getScore()>max.getScore()) max = s;
        }
        scoreView.setText(""+max.getScore());
    }

    @Override
    public void observeUpdate(Object o) {
        ((MainActivity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                update();
            }
        });
    }
}
