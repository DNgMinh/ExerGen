package com.example.exergen.presentation;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.example.exergen.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Entry point of the main navigation container and handles bottom navigation logic
public class MainActivity extends AppCompatActivity {

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
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // Select the fragment based on the clicked ID
            // P.S. We know this is ugly, but we can't use switch statements
            // because Google thought it'd be a good idea to make resource IDs
            // non-final :/
            if (itemId == R.id.nav_timer) {
                selectedFragment = new TimerFragment();
            } else if (itemId == R.id.nav_workouts) {
                selectedFragment = new WorkoutsFragment();
            } else if (itemId == R.id.nav_add) {
                selectedFragment = new WorkoutBuilderFragment();
            } else if (itemId == R.id.nav_stats) {
                selectedFragment = new StatsFragment();
            }

            // Load the fragment if a valid one was selected
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
                return true;
            }
            return false;
        });

        // Set default screen to Timer so it isn't empty on startup
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.nav_timer);
        }
    }

    // Helper method to swap fragments cleanly
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}