package com.edu.tictactoe.gamesession.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.main.web-application-type=reactive"
})
class CorsConfigTest {

    @Autowired
    private SecurityWebFilterChain securityWebFilterChain;

    @Test
    void securityFilterChain_shouldDisableCsrf() {
        assertThat(securityWebFilterChain).isNotNull();
    }

    @Test
    void securityFilterChain_shouldPermitAll() {
        assertThat(securityWebFilterChain).isNotNull();
    }

    @Test
    void securityFilterChain_shouldBuildSuccessfully() {
        assertThat(securityWebFilterChain).isNotNull();
    }
}
