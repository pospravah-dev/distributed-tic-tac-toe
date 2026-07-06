package com.flamingo.tictactoe.gamesession.controller;

import com.flamingo.tictactoe.gamesession.domain.SessionResponse;
import com.flamingo.tictactoe.gamesession.service.SessionService;
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
    public Mono<ResponseEntity<String>> simulateGame(@PathVariable String sessionId) {
        return sessionService.simulateGame(sessionId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
