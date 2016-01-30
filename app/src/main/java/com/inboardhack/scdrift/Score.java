package com.inboardhack.scdrift;

/**
 * Created by benjaminran on 1/29/16.
 */
public class Score {
    private int score;

    public Score() {
        score = 0;
    }

    public int getScore() { return score; }
    public void setScore(int newScore) { score = newScore; }
    public void incrementScore(int increment) { score += increment; }
}
