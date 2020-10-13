package com.card.flight.bowling.service;

import com.card.flight.bowling.model.response.BowlingScoreResponse;

public interface IBowlingService {
    void printFinalBowlingScores();

    BowlingScoreResponse calculateCurrentBowlingScores();
}
