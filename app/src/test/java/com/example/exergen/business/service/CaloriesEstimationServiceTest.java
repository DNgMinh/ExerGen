package com.example.exergen.business.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CaloriesEstimationServiceTest {

    private final CaloriesEstimationService service = new CaloriesEstimationService();

    @Test
    public void estimateCalories_UsesDurationAndIntensityMultiplier() {
        // 10 minutes (600s), Intensity 5 (1.6x)
        // 10 * 12.0 * 1.6 = 192
        int estimated = service.estimateCalories(600, 5);
        assertEquals(192, estimated);
    }

    @Test
    public void estimateCaloriesWithDefaultIntensity_UsesMiddleIntensity() {
        // 10 minutes (600s), Default Intensity 3 (1.2x)
        // 10 * 12.0 * 1.2 = 144
        int estimated = service.estimateCaloriesWithDefaultIntensity(600);
        assertEquals(144, estimated);
    }

    @Test
    public void estimateCalories_AllowsMinimumIntensity() {
        // 5 minutes (300s), Intensity 1 (0.8x)
        // 5 * 12.0 * 0.8 = 48
        int estimated = service.estimateCalories(300, 1);
        assertEquals(48, estimated);
    }

    @Test
    public void estimateCalories_SplitWorkRest_CalculatesCorrectly() {
        // 5 min work at Intensity 3 (1.2x) = 5 * 12 * 1.2 = 72
        // 5 min rest = 5 * 2 = 10
        // Total = 82
        int estimated = service.estimateCalories(300, 300, 3);
        assertEquals(82, estimated);
    }

    @Test
    public void estimateCalories_ZeroDurationReturnsZero() {
        int estimated = service.estimateCalories(0, 3);
        assertEquals(0, estimated);
    }

    @Test(expected = IllegalArgumentException.class)
    public void estimateCalories_RejectsNegativeDuration() {
        service.estimateCalories(-1, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void estimateCalories_RejectsIntensityBelowRange() {
        service.estimateCalories(600, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void estimateCalories_RejectsIntensityOutsideRange() {
        service.estimateCalories(600, 6);
    }
}
