package com.card.flight.bowling.web;

import com.card.flight.bowling.model.response.BowlingScoreResponse;
import com.card.flight.bowling.service.IBowlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BowlingController {

    @Autowired
    private IBowlingService bowlingService;

    @GetMapping(value = "/scores")
    public BowlingScoreResponse getCurrentBowlingScores() {
        return bowlingService.calculateCurrentBowlingScores();
    }
}
