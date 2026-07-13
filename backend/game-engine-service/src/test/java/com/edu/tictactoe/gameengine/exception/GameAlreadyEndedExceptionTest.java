package com.edu.tictactoe.gameengine.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameAlreadyEndedExceptionTest {

    @Test
    void constructor_shouldSetMessage() {
        String gameId = "test-id";

        GameAlreadyEndedException exception = new GameAlreadyEndedException(gameId);

        assertThat(exception.getMessage()).contains(gameId);
    }

    @Test
    void shouldThrowGameAlreadyEndedException() {
        assertThatThrownBy(() -> {
            throw new GameAlreadyEndedException("already-ended");
        })
                .isInstanceOf(GameAlreadyEndedException.class)
                .hasMessageContaining("already-ended");
    }
}
