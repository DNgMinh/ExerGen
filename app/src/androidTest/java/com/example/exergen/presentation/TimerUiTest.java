package com.example.exergen.presentation;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
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
public class TimerUiTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testTimer_StartsAndPauses() {
        // Navigate to Timer
        onView(withId(R.id.nav_timer)).perform(click());

        // Initial state: Start button is visible
        onView(withId(R.id.btn_start)).check(matches(isDisplayed()));

        // Start the timer
        onView(withId(R.id.btn_start)).perform(click());

        // Verify timer is running
        onView(withId(R.id.tv_phase)).check(matches(withText(containsString("WORK"))));

        // Click Pause
        onView(withId(R.id.btn_pause)).perform(click());

        // Verify paused state
        onView(withId(R.id.tv_phase)).check(matches(withText(containsString("PAUSED"))));
        
        // Resume button should be visible
        onView(withId(R.id.btn_start)).check(matches(withText(R.string.btn_resume)));
    }
}
