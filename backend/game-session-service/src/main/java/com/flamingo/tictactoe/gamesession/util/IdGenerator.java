package com.flamingo.tictactoe.gamesession.util;

import java.util.UUID;

public class IdGenerator {
    public static String generateId() {
        return "session-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String generateGameId() {
        return "game-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
