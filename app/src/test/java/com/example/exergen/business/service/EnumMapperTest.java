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
    private IEnumMapper enumMapper;

    @Before
    public void setUp() {
        enumMapper = new EnumMapper();
    }

    @Test
    public void toEquipmentEnums_NullInput_ReturnsEmptyList() {
        List<EquipmentType> result = enumMapper.toEquipmentEnums(null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void toEquipmentEnums_EmptyListInput_ReturnsEmptyList() {
        List<EquipmentType> result = enumMapper.toEquipmentEnums(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    public void toEquipmentEnums_ValidInputs_ReturnsMappedEnums() {
        List<EquipmentType> result = enumMapper.toEquipmentEnums(Arrays.asList("Dumbbells", "Barbell"));
        assertEquals(2, result.size());
        assertEquals(EquipmentType.DUMBBELLS, result.get(0));
        assertEquals(EquipmentType.BARBELL, result.get(1));
    }

    @Test
    public void toEquipmentEnums_ListWithNullsAndBlanks_FiltersCorrectly() {
        List<EquipmentType> result = enumMapper.toEquipmentEnums(Arrays.asList("Dumbbells", null, "", "   ", "Barbell"));
        assertEquals(2, result.size());
        assertEquals(EquipmentType.DUMBBELLS, result.get(0));
        assertEquals(EquipmentType.BARBELL, result.get(1));
    }

    @Test
    public void toEquipmentEnums_InvalidInput_SkipsInvalidAndContinues() {
        List<EquipmentType> result = enumMapper.toEquipmentEnums(Arrays.asList("Dumbbells", "Magic Wand"));
        assertEquals(1, result.size());
        assertEquals(EquipmentType.DUMBBELLS, result.get(0));
    }

    @Test
    public void toMuscleEnums_NullInput_ReturnsEmptyList() {
        List<MuscleGroup> result = enumMapper.toMuscleEnums(null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void toMuscleEnums_EmptyListInput_ReturnsEmptyList() {
        List<MuscleGroup> result = enumMapper.toMuscleEnums(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    @Test
    public void toMuscleEnums_ValidInputs_ReturnsMappedEnums() {
        List<MuscleGroup> result = enumMapper.toMuscleEnums(Arrays.asList("Chest", "Back"));

        assertEquals(2, result.size());
        assertEquals(MuscleGroup.CHEST, result.get(0));
        assertEquals(MuscleGroup.BACK, result.get(1));
    }

    @Test
    public void toMuscleEnums_ListWithNullsAndBlanks_FiltersCorrectly() {
        List<MuscleGroup> result = enumMapper.toMuscleEnums(Arrays.asList("Chest", null, "", "   ", "Back"));

        assertEquals(2, result.size());
        assertEquals(MuscleGroup.CHEST, result.get(0));
        assertEquals(MuscleGroup.BACK, result.get(1));
    }

    @Test
    public void toMuscleEnums_InvalidInput_SkipsInvalidAndContinues() {
        List<MuscleGroup> result = enumMapper.toMuscleEnums(Arrays.asList("Chest", "Brain"));
        assertEquals(1, result.size());
        assertEquals(MuscleGroup.CHEST, result.get(0));
    }

    @Test
    public void toEquipmentLabels_NullInput_ReturnsEmptyList() {
        List<String> result = enumMapper.toEquipmentLabels(null);

        assertTrue("Expected an empty list for null input", result.isEmpty());
    }

    @Test
    public void toEquipmentLabels_EmptyListInput_ReturnsEmptyList() {
        List<String> result = enumMapper.toEquipmentLabels(Collections.emptyList());

        assertTrue("Expected an empty list for empty input", result.isEmpty());
    }

    @Test
    public void toEquipmentLabels_ValidEnums_ReturnsCorrectLabels() {
        List<String> result = enumMapper.toEquipmentLabels(Arrays.asList(EquipmentType.DUMBBELLS, EquipmentType.EZ_CURL_BAR));

        assertEquals(2, result.size());
        assertEquals("Dumbbells", result.get(0));
        assertEquals("E-Z Curl Bar", result.get(1));
    }

    @Test
    public void toMuscleLabels_NullInput_ReturnsEmptyList() {
        List<String> result = enumMapper.toMuscleLabels(null);

        assertTrue("Expected an empty list for null input", result.isEmpty());
    }

    @Test
    public void toMuscleLabels_EmptyListInput_ReturnsEmptyList() {
        List<String> result = enumMapper.toMuscleLabels(Collections.emptyList());

        assertTrue("Expected an empty list for empty input", result.isEmpty());
    }

    @Test
    public void toMuscleLabels_ValidEnums_ReturnsCorrectLabels() {
        List<String> result = enumMapper.toMuscleLabels(Arrays.asList(MuscleGroup.CHEST, MuscleGroup.FULL_BODY));

        assertEquals(2, result.size());
        assertEquals("Chest", result.get(0));
        assertEquals("Full Body", result.get(1));
    }
}
