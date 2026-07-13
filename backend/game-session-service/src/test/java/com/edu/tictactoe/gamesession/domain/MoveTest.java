package com.edu.tictactoe.gamesession.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MoveTest {

    @Test
    void constructor_shouldCreateImmutableRecord() {
        Move move = new Move(5, "X", 1234567890L);

        assertThat(move.position()).isEqualTo(5);
        assertThat(move.player()).isEqualTo("X");
        assertThat(move.timestamp()).isEqualTo(1234567890L);
    }

    @Test
    void equalsAndHashCode_shouldWorkCorrectly() {
        Move move1 = new Move(5, "X", 1234567890L);
        Move move2 = new Move(5, "X", 1234567890L);
        Move move3 = new Move(6, "X", 1234567890L);

        assertThat(move1).isEqualTo(move2);
        assertThat(move1).isNotEqualTo(move3);
        assertThat(move1.hashCode()).isEqualTo(move2.hashCode());
    }

    @Test
    void toString_shouldContainAllFields() {
        Move move = new Move(5, "X", 1234567890L);

        String toString = move.toString();

        assertThat(toString).contains("5", "X", "1234567890");
    }
}
