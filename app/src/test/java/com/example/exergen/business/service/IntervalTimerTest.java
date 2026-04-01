package com.example.exergen.business.service;

import static org.junit.Assert.*;
import com.example.exergen.business.exception.InvalidTimerConfigurationException;
import com.example.exergen.business.exception.TimerAlreadyRunningException;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class IntervalTimerTest {

    private long lastTickValue;
    private TimerPhase phase;
    private boolean isFinished;

    @Test
    public void testTimerLifecycle() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        // Observer to track the state
        TimerObserver observer = new TimerObserver() {
            @Override
            public void onTick(long seconds) {
                lastTickValue = seconds;
            }

            @Override
            public void onPhaseChange(TimerPhase timerPhase) {
                phase = timerPhase;
            }

            @Override
            public void onFinish() {
                isFinished = true;
                latch.countDown(); // Signal that the test can proceed
            }
        };

        IntervalTimer timer = new IntervalTimer(List.of(1), List.of(0), 1, observer);
        timer.start();

        // Wait 3 seconds for it to complete
        boolean completed = latch.await(3, TimeUnit.SECONDS);

        assertTrue("Timer should have completed within 3 seconds", completed);
        assertTrue("onFinish should have been called", isFinished);
        assertEquals("Final tick should be 0", 0, lastTickValue);
        assertEquals(TimerPhase.WORK, phase);
    }

    @Test
    public void testPauseAndCancel() {
        IntervalTimer timer = new IntervalTimer(List.of(10), List.of(5), 3, null);

        timer.start();
        timer.pause();

        timer.cancel();
        assertNotNull(timer);
    }

    @Test(expected = TimerAlreadyRunningException.class)
    public void startWhileRunningThrowsDomainException() {
        IntervalTimer timer = new IntervalTimer(List.of(2), List.of(1), 1, null);
        timer.start();
        timer.start();
    }

    @Test(expected = InvalidTimerConfigurationException.class)
    public void constructorRejectsNonPositiveWorkSeconds() {
        new IntervalTimer(List.of(0), List.of(1), 1, null);
    }

    @Test(expected = InvalidTimerConfigurationException.class)
    public void constructorRejectsNegativeRestSeconds() {
        new IntervalTimer(List.of(1), List.of(-1), 1, null);
    }

    @Test(expected = InvalidTimerConfigurationException.class)
    public void constructorRejectsNonPositiveSets() {
        new IntervalTimer(List.of(1), List.of(1), 0, null);
    }

    @Test
    public void cancelResetsTimerState() {
        IntervalTimer timer = new IntervalTimer(List.of(5), List.of(2), 3, null);
        timer.restoreState(2, TimerPhase.REST, 1);

        timer.cancel();

        assertEquals(1, timer.getCurrentSet());
        assertEquals(TimerPhase.WORK, timer.getCurrentPhase());
        assertEquals(5, timer.getRemainingSeconds());
        assertFalse(timer.isRunning());
    }

    @Test
    public void timerWithRestPhaseTransitionsToRestBeforeFinish() throws InterruptedException {
        CountDownLatch finishedLatch = new CountDownLatch(1);
        List<TimerPhase> phases = new ArrayList<>();

        TimerObserver observer = new TimerObserver() {
            @Override
            public void onTick(long secondsRemaining) {
            }

            @Override
            public void onPhaseChange(TimerPhase phase) {
                phases.add(phase);
            }

            @Override
            public void onFinish() {
                finishedLatch.countDown();
            }
        };

        IntervalTimer timer = new IntervalTimer(List.of(1), List.of(1), 1, observer);
        timer.start();

        boolean completed = finishedLatch.await(6, TimeUnit.SECONDS);
        assertTrue("Timer should finish with rest phase enabled", completed);
        assertTrue("Should include initial WORK phase callback", phases.contains(TimerPhase.WORK));
        assertTrue("Should include REST phase callback", phases.contains(TimerPhase.REST));
    }
}
