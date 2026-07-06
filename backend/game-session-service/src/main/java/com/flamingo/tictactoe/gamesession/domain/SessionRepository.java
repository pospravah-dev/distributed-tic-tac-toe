package com.flamingo.tictactoe.gamesession.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SessionRepository {
    private final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();

    public Session save(String id, Session session) {
        sessions.put(id, session);
        return session;
    }

    public Session findById(String id) {
        return sessions.get(id);
    }

    public Session update(String id, Session session) {
        sessions.put(id, session);
        return session;
    }

    public boolean exists(String id) {
        return sessions.containsKey(id);
    }

    public List<Session> findAll() {
        return new ArrayList<>(sessions.values());
    }
}
