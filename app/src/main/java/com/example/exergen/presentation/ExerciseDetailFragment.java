package com.example.exergen.presentation;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.exergen.R;
import com.example.exergen.business.usecase.ExerciseUseCase;
import com.example.exergen.model.Exercise;

import java.util.List;

public class ExerciseDetailFragment extends Fragment {

    private static final String ARG_EXERCISE_ID = "exercise_id";
    private static final String TAG = "ExerciseDetailFragment";

    private ExerciseUseCase exerciseUseCase;

    private TextView nameText;
    private TextView instructionsText;
    private TextView equipmentText;
    private TextView musclesText;
    private TextView intensityText;
    private ImageView exerciseImage;

    private ExerciseAnimationManager animationManager;

    public static ExerciseDetailFragment newInstance(String exerciseId) {
        ExerciseDetailFragment fragment = new ExerciseDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_EXERCISE_ID, exerciseId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setDependencies(ExerciseUseCase exerciseUseCase) {
        this.exerciseUseCase = exerciseUseCase;
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
        if (exerciseUseCase == null) {
            throw new IllegalStateException("ExerciseDetailFragment dependencies not provided");
        }

        nameText = view.findViewById(R.id.exercise_detail_name);
        instructionsText = view.findViewById(R.id.exercise_detail_instructions);
        equipmentText = view.findViewById(R.id.exercise_detail_equipment);
        musclesText = view.findViewById(R.id.exercise_detail_muscles);
        intensityText = view.findViewById(R.id.exercise_detail_intensity);
        exerciseImage = view.findViewById(R.id.exercise_detail_image);
        animationManager = new ExerciseAnimationManager(exerciseImage, TAG);

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

        Exercise exercise = exerciseUseCase.getExerciseById(exerciseId);
        if (exercise == null) {
            showMissingExercise();
            return;
        }

        nameText.setText(exercise.getName());
        instructionsText.setText(normalizeOrFallback(exercise.getInstructions(), getString(R.string.instructions_fallback)));
        equipmentText.setText(TextUtils.join(", ", exercise.getEquipment()));
        musclesText.setText(TextUtils.join(", ", exercise.getMuscleGroups()));
        
        setupIntensityTag(exercise.getIntensity());

        animationManager.loadAndStart(requireContext(), exercise.getImagePaths());
    }

    private void setupIntensityTag(int intensity) {
        String label = getString(R.string.label_intensity, String.valueOf(intensity));
        intensityText.setText(label.toUpperCase());
        
        GradientDrawable background = (GradientDrawable) ContextCompat.getDrawable(requireContext(), R.drawable.bg_intensity_tag);
        if (background != null) {
            background = (GradientDrawable) background.mutate();
            int color;
            if (intensity <= 2) {
                color = Color.parseColor("#4CAF50"); // Green
            } else if (intensity <= 3) {
                color = Color.parseColor("#FF9800"); // Orange
            } else {
                color = Color.parseColor("#F44336"); // Red
            }
            background.setColor(color);
            intensityText.setBackground(background);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (animationManager != null) {
            animationManager.stop();
        }
    }

    private void showMissingExercise() {
        nameText.setText(getString(R.string.exercise_not_found));
        instructionsText.setText(getString(R.string.instructions_fallback));
        equipmentText.setText(getString(R.string.label_na));
        musclesText.setText(getString(R.string.label_na));
        intensityText.setVisibility(View.GONE);
    }

    private String normalizeOrFallback(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value;
    }
}
