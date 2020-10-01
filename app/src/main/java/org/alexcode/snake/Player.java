package org.alexcode.snake;

public class Player {
    private String name;
    private String hiScore;

    public Player(String name, String hiScore) {
        this.name = name;
        this.hiScore = hiScore;
    }

    public String getName() {
        return name;
    }

    public String getHiScore() {
        return hiScore;
    }
}

