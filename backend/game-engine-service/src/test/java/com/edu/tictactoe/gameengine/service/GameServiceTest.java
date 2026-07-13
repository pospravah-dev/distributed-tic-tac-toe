package com.edu.tictactoe.gameengine.service;

import com.edu.tictactoe.gameengine.domain.Game;
import com.edu.tictactoe.gameengine.domain.GameState;
import com.edu.tictactoe.gameengine.dto.MoveRequest;
import com.edu.tictactoe.gameengine.dto.MoveResponse;
import com.edu.tictactoe.gameengine.event.EventEmitterRepository;
import com.edu.tictactoe.gameengine.exception.GameAlreadyEndedException;
import com.edu.tictactoe.gameengine.exception.GameNotFoundException;
import com.edu.tictactoe.gameengine.exception.InvalidMoveException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.codec.ServerSentEvent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GameServiceTest {

    private EventEmitterRepository emitterRepository;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        emitterRepository = mock(EventEmitterRepository.class);
        gameService = new GameService(emitterRepository);
    }

    @Test
    void createGame_shouldCreateNewGame() {
        String gameId = "test-game";

        Game result = gameService.createGame(gameId).block();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(gameId);
        assertThat(result.getStatus()).isEqualTo(GameState.IN_PROGRESS);
        assertThat(result.getCurrentPlayer()).isEqualTo("X");
    }

    @Test
    void createGame_shouldInitializeWithEmptyBoard() {
        String gameId = "test-game";

        Game result = gameService.createGame(gameId).block();

        assertThat(result.getBoard().getCells()).hasSize(9);
        assertThat(result.getBoard().getCells()).allMatch(cell -> cell.value().equals(" "));
    }

    @Test
    void makeMove_shouldPlaceXOnBoard() {
        String gameId = "test-game";
        gameService.createGame(gameId).block();
        MoveRequest request = new MoveRequest("X", 0);

        MoveResponse response = gameService.makeMove(gameId, request).block();

        assertThat(response).isNotNull();
        assertThat(response.board().get(0)).isEqualTo("X");
    }

    @Test
    void makeMove_shouldSwitchPlayerAfterMove() {
        String gameId = "test-game";
        gameService.createGame(gameId).block();
        MoveRequest request = new MoveRequest("X", 0);

        gameService.makeMove(gameId, request).block();
        Game game = gameService.getGameState(gameId).block();

        assertThat(game.getCurrentPlayer()).isEqualTo("O");
    }

    @Test
    void makeMove_shouldThrowWhenGameNotFound() {
        MoveRequest request = new MoveRequest("X", 0);

        assertThatThrownBy(() -> gameService.makeMove("non-existent", request).block())
                .isInstanceOf(GameNotFoundException.class);
    }

    @Test
    void makeMove_shouldThrowWhenCellOccupied() {
        String gameId = "test-game";
        gameService.createGame(gameId).block();
        MoveRequest firstMove = new MoveRequest("X", 0);
        gameService.makeMove(gameId, firstMove).block();
        MoveRequest secondMove = new MoveRequest("O", 0);

        assertThatThrownBy(() -> gameService.makeMove(gameId, secondMove).block())
                .isInstanceOf(InvalidMoveException.class)
                .hasMessageContaining("occupied");
    }

    @Test
    void makeMove_shouldThrowWhenWrongPlayerTurn() {
        String gameId = "test-game";
        gameService.createGame(gameId).block();
        MoveRequest wrongPlayerMove = new MoveRequest("O", 0);

        assertThatThrownBy(() -> gameService.makeMove(gameId, wrongPlayerMove).block())
                .isInstanceOf(InvalidMoveException.class)
                .hasMessageContaining("not O's turn");
    }

    @Test
    void makeMove_shouldThrowWhenInvalidPosition() {
        String gameId = "test-game";
        gameService.createGame(gameId).block();
        MoveRequest invalidMove = new MoveRequest("X", 10);

        assertThatThrownBy(() -> gameService.makeMove(gameId, invalidMove).block())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void makeMove_shouldThrowWhenPlayerIsNull() {
        String gameId = "test-game";
        gameService.createGame(gameId).block();
        MoveRequest nullPlayer = new MoveRequest(null, 0);

        assertThatThrownBy(() -> gameService.makeMove(gameId, nullPlayer).block())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void makeMove_shouldThrowWhenPlayerIsEmpty() {
        String gameId = "test-game";
        gameService.createGame(gameId).block();
        MoveRequest emptyPlayer = new MoveRequest("", 0);

        assertThatThrownBy(() -> gameService.makeMove(gameId, emptyPlayer).block())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void makeMove_shouldThrowWhenPlayerIsInvalid() {
        String gameId = "test-game";
        gameService.createGame(gameId).block();
        MoveRequest invalidPlayer = new MoveRequest("Y", 0);

        assertThatThrownBy(() -> gameService.makeMove(gameId, invalidPlayer).block())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void makeMove_shouldDetectWinHorizontal() {
        String gameId = "test-game";
        gameService.createGame(gameId).block();

        gameService.makeMove(gameId, new MoveRequest("X", 0)).block();
        gameService.makeMove(gameId, new MoveRequest("O", 3)).block();
        gameService.makeMove(gameId, new MoveRequest("X", 1)).block();
        gameService.makeMove(gameId, new MoveRequest("O", 4)).block();
        gameService.makeMove(gameId, new MoveRequest("X", 2)).block();

        Game game = gameService.getGameState(gameId).block();

        assertThat(game.getStatus()).isEqualTo(GameState.WON);
        assertThat(game.getWinner()).isEqualTo("X");
    }

    @Test
    void makeMove_shouldDetectWinVertical() {
        String gameId = "test-game";
        gameService.createGame(gameId).block();

        gameService.makeMove(gameId, new MoveRequest("X", 0)).block();
        gameService.makeMove(gameId, new MoveRequest("O", 1)).block();
        gameService.makeMove(gameId, new MoveRequest("X", 3)).block();
        gameService.makeMove(gameId, new MoveRequest("O", 2)).block();
        gameService.makeMove(gameId, new MoveRequest("X", 6)).block();

        Game game = gameService.getGameState(gameId).block();

        assertThat(game.getStatus()).isEqualTo(GameState.WON);
        assertThat(game.getWinner()).isEqualTo("X");
    }

    @Test
    void makeMove_shouldDetectWinDiagonal() {
        String gameId = "test-game";
        gameService.createGame(gameId).block();

        gameService.makeMove(gameId, new MoveRequest("X", 0)).block();
        gameService.makeMove(gameId, new MoveRequest("O", 1)).block();
        gameService.makeMove(gameId, new MoveRequest("X", 4)).block();
        gameService.makeMove(gameId, new MoveRequest("O", 2)).block();
        gameService.makeMove(gameId, new MoveRequest("X", 8)).block();

        Game game = gameService.getGameState(gameId).block();

        assertThat(game.getStatus()).isEqualTo(GameState.WON);
        assertThat(game.getWinner()).isEqualTo("X");
    }

    @Test
    void makeMove_shouldDetectDraw() {
        String gameId = "test-game";
        gameService.createGame(gameId).block();

        gameService.makeMove(gameId, new MoveRequest("X", 0)).block();
        gameService.makeMove(gameId, new MoveRequest("O", 3)).block();
        gameService.makeMove(gameId, new MoveRequest("X", 1)).block();
        gameService.makeMove(gameId, new MoveRequest("O", 4)).block();
        gameService.makeMove(gameId, new MoveRequest("X", 6)).block();
        gameService.makeMove(gameId, new MoveRequest("O", 2)).block();
        gameService.makeMove(gameId, new MoveRequest("X", 5)).block();
        gameService.makeMove(gameId, new MoveRequest("O", 8)).block();
        gameService.makeMove(gameId, new MoveRequest("X", 7)).block();

        Game game = gameService.getGameState(gameId).block();

        assertThat(game.getStatus()).isEqualTo(GameState.DRAW);
        assertThat(game.getWinner()).isNull();
    }

    @Test
    void makeMove_shouldThrowWhenGameAlreadyEnded() {
        String gameId = "test-game";
        gameService.createGame(gameId).block();

        gameService.makeMove(gameId, new MoveRequest("X", 0)).block();
        gameService.makeMove(gameId, new MoveRequest("O", 3)).block();
        gameService.makeMove(gameId, new MoveRequest("X", 1)).block();
        gameService.makeMove(gameId, new MoveRequest("O", 4)).block();
        gameService.makeMove(gameId, new MoveRequest("X", 2)).block();

        assertThatThrownBy(() -> gameService.makeMove(gameId, new MoveRequest("O", 5)).block())
                .isInstanceOf(GameAlreadyEndedException.class);
    }

    @Test
    void makeMove_shouldPublishSSEEvent() {
        String gameId = "test-game";
        gameService.createGame(gameId).block();
        MoveRequest request = new MoveRequest("X", 0);

        gameService.makeMove(gameId, request).block();

        ArgumentCaptor<ServerSentEvent<?>> eventCaptor = ArgumentCaptor.forClass(ServerSentEvent.class);
        verify(emitterRepository).emit(eq(gameId), eventCaptor.capture());

        ServerSentEvent<?> capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent).isNotNull();
        assertThat(capturedEvent.event()).isEqualTo("game-update");
    }

    @Test
    void getGameState_shouldReturnGame() {
        String gameId = "test-game";
        gameService.createGame(gameId).block();

        Game result = gameService.getGameState(gameId).block();

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(gameId);
    }

    @Test
    void getGameState_shouldThrowWhenNotFound() {
        assertThatThrownBy(() -> gameService.getGameState("non-existent").block())
                .isInstanceOf(GameNotFoundException.class);
    }

    @Test
    void makeMove_shouldReturnCorrectResponse() {
        String gameId = "test-game";
        gameService.createGame(gameId).block();
        MoveRequest request = new MoveRequest("X", 4);

        MoveResponse response = gameService.makeMove(gameId, request).block();

        assertThat(response.status()).isEqualTo("IN_PROGRESS");
        assertThat(response.winner()).isNull();
        assertThat(response.board()).hasSize(9);
        assertThat(response.board().get(4)).isEqualTo("X");
    }

    @Test
    void makeMove_shouldIncrementMoveCount() {
        String gameId = "test-game";
        gameService.createGame(gameId).block();

        gameService.makeMove(gameId, new MoveRequest("X", 0)).block();
        gameService.makeMove(gameId, new MoveRequest("O", 1)).block();
        gameService.makeMove(gameId, new MoveRequest("X", 2)).block();

        Game game = gameService.getGameState(gameId).block();

        assertThat(game.getMoveCount()).isEqualTo(3);
    }

    @Test
    void makeMove_shouldAddToMoveHistory() {
        String gameId = "test-game";
        gameService.createGame(gameId).block();

        gameService.makeMove(gameId, new MoveRequest("X", 4)).block();
        gameService.makeMove(gameId, new MoveRequest("O", 0)).block();

        Game game = gameService.getGameState(gameId).block();

        assertThat(game.getMoveHistory()).hasSize(2);
        assertThat(game.getMoveHistory().get(0).getPosition()).isEqualTo(4);
        assertThat(game.getMoveHistory().get(0).getPlayer()).isEqualTo("X");
    }
}
