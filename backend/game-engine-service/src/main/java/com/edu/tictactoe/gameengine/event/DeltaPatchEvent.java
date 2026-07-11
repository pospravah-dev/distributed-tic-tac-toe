package com.edu.tictactoe.gameengine.event;

import java.util.List;

public record DeltaPatchEvent(
    String gameId,
    int moveNumber,
    List<String> boardState,
    String currentPlayer,
    String status,
    String winner
) {
}
