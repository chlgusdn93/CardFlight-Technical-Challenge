package com.card.flight.bowling.model;

public class Roll {
    private RollType rollType;
    private int score;

    public Roll(RollType rollType) {
        this.rollType = rollType;
        this.score = rollType.getValue();
    }

    public RollType getRollType() {
        return rollType;
    }

    public int getScore() {
        return score;
    }
}
