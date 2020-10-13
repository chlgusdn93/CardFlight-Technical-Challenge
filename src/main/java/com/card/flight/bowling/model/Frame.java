package com.card.flight.bowling.model;

import org.springframework.lang.Nullable;

public class Frame {

    private Roll firstRoll;
    @Nullable
    private Roll secondRoll;
    private int score;

    private boolean isStrike;
    private boolean isSpare;

    public Frame(Roll firstRoll, int currentScore) {
        this.firstRoll = firstRoll;
        this.isStrike = firstRoll.getRollType().equals(RollType.STRIKE);
        this.score = currentScore + firstRoll.getScore();
    }

    public void addSecondRoll(Roll secondRoll) {
        this.secondRoll = secondRoll;

        if (secondRoll.getRollType().equals(RollType.SPARE)) {
            this.score += secondRoll.getScore() - firstRoll.getScore();
            this.isSpare = true;
        } else {
            this.score += secondRoll.getScore();
            this.isSpare = false;
        }
    }

    public Roll getFirstRoll() {
        return firstRoll;
    }

    public Roll getSecondRoll() {
        return secondRoll;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isStrike() {
        return isStrike;
    }

    public boolean isSpare() {
        return isSpare;
    }
}
