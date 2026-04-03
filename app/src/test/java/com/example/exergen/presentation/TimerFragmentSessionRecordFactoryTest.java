package com.example.exergen.presentation;

import static org.junit.Assert.assertEquals;

import com.example.exergen.model.SessionRecord;

import org.junit.Test;

public class TimerFragmentSessionRecordFactoryTest {

    @Test
    public void buildSessionRecordForCompletedTimerMapsAllFields() {
        // Calculation: 
        // 4 sets of 30s Work (2 min total) at Level 3 intensity (1.2x) = 2 * 12.0 * 1.2 = 28.8 kcal
        // 4 sets of 10s Rest (40s total) = 40/60 * 2.0 = 1.33 kcal
        // Total = 30.13 kcal -> rounded to 30
        SessionRecord record = TimerFragment.buildSessionRecordForCompletedTimer(
                30,
                10,
                4,
                1700000000000L,
                "session-123");

        assertEquals("session-123", record.getId());
        assertEquals("manual-timer", record.getWorkoutId());
        assertEquals("Manual Interval Timer", record.getWorkoutName());
        assertEquals(1700000000000L, record.getCompletedAtEpochMs());
        assertEquals(160, record.getTotalDurationSeconds());
        assertEquals(1, record.getExerciseCount());
        assertEquals(4, record.getSetsPlanned());
        assertEquals(4, record.getSetsCompleted());
        assertEquals(30, record.getEstimatedCalories());
    }
}
