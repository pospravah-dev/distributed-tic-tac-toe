package com.edu.tictactoe.gamesession.integration;

import com.edu.tictactoe.gamesession.dto.SessionResponse;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("integration")
class SessionIntegrationTest {

    @BeforeAll
    static void checkDockerAndImageAvailable() throws IOException, InterruptedException {
        // Check Docker is available
        GenericContainer<?> dummy = new GenericContainer<>("alpine:latest");
        dummy.setCommand("echo test");
        
        // Verify game-engine-service image exists
        ProcessBuilder pb = new ProcessBuilder("docker", "images", "-q", "game-engine-service:test");
        Process process = pb.start();
        int exitCode = process.waitFor();
        
        if (exitCode != 0 || process.getInputStream().readAllBytes().length == 0) {
            throw new IllegalStateException(
                "Docker image 'game-engine-service:test' not found. " +
                "Build it first: docker build -t game-engine-service:test ../game-engine-service");
        }
    }

    @Container
    static GenericContainer<?> gameEngineContainer = new GenericContainer<>("game-engine-service:test")
    .withExposedPorts(8081)
    .waitingFor(
        org.testcontainers.containers.wait.strategy.Wait
            .forHttp("/actuator/health")
            .forPort(8081)
            .forStatusCode(200)
    )
    .withStartupTimeout(Duration.ofMinutes(2));

    @DynamicPropertySource
    static void overrideGameEngineUrl(DynamicPropertyRegistry registry) {
        String url = "http://" + gameEngineContainer.getHost() + ":" + 
                     gameEngineContainer.getMappedPort(8081);
        registry.add("game-engine.url", () -> url);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void createSession_shouldCreateGameInEngine() {
        SessionResponse created = webTestClient.post()
            .uri("/sessions")
            .exchange()
            .expectStatus().isCreated()
            .expectBody(SessionResponse.class)
            .returnResult()
            .getResponseBody();

        assertThat(created).isNotNull();
        assertThat(created.id()).startsWith("session-");
        assertThat(created.state()).isEqualTo("ACTIVE");
        assertThat(created.currentPlayer()).isEqualTo("X");

        webTestClient.get()
            .uri("/sessions/" + created.id())
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.id").isEqualTo(created.id())
            .jsonPath("$.state").isEqualTo("ACTIVE");
    }

    @Test
    void getSession_shouldReturnSessionDetails() {
        SessionResponse created = webTestClient.post()
            .uri("/sessions")
            .exchange()
            .expectStatus().isCreated()
            .expectBody(SessionResponse.class)
            .returnResult()
            .getResponseBody();

        SessionResponse retrieved = webTestClient.get()
            .uri("/sessions/" + created.id())
            .exchange()
            .expectStatus().isOk()
            .expectBody(SessionResponse.class)
            .returnResult()
            .getResponseBody();

        assertThat(retrieved.id()).isEqualTo(created.id());
        assertThat(retrieved.state()).isEqualTo("ACTIVE");
        assertThat(retrieved.moveHistory()).isEmpty();
        assertThat(retrieved.winner()).isNull();
    }

    @Test
    void simulateGame_shouldCompleteSuccessfully() {
        SessionResponse created = webTestClient.post()
            .uri("/sessions")
            .exchange()
            .expectStatus().isCreated()
            .expectBody(SessionResponse.class)
            .returnResult()
            .getResponseBody();

        webTestClient.post()
            .uri("/sessions/" + created.id() + "/simulate")
            .exchange()
            .expectStatus().isAccepted()
            .expectHeader().valueEquals("Location", "/sessions/" + created.id());

        SessionResponse result = Awaitility.await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(500, TimeUnit.MILLISECONDS)
            .until(() -> webTestClient.get()
                    .uri("/sessions/" + created.id())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(SessionResponse.class)
                    .returnResult()
                    .getResponseBody(),
                response -> response != null && "COMPLETED".equals(response.state()));

        assertThat(result).isNotNull();
        assertThat(result.state()).isEqualTo("COMPLETED");
        assertThat(result.winner()).isIn("X", "O", null);
        assertThat(result.moveHistory()).isNotEmpty();
    }

    @Test
    void simulateGame_shouldReturn202Accepted() {
        SessionResponse created = webTestClient.post()
            .uri("/sessions")
            .exchange()
            .expectStatus().isCreated()
            .expectBody(SessionResponse.class)
            .returnResult()
            .getResponseBody();

        var response = webTestClient.post()
            .uri("/sessions/" + created.id() + "/simulate")
            .exchange()
            .expectStatus().isAccepted()
            .expectBody()
            .returnResult();

        assertThat(response.getResponseBody()).isNull();
        assertThat(response.getResponseHeaders().getLocation())
            .hasToString("/sessions/" + created.id());
    }

    @Test
    void multipleSessions_shouldBeIndependent() {
        SessionResponse session1 = webTestClient.post()
            .uri("/sessions")
            .exchange()
            .expectStatus().isCreated()
            .expectBody(SessionResponse.class)
            .returnResult()
            .getResponseBody();

        SessionResponse session2 = webTestClient.post()
            .uri("/sessions")
            .exchange()
            .expectStatus().isCreated()
            .expectBody(SessionResponse.class)
            .returnResult()
            .getResponseBody();

        assertThat(session1.id()).isNotEqualTo(session2.id());

        webTestClient.post()
            .uri("/sessions/" + session1.id() + "/simulate")
            .exchange()
            .expectStatus().isAccepted();

        webTestClient.post()
            .uri("/sessions/" + session2.id() + "/simulate")
            .exchange()
            .expectStatus().isAccepted();

        Awaitility.await()
            .atMost(10, TimeUnit.SECONDS)
            .pollInterval(500, TimeUnit.MILLISECONDS)
            .untilAsserted(() -> {
                SessionResponse s1 = webTestClient.get()
                    .uri("/sessions/" + session1.id())
                    .exchange()
                    .expectBody(SessionResponse.class)
                    .returnResult()
                    .getResponseBody();
                
                SessionResponse s2 = webTestClient.get()
                    .uri("/sessions/" + session2.id())
                    .exchange()
                    .expectBody(SessionResponse.class)
                    .returnResult()
                    .getResponseBody();

                assertThat(s1.state()).isEqualTo("COMPLETED");
                assertThat(s2.state()).isEqualTo("COMPLETED");
            });
    }
}
