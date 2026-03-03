package com.example.exergen.presentation;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.exergen.R;
import com.example.exergen.application.AppBootstrap;
import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.model.Exercise;

public class ExerciseDetailFragment extends Fragment {

    private static final String ARG_EXERCISE_ID = "exercise_id";

    private ExerciseService exerciseService;

    private TextView nameText;
    private TextView instructionsText;
    private TextView equipmentText;
    private TextView musclesText;
    private TextView intensityText;
    private TextView durationText;

    public static ExerciseDetailFragment newInstance(String exerciseId) {
        ExerciseDetailFragment fragment = new ExerciseDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EXERCISE_ID, exerciseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        exerciseService = AppBootstrap.get().exerciseService;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_exercise_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameText = view.findViewById(R.id.exercise_detail_name);
        instructionsText = view.findViewById(R.id.exercise_detail_instructions);
        equipmentText = view.findViewById(R.id.exercise_detail_equipment);
        musclesText = view.findViewById(R.id.exercise_detail_muscles);
        intensityText = view.findViewById(R.id.exercise_detail_intensity);
        durationText = view.findViewById(R.id.exercise_detail_duration);

        Button backButton = view.findViewById(R.id.btn_exercise_detail_back);
        backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        String exerciseId = null;
        Bundle args = getArguments();
        if (args != null) {
            exerciseId = args.getString(ARG_EXERCISE_ID);
        }

        bindExercise(exerciseId);
    }

    private void bindExercise(String exerciseId) {
        if (exerciseId == null || exerciseId.isEmpty()) {
            showMissingExercise();
            return;
        }

        Exercise exercise = exerciseService.getExerciseById(exerciseId);
        if (exercise == null) {
            showMissingExercise();
            return;
        }

        nameText.setText(exercise.getName());
        instructionsText.setText(normalizeOrFallback(exercise.getInstructions(), getString(R.string.instructions_fallback)));
        equipmentText.setText(getString(R.string.label_equipment, TextUtils.join(", ", exercise.getEquipment())));
        musclesText.setText(getString(R.string.label_muscles, TextUtils.join(", ", exercise.getMuscleGroups())));
        intensityText.setText(getString(R.string.label_intensity, exercise.getIntensity()));
        durationText.setText(getString(R.string.label_duration_varies));
    }

    private void showMissingExercise() {
        nameText.setText(getString(R.string.exercise_not_found));
        instructionsText.setText(getString(R.string.instructions_fallback));
        equipmentText.setText(getString(R.string.label_equipment, getString(R.string.label_na)));
        musclesText.setText(getString(R.string.label_muscles, getString(R.string.label_na)));
        intensityText.setText(getString(R.string.label_intensity, getString(R.string.label_na)));
        durationText.setText(getString(R.string.label_duration, getString(R.string.label_na)));
    }

    private String normalizeOrFallback(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value;
    }
}
