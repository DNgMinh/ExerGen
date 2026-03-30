package com.example.exergen.business.usecase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

public class TimerSessionUseCaseTest {

    @Test
    public void initializeAndGetters_WorkForActiveAndInactiveStates() {
        TimerSessionUseCase useCase = new TimerSessionUseCase();

        assertFalse(useCase.hasActiveSession());
        assertFalse(useCase.isRunning());
        assertEquals(0, useCase.getWorkDurationSeconds());
        assertEquals(0, useCase.getRestDurationSeconds());
        assertEquals(0, useCase.getTotalSets());
        assertEquals(1, useCase.getCurrentSet());
        assertEquals(TimerMode.WORK, useCase.getCurrentMode());
        assertEquals(0, useCase.getRemainingSeconds());

        useCase.initialize(30, 10, 3, null);
        assertTrue(useCase.hasActiveSession());
        assertEquals(30, useCase.getWorkDurationSeconds());
        assertEquals(10, useCase.getRestDurationSeconds());
        assertEquals(3, useCase.getTotalSets());

        // Second initialize should not replace active timer.
        useCase.initialize(99, 99, 99, null);
        assertEquals(30, useCase.getWorkDurationSeconds());
        assertEquals(10, useCase.getRestDurationSeconds());
        assertEquals(3, useCase.getTotalSets());

        useCase.stop();
        assertFalse(useCase.hasActiveSession());
    }

    @Test
    public void startPauseAndStop_TransitionsSafely() {
        TimerSessionUseCase useCase = new TimerSessionUseCase();

        useCase.startOrResume(30, 10, 2, null);
        assertTrue(useCase.hasActiveSession());
        assertTrue(useCase.isRunning());

        // Repeated startOrResume while running should be a no-op.
        useCase.startOrResume(30, 10, 2, null);
        assertTrue(useCase.isRunning());

        useCase.pause();
        assertFalse(useCase.isRunning());

        useCase.stop();
        assertFalse(useCase.hasActiveSession());

        // No-op branches should not crash.
        useCase.pause();
        useCase.stop();
    }

    @Test
    public void restoreState_AppliesRestModeAndOptionalRunning() {
        TimerSessionUseCase useCase = new TimerSessionUseCase();
        useCase.restoreState(25, 5, 4, 2, TimerMode.REST, 4, false, null);

        assertTrue(useCase.hasActiveSession());
        assertFalse(useCase.isRunning());
        assertEquals(2, useCase.getCurrentSet());
        assertEquals(TimerMode.REST, useCase.getCurrentMode());
        assertEquals(4, useCase.getRemainingSeconds());

        useCase.restoreState(25, 5, 4, 2, TimerMode.WORK, 6, true, null);
        assertTrue(useCase.isRunning());
        useCase.stop();
    }

    @Test
    public void forwardingObserver_EmitsTickModeAndFinish() throws InterruptedException {
        TimerSessionUseCase useCase = new TimerSessionUseCase();
        AtomicInteger tickCount = new AtomicInteger();
        AtomicInteger modeCount = new AtomicInteger();
        CountDownLatch finishedLatch = new CountDownLatch(1);

        TimerSessionObserver observer = new TimerSessionObserver() {
            @Override
            public void onTick(long secondsRemaining) {
                tickCount.incrementAndGet();
            }

            @Override
            public void onModeChange(TimerMode mode) {
                modeCount.incrementAndGet();
            }

            @Override
            public void onFinish() {
                finishedLatch.countDown();
            }
        };

        useCase.startOrResume(1, 0, 1, observer);

        assertTrue("Expected timer to finish", finishedLatch.await(3, TimeUnit.SECONDS));
        assertTrue(tickCount.get() > 0);
        assertTrue(modeCount.get() > 0);
        assertFalse(useCase.isRunning());

        useCase.stop();
    }
}
