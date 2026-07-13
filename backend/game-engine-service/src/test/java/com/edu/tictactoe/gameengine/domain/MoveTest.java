package com.edu.tictactoe.gameengine.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MoveTest {

    @Test
    void constructor_shouldSetPosition() {
        Move move = new Move(4, "X");

        assertThat(move.getPosition()).isEqualTo(4);
    }

    @Test
    void constructor_shouldSetPlayer() {
        Move move = new Move(4, "X");

        assertThat(move.getPlayer()).isEqualTo("X");
    }

    @Test
    void constructor_shouldSetTimestamp() {
        Move move = new Move(4, "X");

        assertThat(move.getTimestamp()).isNotNull();
    }

    @Test
    void constructor_shouldSetCurrentTimestamp() {
        java.time.LocalDateTime before = java.time.LocalDateTime.now();
        Move move = new Move(4, "X");
        java.time.LocalDateTime after = java.time.LocalDateTime.now();

        assertThat(move.getTimestamp()).isAfterOrEqualTo(before);
        assertThat(move.getTimestamp()).isBeforeOrEqualTo(after);
    }
}
