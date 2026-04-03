package com.example.exergen.presentation;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import android.widget.EditText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.exergen.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class WorkoutsLibraryUiTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    private void openEditorForBeginnerFullBody() {
        // Navigate to Workouts tab
        onView(allOf(withId(R.id.nav_workouts), isDisplayed())).perform(click());

        // Select "Beginner Full Body" to edit
        // We use isDisplayed() to ensure we only interact with the visible fragment
        onView(allOf(withId(R.id.btn_edit_workout),
                isDisplayed(),
                isDescendantOfA(allOf(
                        isDisplayed(),
                        withParent(withId(R.id.exercise_recycler_view)),
                        hasDescendant(withText("Beginner Full Body"))))))
                .perform(click());
    }

    @Test
    public void testEditWorkout_UpdateSets() {
        openEditorForBeginnerFullBody();

        // Change number of sets
        onView(allOf(withId(R.id.et_edit_sets), isDisplayed())).perform(replaceText("5"), closeSoftKeyboard());

        // Save
        onView(allOf(withId(R.id.btn_editor_save), isDisplayed())).perform(click());

        // Verify return to library
        onView(allOf(withText("Beginner Full Body"), isDisplayed())).check(matches(isDisplayed()));
    }

    @Test
    public void testEditWorkout_DeleteAndAddExercise() {
        openEditorForBeginnerFullBody();

        // Delete "Pushups"
        onView(allOf(withId(R.id.btn_remove_step),
                isDisplayed(),
                isDescendantOfA(allOf(
                        isDisplayed(),
                        withParent(withId(R.id.rv_edit_steps)),
                        hasDescendant(withText("Pushups"))))))
                .perform(click());

        // Add "Pushups" back via picker
        onView(allOf(withId(R.id.btn_editor_add_exercise), isDisplayed())).perform(click());
        onView(withText("Pushups")).perform(click());

        // Save
        onView(allOf(withId(R.id.btn_editor_save), isDisplayed())).perform(click());

        // Verify return to library
        onView(allOf(withText("Beginner Full Body"), isDisplayed())).check(matches(isDisplayed()));
    }

    @Test
    public void testEditWorkout_UpdateTiming() {
        openEditorForBeginnerFullBody();

        // Change working time for Pushups
        onView(allOf(withId(R.id.tv_step_work),
                isDisplayed(),
                isDescendantOfA(allOf(
                        isDisplayed(),
                        withParent(withId(R.id.rv_edit_steps)),
                        hasDescendant(withText("Pushups"))))))
                .perform(click());

        onView(allOf(isAssignableFrom(EditText.class), isDisplayed())).perform(replaceText("20"), closeSoftKeyboard());
        onView(withText("OK")).perform(click());

        // Change rest time for Pushups
        onView(allOf(withId(R.id.tv_step_rest),
                isDisplayed(),
                isDescendantOfA(allOf(
                        isDisplayed(),
                        withParent(withId(R.id.rv_edit_steps)),
                        hasDescendant(withText("Pushups"))))))
                .perform(click());

        onView(allOf(isAssignableFrom(EditText.class), isDisplayed())).perform(replaceText("10"), closeSoftKeyboard());
        onView(withText("OK")).perform(click());

        // Save
        onView(allOf(withId(R.id.btn_editor_save), isDisplayed())).perform(click());

        // Verify return to library
        onView(allOf(withText("Beginner Full Body"), isDisplayed())).check(matches(isDisplayed()));
    }
}
