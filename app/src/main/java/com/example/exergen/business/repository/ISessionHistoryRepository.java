package com.example.exergen.business.repository;

import com.example.exergen.model.SessionRecord;

import java.util.List;

public interface ISessionHistoryRepository {
    void saveSession(SessionRecord sessionRecord);

    SessionRecord getSessionById(String sessionId);

    List<SessionRecord> getAllSessions();
}
