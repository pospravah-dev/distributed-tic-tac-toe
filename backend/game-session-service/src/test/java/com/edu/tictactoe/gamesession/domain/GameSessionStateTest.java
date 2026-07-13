package com.edu.tictactoe.gamesession.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameSessionStateTest {

    @Test
    void values_shouldContainActiveAndCompleted() {
        GameSessionState[] values = GameSessionState.values();

        assertThat(values).containsExactly(GameSessionState.ACTIVE, GameSessionState.COMPLETED);
    }

    @Test
    void valueOf_shouldParseCorrectly() {
        assertThat(GameSessionState.valueOf("ACTIVE")).isEqualTo(GameSessionState.ACTIVE);
        assertThat(GameSessionState.valueOf("COMPLETED")).isEqualTo(GameSessionState.COMPLETED);
    }
}
