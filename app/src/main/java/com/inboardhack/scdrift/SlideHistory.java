package com.inboardhack.scdrift;

import java.util.ArrayList;

/**
 * Created by benjaminran on 1/29/16.
 */
public class SlideHistory extends ArrayList<Slide> {
    private static SlideHistory instance;

    private SlideHistory() {
        super();
    }

    public static SlideHistory getInstance() {
        if(instance==null) {
            instance = new SlideHistory();
        }
        return instance;
    }
}
