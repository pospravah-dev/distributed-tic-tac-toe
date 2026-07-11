package com.edu.tictactoe.gamesession.client;

import com.edu.tictactoe.gamesession.domain.Game;
import com.edu.tictactoe.gamesession.domain.MoveRequest;
import com.edu.tictactoe.gamesession.domain.MoveResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class GameEngineClient {
    private final WebClient webClient;

    public GameEngineClient(@Value("${game-engine.url:http://localhost:8081}") String gameEngineUrl) {
        this.webClient = WebClient.builder()
            .baseUrl(gameEngineUrl)
            .build();
    }

    public Mono<MoveResponse> makeMove(String gameId, int position, String player) {
        return webClient.post()
            .uri("/games/{gameId}/move", gameId)
            .bodyValue(new MoveRequest(player, position))
            .retrieve()
            .bodyToMono(MoveResponse.class);
    }

    public Mono<MoveResponse> getGameState(String gameId) {
        return webClient.get()
            .uri("/games/{gameId}", gameId)
            .retrieve()
            .bodyToMono(MoveResponse.class);
    }

    public Mono<String> createGame(String gameId) {
        return webClient.post()
            .uri("/games/{gameId}", gameId)
            .retrieve()
            .bodyToMono(String.class);
    }

    public Mono<Game> getGame(String gameId) {
        return webClient.get()
            .uri("/games/{gameId}", gameId)
            .retrieve()
            .bodyToMono(Game.class);
    }
}
