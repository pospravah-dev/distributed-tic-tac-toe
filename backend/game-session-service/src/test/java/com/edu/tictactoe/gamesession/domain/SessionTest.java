package com.edu.tictactoe.gamesession.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SessionTest {

    @Test
    void constructor_shouldInitializeWithActiveState() {
        Session session = new Session("test-id", "game-id");

        assertThat(session.getState()).isEqualTo(GameSessionState.ACTIVE);
    }

    @Test
    void constructor_shouldInitializeWithXPlayer() {
        Session session = new Session("test-id", "game-id");

        assertThat(session.getCurrentPlayer()).isEqualTo("X");
    }

    @Test
    void constructor_shouldSetTimestamps() {
        Session session = new Session("test-id", "game-id");

        assertThat(session.getCreatedAt()).isNotNull();
        assertThat(session.getLastUpdated()).isNotNull();
    }

    @Test
    void getId_shouldReturnId() {
        Session session = new Session("test-id", "game-id");

        assertThat(session.getId()).isEqualTo("test-id");
    }

    @Test
    void getGameId_shouldReturnGameId() {
        Session session = new Session("test-id", "game-id");

        assertThat(session.getGameId()).isEqualTo("game-id");
    }

    @Test
    void addMove_shouldAddToHistory() {
        Session session = new Session("test-id", "game-id");

        session.addMove(new Move(0, "X", System.currentTimeMillis()));

        assertThat(session.getMoveHistory()).hasSize(1);
    }

    @Test
    void getMoveHistory_shouldReturnUnmodifiableCopy() {
        Session session = new Session("test-id", "game-id");
        session.addMove(new Move(0, "X", System.currentTimeMillis()));

        assertThatThrownBy(() -> session.getMoveHistory().add(new Move(1, "O", 0)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void stateTransitions_shouldUpdateCorrectly() {
        Session session = new Session("test-id", "game-id");
        assertThat(session.getState()).isEqualTo(GameSessionState.ACTIVE);

        session.setState(GameSessionState.COMPLETED);

        assertThat(session.getState()).isEqualTo(GameSessionState.COMPLETED);
    }

    @Test
    void setWinner_shouldUpdateWinner() {
        Session session = new Session("test-id", "game-id");

        session.setWinner("X");

        assertThat(session.getWinner()).isEqualTo("X");
    }

    @Test
    void setLastUpdated_shouldUpdateTimestamp() {
        Session session = new Session("test-id", "game-id");

        session.setLastUpdated(java.time.LocalDateTime.now().plusHours(1));

        assertThat(session.getLastUpdated()).isAfter(session.getCreatedAt());
    }
}
