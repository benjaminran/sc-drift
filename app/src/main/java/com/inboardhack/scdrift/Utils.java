package com.inboardhack.scdrift;

public class Utils {

    public static final double MINSLIDESTRENGTH = 0;

    public static boolean isSliding(double acceleration[], double rotation[], double speed) {
        double caaccel = speed * rotation[5];
        double slideStrength = Math.abs(caaccel) - Math.abs(acceleration[1]);
        return (slideStrength > MINSLIDESTRENGTH);
    }
}
