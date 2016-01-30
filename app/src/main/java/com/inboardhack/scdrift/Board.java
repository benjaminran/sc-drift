package com.inboardhack.scdrift

public class Board {

    private double[] position = new double[9];
    private double[] rotation = new double[9];
    private double[] oldVelocity = new double[3];
    private long lastVelocityUpdate = 0;

    public Board(double[] acceleration /* contains gravity */) {
        position[0] = 0;
        position[1] = 0;
        position[2] = 0;
        position[3] = 0;
        position[4] = 0;
        position[5] = 0;
        position[6] = 0;
        position[7] = 0;
        position[8] = 0;
        rotation[0] = Math.atan(acceleration[1] / acceleration[2]);
        rotation[1] = Math.atan(acceleration[0] / acceleration[2]);
        rotation[2] = 0;
        rotation[3] = 0;
        rotation[4] = 0;
        rotation[5] = 0;
        rotation[6] = 0;
        rotation[7] = 0;
        rotation[8] = 0;
        oldVelocity[0] = 0;
        oldVelocity[1] = 0;
        oldVelocity[2] = 0;
        lastVelocityUpdate = 0;
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
    public double[] setDisplacement(double[] displacement) {
        position[0] = displacement[0];
        position[1] = displacement[1];
        position[2] = displacement[2];
        return getDisplacement();
    }
    public double[] setVelocity(double[] velocity) {
        position[3] = velocity[3];
        position[4] = velocity[4];
        position[5] = velocity[5];
        return getVelocity();
    }
    public double[] incrementVelocity(long timems) {
        return incrementVelocity(getAcceleration(), timems);
    }
    public double[] incrementVelocity(double[] acceleration, long timems) {
        position[3] += acceleration[0] * (timems - lastVelocityUpdate) / 1000;
        position[4] += acceleration[1] * (timems - lastVelocityUpdate) / 1000;
        position[5] += acceleration[2] * (timems - lastVelocityUpdate) / 1000;
        lastVelocityUpdate = timems;
        return getVelocity();
    }
    public double[] setAccurateVelocity(double[] GPSVelocity, long timems) {
        if ((oldVelocity[0] == GPSVelocity[0] && oldVelocity[1] == GPSVelocity[1] && oldVelocity[2] == GPSVelocity[2]) && (timems - lastVelocityUpdate < 1000)) {
            incrementVelocity(timems);
        } else {
            setVelocity(GPSVelocity);
            lastVelocityUpdate = timems;
        }
        return getVelocity();
    }
    public double[] setAccurateVelocity(double[] GPSVelocity, double[] acceleration, long timems) {
        if (oldVelocity[0] == GPSVelocity[0] && oldVelocity[1] == GPSVelocity[1] && oldVelocity[2] == GPSVelocity[2]) {
            incrementVelocity(acceleration, timems);
        } else {
            setVelocity(GPSVelocity);
            oldVelocity[0] = GPSVelocity[0];
            oldVelocity[1] = GPSVelocity[1];
            oldVelocity[2] = GPSVelocity[2];
            lastVelocityUpdate = timems;
        }
        return getVelocity();
    }
    public double[] setAcceleration(double[] acceleration) {
        position[6] = acceleration[6];
        position[7] = acceleration[7];
        position[8] = acceleration[8];
        return getAcceleration();
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
    public double[] setOrientation(double[] orientation) {
        rotation[0] = orientation[0];
        rotation[1] = orientation[1];
        rotation[2] = orientation[2];
        return getOrientation();
    }
    public double[] setAngularVelocity(double[] AngularVelocity) {
        rotation[3] = AngularVelocity[3];
        rotation[4] = AngularVelocity[4];
        rotation[5] = AngularVelocity[5];
        return getAngularVelocity();
    }
    public double[] setAngularAcceleration(double[] angularAcceleration) {
        rotation[6] = angularAcceleration[6];
        rotation[7] = angularAcceleration[7];
        rotation[8] = angularAcceleration[8];
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
}
