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
    public void toEquipmentEnums_NullInput_ReturnsEmptyList() {
        List<EquipmentType> result = mapper.toEquipmentEnums(null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void toEquipmentEnums_EmptyListInput_ReturnsEmptyList() {
        List<EquipmentType> result = mapper.toEquipmentEnums(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    public void toEquipmentEnums_ValidInputs_ReturnsMappedEnums() {
        List<EquipmentType> result = mapper.toEquipmentEnums(Arrays.asList("Dumbbells", "Barbell"));
        assertEquals(2, result.size());
        assertEquals(EquipmentType.DUMBBELLS, result.get(0));
        assertEquals(EquipmentType.BARBELL, result.get(1));
    }

    @Test
    public void toEquipmentEnums_ListWithNullsAndBlanks_FiltersCorrectly() {
        List<EquipmentType> result = mapper.toEquipmentEnums(Arrays.asList("Dumbbells", null, "", "   ", "Barbell"));
        assertEquals(2, result.size());
        assertEquals(EquipmentType.DUMBBELLS, result.get(0));
        assertEquals(EquipmentType.BARBELL, result.get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toEquipmentEnums_InvalidInput_BubblesUpException() {
        mapper.toEquipmentEnums(Arrays.asList("Dumbbells", "Magic Wand"));
    }

    @Test
    public void toMuscleEnums_NullInput_ReturnsEmptyList() {
        List<MuscleGroup> result = mapper.toMuscleEnums(null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void toMuscleEnums_EmptyListInput_ReturnsEmptyList() {
        List<MuscleGroup> result = mapper.toMuscleEnums(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    public void toMuscleEnums_ValidInputs_ReturnsMappedEnums() {
        List<MuscleGroup> result = mapper.toMuscleEnums(Arrays.asList("Chest", "Back"));

        assertEquals(2, result.size());
        assertEquals(MuscleGroup.CHEST, result.get(0));
        assertEquals(MuscleGroup.BACK, result.get(1));
    }

    @Test
    public void toMuscleEnums_ListWithNullsAndBlanks_FiltersCorrectly() {
        List<MuscleGroup> result = mapper.toMuscleEnums(Arrays.asList("Chest", null, "", "   ", "Back"));

        assertEquals(2, result.size());
        assertEquals(MuscleGroup.CHEST, result.get(0));
        assertEquals(MuscleGroup.BACK, result.get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void toMuscleEnums_InvalidInput_BubblesUpException() {
        mapper.toMuscleEnums(Arrays.asList("Chest", "Brain"));
    }
}