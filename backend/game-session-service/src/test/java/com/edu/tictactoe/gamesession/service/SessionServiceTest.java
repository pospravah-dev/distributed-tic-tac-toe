package com.edu.tictactoe.gamesession.service;

import com.edu.tictactoe.gamesession.client.GameEngineClient;
import com.edu.tictactoe.gamesession.domain.Game;
import com.edu.tictactoe.gamesession.domain.GameSessionState;
import com.edu.tictactoe.gamesession.domain.Move;
import com.edu.tictactoe.gamesession.domain.MoveResponse;
import com.edu.tictactoe.gamesession.domain.Session;
import com.edu.tictactoe.gamesession.domain.SessionRepository;
import com.edu.tictactoe.gamesession.exception.SessionNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.atLeastOnce;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private GameEngineClient gameEngineClient;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void createSession_shouldCreateSessionAndCallGameEngine() {
        when(gameEngineClient.createGame(anyString())).thenReturn(Mono.just("game-created"));

        Mono<Session> result = sessionService.createSession();

        StepVerifier.create(result)
                .assertNext(session -> {
                    assertThat(session.getId()).startsWith("session-");
                    assertThat(session.getState()).isEqualTo(GameSessionState.ACTIVE);
                    assertThat(session.getCurrentPlayer()).isEqualTo("X");
                })
                .verifyComplete();

        verify(gameEngineClient).createGame(anyString());
        verify(sessionRepository).save(anyString(), any(Session.class));
    }

    @Test
    void createSession_shouldGenerateValidId() {
        when(gameEngineClient.createGame(anyString())).thenReturn(Mono.just("created"));

        Mono<Session> result = sessionService.createSession();

        StepVerifier.create(result)
                .assertNext(session -> assertThat(session.getId()).startsWith("session-"))
                .verifyComplete();
    }

    @Test
    void createSession_shouldSaveToRepository() {
        when(gameEngineClient.createGame(anyString())).thenReturn(Mono.just("created"));

        sessionService.createSession().block();

        ArgumentCaptor<Session> sessionCaptor = ArgumentCaptor.forClass(Session.class);
        verify(sessionRepository).save(anyString(), sessionCaptor.capture());

        Session savedSession = sessionCaptor.getValue();
        assertThat(savedSession.getState()).isEqualTo(GameSessionState.ACTIVE);
    }

    @Test
    void getSession_shouldReturnSession() {
        Session session = new Session("test-id", "game-id");
        when(sessionRepository.findById("test-id")).thenReturn(session);

        Mono<Session> result = sessionService.getSession("test-id");

        StepVerifier.create(result)
                .assertNext(foundSession -> assertThat(foundSession.getId()).isEqualTo("test-id"))
                .verifyComplete();
    }

    @Test
    void getSession_shouldThrowExceptionWhenNotFound() {
        when(sessionRepository.findById("non-existent")).thenReturn(null);

        Mono<Session> result = sessionService.getSession("non-existent");

        StepVerifier.create(result)
                .expectError(SessionNotFoundException.class)
                .verify();
    }

    @Test
    void simulateGame_shouldCompleteWhenAlreadyCompleted() {
        Session session = new Session("test-id", "game-id");
        session.setState(GameSessionState.COMPLETED);
        when(sessionRepository.findById("test-id")).thenReturn(session);

        Mono<String> result = sessionService.simulateGame("test-id");

        StepVerifier.create(result)
                .assertNext(message -> assertThat(message).contains("Simulation completed"))
                .verifyComplete();
    }

    @Test
    void simulateGame_shouldThrowExceptionWhenSessionNotFound() {
        when(sessionRepository.findById("non-existent")).thenReturn(null);

        Mono<String> result = sessionService.simulateGame("non-existent");

        StepVerifier.create(result)
                .expectError(SessionNotFoundException.class)
                .verify();
    }

    @Test
    void simulateGame_shouldCallGameEngineClient() {
        Session session = new Session("test-id", "game-id");
        when(sessionRepository.findById("test-id")).thenReturn(session);

        Game game = new Game("game-id", "IN_PROGRESS", null, "X", 0,
                new Game.Board(List.of(new Game.Cell(true))));
        when(gameEngineClient.getGame("game-id")).thenReturn(Mono.just(game));

        MoveResponse moveResponse = new MoveResponse("IN_PROGRESS", null, List.of("X", "", "", "", "", "", "", "", ""));
        when(gameEngineClient.makeMove(anyString(), anyInt(), anyString())).thenReturn(Mono.just(moveResponse));

        Mono<String> result = sessionService.simulateGame("test-id");

        StepVerifier.create(result)
                .expectNextMatches(msg -> msg.contains("IN_PROGRESS"))
                .verifyComplete();

        verify(gameEngineClient, atLeastOnce()).getGame("game-id");
        verify(gameEngineClient, atLeastOnce()).makeMove(anyString(), anyInt(), anyString());
    }

    @Test
    void simulateGameAsync_shouldRunInBackground() throws Exception {
        Session session = new Session("test-id", "game-id");
        session.setState(GameSessionState.COMPLETED);
        when(sessionRepository.findById("test-id")).thenReturn(session);

        sessionService.simulateGameAsync("test-id");

        Thread.sleep(100);

        verify(sessionRepository).findById("test-id");
    }

    @Test
    void getAvailablePositions_shouldReturnEmptyWhenAllTaken() {
        List<Move> moveHistory = List.of(
                new Move(0, "X", 1L),
                new Move(1, "O", 2L),
                new Move(2, "X", 3L),
                new Move(3, "O", 4L),
                new Move(4, "X", 5L),
                new Move(5, "O", 6L),
                new Move(6, "X", 7L),
                new Move(7, "O", 8L),
                new Move(8, "X", 9L)
        );

        List<Integer> available = sessionService.getAvailablePositions(moveHistory);

        assertThat(available).isEmpty();
    }

    @Test
    void getAvailablePositions_shouldReturnAvailablePositions() {
        List<Move> moveHistory = List.of(
                new Move(0, "X", 1L),
                new Move(1, "O", 2L)
        );

        List<Integer> available = sessionService.getAvailablePositions(moveHistory);

        assertThat(available).containsExactly(2, 3, 4, 5, 6, 7, 8);
        assertThat(available).doesNotContain(0, 1);
    }

    @Test
    void getAvailablePositions_shouldHandleEmptyHistory() {
        List<Integer> available = sessionService.getAvailablePositions(List.of());

        assertThat(available).hasSize(9);
        assertThat(available).containsExactly(0, 1, 2, 3, 4, 5, 6, 7, 8);
    }
}
