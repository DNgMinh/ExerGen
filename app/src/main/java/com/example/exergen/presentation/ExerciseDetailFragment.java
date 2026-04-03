package com.example.exergen.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.exergen.R;
import com.example.exergen.business.usecase.ExerciseUseCase;
import com.example.exergen.model.EquipmentType;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.MuscleGroup;

import java.util.stream.Collectors;

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
            getParentFragmentManager().popBackStack();
            return;
        }

        nameText = view.findViewById(R.id.exercise_detail_name);
        instructionsText = view.findViewById(R.id.exercise_detail_instructions);
        equipmentText = view.findViewById(R.id.exercise_detail_equipment);
        musclesText = view.findViewById(R.id.exercise_detail_muscles);
        intensityText = view.findViewById(R.id.exercise_detail_intensity);
        exerciseImage = view.findViewById(R.id.exercise_detail_image);
        animationManager = new ExerciseAnimationManager(exerciseImage, TAG);

        Button backButton = view.findViewById(R.id.btn_exercise_detail_back);
        if (backButton != null) {
            backButton.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }

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
        
        String equipment = exercise.getEquipment().stream()
                .map(EquipmentType::getLabel)
                .collect(Collectors.joining(", "));
        equipmentText.setText(equipment);

        String muscles = exercise.getMuscleGroups().stream()
                .map(MuscleGroup::getLabel)
                .collect(Collectors.joining(", "));
        musclesText.setText(muscles);
        
        intensityText.setText(getString(R.string.label_intensity, String.valueOf(exercise.getIntensity())));

        animationManager.load(requireContext(), exercise.getImagePaths());
        animationManager.resume();
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
