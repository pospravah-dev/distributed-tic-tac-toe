package com.flamingo.tictactoe.gamesession.domain;

import java.time.LocalDateTime;
import java.util.List;

public record SessionResponse(
    String id,
    String state,
    String currentPlayer,
    List<Move> moveHistory,
    String winner,
    LocalDateTime createdAt,
    LocalDateTime lastUpdated
) {
    public SessionResponse(Session session) {
        this(
            session.getId(),
            session.getState().name(),
            session.getCurrentPlayer(),
            session.getMoveHistory(),
            session.getWinner(),
            session.getCreatedAt(),
            session.getLastUpdated()
        );
    }
}
