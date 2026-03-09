package com.example.exergen.model;

import java.util.List;

public class ActiveWorkoutSession {
    private final String id;
    private final String templateName;
    private final List<WorkoutSet> sets;
    private final long startTime;
    private long endTime;

    public ActiveWorkoutSession(String id, String templateName, List<WorkoutSet> sets, long startTime) {
        this.id = id;
        this.templateName = templateName;
        this.sets = sets;
        this.startTime = startTime;
        this.endTime = 0; // 0 indicates the session is still actively running
    }

    public String getId() { return id; }
    public String getTemplateName() { return templateName; }
    public List<WorkoutSet> getSets() { return sets; }
    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
