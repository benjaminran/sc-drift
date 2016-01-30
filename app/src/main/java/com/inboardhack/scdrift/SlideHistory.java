package com.inboardhack.scdrift;

import java.util.ArrayList;

/**
 * Created by benjaminran on 1/29/16.
 */
public class SlideHistory extends ArrayList<Slide> {
    private static SlideHistory instance;

    private SlideHistory() {
        super();
        /*for(int i=0; i<4; i++) // dummy data
            add(new Slide(5, System.currentTimeMillis(), Board.getInstance()));
        get(1).incrementScore(new double[]{0,-10,0}, new double[]{1,0,0}, new double[]{0,3,0});
        get(2).incrementScore(new double[]{0,-6.78,0}, new double[]{1,0,0}, new double[]{0,2.3,0});
        get(3).incrementScore(new double[]{0,-10,0}, new double[]{1,0,0}, new double[]{0,6,0});
        get(3).incrementScore(new double[]{0,-12,0}, new double[]{1,0,0}, new double[]{0,6,0});*/
    }

    public static SlideHistory getInstance() {
        if(instance==null) {
            instance = new SlideHistory();
        }
        return instance;
    }
}
