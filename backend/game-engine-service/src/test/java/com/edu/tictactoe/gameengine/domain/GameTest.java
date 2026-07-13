package com.edu.tictactoe.gameengine.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameTest {

    @Test
    void constructor_shouldInitializeWithInGameState() {
        Game game = new Game("test-id");

        assertThat(game.getStatus()).isEqualTo(GameState.IN_PROGRESS);
    }

    @Test
    void constructor_shouldInitializeWithXPlayer() {
        Game game = new Game("test-id");

        assertThat(game.getCurrentPlayer()).isEqualTo("X");
    }

    @Test
    void constructor_shouldSetInitialMoveCountToZero() {
        Game game = new Game("test-id");

        assertThat(game.getMoveCount()).isZero();
    }

    @Test
    void constructor_shouldSetEmptyWinner() {
        Game game = new Game("test-id");

        assertThat(game.getWinner()).isNull();
    }

    @Test
    void constructor_shouldInitializeBoardWithEmptyCells() {
        Game game = new Game("test-id");

        assertThat(game.getBoard().getCells()).hasSize(9);
        assertThat(game.getBoard().getCells()).allMatch(Cell::isEmpty);
    }

    @Test
    void constructor_shouldSetTimestamps() {
        Game game = new Game("test-id");

        assertThat(game.getCreatedAt()).isNotNull();
        assertThat(game.getLastUpdated()).isNotNull();
    }

    @Test
    void getId_shouldReturnId() {
        Game game = new Game("test-id");

        assertThat(game.getId()).isEqualTo("test-id");
    }

    @Test
    void setPlayerSwitching_shouldToggleFromXtoO() {
        Game game = new Game("test-id");

        game.setPlayerSwitching();

        assertThat(game.getCurrentPlayer()).isEqualTo("O");
    }

    @Test
    void setPlayerSwitching_shouldToggleFromOtoX() {
        Game game = new Game("test-id");
        game.setCurrentPlayer("O");

        game.setPlayerSwitching();

        assertThat(game.getCurrentPlayer()).isEqualTo("X");
    }

    @Test
    void setStatus_shouldUpdateState() {
        Game game = new Game("test-id");

        game.setStatus(GameState.WON);

        assertThat(game.getStatus()).isEqualTo(GameState.WON);
    }

    @Test
    void setWinner_shouldUpdateWinner() {
        Game game = new Game("test-id");

        game.setWinner("X");

        assertThat(game.getWinner()).isEqualTo("X");
    }

    @Test
    void setMoveCount_shouldUpdateMoveCount() {
        Game game = new Game("test-id");

        game.setMoveCount(5);

        assertThat(game.getMoveCount()).isEqualTo(5);
    }

    @Test
    void addMove_shouldAddToHistory() {
        Game game = new Game("test-id");

        game.addMove(new Move(0, "X"));

        assertThat(game.getMoveHistory()).hasSize(1);
    }

    @Test
    void getMoveHistory_shouldReturnUnmodifiableCopy() {
        Game game = new Game("test-id");
        game.addMove(new Move(0, "X"));

        assertThatThrownBy(() -> game.getMoveHistory().add(new Move(1, "O")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void setLastUpdated_shouldUpdateTimestamp() {
        Game game = new Game("test-id");

        game.setLastUpdated(game.getCreatedAt().plusHours(1));

        assertThat(game.getLastUpdated()).isAfter(game.getCreatedAt());
    }

    @Test
    void setBoard_shouldUpdateBoard() {
        Game game = new Game("test-id");
        Board newBoard = new Board();

        game.setBoard(newBoard);

        assertThat(game.getBoard()).isEqualTo(newBoard);
    }
}
