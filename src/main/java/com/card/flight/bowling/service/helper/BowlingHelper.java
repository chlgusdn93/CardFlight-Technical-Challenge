package com.card.flight.bowling.service.helper;

import com.card.flight.bowling.model.RollType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class BowlingHelper {
    // Ideally I would've had better validation of the roll inputs, especially for the last frame where if a user
    // rolls a strike, they shouldn't be able to input a spare
    @Nullable
    public static RollType validateRollInput(String rollString) {
        RollType rollType = Arrays.stream(RollType.values()).filter(e -> e.getName().equalsIgnoreCase(rollString)).findAny().orElse(null);
        if (rollType == null) {
            System.out.println("Invalid roll input, try again");
        } else if (rollType.equals(RollType.SPARE)) {
            System.out.println("First roll can not be a spare, try again");
            rollType = null;
        }
        return rollType;
    }

    @Nullable
    public static RollType validateRollInput(String rollString, RollType rollTypeToSumWith) {
        RollType rollType = Arrays.stream(RollType.values()).filter(e -> e.getName().equalsIgnoreCase(rollString)).findAny().orElse(null);
        if (rollType == null) {
            System.out.println("Invalid roll input, try again");
        }
        if ((rollType != null && rollTypeToSumWith != null)
                && !rollType.equals(RollType.SPARE)
                && (rollType.getValue() + rollTypeToSumWith.getValue() >= 10)) {
            System.out.println("Second roll is too high, try again");
            rollType = null;
        }
        return rollType;
    }
}
