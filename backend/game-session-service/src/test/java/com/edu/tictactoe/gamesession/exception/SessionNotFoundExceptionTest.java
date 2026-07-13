package com.edu.tictactoe.gamesession.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionNotFoundExceptionTest {

    @Test
    void constructor_shouldSetMessageWithSessionId() {
        SessionNotFoundException exception = new SessionNotFoundException("test-123");

        assertThat(exception.getMessage()).contains("test-123");
    }

    @Test
    void shouldExtendRuntimeException() {
        SessionNotFoundException exception = new SessionNotFoundException("test-123");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
