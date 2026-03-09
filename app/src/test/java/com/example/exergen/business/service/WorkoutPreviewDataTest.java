package com.example.exergen.business.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class WorkoutPreviewDataTest {

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNullItems() {
        new WorkoutPreviewData(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void itemsAreUnmodifiable() {
        List<WorkoutPreviewItem> items = new ArrayList<>();
        items.add(new WorkoutPreviewItem(1, "Pushup", 30, 10));

        WorkoutPreviewData data = new WorkoutPreviewData(items);
        data.getItems().add(new WorkoutPreviewItem(2, "Squat", 30, 10));
    }

    @Test
    public void getExerciseCountReturnsItemCount() {
        List<WorkoutPreviewItem> items = List.of(
                new WorkoutPreviewItem(1, "Pushup", 30, 10),
                new WorkoutPreviewItem(2, "Squat", 30, 10));

        WorkoutPreviewData data = new WorkoutPreviewData(items);
        assertEquals(2, data.getExerciseCount());
    }
}
