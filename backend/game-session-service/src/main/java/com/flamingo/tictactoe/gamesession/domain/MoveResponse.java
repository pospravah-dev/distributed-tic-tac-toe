package com.flamingo.tictactoe.gamesession.domain;

import java.util.List;

public record MoveResponse(
    String status,
    String winner,
    List<String> board
) {
}
