package com.inboardhack.scdrift;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by benjaminran on 1/29/16.
 */
public class SlideHistory extends ArrayList<Slide> {

    private static SlideHistory instance;
    private ArrayList<Observer> observers;


    private SlideHistory() {
        super();
        observers = new ArrayList<>();
    }

    @Override
    public boolean add(Slide slide) {
        boolean ret = super.add(slide);
        for(Observer o : observers) o.observeUpdate(this);
        return ret;

    }

    public static SlideHistory getInstance() {
        if(instance==null) {
            instance = new SlideHistory();
        }
        return instance;
    }

    public void registerObserver(Observer o) {
        observers.add(o);
    }
}
