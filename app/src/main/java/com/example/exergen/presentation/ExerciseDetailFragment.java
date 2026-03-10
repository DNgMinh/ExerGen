package com.example.exergen.presentation;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
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
import com.example.exergen.application.AppBootstrap;
import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.model.Exercise;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExerciseDetailFragment extends Fragment {

    private static final String ARG_EXERCISE_ID = "exercise_id";
    private static final String TAG = "ExerciseDetailFragment";

    private ExerciseService exerciseService;

    private TextView nameText;
    private TextView instructionsText;
    private TextView equipmentText;
    private TextView musclesText;
    private TextView intensityText;
    private ImageView exerciseImage;

    private final List<Drawable> animationDrawables = new ArrayList<>();
    private final Handler animationHandler = new Handler(Looper.getMainLooper());
    private int currentFrame = 0;
    private boolean isAnimating = false;

    private final Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            if (!animationDrawables.isEmpty()) {
                currentFrame = (currentFrame + 1) % animationDrawables.size();
                exerciseImage.setImageDrawable(animationDrawables.get(currentFrame));
                animationHandler.postDelayed(this, 1000); // 1 second per frame
            }
        }
    };

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
        exerciseImage = view.findViewById(R.id.exercise_detail_image);

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
        equipmentText.setText(TextUtils.join(", ", exercise.getEquipment()));
        musclesText.setText(TextUtils.join(", ", exercise.getMuscleGroups()));
        
        setupIntensityTag(exercise.getIntensity());

        loadImages(exercise.getImagePaths());
        startAnimation();
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

    private void loadImages(List<String> paths) {
        animationDrawables.clear();
        for (String path : paths) {
            try (InputStream is = requireContext().getAssets().open(path)) {
                Drawable d = Drawable.createFromStream(is, null);
                if (d != null) {
                    animationDrawables.add(d);
                }
            } catch (IOException e) {
                Log.e(TAG, "Error loading image: " + path, e);
            }
        }
    }

    private void startAnimation() {
        stopAnimation();
        if (!animationDrawables.isEmpty()) {
            isAnimating = true;
            exerciseImage.setImageDrawable(animationDrawables.get(0));
            animationHandler.post(animationRunnable);
        }
    }

    private void stopAnimation() {
        isAnimating = false;
        animationHandler.removeCallbacks(animationRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopAnimation();
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
