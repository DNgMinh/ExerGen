package com.example.exergen.business.validation;

import static org.junit.Assert.assertEquals;

import com.example.exergen.business.exception.InvalidTimerConfigurationException;

import org.junit.Test;

import java.util.List;

public class ValidationHelperTest {

    @Test
    public void requireNonBlank_returnsValueWhenValid() {
        String value = ValidationHelper.requireNonBlank("abc", "msg");
        assertEquals("abc", value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void requireNonBlank_throwsForBlank() {
        ValidationHelper.requireNonBlank("   ", "msg");
    }

    @Test
    public void requireNonNullList_returnsListWhenValid() {
        List<String> list = List.of("a");
        assertEquals(list, ValidationHelper.requireNonNullList(list, "msg"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void requireNonNullList_throwsForNull() {
        ValidationHelper.requireNonNullList(null, "msg");
    }

    @Test
    public void requireNonEmptyList_returnsListWhenValid() {
        List<String> list = List.of("a");
        assertEquals(list, ValidationHelper.requireNonEmptyList(list, "msg"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void requireNonEmptyList_throwsForEmpty() {
        ValidationHelper.requireNonEmptyList(List.of(), "msg");
    }

    @Test
    public void requireNonNull_returnsObjectWhenValid() {
        Integer value = ValidationHelper.requireNonNull(7, "msg");
        assertEquals(Integer.valueOf(7), value);
    }

    @Test(expected = IllegalArgumentException.class)
    public void requireNonNull_throwsForNull() {
        ValidationHelper.requireNonNull(null, "msg");
    }

    @Test
    public void requirePositive_returnsValueWhenPositive() {
        int value = ValidationHelper.requirePositive(3, "msg");
        assertEquals(3, value);
    }

    @Test(expected = InvalidTimerConfigurationException.class)
    public void requirePositive_throwsForZero() {
        ValidationHelper.requirePositive(0, "msg");
    }

    @Test
    public void requireNonNegative_returnsValueWhenZero() {
        int value = ValidationHelper.requireNonNegative(0, "msg");
        assertEquals(0, value);
    }

    @Test(expected = InvalidTimerConfigurationException.class)
    public void requireNonNegative_throwsForNegative() {
        ValidationHelper.requireNonNegative(-1, "msg");
    }

    @Test
    public void validateMuscles_acceptsNonEmptyCleanList() {
        ValidationHelper.validateMuscles(List.of("Chest"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateMuscles_throwsForEmptyList() {
        ValidationHelper.validateMuscles(List.of());
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateMuscles_throwsForBlankEntry() {
        ValidationHelper.validateMuscles(List.of(" "));
    }

    @Test
    public void validateEquipment_acceptsEmptyList() {
        ValidationHelper.validateEquipment(List.of());
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateEquipment_throwsForNullList() {
        ValidationHelper.validateEquipment(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateEquipment_throwsForBlankEntry() {
        ValidationHelper.validateEquipment(List.of("Dumbbell", " "));
    }
}
