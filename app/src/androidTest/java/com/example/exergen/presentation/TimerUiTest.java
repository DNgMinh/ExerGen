package com.example.exergen.presentation;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

import android.view.View;
import android.widget.NumberPicker;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.exergen.R;

import org.hamcrest.Matcher;
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

    @Test
    public void testTimer_PauseAndReset() {
        // Navigate to Timer
        onView(withId(R.id.nav_timer)).perform(click());

        // Start and immediately pause
        onView(withId(R.id.btn_start)).perform(click());
        onView(withId(R.id.btn_pause)).perform(click());

        // Click Stop (which acts as reset in setup state)
        onView(withId(R.id.btn_stop)).perform(click());

        // Verify the timer reset to the initial state
        onView(withId(R.id.tv_phase)).check(matches(withText("")));
        onView(withId(R.id.tv_timer)).check(matches(withText(R.string.timer_default)));

        // Ensure the Start button text is back to "Begin"
        onView(withId(R.id.btn_start)).check(matches(withText(R.string.btn_start)));
        onView(withId(R.id.picker_container)).check(matches(isDisplayed()));
    }

    @Test
    public void testTimer_TransitionFromWorkToRest() throws InterruptedException {
        onView(withId(R.id.nav_timer)).perform(click());

        // Set short work time
        onView(withId(R.id.np_work)).perform(setNumber(5));
        onView(withId(R.id.np_rest)).perform(setNumber(5));

        // Start timer
        onView(withId(R.id.btn_start)).perform(click());

        // Verify it starts in WORK
        onView(withId(R.id.tv_phase)).check(matches(withText(containsString("WORK"))));

        Thread.sleep(7000);

        // Verify it transitioned to REST
        onView(withId(R.id.tv_phase)).check(matches(withText(containsString("REST"))));
    }

    // Helper action to set NumberPicker value
    public static ViewAction setNumber(final int num) {
        return new ViewAction() {
            @Override
            public void perform(UiController uiController, View view) {
                NumberPicker np = (NumberPicker) view;
                np.setValue(num);
            }

            @Override
            public String getDescription() {
                return "set number on NumberPicker";
            }

            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(NumberPicker.class);
            }
        };
    }
}
