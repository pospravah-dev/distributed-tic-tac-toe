package com.edu.tictactoe.gamesession.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleSessionNotFoundException_shouldReturn404() {
        SessionNotFoundException ex = new SessionNotFoundException("test-123");

        Mono<ResponseEntity<ProblemDetail>> result = handler.handleSessionNotFoundException(ex);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    assertThat(response.getBody()).isNotNull();
                    assertThat(response.getBody().getDetail()).contains("test-123");
                })
                .verifyComplete();
    }

    @Test
    void handleSimulationException_shouldReturn400() {
        SimulationException ex = new SimulationException("simulation failed", new RuntimeException("cause"));

        Mono<ResponseEntity<ProblemDetail>> result = handler.handleSimulationException(ex);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(response.getBody().getDetail()).contains("simulation failed");
                })
                .verifyComplete();
    }

    @Test
    void handleGenericException_shouldReturn500() {
        Exception ex = new RuntimeException("unexpected error");

        Mono<ResponseEntity<ProblemDetail>> result = handler.handleGeneric(ex);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    assertThat(response.getBody().getDetail()).isEqualTo("Unexpected error occurred");
                })
                .verifyComplete();
    }

    @Test
    void handleValidationException_shouldReturn400() {
        WebExchangeBindException ex = mock(WebExchangeBindException.class);
        org.springframework.validation.BindingResult bindingResult = mock(org.springframework.validation.BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(
                Collections.singletonList(new org.springframework.validation.FieldError(
                        "object", "field", "must not be null"
                ))
        );

        Mono<ResponseEntity<ProblemDetail>> result = handler.handleValidation(ex);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    assertThat(response.getBody().getDetail()).contains("must not be null");
                })
                .verifyComplete();
    }

    @Test
    void handleValidation_shouldCollectAllErrors() {
        WebExchangeBindException ex = mock(WebExchangeBindException.class);
        org.springframework.validation.BindingResult bindingResult = mock(org.springframework.validation.BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(
                java.util.Arrays.asList(
                        new org.springframework.validation.FieldError("object", "field1", "error1"),
                        new org.springframework.validation.FieldError("object", "field2", "error2")
                )
        );

        Mono<ResponseEntity<ProblemDetail>> result = handler.handleValidation(ex);

        StepVerifier.create(result)
                .assertNext(response -> {
                    assertThat(response.getBody().getDetail()).contains("error1", "error2");
                })
                .verifyComplete();
    }

    @Test
    void allHandlers_shouldReturnMonoWithResponseEntity() {
        assertThat(handler.handleSessionNotFoundException(new SessionNotFoundException("id")))
                .isNotNull();
        assertThat(handler.handleSimulationException(new SimulationException("msg", new RuntimeException())))
                .isNotNull();
        assertThat(handler.handleGeneric(new RuntimeException()))
                .isNotNull();
    }
}
