package com.inboardhack.scdrift;

import java.util.ArrayList;

/**
 * Created by benjaminran on 1/29/16.
 */
public class SlideHistory extends ArrayList<Slide> {
    private static SlideHistory instance = new SlideHistory();

    private SlideHistory() {
        super();
        for(int i=0; i<15; i++) // dummy data
            add(new Slide());
        get(10).incrementScore(new double[]{0,-10,0}, new double[]{1,0,0}, new double[]{0,3,0});
        get(8).incrementScore(new double[]{0,-6.78,0}, new double[]{1,0,0}, new double[]{0,2.3,0});
        get(7).incrementScore(new double[]{0,-10,0}, new double[]{1,0,0}, new double[]{0,6,0});
    }

    public static SlideHistory getInstance() {
        return instance;
    }
}
