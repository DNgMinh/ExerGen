package com.example.exergen.business.service;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class WorkoutPreviewItemTest {

    @Test
    public void testGetters_ReturnCorrectValues() {
        WorkoutPreviewItem item = new WorkoutPreviewItem(1, "Pushups", 30, 10);
        
        assertEquals(1, item.getSequence());
        assertEquals("Pushups", item.getExerciseName());
        assertEquals(30, item.getWorkSeconds());
        assertEquals(10, item.getRestSeconds());
    }
}
