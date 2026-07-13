package com.edu.tictactoe.gamesession.service;

import com.edu.tictactoe.gamesession.client.GameEngineClient;
import com.edu.tictactoe.gamesession.domain.GameSessionState;
import com.edu.tictactoe.gamesession.domain.Move;
import com.edu.tictactoe.gamesession.domain.Session;
import com.edu.tictactoe.gamesession.domain.SessionRepository;
import com.edu.tictactoe.gamesession.exception.SessionNotFoundException;
import com.edu.tictactoe.gamesession.exception.SimulationException;
import com.edu.tictactoe.gamesession.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


@Service
public class SessionService {
    public static final int BOARD_SIZE = 9;
    private final SessionRepository sessionRepository;
    private final GameEngineClient gameEngineClient;
    private static final Logger log = LoggerFactory.getLogger(SessionService.class);


    public SessionService(SessionRepository sessionRepository, GameEngineClient gameEngineClient) {
        this.sessionRepository = sessionRepository;
        this.gameEngineClient = gameEngineClient;
    }

    public Mono<Session> createSession() {
        String id = IdGenerator.generateId();
        Session session = new Session(id, id);
        return gameEngineClient.createGame(id)
            .thenReturn(session)
            .doOnSuccess(s -> sessionRepository.save(id, s));
    }

    public Mono<Session> getSession(String sessionId) {
        return Mono.fromSupplier(() -> {
            Session session = sessionRepository.findById(sessionId);
            if (session == null) {
                throw new SessionNotFoundException(sessionId);
            }
            return session;
        });
    }

    public Mono<String> simulateGame(String sessionId) {
        return Mono.fromSupplier(() -> {
            Session session = sessionRepository.findById(sessionId);
            if (session == null) {
                throw new SessionNotFoundException(sessionId);
            }
            return session;
        }).flatMap(this::runSimulationLoop);
    }

    private Mono<String> runSimulationLoop(Session session) {
        if (session.getState().equals(GameSessionState.COMPLETED)) {
            return Mono.just("Simulation completed for session: " + session.getId());
        }

        return gameEngineClient.getGame(session.getGameId())
            .flatMap(game -> {
                String currentPlayer = game.currentPlayer();
                List<Integer> availablePositions = getAvailablePositions(
                        session.getMoveHistory() == null ? List.of() : session.getMoveHistory()
                );

                if (availablePositions.isEmpty() || "DRAW".equals(game.status()) || "WON".equals(game.status())) {
                    session.setState(GameSessionState.COMPLETED);
                    sessionRepository.save(session.getId(), session);
                    return Mono.just("Simulation completed - " + game.status());
                }

                int position = availablePositions.get((int) (Math.random() * availablePositions.size()));

                return gameEngineClient.makeMove(session.getGameId(), position, currentPlayer)
                    .flatMap(moveResponse -> {
                        session.addMove(new Move(position, currentPlayer, System.currentTimeMillis()));
                        session.setLastUpdated(java.time.LocalDateTime.now());

                        if ("WON".equals(moveResponse.status())) {
                            session.setWinner(moveResponse.winner());
                            session.setState(GameSessionState.COMPLETED);
                            sessionRepository.save(session.getId(), session);
                            return Mono.just("Simulation completed - Winner: " + moveResponse.winner());
                        }

                                sessionRepository.save(session.getId(), session);
                        return runSimulationLoop(session);
                    });
            })
            .onErrorResume(e -> Mono.error(new SimulationException("Error during simulation: " + e.getMessage(), e)));
    }

    private List<Integer> getAvailablePositions(List<Move> moveHistory) {
        List<Integer> available = new ArrayList<>();
        IntStream.range(0, BOARD_SIZE).forEach(i -> {
            final int pos = i;
            boolean used = moveHistory.stream().anyMatch(m -> m.position() == pos);
            if (!used) {
                available.add(i);
            }
        });
        return available;
    }

    @Async("taskExecutor")
    public void simulateGameAsync(String sessionId) {
        simulateGame(sessionId)
                .toFuture()
                .thenAccept(result -> {
                    log.info("Simulation completed for session {}: {}", sessionId, result);
                })
                .exceptionally(throwable -> {
                    log.error("Simulation failed for session {}: {}", sessionId, throwable.getMessage());
                    return null;
                });
    }
}
