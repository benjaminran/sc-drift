package com.inboardhack.scdrift;

import android.util.Log;

/* TODO: Constants: MINSLIDESTRENGTH, MAXANGULARACCELERATION */
public class Board {

    public static Board instance = null;
    private DataService dataService;

    private double[] position = new double[9];
    private double[] rotation = new double[9];
    private double[] oldPosition = new double[9];
    private double[] realAccel = new double[3];
    private long lastUpdate = 0;
    private Slide lastSlide;
    private float initBearing = 0.0f;
    public static final double MINSLIDESTRENGTH = 0.3;
    public static final double MAX_ANGULAR_ACCELERATION = 0.2;

    /* must be called at a point when gravity vector is available */
    private Board(DataService dataService) {
        this.dataService = dataService;
        double[] acceleration = BluetoothBridge.getInstance().getGravity(); // initialize with gravity vector
        position[0] = 0;
        position[1] = 0;
        position[2] = 0;
        position[3] = 0;
        position[4] = 0;
        position[5] = 0;
        position[6] = 0;
        position[7] = 0;
        position[8] = 0;
        rotation[0] = 0; //roll
        rotation[1] = 0; //pitch
        rotation[2] = 0; //yaw
        rotation[3] = 0;
        rotation[4] = 0;
        rotation[5] = 0;
        rotation[6] = 0;
        rotation[7] = 0;
        rotation[8] = 0;
        oldPosition[0] = 0;
        oldPosition[1] = 0;
        oldPosition[2] = 0;
        oldPosition[3] = 0;
        oldPosition[4] = 0;
        oldPosition[5] = 0;
        oldPosition[6] = 0;
        oldPosition[7] = 0;
        oldPosition[8] = 0;
        realAccel[0] = 0;
        realAccel[1] = 0;
        realAccel[2] = 0;
        lastUpdate = 0;
        // TODO: reinitialize gyro
    }

    private void logBoard() {
        Log.d("scd", "position: " + Utils.join(",", position));
        Log.d("scd", "rotation: " + Utils.join(",", rotation));
        Log.d("scd", "oldPosition: " + Utils.join(",", oldPosition));
        Log.d("scd", "lastUpdate: " + lastUpdate);
        Log.d("scd", "initBearing: " + initBearing);
    }

    public static Board getInstance(DataService dataService) {
        if(instance==null) {
            instance = new Board(dataService);
        }
        return instance;
    }

    /* must not be called before getInstance(DataService) has been called */
    public static Board getInstance() {
        return instance;
    }

    public float calibrate(float bearing) {
        if (bearing != 0.0)
            initBearing = bearing;
        else
            return 0;
        return initBearing;
    }

    public double[] getDisplacement() {
        double[] ret = {position[0], position[1], position[2]};
        return ret;
    }
    public double[] getVelocity() {
        double[] ret = {position[3], position[4], position[5]};
        return ret;
    }
    public double[] getAcceleration() {
        double[] ret = {position[6], position[7], position[8]};
        return ret;
    }
    public double[] getRealAcceleration() {
        double[] ret = {realAccel[0], realAccel[1], realAccel[2]};
        return ret;
    }
    public double[] getPosition() {
        double[] ret = {position[0], position[1], position[2], position[3], position[4], position[5], position[6], position[7], position[8]};
        return ret;
    }
    public double[] getOrientation() {
        double[] ret = {rotation[0], rotation[1], rotation[2]};
        return ret;
    }
    public double[] getAngularVelocity() {
        double[] ret = {rotation[3], rotation[4], rotation[5]};
        return ret;
    }
    public double[] getAngularAcceleration() {
        double[] ret = {rotation[6], rotation[7], rotation[8]};
        return ret;
    }
    public double[] getRotation() {
        double[] ret = {rotation[0], rotation[1], rotation[2], rotation[3], rotation[4], rotation[5], rotation[6], rotation[7], rotation[8]};
        return ret;
    }
    public double[] getDirection() {
        double[] ret = new double[3];
        ret[0] = Math.cos(rotation[1])*Math.cos(rotation[2]);
        ret[1] = Math.cos(rotation[1])*Math.sin(rotation[2]);
        ret[2] = Math.sin(rotation[1]);
        return ret;
    }
    public double[] setDisplacement(double[] displacement) {
        position[0] = displacement[0];
        position[1] = displacement[1];
        position[2] = displacement[2];
        return getDisplacement();
    }
    private double[] incrementDisplacement(long timems) {
        return incrementDisplacement(getVelocity(), timems);
    }
    private double[] incrementDisplacement(double[] velocity, long timems) {
        position[0] += velocity[0] * (timems - lastUpdate) / 1000;
        position[1] += velocity[1] * (timems - lastUpdate) / 1000;
        position[2] += velocity[2] * (timems - lastUpdate) / 1000;
        lastUpdate = timems;
        return getDisplacement();
    }
    public double[] updateDisplacement(double[] GPSDisplacement, long timems) {
        if ((oldPosition[0] == GPSDisplacement[0] && oldPosition[1] == GPSDisplacement[1] && oldPosition[2] == GPSDisplacement[2]) && (timems - lastUpdate < 1000)) {
            incrementDisplacement(timems);
        } else {
            setDisplacement(GPSDisplacement);
            lastUpdate = timems;
        }
        return getDisplacement();
    }
    public double[] setVelocity(double[] velocity) {
        position[3] = velocity[0];
        position[4] = velocity[1];
        position[5] = velocity[2];
        return getVelocity();
    }
    private double[] normalizeVelocity(double speed) {
        double mult;
        if (Math.abs(rotation[5]) > MAX_ANGULAR_ACCELERATION && !isSliding(speed)) {
            mult = speed / Math.sqrt(Math.pow(position[3],2)+Math.pow(position[4],2)+Math.pow(position[5],2));
        } else {
            return getVelocity();
//            mult = speed;
//            setVelocity(getDirection());
        }
        position[3] *= mult;
        position[4] *= mult;
        position[5] *= mult;
        return getVelocity();
    }
    private double[] incrementVelocity(long timems) {
        return incrementVelocity(getAcceleration(), timems);
    }
    private double[] incrementVelocity(double[] acceleration, long timems) {
        position[3] += acceleration[0] * (timems - lastUpdate) / 1000.0;
        position[4] += acceleration[1] * (timems - lastUpdate) / 1000.0;
        position[5] += acceleration[2] * (timems - lastUpdate) / 1000.0;
//        Log.d("scd", String.format("a: %f,%f,%f   dt: %f  <-------------------------", acceleration[0], acceleration[1], acceleration[2], ((timems - lastUpdate)/1000.0)));
        lastUpdate = timems;
        return getVelocity();
    }

    double r0, r1, r2, w0, w1, w2 = 0;
    long n = 0;

    public double[] updateVelocity(double speed, long timems) {
        incrementVelocity(timems);
//        normalizeVelocity(speed);
        lastUpdate = timems;
        r0 += getRealAcceleration()[0];
        r1 += getRealAcceleration()[1];
        r2 += getRealAcceleration()[2];
        w0 += position[6];
        w1 += position[7];
        w2 += position[8];
        Log.d("scd", "Mean: "+Utils.join(",", new double[]{r0/n,r1/n,r2/n,w0/n,w1/n,w2/n++}));
        return getVelocity();
    }
    public double[] updateVelocity(double speed, double[] acceleration, long timems) {
        incrementVelocity(acceleration, timems);
        normalizeVelocity(speed);
        lastUpdate = timems;
        return getVelocity();
    }
    public double[] setAcceleration(double[] acceleration) {
        position[6] = acceleration[0];
        position[7] = acceleration[1];
        position[8] = acceleration[2];
        return getAcceleration();
    }
    public double[] setRealAcceleration(double[] acceleration) {
        //position[6] = Math.cos(-rotation[1])*Math.cos(-rotation[2])*acceleration[0] + (Math.sin(-rotation[1])*Math.cos(-rotation[2])*Math.sin(-rotation[0]) - Math.sin(-rotation[2])*Math.cos(-rotation[0]))*acceleration[1] + (Math.sin(-rotation[1])*Math.cos(-rotation[2])*Math.cos(-rotation[0]) + Math.sin(-rotation[2])*Math.sin(-rotation[0]))*acceleration[2];
        //position[7] = Math.cos(-rotation[1])*Math.sin(-rotation[2])*acceleration[0] + (Math.sin(-rotation[1])*Math.sin(-rotation[2])*Math.sin(-rotation[0]) + Math.cos(-rotation[2])*Math.cos(-rotation[0]))*acceleration[1] + (Math.sin(-rotation[1])*Math.sin(-rotation[2])*Math.cos(-rotation[0]) - Math.cos(-rotation[2])*Math.sin(-rotation[0]))*acceleration[2];
        //position[8] = Math.cos(-rotation[1])*Math.sin(-rotation[0])*acceleration[1] - Math.sin(-rotation[1])*acceleration[0] + Math.cos(-rotation[1])*Math.cos(-rotation[0])*acceleration[2];
        realAccel[0] = acceleration[0];
        realAccel[1] = acceleration[1];
        realAccel[2] = acceleration[2];
        return getRealAcceleration();
    }
    public double[] setPosition(double[] position) {
        this.position[0] = position[0];
        this.position[1] = position[1];
        this.position[2] = position[2];
        this.position[3] = position[3];
        this.position[4] = position[4];
        this.position[5] = position[5];
        this.position[6] = position[6];
        this.position[7] = position[7];
        this.position[8] = position[8];
        return getPosition();
    }
    public double[] updatePosition(double[] worldaccel, double[] realaccel, double speed, long timems) {
        setAcceleration(worldaccel);
        setRealAcceleration(realaccel);
        updateVelocity(speed, timems);
        lastUpdate = timems;
        return getPosition();
    }
    public double[] setOrientation(double[] orientation) {
        rotation[0] = orientation[0];
        rotation[1] = orientation[1];
        rotation[2] = orientation[2];
        return getOrientation();
    }
    public double[] setAngularVelocity(double[] angularVelocity) {
        rotation[3] = angularVelocity[0];
        rotation[4] = angularVelocity[1];
        rotation[5] = angularVelocity[2];
        return getAngularVelocity();
    }
    public double[] setAngularAcceleration(double[] angularAcceleration) {
        rotation[6] = angularAcceleration[0];
        rotation[7] = angularAcceleration[1];
        rotation[8] = angularAcceleration[2];
        return getAngularAcceleration();
    }
    public double[] setRotation(double[] rotation) {
        this.rotation[0] = rotation[0];
        this.rotation[1] = rotation[1];
        this.rotation[2] = rotation[2];
        this.rotation[3] = rotation[3];
        this.rotation[4] = rotation[4];
        this.rotation[5] = rotation[5];
        this.rotation[6] = rotation[6];
        this.rotation[7] = rotation[7];
        this.rotation[8] = rotation[8];
        return getRotation();
    }
    public Slide newSlide(long timems) {
        double speed = Math.sqrt(Math.pow(position[3],2)+Math.pow(position[4],2)+Math.pow(position[5],2));
        if (isSliding(speed) && (lastSlide == null || lastSlide.isComplete())) {
            lastSlide = new Slide(speed, timems, this);
            return lastSlide;
        }
        return null;
    }
    public boolean isSliding(double speed) {
        return (rotation[5] > MAX_ANGULAR_ACCELERATION + MINSLIDESTRENGTH);
      //  double caaccel = speed * rotation[5];
      //  double slideStrength = Math.abs(caaccel) - Math.abs(realAccel[1]);
        /*double[] velocity = getVelocity();
        double[] direction = getDirection();
        double slideStrength = (Math.sqrt(Math.pow(velocity[1]*direction[2] - velocity[2]*direction[1], 2) + Math.pow(velocity[2]*direction[0] - velocity[0]*direction[2], 2) + Math.pow(velocity[0]*direction[1] - velocity[1]*direction[0], 2)));
        return (slideStrength > MINSLIDESTRENGTH);*/
    }
    public Slide getLastSlide() {
        return lastSlide;
    }
    public double[] computeVelocity(float speed, float bearing, double da, double dt) {
        double[] ret = new double[3];
        double dir = Math.toRadians((bearing - initBearing) % 360);
        ret[0] = speed * Math.cos(dir);
        ret[1] = speed * Math.sin(dir);
        ret[2] = da / dt;
        return ret;
    }
}
