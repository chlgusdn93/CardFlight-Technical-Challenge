package com.card.flight.bowling.model.response;

public class BowlingScoreResponse {

    private int currentScore;
    private int currentFrame;
    private int remainingRolls;

    public BowlingScoreResponse(int currentScore, int currentFrame, int remainingRolls) {
        this.currentScore = currentScore;
        this.currentFrame = currentFrame;
        this.remainingRolls = remainingRolls;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public int getRemainingRolls() {
        return remainingRolls;
    }
}
