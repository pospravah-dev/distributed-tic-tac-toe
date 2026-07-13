package com.edu.tictactoe.gamesession.controller;

import com.edu.tictactoe.gamesession.dto.SessionResponse;
import com.edu.tictactoe.gamesession.exception.SessionNotFoundException;
import com.edu.tictactoe.gamesession.service.SessionService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/sessions")
public class SessionController {
    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public Mono<ResponseEntity<SessionResponse>> createSession() {
        return sessionService.createSession()
            .map(session -> ResponseEntity.ok(new SessionResponse(session)));
    }

    @GetMapping("/{sessionId}")
    public Mono<ResponseEntity<SessionResponse>> getSession(@PathVariable String sessionId) {
        return sessionService.getSession(sessionId)
            .map(session -> ResponseEntity.ok(new SessionResponse(session)));
    }

    @PostMapping("/{sessionId}/simulate")
    public Mono<ResponseEntity<Void>> simulateGame(@PathVariable String sessionId) {
        return sessionService.getSession(sessionId)
                .flatMap(session -> {
                    // Fire-and-forget: start async simulation
                    sessionService.simulateGameAsync(sessionId);
                    return Mono.just(ResponseEntity.accepted()
                            .header("Location", "/sessions/" + sessionId)
                            .<Void>build());
                })
                .onErrorResume(SessionNotFoundException.class, e ->
                        Mono.just(ResponseEntity.notFound().build())
                );
    }
}
