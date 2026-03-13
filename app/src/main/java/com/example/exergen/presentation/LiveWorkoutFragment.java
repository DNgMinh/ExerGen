package com.example.exergen.presentation;

import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.example.exergen.application.AppBootstrap;
import com.example.exergen.business.service.TimerPhase;
import com.example.exergen.model.Exercise;
import com.example.exergen.model.Workout;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LiveWorkoutFragment extends Fragment {
    private static final String ARG_WORKOUT_ID = "workout_id";
    private static final String TAG = "LiveWorkoutFragment";

    private String workoutId;
    private Workout workout;
    private LiveWorkoutViewModel viewModel;
    private ToneGenerator toneGenerator;

    private TextView tvTimer, tvPhase, tvWorkoutName, tvCurrentExercise, tvNextExercise;
    private ImageView ivAnimation;
    private Button btnStart, btnPause, btnCancel;
    private ImageButton btnExit;
    private LinearLayout setupContainer;
    private View activeContainer;
    private NumberPicker npWork, npRest;

    private final List<Drawable> animationDrawables = new ArrayList<>();
    private final Handler animationHandler = new Handler(Looper.getMainLooper());
    private int currentFrame = 0;
    private boolean isAnimating = false;

    private final Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            if (!animationDrawables.isEmpty() && isAnimating) {
                currentFrame = (currentFrame + 1) % animationDrawables.size();
                ivAnimation.setImageDrawable(animationDrawables.get(currentFrame));
                animationHandler.postDelayed(this, 1000);
            }
        }
    };

    public static LiveWorkoutFragment newInstance(String workoutId) {
        LiveWorkoutFragment fragment = new LiveWorkoutFragment();
        Bundle args = new Bundle();
        args.putString(ARG_WORKOUT_ID, workoutId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toneGenerator = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
        if (getArguments() != null) {
            workoutId = getArguments().getString(ARG_WORKOUT_ID);
            workout = AppBootstrap.get().workoutUseCase.getWorkoutById(workoutId);
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
    }

    private void setupPickers() {
        npWork.setMinValue(5);
        npWork.setMaxValue(300);
        npWork.setValue(30);
        npRest.setMinValue(0);
        npRest.setMaxValue(300);
        npRest.setValue(10);
    }

    private void setupButtons() {
        btnStart.setOnClickListener(v -> {
            if (workout != null) {
                viewModel.init(workout, npWork.getValue(), npRest.getValue());
                viewModel.start();
                setupContainer.setVisibility(View.GONE);
                activeContainer.setVisibility(View.VISIBLE);
                btnPause.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.GONE);
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
            if (seconds > 0 && seconds <= 3) {
                toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
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
                stopAnimation();
                stopAnimation();
                ivAnimation.setImageDrawable(null);
            }
            toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 500);
        });

        viewModel.getCurrentExercise().observe(getViewLifecycleOwner(), exercise -> {
            if (exercise != null && viewModel.getPhase().getValue() == TimerPhase.WORK) {
                tvCurrentExercise.setText(exercise.getName());
                loadAndStartAnimation(exercise.getImagePaths());
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

        viewModel.getIsRunning().observe(getViewLifecycleOwner(), isRunning -> {
            if (isRunning) {
                btnStart.setVisibility(View.GONE);
                btnPause.setEnabled(true);
                if (viewModel.getPhase().getValue() == TimerPhase.WORK) {
                    resumeAnimation();
                }
            }
            else {
                if (setupContainer.getVisibility() == View.VISIBLE) {
                    btnStart.setText(getString(R.string.btn_start));
                } else {
                    btnStart.setText(getString(R.string.btn_resume));
                    tvPhase.setText(getString(R.string.timer_paused));
                }
                btnStart.setVisibility(View.VISIBLE);
                btnPause.setEnabled(false);
                pauseAnimation();
            }
        });

        viewModel.getIsFinished().observe(getViewLifecycleOwner(), isFinished -> {
            if (isFinished) {
                tvPhase.setText(getString(R.string.live_workout_finished));
                tvPhase.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));
                tvTimer.setVisibility(View.GONE);
                tvCurrentExercise.setText("");
                tvNextExercise.setText("");
                stopAnimation();
                ivAnimation.setImageResource(R.drawable.ic_check_circle);
                btnPause.setVisibility(View.GONE);
                btnStart.setVisibility(View.GONE);
                btnCancel.setText(R.string.btn_back);
                btnCancel.setVisibility(View.VISIBLE);
                setBottomNavVisibility(View.VISIBLE);
            }
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

    private void loadAndStartAnimation(List<String> paths) {
        animationHandler.removeCallbacks(animationRunnable);
        animationDrawables.clear();
        for (String path : paths) {
            try (InputStream is = requireContext().getAssets().open(path)) {
                Drawable d = Drawable.createFromStream(is, null);
                if (d != null) animationDrawables.add(d);
            }
            catch (IOException e) {
                Log.e(TAG, "Error loading image: " + path, e);
            }
        }
        if (!animationDrawables.isEmpty()) {
            isAnimating = true;
            currentFrame = 0;
            ivAnimation.setImageDrawable(animationDrawables.get(0));
            animationHandler.post(animationRunnable);
        }
    }

    private void pauseAnimation() {
        isAnimating = false;
        animationHandler.removeCallbacks(animationRunnable);
    }

    private void resumeAnimation() {
        if (!isAnimating && !animationDrawables.isEmpty()) {
            isAnimating = true;
            animationHandler.post(animationRunnable);
        }
    }

    private void stopAnimation() {
        isAnimating = false;
        animationHandler.removeCallbacks(animationRunnable);
        animationDrawables.clear();
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
        if (toneGenerator != null) toneGenerator.release();
        stopAnimation();
        setBottomNavVisibility(View.VISIBLE);
    }
}
