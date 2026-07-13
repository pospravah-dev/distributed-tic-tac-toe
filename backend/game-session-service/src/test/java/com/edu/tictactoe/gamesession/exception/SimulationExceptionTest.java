package com.edu.tictactoe.gamesession.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimulationExceptionTest {

    @Test
    void constructor_shouldSetMessageAndCause() {
        Exception cause = new RuntimeException("cause");
        SimulationException exception = new SimulationException("test message", cause);

        assertThat(exception.getMessage()).contains("test message");
        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    void shouldExtendRuntimeException() {
        SimulationException exception = new SimulationException("test", new RuntimeException());

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
