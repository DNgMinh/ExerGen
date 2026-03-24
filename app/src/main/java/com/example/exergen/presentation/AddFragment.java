package com.example.exergen.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.exergen.R;
import com.example.exergen.business.service.EnumMapper;
import com.example.exergen.business.usecase.ExerciseUseCase;
import com.example.exergen.model.Exercise;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;

// Fragment responsible for displaying the list of available exercises
public class AddFragment extends Fragment {

    private ExerciseUseCase exerciseUseCase;
    private RecyclerView recyclerView;
    private TextView emptyStateText;

    public void setDependencies(ExerciseUseCase exerciseUseCase) {
        this.exerciseUseCase = exerciseUseCase;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workouts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (exerciseUseCase == null) {
            throw new IllegalStateException("AddFragment dependencies not provided");
        }

        recyclerView = view.findViewById(R.id.exercise_recycler_view);
        emptyStateText = view.findViewById(R.id.empty_state_text);
        emptyStateText.setText(getString(R.string.exercises_empty));

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch data from business layer
        List<Exercise> exercises = exerciseUseCase.getAllExercises();

        if (exercises == null || exercises.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyStateText.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyStateText.setVisibility(View.GONE);
            recyclerView.setAdapter(new ExerciseAdapter(buildExerciseItems(exercises), this::openExerciseDetail));
        }
    }

    private List<ExerciseListItem> buildExerciseItems(List<Exercise> exercises) {
        List<ExerciseListItem> items = new ArrayList<>();
        for (Exercise exercise : exercises) {
            String muscles = TextUtils.join(", ", EnumMapper.toMuscleLabels(exercise.getMuscleGroups()));
            String equipment = TextUtils.join(", ", EnumMapper.toEquipmentLabels(exercise.getEquipment()));
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
}