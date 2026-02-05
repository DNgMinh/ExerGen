package com.example.exergen.presentation;

import android.os.Bundle;
<<<<<<< HEAD
import androidx.appcompat.app.AppCompatActivity;
import com.example.exergen.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
=======
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.exergen.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This is the Java equivalent of enableEdgeToEdge()
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // --- Handle the Edge-to-Edge window insets ---
        // Note: The original 'enableEdgeToEdge()' is a Kotlin extension.
        // This is the direct Java way to achieve the same padding adjustment.
        View mainView = findViewById(R.id.main);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // --- ADD THE BOTTOM NAVIGATION LOGIC HERE ---
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_workouts) {
                Toast.makeText(MainActivity.this, "Workouts Clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_add) {
                Toast.makeText(MainActivity.this, "Add Clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_stats) {
                Toast.makeText(MainActivity.this, "Stats Clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });
>>>>>>> e681749498421cb6c3bd566de5ae59d95facb2a8
    }
}
