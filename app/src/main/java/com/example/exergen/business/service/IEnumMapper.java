package com.example.exergen.business.service;

import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.MuscleGroup;
import java.util.List;

public interface IEnumMapper {
    List<EquipmentType> toEquipmentEnums(List<String> labels);
    List<MuscleGroup> toMuscleEnums(List<String> labels);
    List<String> toEquipmentLabels(List<EquipmentType> types);
    List<String> toMuscleLabels(List<MuscleGroup> groups);
}
