package com.example.exergen.persistence;

import com.example.exergen.business.repository.ISessionHistoryRepository;
import com.example.exergen.model.SessionRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionHistoryRepositoryStub implements ISessionHistoryRepository {
    private final Map<String, SessionRecord> store = new HashMap<>();

    @Override
    public void saveSession(SessionRecord sessionRecord) {
        if (sessionRecord == null) {
            throw new IllegalArgumentException("sessionRecord required");
        }
        store.put(sessionRecord.getId(), sessionRecord);
    }

    @Override
    public SessionRecord getSessionById(String sessionId) {
        return store.get(sessionId);
    }

    @Override
    public List<SessionRecord> getAllSessions() {
        List<SessionRecord> allSessions = new ArrayList<>(store.values());
        allSessions.sort((left, right) -> Long.compare(
                right.getCompletedAtEpochMs(),
                left.getCompletedAtEpochMs()));
        return allSessions;
    }
}
