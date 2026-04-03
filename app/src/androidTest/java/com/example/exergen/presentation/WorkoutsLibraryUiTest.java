package com.example.exergen.presentation;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
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
        // Navigate to Workouts tab - Ensure it's clicked even if it's the default
        onView(withId(R.id.nav_workouts)).perform(click());

        // Select "Beginner Full Body" to edit
        onView(allOf(withId(R.id.btn_edit_workout), 
                isDescendantOfA(allOf(
                        withParent(withId(R.id.exercise_recycler_view)),
                        hasDescendant(withText("Beginner Full Body"))))))
                .perform(click());
    }

    @Test
    public void testEditWorkout_UpdateSets() {
        openEditorForBeginnerFullBody();

        // Change number of sets
        onView(withId(R.id.et_edit_sets)).perform(replaceText("5"), closeSoftKeyboard());

        // Save
        onView(withId(R.id.btn_editor_save)).perform(click());

        // Verify return to library
        onView(withText("Beginner Full Body")).check(matches(isDisplayed()));
    }

    @Test
    public void testEditWorkout_DeleteAndAddExercise() {
        openEditorForBeginnerFullBody();

        // Delete "Pushups" - targeted specifically within its row item
        onView(allOf(withId(R.id.btn_remove_step), 
                isDescendantOfA(allOf(
                        withParent(withId(R.id.rv_edit_steps)),
                        hasDescendant(withText("Pushups"))))))
                .perform(click());

        // Add "Pushups" back via picker
        onView(withId(R.id.btn_editor_add_exercise)).perform(click());
        onView(withText("Pushups")).perform(click());

        // Save
        onView(withId(R.id.btn_editor_save)).perform(click());

        // Verify return to library
        onView(withText("Beginner Full Body")).check(matches(isDisplayed()));
    }

    @Test
    public void testEditWorkout_UpdateTiming() {
        openEditorForBeginnerFullBody();

        // Change working time for Pushups
        onView(allOf(withId(R.id.tv_step_work), 
                isCompletelyDisplayed(),
                isDescendantOfA(allOf(
                        withParent(withId(R.id.rv_edit_steps)),
                        hasDescendant(withText("Pushups"))))))
                .perform(click());

        onView(isAssignableFrom(EditText.class)).perform(replaceText("20"), closeSoftKeyboard());
        onView(withText("OK")).perform(click());

        // Change rest time for Pushups
        onView(allOf(withId(R.id.tv_step_rest), 
                isCompletelyDisplayed(),
                isDescendantOfA(allOf(
                        withParent(withId(R.id.rv_edit_steps)),
                        hasDescendant(withText("Pushups"))))))
                .perform(click());
        
        onView(isAssignableFrom(EditText.class)).perform(replaceText("10"), closeSoftKeyboard());
        onView(withText("OK")).perform(click());

        // Save
        onView(withId(R.id.btn_editor_save)).perform(click());

        // Verify return to library
        onView(withText("Beginner Full Body")).check(matches(isDisplayed()));
    }
}
