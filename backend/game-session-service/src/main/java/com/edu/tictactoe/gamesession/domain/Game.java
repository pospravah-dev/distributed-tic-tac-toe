package com.edu.tictactoe.gamesession.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Game(
    String id,
    String status,
    String winner,
    @JsonProperty("currentPlayer") String currentPlayer,
    @JsonProperty("moveCount") int moveCount,
    @JsonProperty("board") Board board
) {
    public boolean isGameOver() {
        return "WON".equals(status) || "DRAW".equals(status);
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Board(
        List<Cell> cells
    ) {}
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Cell(
        boolean empty
    ) {}
}
