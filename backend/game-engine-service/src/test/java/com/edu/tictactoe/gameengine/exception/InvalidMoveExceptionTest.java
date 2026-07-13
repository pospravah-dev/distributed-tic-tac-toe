package com.edu.tictactoe.gameengine.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InvalidMoveExceptionTest {

    @Test
    void constructor_shouldSetMessage() {
        String message = "Invalid move at position 5";

        InvalidMoveException exception = new InvalidMoveException(message);

        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    void shouldThrowInvalidMoveException() {
        assertThatThrownBy(() -> {
            throw new InvalidMoveException("Cell is occupied");
        })
                .isInstanceOf(InvalidMoveException.class)
                .hasMessageContaining("occupied");
    }
}
