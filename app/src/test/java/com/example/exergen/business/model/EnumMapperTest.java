package com.example.exergen.business.model;

import com.example.exergen.model.EnumMapper;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.MuscleGroup;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EnumMapperTest {
    private EnumMapper mapper;

    @Before
    public void setUp() {
        mapper = new EnumMapper();
    }

    @Test
    public void testToMuscleEnums_withPerfectLabels_returnsCorrectEnums() {
        List<String> input = Arrays.asList("Chest", "Legs");
        List<MuscleGroup> result = mapper.toMuscleEnums(input);

        assertEquals(2, result.size());
        assertEquals(MuscleGroup.CHEST, result.get(0));
        assertEquals(MuscleGroup.LEGS, result.get(1));
    }

    @Test
    public void testToMuscleEnums_withNullsAndEmptyStrings_skipsThemGracefully() {

        List<String> input = Arrays.asList("Chest", null, "   ", "", "Legs");
        List<MuscleGroup> result = mapper.toMuscleEnums(input);

        assertEquals(2, result.size());
        assertEquals(MuscleGroup.CHEST, result.get(0));
        assertEquals(MuscleGroup.LEGS, result.get(1));
    }

    @Test
    public void testToMuscleEnums_withWeirdCasing_mapsCorrectly() {
        List<String> input = Arrays.asList("  cHeSt  ", "LEGS");
        List<MuscleGroup> result = mapper.toMuscleEnums(input);

        assertEquals(2, result.size());
        assertEquals(MuscleGroup.CHEST, result.get(0));
        assertEquals(MuscleGroup.LEGS, result.get(1));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testToMuscleEnums_withFakeMuscle_throwsException() {
        List<String> input = Arrays.asList("Chest", "Fake Muscle", "Legs");

        mapper.toMuscleEnums(input);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testToEquipmentEnums_withFakeEquipment_throwsException() {
        List<String> input = Arrays.asList("Dumbbell", "Magic Wand");

        mapper.toEquipmentEnums(input);
    }
}
