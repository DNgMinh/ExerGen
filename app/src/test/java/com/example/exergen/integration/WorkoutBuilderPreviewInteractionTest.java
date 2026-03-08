package com.example.exergen.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.example.exergen.R;
import com.example.exergen.business.repository.IExerciseRepository;
import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.usecase.WorkoutBuilderUseCase;
import com.example.exergen.business.usecase.WorkoutUseCase;
import com.example.exergen.model.Exercise;
import com.example.exergen.persistence.WorkoutRepositoryStub;
import com.example.exergen.presentation.WorkoutBuilderFragment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = { Build.VERSION_CODES.O_MR1 })
public class WorkoutBuilderPreviewInteractionTest {

    @Test
    public void generateShowsPreviewActionsAndLocksInputs() {
        WorkoutBuilderFragment fragment = launchWorkoutBuilderFragment();
        View rootView = fragment.requireView();

        EditText durationInput = rootView.findViewById(R.id.et_duration_minutes);
        CheckBox chestCheckbox = rootView.findViewById(R.id.cb_muscle_chest);
        Button generateButton = rootView.findViewById(R.id.btn_generate_workout);
        LinearLayout previewActions = rootView.findViewById(R.id.preview_action_container);
        TextView previewText = rootView.findViewById(R.id.tv_builder_preview);

        assertNotNull(durationInput);
        assertNotNull(chestCheckbox);
        assertNotNull(generateButton);
        assertNotNull(previewActions);
        assertNotNull(previewText);

        durationInput.setText("2");
        chestCheckbox.setChecked(true);
        generateButton.performClick();
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();

        assertEquals(View.VISIBLE, previewActions.getVisibility());
        assertEquals(View.GONE, generateButton.getVisibility());
        assertFalse(durationInput.isEnabled());
        assertTrue(previewText.getText().length() > 0);
    }

    @Test
    public void editConstraintsReturnsToInputModeAfterGeneration() {
        WorkoutBuilderFragment fragment = launchWorkoutBuilderFragment();
        View rootView = fragment.requireView();

        EditText durationInput = rootView.findViewById(R.id.et_duration_minutes);
        CheckBox chestCheckbox = rootView.findViewById(R.id.cb_muscle_chest);
        Button generateButton = rootView.findViewById(R.id.btn_generate_workout);
        Button editConstraintsButton = rootView.findViewById(R.id.btn_edit_constraints);
        LinearLayout previewActions = rootView.findViewById(R.id.preview_action_container);

        durationInput.setText("2");
        chestCheckbox.setChecked(true);
        generateButton.performClick();
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();

        assertEquals(View.VISIBLE, previewActions.getVisibility());

        editConstraintsButton.performClick();
        Shadows.shadowOf(android.os.Looper.getMainLooper()).idle();

        assertEquals(View.GONE, previewActions.getVisibility());
        assertEquals(View.VISIBLE, generateButton.getVisibility());
        assertTrue(durationInput.isEnabled());
    }

    private WorkoutBuilderFragment launchWorkoutBuilderFragment() {
        FragmentActivity activity = Robolectric.buildActivity(FragmentActivity.class).setup().get();

        ExerciseService exerciseService = new ExerciseService(new LocalExerciseRepository());
        WorkoutBuilderUseCase workoutBuilderUseCase = new WorkoutBuilderUseCase(exerciseService);
        WorkoutUseCase workoutUseCase = new WorkoutUseCase(new WorkoutRepositoryStub(), exerciseService);

        WorkoutBuilderFragment fragment = new WorkoutBuilderFragment();
        fragment.setDependenciesForTesting(workoutBuilderUseCase, workoutUseCase, exerciseService);

        activity.getSupportFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, fragment)
                .commitNow();

        return fragment;
    }

    private static class LocalExerciseRepository implements IExerciseRepository {
        private final java.util.List<Exercise> exercises = java.util.List.of(
                new Exercise("it-preview-1", "Pushup", java.util.List.of("Chest"), java.util.List.of("Bodyweight"), "",
                        2,
                        "img"),
                new Exercise("it-preview-2", "Air Squat", java.util.List.of("Legs"), java.util.List.of("Bodyweight"),
                        "", 2,
                        "img"));

        @Override
        public java.util.List<Exercise> getAllExercises() {
            return exercises;
        }

        @Override
        public java.util.List<Exercise> filterByEquipment(String equipment) {
            return java.util.List.of();
        }

        @Override
        public java.util.List<Exercise> filterByMuscleGroup(String muscleGroup) {
            return java.util.List.of();
        }

        @Override
        public void insertExercise(Exercise exercise) {
        }

        @Override
        public Exercise getExerciseById(String id) {
            for (Exercise exercise : exercises) {
                if (exercise.getId().equals(id)) {
                    return exercise;
                }
            }
            return null;
        }

        @Override
        public void deleteExercise(String id) {
        }

        @Override
        public void seedData() {
        }
    }
}
