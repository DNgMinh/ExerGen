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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

// Fragment responsible for displaying the list of available exercises
public class ExercisesFragment extends Fragment {

    private ExerciseUseCase exerciseUseCase;
    private RecyclerView recyclerView;
    private TextView emptyStateText;

    private List<EquipmentType> activeFilters = new ArrayList<>(Arrays.asList(EquipmentType.values()));
    private ChipGroup filterChipGroup;

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

        Button toggleBtn = view.findViewById(R.id.btn_filter_toggle);
        filterChipGroup = view.findViewById(R.id.filter_chip_group);
        recyclerView = view.findViewById(R.id.exercise_recycler_view);
        emptyStateText = view.findViewById(R.id.empty_state_text);

        toggleBtn.setOnClickListener(v -> {
            int visibility = (filterChipGroup.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
            filterChipGroup.setVisibility(visibility);
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
        filterChipGroup.removeAllViews();
        activeFilters = new ArrayList<>(Arrays.asList(EquipmentType.values()));

        for (EquipmentType type : EquipmentType.values()) {
            Chip chip = new Chip(getContext());
            chip.setText(type.getLabel());
            chip.setCheckable(true);
            chip.setChecked(true);

            chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (!activeFilters.contains(type)) activeFilters.add(type);
                } else {
                    activeFilters.remove(type);
                }
                refreshList();
            });
            filterChipGroup.addView(chip);
        }
    }

    private void refreshList() {
        List<Exercise> exercises = exerciseUseCase.getExercisesByEquipment(activeFilters);

        if (exercises == null || exercises.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
            emptyStateText.setText("No exercises found for these filters.");
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateText.setVisibility(View.GONE);
            // Use your existing adapter logic
            recyclerView.setAdapter(new ExerciseAdapter(buildExerciseItems(exercises), this::openExerciseDetail));
        }
    }
}