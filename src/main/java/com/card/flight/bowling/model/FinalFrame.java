package com.card.flight.bowling.model;

import org.springframework.lang.Nullable;

public class FinalFrame extends Frame {
    @Nullable
    private Roll thirdRoll;

    public FinalFrame(Roll firstRoll, int currentScore) {
        super(firstRoll, currentScore);
    }

    public Roll getThirdRoll() {
        return thirdRoll;
    }

    public void addThirdRoll(Roll thirdRoll) {
        this.thirdRoll = thirdRoll;
        super.setScore(super.getScore() + thirdRoll.getScore());
    }
}
