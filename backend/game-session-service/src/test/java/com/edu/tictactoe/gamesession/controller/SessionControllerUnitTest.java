package com.edu.tictactoe.gamesession.controller;

import com.edu.tictactoe.gamesession.domain.GameSessionState;
import com.edu.tictactoe.gamesession.domain.Session;
import com.edu.tictactoe.gamesession.exception.SessionNotFoundException;
import com.edu.tictactoe.gamesession.service.SessionService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class SessionControllerUnitTest {

    private final SessionService sessionService = mock(SessionService.class);
    private final SessionController controller = new SessionController(sessionService);

    @Test
    void createSession_shouldReturnSession() {
        Session session = new Session("session-123", "game-123");
        when(sessionService.createSession()).thenReturn(Mono.just(session));

        Mono<org.springframework.http.ResponseEntity<com.edu.tictactoe.gamesession.dto.SessionResponse>> result = 
            controller.createSession();

        org.springframework.http.ResponseEntity<com.edu.tictactoe.gamesession.dto.SessionResponse> response = result.block();
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isEqualTo("session-123");
        verify(sessionService).createSession();
    }

    @Test
    void getSession_shouldReturnSession() {
        Session session = new Session("session-123", "game-123");
        when(sessionService.getSession("session-123")).thenReturn(Mono.just(session));

        var result = controller.getSession("session-123");

        var response = result.block();
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().id()).isEqualTo("session-123");
        verify(sessionService).getSession("session-123");
    }

    @Test
    void getSession_shouldReturn404WhenNotFound() {
        when(sessionService.getSession("non-existent")).thenReturn(
            Mono.error(new SessionNotFoundException("non-existent"))
        );

        var result = controller.getSession("non-existent");

        var response = result.onErrorResume(e -> Mono.just(
            org.springframework.http.ResponseEntity.notFound().build()
        )).block();
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void simulateGame_shouldReturn202Accepted() {
        Session session = new Session("session-123", "game-123");
        when(sessionService.getSession("session-123")).thenReturn(Mono.just(session));
        doNothing().when(sessionService).simulateGameAsync("session-123");

        var result = controller.simulateGame("session-123");

        var response = result.block();
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        assertThat(response.getHeaders().getLocation().toString()).isEqualTo("/sessions/session-123");
        verify(sessionService).getSession("session-123");
        verify(sessionService).simulateGameAsync("session-123");
    }

    @Test
    void simulateGame_shouldReturn404WhenNotFound() {
        when(sessionService.getSession("non-existent")).thenReturn(
            Mono.error(new SessionNotFoundException("non-existent"))
        );

        var result = controller.simulateGame("non-existent");

        var response = result.block();
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(sessionService, never()).simulateGameAsync(anyString());
    }
}
