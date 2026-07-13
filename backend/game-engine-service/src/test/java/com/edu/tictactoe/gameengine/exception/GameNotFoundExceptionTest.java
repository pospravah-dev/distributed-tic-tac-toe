package com.edu.tictactoe.gameengine.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GameNotFoundExceptionTest {

    @Test
    void constructor_shouldSetMessage() {
        String gameId = "test-id";

        GameNotFoundException exception = new GameNotFoundException(gameId);

        assertThat(exception.getMessage()).contains(gameId);
    }

    @Test
    void shouldThrowGameNotFoundException() {
        assertThatThrownBy(() -> {
            throw new GameNotFoundException("non-existent");
        })
                .isInstanceOf(GameNotFoundException.class)
                .hasMessageContaining("non-existent");
    }
}
