package com.example.exergen.business.service;

import static org.junit.Assert.*;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.MuscleGroup;
import org.junit.Before;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EnumMapperTest {
    private EnumMapper mapper;

    @Before
    public void setUp() {
        mapper = new EnumMapper();
    }

    @Test
    public void testToEquipmentEnums_HandlesPluralAndSingularDumbbells() {
        // Test both variations found in your logs/CSV
        List<String> input = Arrays.asList("Dumbbell", "DUMBBELLS");
        List<EquipmentType> result = mapper.toEquipmentEnums(input);

        assertEquals(2, result.size());
        assertEquals(EquipmentType.DUMBBELLS, result.get(0));
        assertEquals(EquipmentType.DUMBBELLS, result.get(1));
    }

    @Test
    public void testToEquipmentEnums_MapsNewTypes() {
        // Ensure new additions like EZ Curl Bar work
        List<String> input = Arrays.asList("EZ Curl Bar", "Cable");
        List<EquipmentType> result = mapper.toEquipmentEnums(input);

        assertTrue(result.contains(EquipmentType.EZ_CURL_BAR));
        assertTrue(result.contains(EquipmentType.CABLE));
    }

    @Test
    public void testToEquipmentEnums_EmptyListDefaultsToBodyweight() {
        // Validation: Exercise requires non-empty equipment
        List<EquipmentType> result = mapper.toEquipmentEnums(Collections.emptyList());
        assertEquals(1, result.size());
        assertEquals(EquipmentType.BODYWEIGHT, result.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToMuscleEnums_ThrowsOnInvalidData() {
        // Strict testing for your professor's requirements
        mapper.toMuscleEnums(Arrays.asList("Chest", "NotAMuscle"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testToEquipmentEnums_ThrowsOnInvalidData() {
        mapper.toEquipmentEnums(Arrays.asList("Dumbbell", "Magic Wand"));
    }
}