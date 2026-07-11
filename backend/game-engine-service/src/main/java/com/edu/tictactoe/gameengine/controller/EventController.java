package com.edu.tictactoe.gameengine.controller;

import com.edu.tictactoe.gameengine.event.EventEmitterRepository;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventEmitterRepository emitterRepository;

    public EventController(EventEmitterRepository emitterRepository) {
        this.emitterRepository = emitterRepository;
    }

    @GetMapping(value = "/{gameId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<?>> streamEvents(@PathVariable String gameId) {
        emitterRepository.createEmitter(gameId);
        return emitterRepository.getEmitter(gameId)
                .switchIfEmpty(Flux.error(new IllegalStateException("Emitter not found: " + gameId)));
    }
}
