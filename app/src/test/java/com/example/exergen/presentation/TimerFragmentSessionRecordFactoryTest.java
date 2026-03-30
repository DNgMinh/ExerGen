package com.example.exergen.presentation;

import static org.junit.Assert.assertEquals;

import com.example.exergen.model.SessionRecord;

import org.junit.Test;

public class TimerFragmentSessionRecordFactoryTest {

    @Test
    public void buildSessionRecordForCompletedTimerMapsAllFields() {
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
        assertEquals(26, record.getEstimatedCalories());
    }
}
