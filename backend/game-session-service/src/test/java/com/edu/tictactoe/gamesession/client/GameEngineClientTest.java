package com.edu.tictactoe.gamesession.client;

import com.edu.tictactoe.gamesession.domain.Game;
import com.edu.tictactoe.gamesession.domain.MoveResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GameEngineClientTest {

    private MockWebServer mockWebServer;
    private GameEngineClient client;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        client = new GameEngineClient("http://localhost:" + mockWebServer.getPort());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void createGame_shouldSendPostRequest() throws Exception {
        mockWebServer.enqueue(new MockResponse().setBody("created").setResponseCode(200));

        StepVerifier.create(client.createGame("test-game"))
                .assertNext(body -> assertThat(body).isEqualTo("created"))
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/games/test-game");
    }

    @Test
    void createGame_shouldReturnResponseBody() {
        mockWebServer.enqueue(new MockResponse().setBody("game-created").setResponseCode(200));

        StepVerifier.create(client.createGame("game-123"))
                .assertNext(body -> assertThat(body).isEqualTo("game-created"))
                .verifyComplete();
    }

    @Test
    void makeMove_shouldSendPostWithBody() throws Exception {
        String jsonResponse = "{\"status\":\"IN_PROGRESS\",\"winner\":null,\"board\":[\"X\",\"O\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"]}";
        mockWebServer.enqueue(new MockResponse()
                .setBody(jsonResponse)
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json"));

        StepVerifier.create(client.makeMove("game-123", 5, "X"))
                .assertNext(response -> assertThat(response.status()).isEqualTo("IN_PROGRESS"))
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/games/game-123/move");
        assertThat(request.getBody().readUtf8()).contains("\"player\":\"X\"", "\"position\":5");
    }

    @Test
    void makeMove_shouldReturnMoveResponse() {
        String jsonResponse = "{\"status\":\"WON\",\"winner\":\"X\",\"board\":[\"X\",\"X\",\"X\",\"O\",\"O\",\"\",\"\",\"\",\"\"]}";
        mockWebServer.enqueue(new MockResponse()
                .setBody(jsonResponse)
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json"));

        StepVerifier.create(client.makeMove("game-123", 2, "X"))
                .assertNext(response -> {
                    assertThat(response.status()).isEqualTo("WON");
                    assertThat(response.winner()).isEqualTo("X");
                    assertThat(response.board()).hasSize(9);
                })
                .verifyComplete();
    }

    @Test
    void getGame_shouldSendGetRequest() throws Exception {
        String jsonResponse = "{\"id\":\"game-123\",\"status\":\"IN_PROGRESS\",\"winner\":null,\"currentPlayer\":\"O\",\"moveCount\":3,\"board\":{\"cells\":[{\"empty\":false},{\"empty\":false},{\"empty\":true}]}}";
        mockWebServer.enqueue(new MockResponse()
                .setBody(jsonResponse)
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json"));

        StepVerifier.create(client.getGame("game-123"))
                .assertNext(game -> {
                    assertThat(game.id()).isEqualTo("game-123");
                    assertThat(game.status()).isEqualTo("IN_PROGRESS");
                    assertThat(game.currentPlayer()).isEqualTo("O");
                })
                .verifyComplete();

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/games/game-123");
    }

    @Test
    void getGame_shouldHandleNotFound() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        StepVerifier.create(client.getGame("non-existent"))
                .expectErrorMatches(throwable -> throwable instanceof org.springframework.web.reactive.function.client.WebClientResponseException.NotFound)
                .verify();
    }

    @Test
    void makeMove_shouldHandleServerError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500).setBody("Internal Server Error"));

        StepVerifier.create(client.makeMove("game-123", 5, "X"))
                .expectErrorMatches(throwable -> throwable instanceof org.springframework.web.reactive.function.client.WebClientResponseException.InternalServerError)
                .verify();
    }

    @Test
    void getGameState_shouldReturnGameState() {
        String jsonResponse = "{\"status\":\"IN_PROGRESS\",\"winner\":null,\"board\":[\"X\",\"O\",\"\",\"\",\"\",\"\",\"\",\"\",\"\"]}";
        mockWebServer.enqueue(new MockResponse()
                .setBody(jsonResponse)
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json"));

        StepVerifier.create(client.getGameState("game-123"))
                .assertNext(response -> assertThat(response.status()).isEqualTo("IN_PROGRESS"))
                .verifyComplete();
    }

    @Test
    void getGameState_shouldHandleError() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(503).setBody("Service Unavailable"));

        StepVerifier.create(client.getGameState("game-123"))
                .expectErrorMatches(throwable -> throwable instanceof org.springframework.web.reactive.function.client.WebClientResponseException.ServiceUnavailable)
                .verify();
    }
}
