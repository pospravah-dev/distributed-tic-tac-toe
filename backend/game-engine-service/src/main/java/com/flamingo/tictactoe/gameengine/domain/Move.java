package com.flamingo.tictactoe.gameengine.domain;

import java.time.LocalDateTime;

public class Move {
    private final int position;
    private final String player;
    private final LocalDateTime timestamp;

    public Move(int position, String player) {
        this.position = position;
        this.player = player;
        this.timestamp = LocalDateTime.now();
    }

    public int getPosition() {
        return position;
    }

    public String getPlayer() {
        return player;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
