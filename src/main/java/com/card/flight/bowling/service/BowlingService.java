package com.card.flight.bowling.service;

import com.card.flight.bowling.BowlingApplication;
import com.card.flight.bowling.model.FinalFrame;
import com.card.flight.bowling.model.Frame;
import com.card.flight.bowling.model.RollType;
import com.card.flight.bowling.model.response.BowlingScoreResponse;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BowlingService implements IBowlingService {
    private static final int MAXIMUM_FRAMES = 10;

    @Override
    public void printFinalBowlingScores() {
        calculateCurrentBowlingScores();
        System.out.println("| Frame |  1  |  2  |  3  |  4  |  5  |  6  |  7  |  8  |  9  |  10 |");
        System.out.println("|  ---  | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |");
        System.out.print("| Input ");

        for (int i = 0; i < BowlingApplication.frames.size() - 1; i++) {
            Frame frame = BowlingApplication.frames.get(i);
            System.out.print("| " + frame.getFirstRoll().getRollType().getInput() + " "
                    + (frame.getSecondRoll() != null ? frame.getSecondRoll().getRollType().getInput() : "") + " ");
        }

        FinalFrame finalFrame = (FinalFrame) BowlingApplication.frames.get(BowlingApplication.frames.size() - 1);
        System.out.println("| " + finalFrame.getFirstRoll().getRollType().getInput() + " "
                // second roll shouldn't be null by the time we call this method
                + finalFrame.getSecondRoll().getRollType().getInput() + " "
                // third coll can be null if user didn't roll a strike the first roll
                + (finalFrame.getThirdRoll() != null ? finalFrame.getThirdRoll().getRollType().getInput() : " ") + " |");

        System.out.print("| Score ");
        for (Frame frame : BowlingApplication.frames) {
            System.out.print("| " + frame.getScore() + "  ");
        }
        System.out.println("|");
    }

    @Override
    public BowlingScoreResponse calculateCurrentBowlingScores() {
        List<Frame> currentFrames = BowlingApplication.frames;
        if (currentFrames.isEmpty()) {
            // no rolls yet
            return new BowlingScoreResponse(0, 1, 2);
        }

        // we need to iterate through and adjust strikes and spares
        int currentScore = 0;
        int currentFrame = 1;
        int remainingRolls = 0;
        for (Frame frame : currentFrames) {
            if (frame.isStrike()) {
                adjustStrikeFrame(frame, currentFrame - 1, currentScore);
            } else if (frame.isSpare()) {
                adjustSpareFrame(frame, currentFrame - 1, currentScore);
            } else {
                currentScore += frame.getFirstRoll().getScore()
                        + (frame.getSecondRoll() != null ? frame.getSecondRoll().getScore() : 0);
                frame.setScore(currentScore);
            }

            currentScore = frame.getScore();
            currentFrame++;
            if (currentFrame > currentFrames.size()) {
                remainingRolls = (frame.getSecondRoll() == null && !frame.isStrike()) ? 1 : 2;
                break;
            }
        }

        currentFrame = (currentFrame != 11 && remainingRolls == 2) ? currentFrame : currentFrame - 1;

        if (currentFrame == MAXIMUM_FRAMES) {
            remainingRolls = adjustFinalFrameRemainingRolls();
        }

        return new BowlingScoreResponse(currentScore, currentFrame, remainingRolls);
    }

    private void adjustStrikeFrame(Frame strikeFrame, int strikeFrameIndex, int currentScore) {
        Frame nextFrame = (BowlingApplication.frames.size() - 1 >= strikeFrameIndex + 1)
                ? BowlingApplication.frames.get(strikeFrameIndex + 1) : null;
        int nextFrameFirstRoll = nextFrame != null ? nextFrame.getFirstRoll().getScore() : 0;
        int nextFrameSecondRoll = nextFrame != null && nextFrame.getSecondRoll() != null ? nextFrame.getSecondRoll().getScore() : 0;
        int adjustedScore = currentScore + RollType.STRIKE.getValue();

        if (strikeFrameIndex == 8) {
            adjustedScore = calculateStrikeAdjustedScore(adjustedScore, nextFrame, nextFrameFirstRoll, nextFrameSecondRoll);
        } else if (strikeFrameIndex == 9) {
            if (strikeFrame.getSecondRoll() != null) {
                adjustedScore += strikeFrame.getSecondRoll().getScore();
            }
            if (((FinalFrame) strikeFrame).getThirdRoll() != null) {
                adjustedScore += ((FinalFrame) strikeFrame).getThirdRoll().getScore();
            }
        } else {
            adjustedScore = calculateStrikeAdjustedScore(adjustedScore, nextFrame, nextFrameFirstRoll, nextFrameSecondRoll);

            // if strike we need to grab next frame
            if (nextFrame != null && nextFrame.isStrike()) {
                Frame secondNextFrame = (BowlingApplication.frames.size() - 1 >= strikeFrameIndex + 2)
                        ? BowlingApplication.frames.get(strikeFrameIndex + 2) : null;
                int secondNextFrameFirstRollScore = secondNextFrame != null ? secondNextFrame.getFirstRoll().getScore() : 0;
                adjustedScore += secondNextFrameFirstRollScore;
            }
        }
        strikeFrame.setScore(adjustedScore);
    }

    private int calculateStrikeAdjustedScore(int currentAdjustedScore, @Nullable Frame nextFrame, int nextFrameFirstRoll,
                                             int nextFrameSecondRoll) {
        int adjustedScore = currentAdjustedScore + nextFrameFirstRoll + nextFrameSecondRoll;
        if (nextFrame != null && nextFrame.getSecondRoll() != null && nextFrame.isSpare()) {
            adjustedScore -= nextFrame.getFirstRoll().getScore();
        }

        return adjustedScore;
    }

    private void adjustSpareFrame(Frame spareFrame, int spareFrameIndex, int currentScore) {
        int adjustedScore = currentScore + RollType.SPARE.getValue();
        if (spareFrameIndex == 9) {
            adjustedScore += (((FinalFrame) spareFrame).getThirdRoll() != null
                    ? ((FinalFrame) spareFrame).getThirdRoll().getScore() : 0);
        } else {
            Frame nextFrame = (BowlingApplication.frames.size() - 1 >= spareFrameIndex + 1)
                    ? BowlingApplication.frames.get(spareFrameIndex + 1) : null;
            int nextFrameFirstRollScore = nextFrame != null ? nextFrame.getFirstRoll().getScore() : 0;
            adjustedScore += nextFrameFirstRollScore;
        }

        spareFrame.setScore(adjustedScore);
    }

    // a bit brute force-y
    private int adjustFinalFrameRemainingRolls() {
        int remainingRolls = 0;
        Frame frame = BowlingApplication.frames.get(BowlingApplication.frames.size() - 1);

        if (frame.isStrike()) {
            remainingRolls = 2;

            if (frame.getSecondRoll() != null) {
                remainingRolls -= 1;
            }
            if (frame instanceof FinalFrame && ((FinalFrame) frame).getThirdRoll() != null) {
                remainingRolls -= 1;
            }
        } else if (frame.isSpare()) {
            remainingRolls = 1;

            if (frame instanceof FinalFrame && ((FinalFrame) frame).getThirdRoll() != null) {
                remainingRolls -= 1;
            }
        } else {
            if (frame.getSecondRoll() == null) {
                remainingRolls = 1;
            }
        }

        return remainingRolls;
    }
}
