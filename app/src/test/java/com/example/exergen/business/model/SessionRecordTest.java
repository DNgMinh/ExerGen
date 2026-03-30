package com.example.exergen.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SessionRecordTest {

    @Test
    public void validSessionRecordCreatesSuccessfully() {
        SessionRecord sessionRecord = new SessionRecord(
                "s1",
                "w1",
                "Leg Day",
                1700000000000L,
                1200,
                6,
                3,
                3);

        assertEquals("s1", sessionRecord.getId());
        assertEquals("w1", sessionRecord.getWorkoutId());
        assertEquals("Leg Day", sessionRecord.getWorkoutName());
        assertEquals(1700000000000L, sessionRecord.getCompletedAtEpochMs());
        assertEquals(1200, sessionRecord.getTotalDurationSeconds());
        assertEquals(6, sessionRecord.getExerciseCount());
        assertEquals(3, sessionRecord.getSetsPlanned());
        assertEquals(3, sessionRecord.getSetsCompleted());
        assertEquals(SessionRecord.UNKNOWN_ESTIMATED_CALORIES, sessionRecord.getEstimatedCalories());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsBlankSessionId() {
        new SessionRecord(" ", "w1", "Leg Day", 1700000000000L, 1200, 6, 3, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsInvalidCompletionTimestamp() {
        new SessionRecord("s1", "w1", "Leg Day", 0L, 1200, 6, 3, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsBlankWorkoutId() {
        new SessionRecord("s1", " ", "Leg Day", 1700000000000L, 1200, 6, 3, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsBlankWorkoutName() {
        new SessionRecord("s1", "w1", " ", 1700000000000L, 1200, 6, 3, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNegativeDuration() {
        new SessionRecord("s1", "w1", "Leg Day", 1700000000000L, -1, 6, 3, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsZeroExerciseCount() {
        new SessionRecord("s1", "w1", "Leg Day", 1700000000000L, 1200, 0, 3, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsZeroSetsPlanned() {
        new SessionRecord("s1", "w1", "Leg Day", 1700000000000L, 1200, 6, 0, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNegativeSetsCompleted() {
        new SessionRecord("s1", "w1", "Leg Day", 1700000000000L, 1200, 6, 3, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsSetsCompletedGreaterThanPlanned() {
        new SessionRecord("s1", "w1", "Leg Day", 1700000000000L, 1200, 6, 3, 4);
    }

    @Test
    public void constructorAcceptsEstimatedCaloriesWhenProvided() {
        SessionRecord sessionRecord = new SessionRecord(
                "s1",
                "w1",
                "Leg Day",
                1700000000000L,
                1200,
                6,
                3,
                3,
                245);
        assertEquals(245, sessionRecord.getEstimatedCalories());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsEstimatedCaloriesLessThanUnknownSentinel() {
        new SessionRecord("s1", "w1", "Leg Day", 1700000000000L, 1200, 6, 3, 3, -2);
    }
}
