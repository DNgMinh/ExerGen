package com.example.exergen.business.service;

import java.util.Collections;
import java.util.List;

public final class WorkoutPreviewData {
    private final List<WorkoutPreviewItem> items;

    public WorkoutPreviewData(List<WorkoutPreviewItem> items) {
        if (items == null) {
            throw new IllegalArgumentException("items required.");
        }
        this.items = Collections.unmodifiableList(items);
    }

    public List<WorkoutPreviewItem> getItems() {
        return items;
    }

    public int getExerciseCount() {
        return items.size();
    }
}
