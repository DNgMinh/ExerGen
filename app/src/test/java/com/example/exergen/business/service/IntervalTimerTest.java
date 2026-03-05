package com.example.exergen.business.service;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class IntervalTimerTest {

    private long lastTickValue;
    private boolean isWorkPhase;
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
            public void onPhaseChange(boolean work) {
                isWorkPhase = work;
            }

            @Override
            public void onFinish() {
                isFinished = true;
                latch.countDown(); // Signal that the test can proceed
            }
        };

        IntervalTimer timer = new IntervalTimer(1, 0, 1, observer);
        timer.start();

        // Wait 3 seconds for it to complete
        boolean completed = latch.await(3, TimeUnit.SECONDS);

        assertTrue("Timer should have completed within 3 seconds", completed);
        assertTrue("onFinish should have been called", isFinished);
        assertEquals("Final tick should be 0", 0, lastTickValue);
    }

    @Test
    public void testPauseAndCancel() {
        IntervalTimer timer = new IntervalTimer(10, 5, 3, null);

        timer.start();
        timer.pause();

        timer.cancel();
        assertNotNull(timer);
    }
}