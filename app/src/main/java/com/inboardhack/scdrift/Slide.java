package com.inboardhack.scdrift;

public class Slide {

    private int score;
    private double initSpeed;
    private long startTime, endTime;
    private boolean complete;
    private Board board;
    private static final double ANGLEGAIN = 1; // TODO: determine experimenting
    private static final double ACCELERATIONGAIN = 1; // TODO: determine experimenting

    public Slide (double speed, long timems, Board board) {
        score = 0;
        initSpeed = speed;
        startTime = timems;
        complete = false;
        this.board = board;
    }
    public int getScore() {
        return score;
    }
    public int incrementScore(double velocity[], double direction[], double acceleration[]) {
        if (board.isSliding(initSpeed) && !complete) {
            score += 1 + (int) (ANGLEGAIN * Math.sqrt(Math.pow(velocity[1]*direction[2] - velocity[2]*direction[1], 2) + Math.pow(velocity[2]*direction[0] - velocity[0]*direction[2], 2) + Math.pow(velocity[0]*direction[1] - velocity[1]*direction[0], 2)) + ACCELERATIONGAIN * Math.sqrt(Math.pow(acceleration[0], 2) + Math.pow(acceleration[1], 2) + Math.pow(acceleration[2], 2)));
        } else {
            complete = true;
        }
        return score;
    }
    public boolean isComplete() {
        return complete;
    }
    public void forceComplete() {
        complete = true;
    }

    public void setEndTime(long t) { endTime = t; }
    public long getEndTime() { return endTime; }
    public long getStartTime() { return startTime; }

    public double getDuration() { return (endTime - startTime) / 1000.0; }
}
