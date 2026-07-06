package com.flamingo.tictactoe.gameengine.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Game {
    private final String id;
    private Board board;
    private GameState status;
    private String currentPlayer;
    private String winner;
    private int moveCount;
    private final List<Move> moveHistory;
    private final LocalDateTime createdAt;
    private LocalDateTime lastUpdated;

    public Game(String id) {
        this.id = id;
        this.board = new Board();
        this.status = GameState.IN_PROGRESS;
        this.currentPlayer = "X";
        this.winner = null;
        this.moveCount = 0;
        this.moveHistory = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public GameState getStatus() {
        return status;
    }

    public void setStatus(GameState status) {
        this.status = status;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void setPlayerSwitching() {
        this.currentPlayer = this.currentPlayer.equals("X") ? "O" : "X";
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public List<Move> getMoveHistory() {
        return List.copyOf(moveHistory);
    }

    public void addMove(Move move) {
        moveHistory.add(move);
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
