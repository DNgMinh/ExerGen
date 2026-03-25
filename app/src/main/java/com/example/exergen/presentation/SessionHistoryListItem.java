package com.example.exergen.presentation;

public class SessionHistoryListItem {
    private final String sessionId;
    private final String workoutName;
    private final String summary;

    public SessionHistoryListItem(String sessionId, String workoutName, String summary) {
        this.sessionId = sessionId;
        this.workoutName = workoutName == null ? "" : workoutName;
        this.summary = summary == null ? "" : summary;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getWorkoutName() {
        return workoutName;
    }

    public String getSummary() {
        return summary;
    }
}
