package com.example.exergen.business.usecase;

import com.example.exergen.business.repository.ISessionHistoryRepository;
import com.example.exergen.model.SessionRecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SessionHistoryUseCase {
    private final ISessionHistoryRepository sessionHistoryRepository;

    public SessionHistoryUseCase(ISessionHistoryRepository sessionHistoryRepository) {
        if (sessionHistoryRepository == null) {
            throw new IllegalArgumentException("sessionHistoryRepository required");
        }
        this.sessionHistoryRepository = sessionHistoryRepository;
    }

    public void saveCompletedSession(SessionRecord sessionRecord) {
        if (sessionRecord == null) {
            throw new IllegalArgumentException("sessionRecord required");
        }
        sessionHistoryRepository.saveSession(sessionRecord);
    }

    public List<SessionRecord> getSessionHistory() {
        List<SessionRecord> sessions = sessionHistoryRepository.getAllSessions();
        if (sessions == null || sessions.isEmpty()) {
            return new ArrayList<>();
        }

        List<SessionRecord> orderedSessions = new ArrayList<>(sessions);
        orderedSessions.sort(Comparator.comparingLong(SessionRecord::getCompletedAtEpochMs).reversed());
        return orderedSessions;
    }
}
