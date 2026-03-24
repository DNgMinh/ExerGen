package com.example.exergen.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.exergen.R;
import com.example.exergen.business.service.TimerPhase;
import com.example.exergen.business.usecase.WorkoutUseCase;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.Workout;

import java.util.List;

public class LiveWorkoutFragment extends Fragment {
    private static final String ARG_WORKOUT_ID = "workout_id";
    private static final String TAG = "LiveWorkoutFragment";

    private String workoutId;
    private Workout workout;
    private WorkoutUseCase workoutUseCase;
    private LiveWorkoutViewModel viewModel;
    private SoundFeedbackHelper soundFeedbackHelper;
    private ExerciseAnimationManager animationManager;

    private TextView tvTimer, tvPhase, tvWorkoutName, tvCurrentExercise, tvNextExercise;
    private ImageView ivAnimation;
    private Button btnStart, btnPause, btnCancel;
    private ImageButton btnExit;
    private LinearLayout setupContainer;
    private View activeContainer;
    private NumberPicker npWork, npRest, npSets;

    public static LiveWorkoutFragment newInstance(String workoutId) {
        LiveWorkoutFragment fragment = new LiveWorkoutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WORKOUT_ID, workoutId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setDependencies(WorkoutUseCase workoutUseCase) {
        this.workoutUseCase = workoutUseCase;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        soundFeedbackHelper = new SoundFeedbackHelper();
        if (workoutUseCase == null) {
            throw new IllegalStateException("LiveWorkoutFragment dependencies not provided");
        }
        if (getArguments() != null) {
            workoutId = getArguments().getString(ARG_WORKOUT_ID);
            workout = workoutUseCase.getWorkoutById(workoutId);
        }
        viewModel = new ViewModelProvider(this).get(LiveWorkoutViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_live_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        setupPickers();
        setupButtons();
        setupObservers();

        if (workout != null) {
            tvWorkoutName.setText(workout.getName());
        }
        setBottomNavVisibility(View.GONE);
    }

    private void initializeViews(View view) {
        tvTimer = view.findViewById(R.id.tv_live_timer);
        tvPhase = view.findViewById(R.id.tv_live_phase);
        tvWorkoutName = view.findViewById(R.id.tv_workout_name);
        tvCurrentExercise = view.findViewById(R.id.tv_current_exercise);
        tvNextExercise = view.findViewById(R.id.tv_next_exercise);
        ivAnimation = view.findViewById(R.id.iv_exercise_animation);
        btnStart = view.findViewById(R.id.btn_live_start);
        btnPause = view.findViewById(R.id.btn_live_pause);
        btnCancel = view.findViewById(R.id.btn_live_cancel);
        btnExit = view.findViewById(R.id.btn_exit_workout);
        setupContainer = view.findViewById(R.id.setup_picker_container);
        activeContainer = view.findViewById(R.id.active_exercise_container);
        npWork = view.findViewById(R.id.np_live_work);
        npRest = view.findViewById(R.id.np_live_rest);
        npSets = view.findViewById(R.id.np_live_sets);
        animationManager = new ExerciseAnimationManager(ivAnimation, TAG);
    }

    private void setupPickers() {
        npWork.setMinValue(5);
        npWork.setMaxValue(300);
        npWork.setValue(30);
        npRest.setMinValue(0);
        npRest.setMaxValue(300);
        npRest.setValue(10);
        npSets.setMinValue(1);
        npSets.setMaxValue(20);
        npSets.setValue(workout != null ? workout.getSets() : 1);
    }

    private void setupButtons() {
        btnStart.setOnClickListener(v -> {
            if (workout != null) {
                viewModel.startWorkout(workout, npWork.getValue(), npRest.getValue(), npSets.getValue());
            }
        });
        btnPause.setOnClickListener(v -> viewModel.pause());
        btnCancel.setOnClickListener(v -> exitWorkout());
        btnExit.setOnClickListener(v -> confirmExit());
    }

    private void setupObservers() {
        viewModel.getTimeLeft().observe(getViewLifecycleOwner(), seconds -> {
            String timeString = String.format(java.util.Locale.getDefault(), "%02d:%02d",
                    seconds / 60, seconds % 60);
            tvTimer.setText(timeString);
            if (Boolean.TRUE.equals(viewModel.getIsRunning().getValue()) && seconds > 0 && seconds <= 3) {
                soundFeedbackHelper.playCountdownBeep();
            }
        });

        viewModel.getPhase().observe(getViewLifecycleOwner(), phase -> {
            if (phase == TimerPhase.WORK) {
                tvPhase.setText(getString(R.string.timer_work));
                tvPhase.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));
            }
            else {
                tvPhase.setText(getString(R.string.timer_rest));
                tvPhase.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_orange_dark));
                tvCurrentExercise.setText(getString(R.string.timer_rest).toUpperCase());
                animationManager.stop();
                ivAnimation.setImageDrawable(null);
            }
            soundFeedbackHelper.playTransitionBeep();
        });

        viewModel.getCurrentExercise().observe(getViewLifecycleOwner(), exercise -> {
            if (exercise != null && viewModel.getPhase().getValue() == TimerPhase.WORK) {
                tvCurrentExercise.setText(exercise.getName());
                animationManager.loadAndStart(requireContext(), exercise.getImagePaths());
            }
        });

        viewModel.getNextExercise().observe(getViewLifecycleOwner(), exercise -> {
            if (exercise != null) {
                tvNextExercise.setText(getString(R.string.live_workout_next_label, exercise.getName()));
                tvNextExercise.setVisibility(View.VISIBLE);
            }
            else {
                tvNextExercise.setVisibility(View.GONE);
            }
        });

        viewModel.getUiState().observe(getViewLifecycleOwner(), state -> {
            if (state == null) return;
            setupContainer.setVisibility(state.isSetupVisible() ? View.VISIBLE : View.GONE);
            activeContainer.setVisibility(state.isActiveVisible() ? View.VISIBLE : View.GONE);
            btnStart.setVisibility(state.isStartVisible() ? View.VISIBLE : View.GONE);
            btnPause.setVisibility(state.isPauseVisible() ? View.VISIBLE : View.GONE);
            btnCancel.setVisibility(state.isCancelVisible() ? View.VISIBLE : View.GONE);
            btnPause.setEnabled(state.isPauseEnabled());
            btnStart.setText(getString(state.getStartTextResId()));
            btnCancel.setText(state.getCancelTextResId());
            tvTimer.setVisibility(state.isTimerVisible() ? View.VISIBLE : View.GONE);

            if (state.isRunning()) {
                if (viewModel.getPhase().getValue() == TimerPhase.WORK) {
                    animationManager.resume();
                }
            } else {
                animationManager.pause();
                if (!state.isFinished() && !state.isSetupVisible()) {
                    tvPhase.setText(getString(R.string.timer_paused));
                }
            }

            if (state.isFinished()) {
                tvPhase.setText(getString(R.string.live_workout_finished));
                tvPhase.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));
                tvCurrentExercise.setText("");
                tvNextExercise.setText("");
                animationManager.stop();
                ivAnimation.setImageResource(R.drawable.ic_check_circle);
            }
            setBottomNavVisibility(state.isShowBottomNav() ? View.VISIBLE : View.GONE);
        });
    }

    private void confirmExit() {
        if (Boolean.TRUE.equals(viewModel.getIsFinished().getValue())) {
            exitWorkout();
            return;
        }
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.live_workout_exit_confirm)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> exitWorkout())
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void exitWorkout() {
        viewModel.stop();
        setBottomNavVisibility(View.VISIBLE);
        getParentFragmentManager().popBackStack();
    }

    private void setBottomNavVisibility(int visibility) {
        if (getActivity() != null) {
            View nav = getActivity().findViewById(R.id.bottom_navigation);
            if (nav != null) nav.setVisibility(visibility);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (soundFeedbackHelper != null) {
            soundFeedbackHelper.release();
        }
        if (animationManager != null) {
            animationManager.stop();
        }
        setBottomNavVisibility(View.VISIBLE);
    }
}
