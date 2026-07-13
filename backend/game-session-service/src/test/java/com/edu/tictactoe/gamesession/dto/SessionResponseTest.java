package com.edu.tictactoe.gamesession.dto;

import com.edu.tictactoe.gamesession.domain.GameSessionState;
import com.edu.tictactoe.gamesession.domain.Move;
import com.edu.tictactoe.gamesession.domain.Session;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SessionResponseTest {

    @Test
    void constructor_shouldMapFromSession() {
        Session session = new Session("session-123", "game-123");
        session.addMove(new Move(0, "X", System.currentTimeMillis()));
        session.setWinner("X");
        session.setState(GameSessionState.COMPLETED);

        SessionResponse response = new SessionResponse(session);

        assertThat(response.id()).isEqualTo("session-123");
        assertThat(response.state()).isEqualTo("COMPLETED");
        assertThat(response.currentPlayer()).isEqualTo("X");
        assertThat(response.winner()).isEqualTo("X");
    }

    @Test
    void shouldIncludeAllSessionFields() {
        Session session = new Session("session-123", "game-123");

        SessionResponse response = new SessionResponse(session);

        assertThat(response.id()).isNotNull();
        assertThat(response.state()).isNotNull();
        assertThat(response.currentPlayer()).isNotNull();
        assertThat(response.moveHistory()).isNotNull();
        assertThat(response.createdAt()).isNotNull();
        assertThat(response.lastUpdated()).isNotNull();
    }

    @Test
    void moveHistory_shouldBeCopied() {
        Session session = new Session("session-123", "game-123");
        Move move = new Move(0, "X", System.currentTimeMillis());
        session.addMove(move);

        SessionResponse response = new SessionResponse(session);

        assertThat(response.moveHistory()).containsExactly(move);
        assertThat(response.moveHistory()).isNotSameAs(session.getMoveHistory());
    }

    @Test
    void state_shouldBeEnumName() {
        Session session = new Session("session-123", "game-123");
        session.setState(GameSessionState.ACTIVE);

        SessionResponse response = new SessionResponse(session);

        assertThat(response.state()).isEqualTo("ACTIVE");
    }
}
