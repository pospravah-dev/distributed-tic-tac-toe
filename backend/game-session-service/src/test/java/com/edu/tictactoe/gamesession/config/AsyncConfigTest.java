package com.edu.tictactoe.gamesession.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.main.web-application-type=reactive"
})
class AsyncConfigTest {

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Test
    void taskExecutor_shouldHaveCorrectPoolSize() {
        assertThat(taskExecutor.getCorePoolSize()).isEqualTo(5);
        assertThat(taskExecutor.getMaxPoolSize()).isEqualTo(10);
    }

    @Test
    void taskExecutor_shouldHaveCorrectQueueCapacity() {
        assertThat(taskExecutor.getQueueCapacity()).isEqualTo(100);
    }

    @Test
    void taskExecutor_shouldHaveThreadNamePrefix() {
        assertThat(taskExecutor.getThreadNamePrefix()).isEqualTo("simulation-");
    }
}
