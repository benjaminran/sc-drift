package com.inboardhack.scdrift;

import java.util.ArrayList;

/**
 * Created by benjaminran on 1/29/16.
 */
public class SlideHistory extends ArrayList<Slide> {
    private static SlideHistory instance = new SlideHistory();

    private SlideHistory() {
        super();
    }

    public static SlideHistory getInstance() {
        return instance;
    }
}
