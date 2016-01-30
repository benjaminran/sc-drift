package com.inboardhack.scdrift;

/**
 * Created by benjaminran on 1/29/16.
 */
public interface Observer {
    void notify(double velocity, double bearing, double altitude);
    void notifyUpdated();
}
