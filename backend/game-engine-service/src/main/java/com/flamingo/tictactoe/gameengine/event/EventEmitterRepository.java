package com.flamingo.tictactoe.gameengine.event;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import org.springframework.http.codec.ServerSentEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EventEmitterRepository {
    private final Map<String, Sinks.Many<ServerSentEvent<?>>> emitters = new ConcurrentHashMap<>();

    public Sinks.Many<ServerSentEvent<?>> createEmitter(String gameId) {
        return emitters.computeIfAbsent(gameId, k -> Sinks.many().multicast().onBackpressureBuffer());
    }

    public void removeEmitter(String gameId) {
        emitters.remove(gameId);
    }

    public Flux<ServerSentEvent<?>> getEmitter(String gameId) {
        Sinks.Many<ServerSentEvent<?>> sink = emitters.get(gameId);
        return sink != null ? sink.asFlux() : Flux.empty();
    }

    public void emit(String gameId, ServerSentEvent<?> event) {
        Sinks.Many<ServerSentEvent<?>> sink = emitters.get(gameId);
        if (sink != null) {
            sink.tryEmitNext(event);
        }
    }
}
