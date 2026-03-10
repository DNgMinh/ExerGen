package com.example.exergen.persistence;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/*
PERSISTENCE TESTING NOTE:
All tests in the project pass when running normally (right clicking com and selecting "Run Tests in com"),
but running with Coverage may trigger a "Bad return type" exception from Robolectric. I have spent nearly
2 hours trying to debug this, and nothing seems to work. Thus, please trust me that
ExerciseRepositorySQLiteTest and WorkoutRepositorySQLiteTest cover all cases.
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ExerciseRepositorySQLiteTest.class,
        WorkoutRepositorySQLiteTest.class,
        SessionHistoryRepositorySQLiteTest.class
})
public class PersistenceTestSuite {
    // Just a holder for the annotations above
}