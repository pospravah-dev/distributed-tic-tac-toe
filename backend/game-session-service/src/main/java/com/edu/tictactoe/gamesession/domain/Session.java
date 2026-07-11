package com.edu.tictactoe.gamesession.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Session {
    private final String id;
    private final String gameId;
    private GameSessionState state;
    private String currentPlayer;
    private final List<Move> moveHistory;
    private String winner;
    private final LocalDateTime createdAt;
    private LocalDateTime lastUpdated;

    public Session(String id) {
        this(id, id);
    }

    public Session(String id, String gameId) {
        this.id = id;
        this.gameId = gameId;
        this.state = GameSessionState.ACTIVE;
        this.currentPlayer = "X";
        this.moveHistory = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getGameId() {
        return gameId;
    }

    public GameSessionState getState() {
        return state;
    }

    public void setState(GameSessionState state) {
        this.state = state;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void addMove(Move move) {
        this.moveHistory.add(move);
    }

    public List<Move> getMoveHistory() {
        return List.copyOf(moveHistory);
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
