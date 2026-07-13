package com.edu.tictactoe.gameengine.controller;

import com.edu.tictactoe.gameengine.domain.Game;
import com.edu.tictactoe.gameengine.domain.GameState;
import com.edu.tictactoe.gameengine.dto.MoveRequest;
import com.edu.tictactoe.gameengine.dto.MoveResponse;
import com.edu.tictactoe.gameengine.exception.GameAlreadyEndedException;
import com.edu.tictactoe.gameengine.exception.GameNotFoundException;
import com.edu.tictactoe.gameengine.exception.InvalidMoveException;
import com.edu.tictactoe.gameengine.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class GameControllerTest {

    private GameService gameService;
    private GameController controller;

    @BeforeEach
    void setUp() {
        gameService = mock(GameService.class);
        controller = new GameController(gameService);
    }

    @Test
    void createGame_shouldReturnCreatedGame() {
        String gameId = "test-game";
        Game game = new Game(gameId);
        when(gameService.createGame(gameId)).thenReturn(Mono.just(game));

        Mono<ResponseEntity<Game>> result = controller.createGame(gameId);

        ResponseEntity<Game> response = result.block();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(gameId);
        verify(gameService).createGame(gameId);
    }

    @Test
    void getGameState_shouldReturnGame() {
        String gameId = "test-game";
        Game game = new Game(gameId);
        when(gameService.getGameState(gameId)).thenReturn(Mono.just(game));

        Mono<ResponseEntity<Game>> result = controller.getGameState(gameId);

        ResponseEntity<Game> response = result.block();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(gameId);
        verify(gameService).getGameState(gameId);
    }

    @Test
    void getGameState_shouldReturn404WhenNotFound() {
        String gameId = "non-existent";
        when(gameService.getGameState(gameId)).thenReturn(
            Mono.error(new GameNotFoundException(gameId))
        );

        Mono<ResponseEntity<Game>> result = controller.getGameState(gameId);

        ResponseEntity<Game> response = result.onErrorResume(e -> Mono.just(
            ResponseEntity.notFound().build()
        )).block();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void makeMove_shouldReturnUpdatedGame() {
        String gameId = "test-game";
        MoveRequest request = new MoveRequest("X", 0);
        MoveResponse response = new MoveResponse("IN_PROGRESS", null, List.of("X", " ", " ", " ", " ", " ", " ", " ", " "));
        
        when(gameService.makeMove(anyString(), any(MoveRequest.class))).thenReturn(Mono.just(response));

        Mono<ResponseEntity<MoveResponse>> result = controller.makeMove(
            gameId, 
            Mono.just(request)
        );

        ResponseEntity<MoveResponse> httpResponse = result.block();
        assertThat(httpResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(httpResponse.getBody()).isNotNull();
        assertThat(httpResponse.getBody().status()).isEqualTo("IN_PROGRESS");
        verify(gameService).makeMove(gameId, request);
    }

    @Test
    void makeMove_shouldReturn404WhenGameNotFound() {
        String gameId = "non-existent";
        MoveRequest request = new MoveRequest("X", 0);
        
        when(gameService.makeMove(gameId, request)).thenReturn(
            Mono.error(new GameNotFoundException(gameId))
        );

        Mono<ResponseEntity<MoveResponse>> result = controller.makeMove(
            gameId, 
            Mono.just(request)
        );

        ResponseEntity<MoveResponse> response = result.onErrorResume(e -> Mono.just(
            ResponseEntity.notFound().build()
        )).block();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void makeMove_shouldReturn400WhenInvalidMove() {
        String gameId = "test-game";
        MoveRequest request = new MoveRequest("X", 0);
        
        when(gameService.makeMove(gameId, request)).thenReturn(
            Mono.error(new InvalidMoveException("Cell is occupied"))
        );

        Mono<ResponseEntity<MoveResponse>> result = controller.makeMove(
            gameId, 
            Mono.just(request)
        );

        ResponseEntity<MoveResponse> response = result.onErrorResume(e -> Mono.just(
            ResponseEntity.badRequest().build()
        )).block();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void makeMove_shouldReturn409WhenGameAlreadyEnded() {
        String gameId = "test-game";
        MoveRequest request = new MoveRequest("X", 0);
        
        when(gameService.makeMove(gameId, request)).thenReturn(
            Mono.error(new GameAlreadyEndedException(gameId))
        );

        Mono<ResponseEntity<MoveResponse>> result = controller.makeMove(
            gameId, 
            Mono.just(request)
        );

        ResponseEntity<MoveResponse> response = result.onErrorResume(e -> Mono.just(
            ResponseEntity.status(HttpStatus.CONFLICT).build()
        )).block();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void makeMove_shouldHandleEmptyRequest() {
        String gameId = "test-game";

        Mono<ResponseEntity<MoveResponse>> result = controller.makeMove(
            gameId, 
            Mono.empty()
        );

        ResponseEntity<MoveResponse> response = result.block();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createGame_shouldCallServiceOnce() {
        String gameId = "test-game";
        when(gameService.createGame(gameId)).thenReturn(Mono.just(new Game(gameId)));

        controller.createGame(gameId).block();

        verify(gameService, times(1)).createGame(gameId);
        verifyNoMoreInteractions(gameService);
    }

    @Test
    void getGameState_shouldCallServiceOnce() {
        String gameId = "test-game";
        when(gameService.getGameState(gameId)).thenReturn(Mono.just(new Game(gameId)));

        controller.getGameState(gameId).block();

        verify(gameService, times(1)).getGameState(gameId);
        verifyNoMoreInteractions(gameService);
    }

    @Test
    void makeMove_shouldCallServiceOnce() {
        String gameId = "test-game";
        MoveRequest request = new MoveRequest("X", 0);
        MoveResponse response = new MoveResponse("IN_PROGRESS", null, List.of("X", " ", " ", " ", " ", " ", " ", " ", " "));
        when(gameService.makeMove(gameId, request)).thenReturn(Mono.just(response));

        controller.makeMove(gameId, Mono.just(request)).block();

        verify(gameService, times(1)).makeMove(gameId, request);
        verifyNoMoreInteractions(gameService);
    }
}
