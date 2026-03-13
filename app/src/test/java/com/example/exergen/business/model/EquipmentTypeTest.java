package com.example.exergen.business.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.exergen.model.EquipmentType;

import org.junit.Test;

public class EquipmentTypeTest {
    @Test
    public void isValidLabelAcceptsNewEquipmentTypes() {
        assertTrue(EquipmentType.isValidLabel("E-Z Curl Bar"));
        assertTrue(EquipmentType.isValidLabel("Machine"));
        assertTrue(EquipmentType.isValidLabel("Cable"));
    }

    @Test
    public void fromStringAcceptsSingularAndPluralDumbbell() {
        assertEquals(EquipmentType.DUMBBELLS, EquipmentType.fromString("Dumbbell"));
        assertEquals(EquipmentType.DUMBBELLS, EquipmentType.fromString("Dumbbells"));
    }

    @Test
    public void fromStringAcceptsEnumNameCaseInsensitive() {
        assertEquals(EquipmentType.EZ_CURL_BAR, EquipmentType.fromString("ez_curl_bar"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromStringRejectsBlankValue() {
        EquipmentType.fromString("   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void fromStringRejectsUnknownValue() {
        EquipmentType.fromString("Laser Cannon");
    }

    @Test
    public void isValidLabelReturnsFalseForInvalidValues() {
        assertFalse(EquipmentType.isValidLabel(null));
        assertFalse(EquipmentType.isValidLabel("Unknown"));
    }
}
