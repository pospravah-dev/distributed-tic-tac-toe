package com.flamingo.tictactoe.gameengine.exception;

public class GameAlreadyEndedException extends RuntimeException {
    public GameAlreadyEndedException(String gameId) {
        super("Game is already ended: " + gameId);
    }
}
