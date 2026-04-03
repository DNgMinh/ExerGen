package com.example.exergen.presentation;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.exergen.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class WorkoutBuilderUiTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testWorkoutBuilder_GeneratesWorkout() {
        // Navigate to Workout Builder
        onView(withId(R.id.nav_add)).perform(click());

        // Fill in details: 3 exercises
        onView(withId(R.id.et_exercise_count)).perform(typeText("3"), closeSoftKeyboard());
        
        // Select muscle groups
        onView(withId(R.id.btn_muscle_toggle)).perform(click());
        
        // Since checkboxes are now generated dynamically, we find them by their text label
        onView(withText("Chest")).perform(click());

        // Generate
        onView(withId(R.id.btn_generate_workout)).perform(click());

        // Verify results dialog appeared with the correct title and content
        onView(withText("Your Generated Routine")).check(matches(isDisplayed()));
        onView(withText(containsString("Chest"))).check(matches(isDisplayed()));
    }
}
