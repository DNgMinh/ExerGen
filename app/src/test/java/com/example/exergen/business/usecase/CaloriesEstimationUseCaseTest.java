package com.example.exergen.business.usecase;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.exergen.business.service.CaloriesEstimationService;

import org.junit.Test;

public class CaloriesEstimationUseCaseTest {

    @Test
    public void estimateCalories_DelegatesToService() {
        CaloriesEstimationService service = mock(CaloriesEstimationService.class);
        when(service.estimateCalories(900, 4)).thenReturn(144);

        CaloriesEstimationUseCase useCase = new CaloriesEstimationUseCase(service);
        int result = useCase.estimateCalories(900, 4);

        assertEquals(144, result);
        verify(service).estimateCalories(900, 4);
    }

    @Test
    public void estimateCaloriesWithDefaultIntensity_DelegatesToService() {
        CaloriesEstimationService service = mock(CaloriesEstimationService.class);
        when(service.estimateCaloriesWithDefaultIntensity(300)).thenReturn(48);

        CaloriesEstimationUseCase useCase = new CaloriesEstimationUseCase(service);
        int result = useCase.estimateCaloriesWithDefaultIntensity(300);

        assertEquals(48, result);
        verify(service).estimateCaloriesWithDefaultIntensity(300);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructor_RejectsNullService() {
        new CaloriesEstimationUseCase(null);
    }
}
