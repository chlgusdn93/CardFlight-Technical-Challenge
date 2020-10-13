package com.card.flight.bowling;

import com.card.flight.bowling.model.FinalFrame;
import com.card.flight.bowling.model.Frame;
import com.card.flight.bowling.model.Roll;
import com.card.flight.bowling.model.RollType;
import com.card.flight.bowling.service.IBowlingService;
import com.card.flight.bowling.service.helper.BowlingHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class BowlingApplication {
    public static List<Frame> frames = new ArrayList<>();
    private static final Integer FRAME_LIMIT = 10;

    public static void main(String[] args) {
        ConfigurableApplicationContext appContext = SpringApplication.run(BowlingApplication.class, args);
        IBowlingService bowlingService = appContext.getBean(IBowlingService.class);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your rolls one at a time: ");

        int currentScore = 0;
        for (int i = 0; i < FRAME_LIMIT; i++) {
            if (i != FRAME_LIMIT - 1) {
                String firstRollString = null;
                RollType firstRollType = null;
                do {
                    System.out.print("Frame " + (i + 1) + " roll 1: ");
                    firstRollString = scanner.nextLine();
                    firstRollType = BowlingHelper.validateRollInput(firstRollString);
                } while (firstRollType == null);
                Frame frame = new Frame(new Roll(firstRollType), currentScore);
                frames.add(frame);

                String secondRollString = null;
                RollType secondRollType = null;
                if (!firstRollType.equals(RollType.STRIKE)) {
                    do {
                        System.out.print("Frame " + (i + 1) + " roll 2: ");
                        secondRollString = scanner.nextLine();
                        secondRollType = BowlingHelper.validateRollInput(secondRollString, firstRollType);
                    } while (secondRollType == null);
                    frame.addSecondRoll(new Roll(secondRollType));
                }

                currentScore += frame.getScore();
            } else {
                // final frame
                String firstRollString = null;
                RollType firstRollType = null;
                do {
                    System.out.print("Frame 10 roll 1: ");
                    firstRollString = scanner.nextLine();
                    firstRollType = BowlingHelper.validateRollInput(firstRollString);
                } while (firstRollType == null);
                FinalFrame finalFrame = new FinalFrame(new Roll(firstRollType), currentScore);
                frames.add(finalFrame);

                String secondRollString = null;
                RollType secondRollType = null;
                do {
                    System.out.print("Frame 10 roll 2: ");
                    secondRollString = scanner.nextLine();
                    secondRollType = BowlingHelper.validateRollInput(secondRollString, null);
                } while (secondRollType == null);
                finalFrame.addSecondRoll(new Roll(secondRollType));

                if (firstRollType.equals(RollType.STRIKE) || secondRollType.equals(RollType.SPARE)) {
                    String thirdRollString = null;
                    RollType thirdRollType = null;
                    do {
                        System.out.print("Frame 10 roll 3: ");
                        thirdRollString = scanner.nextLine();
                        thirdRollType = BowlingHelper.validateRollInput(thirdRollString, null);
                    } while (thirdRollType == null);
                    finalFrame.addThirdRoll(new Roll(thirdRollType));
                }
            }
        }

        bowlingService.printFinalBowlingScores();
    }
}
