package com.flamingo.tictactoe.gameengine.dto;

import java.util.List;

public record MoveResponse(
    String status,
    String winner,
    List<String> board
) {
}
