package com.example.exergen.presentation;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.CoreMatchers.containsString;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.exergen.R;
import com.example.exergen.model.SessionRecord;
import com.example.exergen.application.helper.SessionHistoryRepositorySQLite;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CaloriesSessionDetailsUiTest {

    @Test
    public void statsSessionDetails_ShowsEstimatedCaloriesAndExplanation() {
        long now = System.currentTimeMillis();
        String workoutName = "UI Calories Workout " + now;
        SessionRecord sessionRecord = new SessionRecord(
                "ui-calories-" + now,
                "ui-workout-" + now,
                workoutName,
                now,
                900,
                4,
                3,
                3,
                180);

        Context context = ApplicationProvider.getApplicationContext();
        SessionHistoryRepositorySQLite repository = new SessionHistoryRepositorySQLite(context);
        repository.saveSession(sessionRecord);

        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.nav_stats)).perform(click());
            onView(withText(workoutName)).perform(click());

            // Updated to match the new BottomSheet UI which separates label and value
            onView(withId(R.id.session_detail_calories))
                    .check(matches(withText("180 kcal")));

            onView(withText(containsString("approximate and not a medical measurement")))
                    .check(matches(isDisplayed()));
        }
    }
}
