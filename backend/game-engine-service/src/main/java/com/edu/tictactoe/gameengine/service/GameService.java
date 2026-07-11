package com.edu.tictactoe.gameengine.service;

import com.edu.tictactoe.gameengine.domain.Cell;
import com.edu.tictactoe.gameengine.domain.Game;
import com.edu.tictactoe.gameengine.domain.GameState;
import com.edu.tictactoe.gameengine.domain.Move;
import com.edu.tictactoe.gameengine.exception.GameAlreadyEndedException;
import com.edu.tictactoe.gameengine.exception.GameNotFoundException;
import com.edu.tictactoe.gameengine.exception.InvalidMoveException;
import com.edu.tictactoe.gameengine.event.DeltaPatchEvent;
import com.edu.tictactoe.gameengine.dto.MoveRequest;
import com.edu.tictactoe.gameengine.dto.MoveResponse;
import com.edu.tictactoe.gameengine.event.EventEmitterRepository;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {
    private final ConcurrentHashMap<String, Game> games = new ConcurrentHashMap<>();
    private final List<DeltaPatchEvent> eventBuffer = new ArrayList<>();
    private final EventEmitterRepository emitterRepository;

    public GameService(EventEmitterRepository emitterRepository) {
        this.emitterRepository = emitterRepository;
    }

    public Mono<Game> createGame(String gameId) {
        Game game = new Game(gameId);
        games.putIfAbsent(gameId, game);
        return Mono.just(game);
    }

    public Mono<MoveResponse> makeMove(String gameId, MoveRequest request) {
        return Mono.fromSupplier(() -> {
            Game game = games.get(gameId);
            if (game == null) {
                throw new GameNotFoundException(gameId);
            }
            if (!game.getStatus().equals(GameState.IN_PROGRESS)) {
                throw new GameAlreadyEndedException(gameId);
            }

            validateMove(game, request);

            game.getBoard().setCell(request.position(), new Cell(request.player()));
            game.setMoveCount(game.getMoveCount() + 1);
            game.addMove(new Move(request.position(), request.player()));
            game.setLastUpdated(java.time.LocalDateTime.now());

            checkGameState(game);
            game.setPlayerSwitching();
            publishDeltaPatch(game);

            return new MoveResponse(
                game.getStatus().name(),
                game.getWinner(),
                game.getBoard().getCells().stream()
                    .map(Cell::value)
                    .toList()
            );
        });
    }

    private void validateMove(Game game, MoveRequest request) {
        if (request.player() == null || request.player().isEmpty()) {
            throw new IllegalArgumentException("Player cannot be null or empty");
        }
        if (!"X".equals(request.player()) && !"O".equals(request.player())) {
            throw new IllegalArgumentException("Invalid player: " + request.player());
        }
        if (request.position() < 0 || request.position() > 8) {
            throw new IllegalArgumentException("Invalid position: " + request.position());
        }
        if (game.getBoard().isOccupied(request.position())) {
            throw new InvalidMoveException("Cell at position " + request.position() + " is already occupied");
        }
        if (!game.getCurrentPlayer().equals(request.player())) {
            throw new InvalidMoveException("It's not " + request.player() + "'s turn");
        }
    }

    private void checkGameState(Game game) {
        if (isWin(game)) {
            game.setStatus(GameState.WON);
            game.setWinner(game.getCurrentPlayer());
        } else if (game.getMoveCount() >= 9) {
            game.setStatus(GameState.DRAW);
        }
    }

    private boolean isWin(Game game) {
        List<List<Integer>> winPatterns = new ArrayList<>();
        winPatterns.add(List.of(0, 1, 2));
        winPatterns.add(List.of(3, 4, 5));
        winPatterns.add(List.of(6, 7, 8));
        winPatterns.add(List.of(0, 3, 6));
        winPatterns.add(List.of(1, 4, 7));
        winPatterns.add(List.of(2, 5, 8));
        winPatterns.add(List.of(0, 4, 8));
        winPatterns.add(List.of(2, 4, 6));

        String player = game.getCurrentPlayer();
        return winPatterns.stream()
            .anyMatch(pattern -> pattern.stream()
                .allMatch(pos -> game.getBoard().getCell(pos).value().equals(player)));
    }

    private void publishDeltaPatch(Game game) {
        DeltaPatchEvent event = new DeltaPatchEvent(
            game.getId(),
            game.getMoveCount(),
            game.getBoard().getCells().stream().map(Cell::value).toList(),
            game.getCurrentPlayer(),
            game.getStatus().name(),
            game.getWinner()
        );
        eventBuffer.add(event);

        ServerSentEvent<Object> sse = ServerSentEvent.builder()
            .event("game-update")
            .data(event)
            .build();
        emitterRepository.emit(game.getId(), sse);
    }

    public Mono<Game> getGameState(String gameId) {
        return Mono.fromSupplier(() -> {
            Game game = games.get(gameId);
            if (game == null) {
                throw new GameNotFoundException(gameId);
            }
            return game;
        });
    }
}
