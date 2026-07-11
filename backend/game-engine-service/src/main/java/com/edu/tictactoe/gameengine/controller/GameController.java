package com.edu.tictactoe.gameengine.controller;

import com.edu.tictactoe.gameengine.domain.Game;
import com.flamingo.tictactoe.gameengine.domain.*;
import com.edu.tictactoe.gameengine.dto.MoveRequest;
import com.edu.tictactoe.gameengine.dto.MoveResponse;
import com.edu.tictactoe.gameengine.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/{gameId}/move")
    public Mono<ResponseEntity<MoveResponse>> makeMove(
        @PathVariable String gameId,
        @RequestBody Mono<MoveRequest> request
    ) {
        return request
            .flatMap(req -> gameService.makeMove(gameId, req))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @GetMapping("/{gameId}")
    public Mono<ResponseEntity<Game>> getGameState(@PathVariable String gameId) {
        return gameService.getGameState(gameId)
            .map(ResponseEntity::ok)
            .onErrorReturn(ResponseEntity.notFound().build());
    }

    @PostMapping("/{gameId}")
    public Mono<ResponseEntity<Game>> createGame(@PathVariable String gameId) {
        return gameService.createGame(gameId)
            .map(ResponseEntity::ok);
    }
}
