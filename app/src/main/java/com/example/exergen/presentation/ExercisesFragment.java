package com.example.exergen.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.exergen.R;
import com.example.exergen.business.usecase.ExerciseUseCase;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.chip.Chip;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Fragment responsible for displaying the list of available exercises
public class ExercisesFragment extends Fragment {

    private ExerciseUseCase exerciseUseCase;
    private RecyclerView recyclerView;
    private TextView emptyStateText;

    private ChipGroup equipmentChipGroup;
    private ChipGroup muscleChipGroup;

    public void setDependencies(ExerciseUseCase exerciseUseCase) {
        this.exerciseUseCase = exerciseUseCase;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercises, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button equipmentToggle = view.findViewById(R.id.btn_filter_equipment_toggle);
        Button muscleToggle = view.findViewById(R.id.btn_filter_muscle_toggle);
        
        equipmentChipGroup = view.findViewById(R.id.filter_equipment_chip_group);
        muscleChipGroup = view.findViewById(R.id.filter_muscle_chip_group);
        
        recyclerView = view.findViewById(R.id.exercise_recycler_view);
        emptyStateText = view.findViewById(R.id.empty_state_text);

        equipmentToggle.setOnClickListener(v -> {
            int visibility = (equipmentChipGroup.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
            equipmentChipGroup.setVisibility(visibility);
            muscleChipGroup.setVisibility(View.GONE);
        });

        muscleToggle.setOnClickListener(v -> {
            int visibility = (muscleChipGroup.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
            muscleChipGroup.setVisibility(visibility);
            equipmentChipGroup.setVisibility(View.GONE);
        });

        setupFilterChips();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshList();
    }

    private List<ExerciseListItem> buildExerciseItems(List<Exercise> exercises) {
        List<ExerciseListItem> items = new ArrayList<>();
        for (Exercise exercise : exercises) {
            String muscles = exercise.getMuscleGroups().stream()
                    .map(MuscleGroup::getLabel)
                    .collect(Collectors.joining(", "));
            String equipment = exercise.getEquipment().stream()
                    .map(EquipmentType::getLabel)
                    .collect(Collectors.joining(", "));
            String attributes = getString(R.string.exercise_attributes_format, muscles, equipment);
            items.add(new ExerciseListItem(exercise, exercise.getName(), attributes));
        }
        return items;
    }

    private void openExerciseDetail(Exercise exercise) {
        if (exercise == null) {
            return;
        }

        ExerciseDetailFragment detailFragment = ExerciseDetailFragment.newInstance(exercise.getId());
        detailFragment.setDependencies(exerciseUseCase);
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    private void setupFilterChips() {
        // Equipment Chips
        equipmentChipGroup.removeAllViews();
        List<EquipmentType> savedEquip = exerciseUseCase.getEquipmentFilters();
        for (EquipmentType type : EquipmentType.values()) {
            Chip chip = new Chip(getContext());
            chip.setText(type.getLabel());
            chip.setCheckable(true);
            chip.setChecked(savedEquip.contains(type));
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                List<EquipmentType> updated = exerciseUseCase.getEquipmentFilters();
                if (isChecked) {
                    if (!updated.contains(type)) updated.add(type);
                } else {
                    updated.remove(type);
                }
                exerciseUseCase.setEquipmentFilters(updated);
                refreshList();
            });
            equipmentChipGroup.addView(chip);
        }

        // Muscle Chips
        muscleChipGroup.removeAllViews();
        List<MuscleGroup> savedMuscles = exerciseUseCase.getMuscleFilters();
        for (MuscleGroup group : MuscleGroup.values()) {
            Chip chip = new Chip(getContext());
            chip.setText(group.getLabel());
            chip.setCheckable(true);
            chip.setChecked(savedMuscles.contains(group));
            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                List<MuscleGroup> updated = exerciseUseCase.getMuscleFilters();
                if (isChecked) {
                    if (!updated.contains(group)) updated.add(group);
                } else {
                    updated.remove(group);
                }
                exerciseUseCase.setMuscleFilters(updated);
                refreshList();
            });
            muscleChipGroup.addView(chip);
        }
    }

    private void refreshList() {
        List<Exercise> exercises = exerciseUseCase.getFilteredExercises();

        if (exercises == null || exercises.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
            emptyStateText.setText("No exercises found for your selected filters.");
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateText.setVisibility(View.GONE);
            recyclerView.setAdapter(new ExerciseAdapter(buildExerciseItems(exercises), this::openExerciseDetail));
        }
    }
}
