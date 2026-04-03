package com.example.exergen.presentation;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.exergen.R;
import com.example.exergen.application.AppBootstrap;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.HashMap;
import java.util.Map;

// Entry point of the main navigation container and handles bottom navigation logic
public class MainActivity extends AppCompatActivity {

    private final Map<Integer, Fragment> fragmentCache = new HashMap<>();
    private Fragment activeFragment;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Handle edge-to-edge display logic
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Initialize bottom navigation
        bottomNav = findViewById(R.id.bottom_navigation);

        // Hover effect: Toggle label visibility when mouse/pointer enters/exits the bar
        View.OnHoverListener hoverListener = (v, event) -> {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_HOVER_ENTER || action == MotionEvent.ACTION_HOVER_MOVE) {
                bottomNav.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
            } else if (action == MotionEvent.ACTION_HOVER_EXIT) {
                bottomNav.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_AUTO);
            }
            return false;
        };
        bottomNav.setOnHoverListener(hoverListener);

        // Ensure the hover listener is applied to all child items for responsiveness
        bottomNav.post(() -> {
            View menuView = bottomNav.getChildAt(0);
            if (menuView instanceof ViewGroup) {
                ViewGroup navigationMenuView = (ViewGroup) menuView;
                for (int i = 0; i < navigationMenuView.getChildCount(); i++) {
                    navigationMenuView.getChildAt(i).setOnHoverListener(hoverListener);
                }
            }
        });

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment selectedFragment = fragmentCache.get(itemId);

            if (selectedFragment == null) {
                if (itemId == R.id.nav_timer) {
                    TimerFragment fragment = new TimerFragment();
                    fragment.setDependencies(
                            AppBootstrap.get().sessionHistoryUseCase,
                            AppBootstrap.get().caloriesEstimationUseCase);
                    selectedFragment = fragment;
                } else if (itemId == R.id.nav_workouts) {
                    WorkoutsFragment fragment = new WorkoutsFragment();
                    fragment.setDependencies(
                            AppBootstrap.get().workoutUseCase,
                            AppBootstrap.get().exerciseUseCase,
                            AppBootstrap.get().sessionHistoryUseCase,
                            AppBootstrap.get().caloriesEstimationUseCase);
                    selectedFragment = fragment;
                } else if (itemId == R.id.nav_add) {
                    WorkoutBuilderFragment fragment = new WorkoutBuilderFragment();
                    fragment.setDependencies(
                            AppBootstrap.get().workoutBuilderUseCase,
                            AppBootstrap.get().workoutUseCase,
                            AppBootstrap.get().exerciseUseCase,
                            AppBootstrap.get().sessionHistoryUseCase,
                            AppBootstrap.get().caloriesEstimationUseCase,
                            AppBootstrap.get().enumMapper);
                    selectedFragment = fragment;
                } else if (itemId == R.id.nav_exercises) {
                    ExercisesFragment fragment = new ExercisesFragment();
                    fragment.setDependencies(AppBootstrap.get().exerciseUseCase);
                    selectedFragment = fragment;
                } else if (itemId == R.id.nav_stats) {
                    StatsFragment fragment = new StatsFragment();
                    fragment.setDependencies(
                            AppBootstrap.get().sessionHistoryUseCase,
                            AppBootstrap.get().statisticsUseCase);
                    selectedFragment = fragment;
                }
                
                if (selectedFragment != null) {
                    fragmentCache.put(itemId, selectedFragment);
                    getSupportFragmentManager().beginTransaction()
                            .add(R.id.fragment_container, selectedFragment, String.valueOf(itemId))
                            .hide(selectedFragment)
                            .commit();
                }
            }

            if (selectedFragment != null) {
                switchFragment(selectedFragment);
                return true;
            }
            return false;
        });

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                bottomNav.setVisibility(View.GONE);
            } else {
                bottomNav.setVisibility(View.VISIBLE);
            }
        });

        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_workouts);
        }
    }

    private void switchFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        // Clear backstack when switching main tabs
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        
        FragmentTransaction transaction = fm.beginTransaction();
        if (activeFragment != null) {
            transaction.hide(activeFragment);
        }
        transaction.show(fragment).commit();
        activeFragment = fragment;
    }
}
