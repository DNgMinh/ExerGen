package com.example.exergen.presentation;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.exergen.R;
import com.example.exergen.application.AppBootstrap;
import com.example.exergen.business.service.ExerciseService;
import com.example.exergen.business.service.WorkoutGenerationConstraints;
import com.example.exergen.business.usecase.WorkoutBuilderUseCase;
import com.example.exergen.business.usecase.WorkoutUseCase;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.Workout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class WorkoutBuilderFragment extends Fragment {
    private static final String KEY_DURATION = "builder_duration";
    private static final String KEY_MUSCLE_CHEST = "builder_muscle_chest";
    private static final String KEY_MUSCLE_LEGS = "builder_muscle_legs";
    private static final String KEY_MUSCLE_BACK = "builder_muscle_back";
    private static final String KEY_EQUIP_BODYWEIGHT = "builder_equipment_bodyweight";
    private static final String KEY_EQUIP_DUMBBELLS = "builder_equipment_dumbbells";
    private static final String KEY_EQUIP_BARBELL = "builder_equipment_barbell";
    private static final String KEY_SUMMARY = "builder_summary";
    private static final String KEY_PREVIEW = "builder_preview";
    private static final String KEY_PREVIEW_MODE = "builder_preview_mode";

    private WorkoutBuilderUseCase workoutBuilderUseCase;
    private WorkoutUseCase workoutUseCase;
    private ExerciseService exerciseService;
    private Workout lastGeneratedWorkout;

    private EditText etDurationMinutes;
    private CheckBox cbMuscleChest;
    private CheckBox cbMuscleLegs;
    private CheckBox cbMuscleBack;
    private CheckBox cbEquipmentBodyweight;
    private CheckBox cbEquipmentDumbbells;
    private CheckBox cbEquipmentBarbell;
    private TextView tvBuilderSummary;
    private TextView tvBuilderPreview;
    private Button btnGenerateWorkout;
    private LinearLayout previewActionContainer;
    private Button btnStartWorkout;
    private Button btnRegenerateWorkout;
    private Button btnEditConstraints;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        workoutBuilderUseCase = AppBootstrap.get().workoutBuilderUseCase;
        workoutUseCase = AppBootstrap.get().workoutUseCase;
        exerciseService = AppBootstrap.get().exerciseService;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workout_builder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindViews(view);
        restoreState(savedInstanceState);

        btnGenerateWorkout.setOnClickListener(v -> generateAndPreviewWorkout());
        btnRegenerateWorkout.setOnClickListener(v -> generateAndPreviewWorkout());
        btnEditConstraints.setOnClickListener(v -> setPreviewMode(false));
        btnStartWorkout.setOnClickListener(v -> openTimer());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_DURATION, etDurationMinutes.getText().toString());
        outState.putBoolean(KEY_MUSCLE_CHEST, cbMuscleChest.isChecked());
        outState.putBoolean(KEY_MUSCLE_LEGS, cbMuscleLegs.isChecked());
        outState.putBoolean(KEY_MUSCLE_BACK, cbMuscleBack.isChecked());
        outState.putBoolean(KEY_EQUIP_BODYWEIGHT, cbEquipmentBodyweight.isChecked());
        outState.putBoolean(KEY_EQUIP_DUMBBELLS, cbEquipmentDumbbells.isChecked());
        outState.putBoolean(KEY_EQUIP_BARBELL, cbEquipmentBarbell.isChecked());
        outState.putString(KEY_SUMMARY, tvBuilderSummary.getText().toString());
        outState.putString(KEY_PREVIEW, tvBuilderPreview.getText().toString());
        outState.putBoolean(KEY_PREVIEW_MODE, previewActionContainer.getVisibility() == View.VISIBLE);
    }

    private void bindViews(View view) {
        etDurationMinutes = view.findViewById(R.id.et_duration_minutes);
        cbMuscleChest = view.findViewById(R.id.cb_muscle_chest);
        cbMuscleLegs = view.findViewById(R.id.cb_muscle_legs);
        cbMuscleBack = view.findViewById(R.id.cb_muscle_back);
        cbEquipmentBodyweight = view.findViewById(R.id.cb_equipment_bodyweight);
        cbEquipmentDumbbells = view.findViewById(R.id.cb_equipment_dumbbells);
        cbEquipmentBarbell = view.findViewById(R.id.cb_equipment_barbell);
        tvBuilderSummary = view.findViewById(R.id.tv_builder_summary);
        tvBuilderPreview = view.findViewById(R.id.tv_builder_preview);
        btnGenerateWorkout = view.findViewById(R.id.btn_generate_workout);
        previewActionContainer = view.findViewById(R.id.preview_action_container);
        btnStartWorkout = view.findViewById(R.id.btn_start_workout);
        btnRegenerateWorkout = view.findViewById(R.id.btn_regenerate_workout);
        btnEditConstraints = view.findViewById(R.id.btn_edit_constraints);
    }

    private void restoreState(@Nullable Bundle state) {
        if (state == null) {
            return;
        }
        etDurationMinutes.setText(state.getString(KEY_DURATION, ""));
        cbMuscleChest.setChecked(state.getBoolean(KEY_MUSCLE_CHEST, false));
        cbMuscleLegs.setChecked(state.getBoolean(KEY_MUSCLE_LEGS, false));
        cbMuscleBack.setChecked(state.getBoolean(KEY_MUSCLE_BACK, false));
        cbEquipmentBodyweight.setChecked(state.getBoolean(KEY_EQUIP_BODYWEIGHT, false));
        cbEquipmentDumbbells.setChecked(state.getBoolean(KEY_EQUIP_DUMBBELLS, false));
        cbEquipmentBarbell.setChecked(state.getBoolean(KEY_EQUIP_BARBELL, false));
        tvBuilderSummary.setText(state.getString(KEY_SUMMARY, ""));
        tvBuilderPreview.setText(state.getString(KEY_PREVIEW, ""));
        setPreviewMode(state.getBoolean(KEY_PREVIEW_MODE, false));
    }

    private void generateAndPreviewWorkout() {
        String durationText = etDurationMinutes.getText().toString().trim();
        if (TextUtils.isEmpty(durationText)) {
            showToast(getString(R.string.workout_builder_error_duration_required));
            return;
        }

        int durationMinutes;
        try {
            durationMinutes = Integer.parseInt(durationText);
        } catch (NumberFormatException ex) {
            showToast(getString(R.string.workout_builder_error_duration_invalid));
            return;
        }

        List<String> targetMuscles = getSelectedMuscles();
        if (targetMuscles.isEmpty()) {
            showToast(getString(R.string.workout_builder_error_muscle_required));
            return;
        }

        List<String> selectedEquipment = getSelectedEquipment();
        WorkoutGenerationConstraints constraints = new WorkoutGenerationConstraints(selectedEquipment, targetMuscles,
                durationMinutes);

        String summaryText = getString(
                R.string.workout_builder_summary_format,
                constraints.getDesiredDurationMinutes(),
                String.join(", ", constraints.getTargetMuscleGroups()),
                constraints.getSelectedEquipment().isEmpty()
                        ? getString(R.string.workout_builder_equipment_any)
                        : String.join(", ", constraints.getSelectedEquipment()));

        try {
            Workout generatedWorkout = workoutBuilderUseCase.generateWorkout(constraints);
            lastGeneratedWorkout = generatedWorkout;
            tvBuilderSummary.setText(summaryText);
            tvBuilderPreview.setText(buildPreviewText(generatedWorkout));
            setPreviewMode(true);
        } catch (IllegalArgumentException ex) {
            lastGeneratedWorkout = null;
            tvBuilderPreview.setText("");
            setPreviewMode(false);
            showToast(ex.getMessage());
        }
    }

    private void setPreviewMode(boolean enabled) {
        previewActionContainer.setVisibility(enabled ? View.VISIBLE : View.GONE);
        btnGenerateWorkout.setVisibility(enabled ? View.GONE : View.VISIBLE);
        setInputsEnabled(!enabled);
    }

    private void setInputsEnabled(boolean enabled) {
        etDurationMinutes.setEnabled(enabled);
        cbMuscleChest.setEnabled(enabled);
        cbMuscleLegs.setEnabled(enabled);
        cbMuscleBack.setEnabled(enabled);
        cbEquipmentBodyweight.setEnabled(enabled);
        cbEquipmentDumbbells.setEnabled(enabled);
        cbEquipmentBarbell.setEnabled(enabled);
    }

    private void openTimer() {
        if (lastGeneratedWorkout == null) {
            showToast(getString(R.string.workout_builder_error_generate_first));
            return;
        }
        workoutUseCase.saveWorkout(lastGeneratedWorkout);
        showToast(getString(R.string.workout_builder_saved_message));

        if (getActivity() == null) {
            return;
        }
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_timer);
            return;
        }
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new TimerFragment())
                .addToBackStack(null)
                .commit();
    }

    private String buildPreviewText(Workout workout) {
        StringBuilder preview = new StringBuilder();
        preview.append(getString(R.string.workout_builder_preview_header)).append('\n');
        preview.append(getString(R.string.workout_builder_preview_count_format, workout.getExerciseIds().size()));
        preview.append('\n');

        List<String> exerciseIds = workout.getExerciseIds();
        for (int i = 0; i < exerciseIds.size(); i++) {
            String id = exerciseIds.get(i);
            Exercise exercise = exerciseService.getExerciseById(id);
            String name = exercise != null ? exercise.getName() : id;
            preview.append(i + 1)
                    .append(". ")
                    .append(name)
                    .append(" (")
                    .append(workout.getWorkSeconds().get(i))
                    .append("s work / ")
                    .append(workout.getRestSeconds().get(i))
                    .append("s rest)")
                    .append('\n');
        }
        return preview.toString().trim();
    }

    private List<String> getSelectedMuscles() {
        List<String> muscles = new ArrayList<>();
        if (cbMuscleChest.isChecked()) {
            muscles.add("Chest");
        }
        if (cbMuscleLegs.isChecked()) {
            muscles.add("Legs");
        }
        if (cbMuscleBack.isChecked()) {
            muscles.add("Back");
        }
        return muscles;
    }

    private List<String> getSelectedEquipment() {
        List<String> equipment = new ArrayList<>();
        if (cbEquipmentBodyweight.isChecked()) {
            equipment.add("Bodyweight");
        }
        if (cbEquipmentDumbbells.isChecked()) {
            equipment.add("Dumbbells");
        }
        if (cbEquipmentBarbell.isChecked()) {
            equipment.add("Barbell");
        }
        return equipment;
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
