package com.example.exergen.business.model;

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
}
