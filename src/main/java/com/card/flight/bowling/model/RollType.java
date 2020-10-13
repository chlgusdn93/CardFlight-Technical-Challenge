package com.card.flight.bowling.model;

public enum RollType {
    ONE("1", "1", 1),
    TWO("2", "2", 2),
    THREE("3", "3", 3),
    FOUR("4", "4", 4),
    FIVE("5", "5", 5),
    SIX("6", "6", 6),
    SEVEN("7", "7", 7),
    EIGHT("8", "8", 8),
    NINE("9", "9", 9),
    SPARE("spare", "/", 10),
    STRIKE("strike", "X", 10),
    MISS("miss", "-", 0);

    private String name;
    private String input;
    private int value;

    RollType(String name, String input, int value) {
        this.name = name;
        this.input = input;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String getInput() {
        return this.input;
    }

    public int getValue() {
        return this.value;
    }
}
