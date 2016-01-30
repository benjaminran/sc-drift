package com.inboardhack.scdrift;

import android.text.TextUtils;
import android.util.Log;

public class Board implements Observer {

    public static Board instance = null;
    private DataService dataService;

    private double[] latestGPSVelocity;

    private double[] position = new double[9];
    private double[] rotation = new double[9];
    private double[] oldPosition = new double[9];
    private double[] realAccel = new double[3];
    private long lastUpdate = 0;
    private Slide lastSlide;
    private float initBearing = 0.0f;
    public static final double MINSLIDESTRENGTH = 0;

    /* must be called at a point when gravity vector is available */
    private Board(DataService dataService) {
        dataService.bridge.registerObserver(this);
        double[] acceleration = dataService.bridge.getAccelerometerDataWithGravity(); // initialize with gravity vector
        position[0] = 0;
        position[1] = 0;
        position[2] = 0;
        position[3] = 0;
        position[4] = 0;
        position[5] = 0;
        position[6] = 0;
        position[7] = 0;
        position[8] = 0;
        rotation[0] = Math.atan(acceleration[1] / acceleration[2]); //roll
        rotation[1] = Math.atan(acceleration[0] / acceleration[2]); //pitch
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
    }

    private void logBoard() {
        Log.d("scd", "position: " + Utils.join(",", position));
        Log.d("scd", "rotaion: " + Utils.join(",", rotation));
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
        return Board.getInstance(null);
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
        ret[0] = Math.cos(-rotation[1])*Math.cos(-rotation[2]) + (Math.sin(-rotation[1])*Math.cos(-rotation[2])*Math.sin(-rotation[0]) - Math.sin(-rotation[2])*Math.cos(-rotation[0])) + (Math.sin(-rotation[1])*Math.cos(-rotation[2])*Math.cos(-rotation[0]) + Math.sin(-rotation[2])*Math.sin(-rotation[0]));
        ret[1] = Math.cos(-rotation[1])*Math.sin(-rotation[2]) + (Math.sin(-rotation[1])*Math.sin(-rotation[2])*Math.sin(-rotation[0]) + Math.cos(-rotation[2])*Math.cos(-rotation[0])) + (Math.sin(-rotation[1])*Math.sin(-rotation[2])*Math.cos(-rotation[0]) - Math.cos(-rotation[2])*Math.sin(-rotation[0]));
        ret[2] = Math.cos(-rotation[1])*Math.sin(-rotation[0]) - Math.sin(-rotation[1]) + Math.cos(-rotation[1])*Math.cos(-rotation[0]);
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
    private double[] incrementVelocity(long timems) {
        return incrementVelocity(getAcceleration(), timems);
    }
    private double[] incrementVelocity(double[] acceleration, long timems) {
        position[3] += acceleration[0] * (timems - lastUpdate) / 1000;
        position[4] += acceleration[1] * (timems - lastUpdate) / 1000;
        position[5] += acceleration[2] * (timems - lastUpdate) / 1000;
        lastUpdate = timems;
        return getVelocity();
    }
    public double[] updateVelocity(double[] GPSVelocity, long timems) {
        if ((oldPosition[3] == GPSVelocity[0] && oldPosition[4] == GPSVelocity[1] && oldPosition[5] == GPSVelocity[2]) && (timems - lastUpdate < 1000)) {
            incrementVelocity(timems);
        } else {
            setVelocity(GPSVelocity);
            lastUpdate = timems;
        }
        return getVelocity();
    }
    public double[] updateVelocity(double[] GPSVelocity, double[] acceleration, long timems) {
        if (oldPosition[3] == GPSVelocity[0] && oldPosition[4] == GPSVelocity[1] && oldPosition[5] == GPSVelocity[2]) {
            incrementVelocity(acceleration, timems);
        } else {
            setVelocity(GPSVelocity);
            oldPosition[3] = GPSVelocity[0];
            oldPosition[4] = GPSVelocity[1];
            oldPosition[5] = GPSVelocity[2];
            lastUpdate = timems;
        }
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
    public double[] updatePosition(double[] worldaccel, double[] realaccel, double[] GPSVelocity, double[] GPSDisplacement, long timems) {
        setAcceleration(worldaccel);
        setRealAcceleration(realaccel);
        updateVelocity(GPSVelocity, timems);
        updateDisplacement(GPSDisplacement, timems);
        lastUpdate = timems;
        logBoard();
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
        if (isSliding(speed) && (lastSlide.isComplete() || lastSlide == null)) {
            lastSlide = new Slide(speed, timems, this);
            return lastSlide;
        }
        return null;
    }
    public boolean isSliding(double speed) {
        double caaccel = speed * rotation[5];
        double slideStrength = Math.abs(caaccel) - Math.abs(realAccel[1]);
        return (slideStrength > MINSLIDESTRENGTH);
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

    @Override
    public void observeUpdate(Object origin) {
        if(origin instanceof BluetoothBridge) {
            if(dataService==null) return; // TODO: ok?
            BluetoothBridge bridge = dataService.bridge;

            double[] velocity = computeVelocity(dataService.getVelocityMeter().speed, dataService.getVelocityMeter().bearing, dataService.getVelocityMeter().da, dataService.getVelocityMeter().dt);
            updatePosition(bridge.getWorldAccel(), bridge.getRealAccel(), velocity, new double[]{0, 0, 0}, System.currentTimeMillis()); // TODO: time of bridge or location reading? TODO: world accel, real accel
            setRotation(bridge.getOrientationData());
            Slide slide = newSlide(System.currentTimeMillis());
            if(slide==null && getLastSlide()!=null){ // just started sliding
                getLastSlide().incrementScore(velocity, getDirection(), bridge.getRealAccel());
            }
            else if(getLastSlide()==null) { // not sliding
                return;
            }
            else if(slide!=null) { // in process of sliding
                slide.incrementScore(velocity, getDirection(), bridge.getRealAccel());
            }

            if(lastSlide!=null && getLastSlide().isComplete()){ // just finished
                SlideHistory.getInstance().add(getLastSlide());
                lastSlide = null;
            }
        }
        else if(origin instanceof VelocityMeter) {}
    }
}
