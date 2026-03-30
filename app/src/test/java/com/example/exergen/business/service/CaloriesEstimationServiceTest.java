package com.example.exergen.business.service;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CaloriesEstimationServiceTest {

    private final CaloriesEstimationService service = new CaloriesEstimationService();

    @Test
    public void estimateCalories_UsesDurationAndIntensityMultiplier() {
        int estimated = service.estimateCalories(600, 5);
        assertEquals(128, estimated);
    }

    @Test
    public void estimateCaloriesWithDefaultIntensity_UsesMiddleIntensity() {
        int estimated = service.estimateCaloriesWithDefaultIntensity(600);
        assertEquals(96, estimated);
    }

    @Test(expected = IllegalArgumentException.class)
    public void estimateCalories_RejectsNegativeDuration() {
        service.estimateCalories(-1, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void estimateCalories_RejectsIntensityOutsideRange() {
        service.estimateCalories(600, 6);
    }
}
