package com.example.exergen.business.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.exergen.model.MuscleGroup;

import org.junit.Test;

public class MuscleGroupTest {
    @Test
    public void fromString_ExactLabelMatch_ReturnsEnum() {
        assertEquals(MuscleGroup.CHEST, MuscleGroup.fromString("Chest"));
        assertEquals(MuscleGroup.FULL_BODY, MuscleGroup.fromString("Full Body"));
    }

    @Test
    public void fromString_ExactNameMatch_ReturnsEnum() {
        assertEquals(MuscleGroup.CHEST, MuscleGroup.fromString("CHEST"));
        assertEquals(MuscleGroup.FULL_BODY, MuscleGroup.fromString("FULL_BODY"));
    }

    @Test
    public void fromString_MessyCapitalization_ReturnsEnum() {
        assertEquals(MuscleGroup.LEGS, MuscleGroup.fromString("lEgS"));
        assertEquals(MuscleGroup.FULL_BODY, MuscleGroup.fromString("fUlL bOdY"));
    }

    @Test
    public void fromString_ExtraSpacesAndPunctuation_ReturnsEnum() {
        // Tests the normalize() method's regex [^a-zA-Z0-9]
        assertEquals(MuscleGroup.FULL_BODY, MuscleGroup.fromString("  Full-Body  "));
        assertEquals(MuscleGroup.TRICEPS, MuscleGroup.fromString("tri_ceps"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromString_NullInput_ThrowsException() {
        MuscleGroup.fromString(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromString_EmptyOrWhitespaceInput_ThrowsException() {
        MuscleGroup.fromString("   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromString_InvalidInput_ThrowsException() {
        MuscleGroup.fromString("Brain");
    }

    @Test
    public void isValidLabel_ValidInputs_ReturnsTrue() {
        assertTrue(MuscleGroup.isValidLabel("Chest"));
        assertTrue(MuscleGroup.isValidLabel("CHEST"));
        assertTrue(MuscleGroup.isValidLabel(" c-h-e-s-t "));
    }

    @Test
    public void isValidLabel_InvalidInputs_ReturnsFalse() {
        assertFalse(MuscleGroup.isValidLabel("Brain"));
        assertFalse(MuscleGroup.isValidLabel("Unknown_Muscle"));
    }

    @Test
    public void isValidLabel_NullOrEmptyInputs_ReturnsFalse() {
        assertFalse(MuscleGroup.isValidLabel(null));
        assertFalse(MuscleGroup.isValidLabel(""));
        assertFalse(MuscleGroup.isValidLabel("   "));
    }

    @Test
    public void getLabel_ReturnsCorrectString() {
        assertEquals("Chest", MuscleGroup.CHEST.getLabel());
        assertEquals("Full Body", MuscleGroup.FULL_BODY.getLabel());
    }
}
