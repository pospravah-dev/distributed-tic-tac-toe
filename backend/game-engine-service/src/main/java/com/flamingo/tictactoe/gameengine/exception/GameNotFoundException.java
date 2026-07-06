package com.flamingo.tictactoe.gameengine.exception;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String gameId) {
        super("Game not found: " + gameId);
    }
}
