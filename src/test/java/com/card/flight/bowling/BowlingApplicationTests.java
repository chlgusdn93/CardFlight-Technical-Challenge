package com.card.flight.bowling;

import com.card.flight.bowling.model.FinalFrame;
import com.card.flight.bowling.model.Frame;
import com.card.flight.bowling.model.Roll;
import com.card.flight.bowling.model.RollType;
import com.card.flight.bowling.model.response.BowlingScoreResponse;
import com.card.flight.bowling.service.IBowlingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
class BowlingApplicationTests {

    @Autowired
    BowlingApplication bowlingApplication;

    @Autowired
    IBowlingService bowlingService;

    @AfterEach
    public void resetFrames() {
        bowlingApplication.frames = new ArrayList<>();
    }

    // Ideally, I would have test util cases here with functions that I could reuse to set up the frames to test with
    @Test
    public void baseCaseNoRollTest() {
        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(1, bowlingScoreResponse.getCurrentFrame());
        assertEquals(0, bowlingScoreResponse.getCurrentScore());
        assertEquals(2, bowlingScoreResponse.getRemainingRolls());
    }

    @Test
    public void baseBowlingTestSingleFrame() {
        Frame frame = new Frame(new Roll(RollType.THREE), 0);
        bowlingApplication.frames = Arrays.asList(frame);

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(1, bowlingScoreResponse.getCurrentFrame());
        assertEquals(3, bowlingScoreResponse.getCurrentScore());
        assertEquals(1, bowlingScoreResponse.getRemainingRolls());
    }

    @Test
    public void basicMissTest() {
        Frame frame = new Frame(new Roll(RollType.THREE), 0);
        frame.addSecondRoll(new Roll(RollType.MISS));
        bowlingApplication.frames = Arrays.asList(frame);

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(2, bowlingScoreResponse.getCurrentFrame());
        assertEquals(3, bowlingScoreResponse.getCurrentScore());
        assertEquals(2, bowlingScoreResponse.getRemainingRolls());
    }

    @Test
    public void baseBowlingTestSingleFrameMultipleRoll() {
        Frame frame = new Frame(new Roll(RollType.THREE), 0);
        frame.addSecondRoll(new Roll(RollType.SIX));
        bowlingApplication.frames = Arrays.asList(frame);

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(2, bowlingScoreResponse.getCurrentFrame());
        assertEquals(9, bowlingScoreResponse.getCurrentScore());
        assertEquals(2, bowlingScoreResponse.getRemainingRolls());
    }

    @Test
    public void baseStrikeTest() {
        Frame firstFrame = new Frame(new Roll(RollType.STRIKE), 0);
        bowlingApplication.frames = Arrays.asList(firstFrame);

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(2, bowlingScoreResponse.getCurrentFrame());
        assertEquals(10, bowlingScoreResponse.getCurrentScore());
        assertEquals(2, bowlingScoreResponse.getRemainingRolls());
        assertEquals(10, firstFrame.getScore());
    }

    @Test
    public void basicStrikeTest() {
        Frame firstFrame = new Frame(new Roll(RollType.STRIKE), 0);
        Frame secondFrame = new Frame(new Roll(RollType.THREE), firstFrame.getScore());
        secondFrame.addSecondRoll(new Roll(RollType.SIX));
        bowlingApplication.frames = Arrays.asList(firstFrame, secondFrame);

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(3, bowlingScoreResponse.getCurrentFrame());
        assertEquals(28, bowlingScoreResponse.getCurrentScore());
        assertEquals(2, bowlingScoreResponse.getRemainingRolls());
        assertEquals(19, firstFrame.getScore());
    }

    @Test
    public void basicStrikeTestIncompleteFrame() {
        Frame firstFrame = new Frame(new Roll(RollType.STRIKE), 0);
        Frame secondFrame = new Frame(new Roll(RollType.THREE), firstFrame.getScore());
        bowlingApplication.frames = Arrays.asList(firstFrame, secondFrame);

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(2, bowlingScoreResponse.getCurrentFrame());
        assertEquals(16, bowlingScoreResponse.getCurrentScore());
        assertEquals(1, bowlingScoreResponse.getRemainingRolls());
        assertEquals(13, firstFrame.getScore());
    }

    @Test
    public void basicStrikeSameFrameTest() {
        Frame firstFrame = new Frame(new Roll(RollType.STRIKE), 0);
        bowlingApplication.frames = Arrays.asList(firstFrame);

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(2, bowlingScoreResponse.getCurrentFrame());
        assertEquals(10, bowlingScoreResponse.getCurrentScore());
        assertEquals(2, bowlingScoreResponse.getRemainingRolls());
        assertEquals(10, firstFrame.getScore());
    }

    @Test
    public void basicSpareTest() {
        Frame firstFrame = new Frame(new Roll(RollType.THREE), 0);
        firstFrame.addSecondRoll(new Roll(RollType.SPARE));
        Frame secondFrame = new Frame(new Roll(RollType.THREE), firstFrame.getScore());
        secondFrame.addSecondRoll(new Roll(RollType.SIX));
        bowlingApplication.frames = Arrays.asList(firstFrame, secondFrame);

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(3, bowlingScoreResponse.getCurrentFrame());
        assertEquals(22, bowlingScoreResponse.getCurrentScore());
        assertEquals(2, bowlingScoreResponse.getRemainingRolls());
        assertEquals(13, firstFrame.getScore());
    }

    @Test
    public void basicSpareSameFrameTest() {
        Frame firstFrame = new Frame(new Roll(RollType.THREE), 0);
        firstFrame.addSecondRoll(new Roll(RollType.SPARE));
        bowlingApplication.frames = Arrays.asList(firstFrame);

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(2, bowlingScoreResponse.getCurrentFrame());
        assertEquals(10, bowlingScoreResponse.getCurrentScore());
        assertEquals(2, bowlingScoreResponse.getRemainingRolls());
        assertEquals(10, firstFrame.getScore());
    }

    @Test
    public void finalFrameStrikeTest() {
        List<Frame> frames = new ArrayList<>();
        int currentScore = 0;
        for (int i = 0; i < 9; i++) {
            Frame frame = new Frame(new Roll(RollType.FOUR), currentScore);
            frame.addSecondRoll(new Roll(RollType.FOUR));
            currentScore = frame.getScore();
            frames.add(frame);
        }

        FinalFrame finalFrame = new FinalFrame(new Roll(RollType.STRIKE), currentScore);
        frames.add(finalFrame);
        BowlingApplication.frames = frames;

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(10, bowlingScoreResponse.getCurrentFrame());
        assertEquals(82, bowlingScoreResponse.getCurrentScore());
        assertEquals(2, bowlingScoreResponse.getRemainingRolls());
    }

    @Test
    public void finalFrameSpareTest() {
        List<Frame> frames = new ArrayList<>();
        int currentScore = 0;
        for (int i = 0; i < 9; i++) {
            Frame frame = new Frame(new Roll(RollType.FOUR), currentScore);
            frame.addSecondRoll(new Roll(RollType.FOUR));
            currentScore = frame.getScore();
            frames.add(frame);
        }

        FinalFrame finalFrame = new FinalFrame(new Roll(RollType.THREE), currentScore);
        finalFrame.addSecondRoll(new Roll(RollType.SPARE));
        frames.add(finalFrame);
        BowlingApplication.frames = frames;

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(10, bowlingScoreResponse.getCurrentFrame());
        assertEquals(82, bowlingScoreResponse.getCurrentScore());
        assertEquals(1, bowlingScoreResponse.getRemainingRolls());
    }

    @Test
    public void finalFrameDoubleStrikeTest() {
        List<Frame> frames = new ArrayList<>();
        int currentScore = 0;
        for (int i = 0; i < 9; i++) {
            Frame frame = new Frame(new Roll(RollType.FOUR), currentScore);
            frame.addSecondRoll(new Roll(RollType.FOUR));
            currentScore = frame.getScore();
            frames.add(frame);
        }

        FinalFrame finalFrame = new FinalFrame(new Roll(RollType.STRIKE), currentScore);
        finalFrame.addSecondRoll(new Roll(RollType.STRIKE));
        frames.add(finalFrame);
        BowlingApplication.frames = frames;

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(10, bowlingScoreResponse.getCurrentFrame());
        assertEquals(92, bowlingScoreResponse.getCurrentScore());
        assertEquals(1, bowlingScoreResponse.getRemainingRolls());
    }

    @Test
    public void finalFrameTripleStrikeTest() {
        List<Frame> frames = new ArrayList<>();
        int currentScore = 0;
        for (int i = 0; i < 9; i++) {
            Frame frame = new Frame(new Roll(RollType.FOUR), currentScore);
            frame.addSecondRoll(new Roll(RollType.FOUR));
            currentScore = frame.getScore();
            frames.add(frame);
        }

        FinalFrame finalFrame = new FinalFrame(new Roll(RollType.STRIKE), currentScore);
        finalFrame.addSecondRoll(new Roll(RollType.STRIKE));
        finalFrame.addThirdRoll(new Roll(RollType.STRIKE));
        frames.add(finalFrame);
        BowlingApplication.frames = frames;

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(10, bowlingScoreResponse.getCurrentFrame());
        assertEquals(102, bowlingScoreResponse.getCurrentScore());
        assertEquals(0, bowlingScoreResponse.getRemainingRolls());
    }

    @Test
    public void finalFrameSpareCompleteTest() {
        List<Frame> frames = new ArrayList<>();
        int currentScore = 0;
        for (int i = 0; i < 9; i++) {
            Frame frame = new Frame(new Roll(RollType.FOUR), currentScore);
            frame.addSecondRoll(new Roll(RollType.FOUR));
            currentScore = frame.getScore();
            frames.add(frame);
        }

        FinalFrame finalFrame = new FinalFrame(new Roll(RollType.THREE), currentScore);
        finalFrame.addSecondRoll(new Roll(RollType.SPARE));
        finalFrame.addThirdRoll(new Roll(RollType.STRIKE));
        frames.add(finalFrame);
        BowlingApplication.frames = frames;

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(10, bowlingScoreResponse.getCurrentFrame());
        assertEquals(92, bowlingScoreResponse.getCurrentScore());
        assertEquals(0, bowlingScoreResponse.getRemainingRolls());
    }

    @Test
    public void perfectGameTest() {
        List<Frame> frames = new ArrayList<>();
        int currentScore = 0;
        for (int i = 0; i < 9; i++) {
            Frame frame = new Frame(new Roll(RollType.STRIKE), currentScore);
            currentScore = frame.getScore();
            frames.add(frame);
        }

        FinalFrame finalFrame = new FinalFrame(new Roll(RollType.STRIKE), currentScore);
        finalFrame.addSecondRoll(new Roll(RollType.STRIKE));
        finalFrame.addThirdRoll(new Roll(RollType.STRIKE));
        frames.add(finalFrame);
        BowlingApplication.frames = frames;

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(10, bowlingScoreResponse.getCurrentFrame());
        assertEquals(300, bowlingScoreResponse.getCurrentScore());
        assertEquals(0, bowlingScoreResponse.getRemainingRolls());
    }

    @Test
    public void mostlyStrikeTest() {
        List<Frame> frames = new ArrayList<>();
        int currentScore = 0;
        for (int i = 0; i < 9; i++) {
            Frame frame = new Frame(new Roll(RollType.STRIKE), currentScore);
            currentScore = frame.getScore();
            frames.add(frame);
        }

        FinalFrame finalFrame = new FinalFrame(new Roll(RollType.THREE), currentScore);
        finalFrame.addSecondRoll(new Roll(RollType.SPARE));
        finalFrame.addThirdRoll(new Roll(RollType.STRIKE));
        frames.add(finalFrame);
        BowlingApplication.frames = frames;

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(10, bowlingScoreResponse.getCurrentFrame());
        assertEquals(273, bowlingScoreResponse.getCurrentScore());
        assertEquals(0, bowlingScoreResponse.getRemainingRolls());
    }

    @Test
    public void mostlySpareTest() {
        List<Frame> frames = new ArrayList<>();
        int currentScore = 0;
        for (int i = 0; i < 9; i++) {
            Frame frame = new Frame(new Roll(RollType.THREE), currentScore);
            frame.addSecondRoll(new Roll(RollType.SPARE));
            currentScore = frame.getScore();
            frames.add(frame);
        }

        FinalFrame finalFrame = new FinalFrame(new Roll(RollType.THREE), currentScore);
        finalFrame.addSecondRoll(new Roll(RollType.SPARE));
        finalFrame.addThirdRoll(new Roll(RollType.FIVE));
        frames.add(finalFrame);
        BowlingApplication.frames = frames;

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(10, bowlingScoreResponse.getCurrentFrame());
        assertEquals(132, bowlingScoreResponse.getCurrentScore());
        assertEquals(0, bowlingScoreResponse.getRemainingRolls());
    }

    @Test
    public void mostlySpareWithFinalAllStrikeTest() {
        List<Frame> frames = new ArrayList<>();
        int currentScore = 0;
        for (int i = 0; i < 9; i++) {
            Frame frame = new Frame(new Roll(RollType.FIVE), currentScore);
            frame.addSecondRoll(new Roll(RollType.SPARE));
            currentScore = frame.getScore();
            frames.add(frame);
        }

        FinalFrame finalFrame = new FinalFrame(new Roll(RollType.STRIKE), currentScore);
        finalFrame.addSecondRoll(new Roll(RollType.STRIKE));
        finalFrame.addThirdRoll(new Roll(RollType.STRIKE));
        frames.add(finalFrame);
        BowlingApplication.frames = frames;

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(10, bowlingScoreResponse.getCurrentFrame());
        assertEquals(170, bowlingScoreResponse.getCurrentScore());
        assertEquals(0, bowlingScoreResponse.getRemainingRolls());
    }

    @Test
    public void mixedRollsTest() {
        List<Frame> frames = new ArrayList<>();
        int currentScore = 0;
        for (int i = 0; i < 2; i++) {
            Frame frame = new Frame(new Roll(RollType.STRIKE), currentScore);
            currentScore = frame.getScore();
            frames.add(frame);
        }

        Frame spareFrame = new Frame(new Roll(RollType.FIVE), currentScore);
        spareFrame.addSecondRoll(new Roll(RollType.SPARE));
        currentScore = spareFrame.getScore();
        frames.add(spareFrame);

        for (int i = 0; i < 2; i++) {
            Frame frame = new Frame(new Roll(RollType.STRIKE), currentScore);
            currentScore = frame.getScore();
            frames.add(frame);
        }

        for (int i = 0; i < 2; i++) {
            spareFrame = new Frame(new Roll(RollType.FIVE), currentScore);
            spareFrame.addSecondRoll(new Roll(RollType.SPARE));
            currentScore = spareFrame.getScore();
            frames.add(spareFrame);
        }

        for (int i = 0; i < 2; i++) {
            Frame frame = new Frame(new Roll(RollType.STRIKE), currentScore);
            currentScore = frame.getScore();
            frames.add(frame);
        }

        FinalFrame finalFrame = new FinalFrame(new Roll(RollType.FIVE), currentScore);
        finalFrame.addSecondRoll(new Roll(RollType.SPARE));
        finalFrame.addThirdRoll(new Roll(RollType.STRIKE));
        frames.add(finalFrame);
        BowlingApplication.frames = frames;

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(10, bowlingScoreResponse.getCurrentFrame());
        assertEquals(210, bowlingScoreResponse.getCurrentScore());
        assertEquals(0, bowlingScoreResponse.getRemainingRolls());
    }

    @Test
    public void cardFlightExampleTest() {
        List<Frame> frames = new ArrayList<>();
        // Input: Strike, 7,Spare, 9,Miss, Strike, Miss,8, 8,Spare, Miss,6, Strike, Strike, Strike,8, 1
        int currentScore = 0;
        Frame firstFrame = new Frame(new Roll(RollType.STRIKE), currentScore);
        currentScore = firstFrame.getScore();
        frames.add(firstFrame);

        Frame secondFrame = new Frame(new Roll(RollType.SEVEN), currentScore);
        secondFrame.addSecondRoll(new Roll(RollType.SPARE));
        currentScore = secondFrame.getScore();
        frames.add(secondFrame);

        Frame thirdFrame = new Frame(new Roll(RollType.NINE), currentScore);
        thirdFrame.addSecondRoll(new Roll(RollType.MISS));
        currentScore = thirdFrame.getScore();
        frames.add(thirdFrame);

        Frame fourthFrame = new Frame(new Roll(RollType.STRIKE), currentScore);
        currentScore = fourthFrame.getScore();
        frames.add(fourthFrame);

        Frame fifthFrame = new Frame(new Roll(RollType.MISS), currentScore);
        fifthFrame.addSecondRoll(new Roll(RollType.EIGHT));
        currentScore = fifthFrame.getScore();
        frames.add(fifthFrame);

        Frame sixthFrame = new Frame(new Roll(RollType.EIGHT), currentScore);
        sixthFrame.addSecondRoll(new Roll(RollType.SPARE));
        currentScore = sixthFrame.getScore();
        frames.add(sixthFrame);

        Frame seventhFrame = new Frame(new Roll(RollType.MISS), currentScore);
        seventhFrame.addSecondRoll(new Roll(RollType.SIX));
        currentScore = seventhFrame.getScore();
        frames.add(seventhFrame);

        Frame eighthFrame = new Frame(new Roll(RollType.STRIKE), currentScore);
        currentScore = eighthFrame.getScore();
        frames.add(eighthFrame);

        Frame ninthFrame = new Frame(new Roll(RollType.STRIKE), currentScore);
        currentScore = ninthFrame.getScore();
        frames.add(ninthFrame);


        FinalFrame finalFrame = new FinalFrame(new Roll(RollType.STRIKE), currentScore);
        finalFrame.addSecondRoll(new Roll(RollType.EIGHT));
        finalFrame.addThirdRoll(new Roll(RollType.ONE));
        frames.add(finalFrame);
        BowlingApplication.frames = frames;

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(10, bowlingScoreResponse.getCurrentFrame());
        assertEquals(167, bowlingScoreResponse.getCurrentScore());
        assertEquals(0, bowlingScoreResponse.getRemainingRolls());
    }

    @Test
    public void finalFrameOneRollTest() {
        List<Frame> frames = new ArrayList<>();
        int currentScore = 0;
        for (int i = 0; i < 9; i++) {
            Frame frame = new Frame(new Roll(RollType.FIVE), currentScore);
            frame.addSecondRoll(new Roll(RollType.SPARE));
            currentScore = frame.getScore();
            frames.add(frame);
        }

        FinalFrame finalFrame = new FinalFrame(new Roll(RollType.FIVE), currentScore);
        frames.add(finalFrame);
        BowlingApplication.frames = frames;

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(1, bowlingScoreResponse.getRemainingRolls());
    }

    @Test
    public void finalFrameOneRollStrikeTest() {
        List<Frame> frames = new ArrayList<>();
        int currentScore = 0;
        for (int i = 0; i < 9; i++) {
            Frame frame = new Frame(new Roll(RollType.FIVE), currentScore);
            frame.addSecondRoll(new Roll(RollType.SPARE));
            currentScore = frame.getScore();
            frames.add(frame);
        }

        FinalFrame finalFrame = new FinalFrame(new Roll(RollType.STRIKE), currentScore);
        frames.add(finalFrame);
        BowlingApplication.frames = frames;

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(2, bowlingScoreResponse.getRemainingRolls());
    }

    @Test
    public void finalFrameTwoRollTest() {
        List<Frame> frames = new ArrayList<>();
        int currentScore = 0;
        for (int i = 0; i < 9; i++) {
            Frame frame = new Frame(new Roll(RollType.FIVE), currentScore);
            frame.addSecondRoll(new Roll(RollType.SPARE));
            currentScore = frame.getScore();
            frames.add(frame);
        }

        FinalFrame finalFrame = new FinalFrame(new Roll(RollType.THREE), currentScore);
        finalFrame.addSecondRoll(new Roll(RollType.SIX));
        frames.add(finalFrame);
        BowlingApplication.frames = frames;

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(0, bowlingScoreResponse.getRemainingRolls());
    }

    @Test
    public void finalFrameTwoRollSpareTest() {
        List<Frame> frames = new ArrayList<>();
        int currentScore = 0;
        for (int i = 0; i < 9; i++) {
            Frame frame = new Frame(new Roll(RollType.FIVE), currentScore);
            frame.addSecondRoll(new Roll(RollType.SPARE));
            currentScore = frame.getScore();
            frames.add(frame);
        }

        FinalFrame finalFrame = new FinalFrame(new Roll(RollType.THREE), currentScore);
        finalFrame.addSecondRoll(new Roll(RollType.SPARE));
        frames.add(finalFrame);
        BowlingApplication.frames = frames;

        BowlingScoreResponse bowlingScoreResponse = bowlingService.calculateCurrentBowlingScores();
        assertEquals(1, bowlingScoreResponse.getRemainingRolls());
    }
}
