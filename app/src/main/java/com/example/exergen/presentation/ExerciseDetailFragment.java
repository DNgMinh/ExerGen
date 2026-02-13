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
        instructionsText.setText(normalizeOrFallback(exercise.getInstructions(), "No instructions available."));
        equipmentText.setText("Equipment: " + TextUtils.join(", ", exercise.getEquipment()));
        musclesText.setText("Muscles: " + TextUtils.join(", ", exercise.getMuscleGroups()));
        intensityText.setText("Intensity: " + exercise.getIntensity());
        durationText.setText("Duration: Varies by workout");
    }

    private void showMissingExercise() {
        nameText.setText("Exercise not found");
        instructionsText.setText("No instructions available.");
        equipmentText.setText("Equipment: N/A");
        musclesText.setText("Muscles: N/A");
        intensityText.setText("Intensity: N/A");
        durationText.setText("Duration: N/A");
    }

    private String normalizeOrFallback(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value;
    }
}
