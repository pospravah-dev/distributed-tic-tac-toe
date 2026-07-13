package com.edu.tictactoe.gamesession.util;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class IdGeneratorTest {

    @Test
    void generateId_shouldReturnValidFormat() {
        String id = IdGenerator.generateId();

        assertThat(id).startsWith("session-");
    }

    @Test
    void generateId_shouldHaveCorrectLength() {
        String id = IdGenerator.generateId();

        assertThat(id).hasSize(16);
    }

    @Test
    void generateId_shouldReturnUniqueIds() {
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            ids.add(IdGenerator.generateId());
        }

        assertThat(ids).hasSize(1000);
    }

    @Test
    void generateGameId_shouldReturnValidFormat() {
        String id = IdGenerator.generateGameId();

        assertThat(id).startsWith("game-");
    }

    @Test
    void generateGameId_shouldHaveCorrectLength() {
        String id = IdGenerator.generateGameId();

        assertThat(id).hasSize(13);
    }
}
