package com.example.exergen.business.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.exergen.model.MuscleGroup;

import org.junit.Test;

public class MuscleGroupTest {
    @Test
    public void fromLabelMapsCaseInsensitiveValue() {
        assertEquals(MuscleGroup.FULL_BODY, MuscleGroup.fromLabel("full body"));
    }

    @Test
    public void isValidLabelAcceptsKnownLabels() {
        assertTrue(MuscleGroup.CHEST.isValidLabel("Chest"));
        assertTrue(MuscleGroup.CHEST.isValidLabel("legs"));
    }

    @Test
    public void isValidLabelRejectsUnknownLabel() {
        assertFalse(MuscleGroup.CHEST.isValidLabel("Neck"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromLabelRejectsUnknownLabel() {
        MuscleGroup.fromLabel("Neck");
    }
}
