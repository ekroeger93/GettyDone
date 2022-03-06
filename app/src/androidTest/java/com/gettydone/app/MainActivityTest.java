package com.gettydone.app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import org.junit.Rule;
import org.junit.Test;

public class MainActivityTest  {


    @Rule public ActivityScenarioRule<MainActivity> activityScenarioRule
            = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void test(){

        onView(withId(R.id.addDeleteBtn)).perform(click());
        onView(withId(R.id.addDeleteBtn)).perform(click());
        onView(withId(R.id.addDeleteBtn)).perform(click());

        onView(withId(R.id.ScrollView)).perform
                (RecyclerViewActions.actionOnItemAtPosition(1, ViewActions.longClick())
                );

        onView(withId(R.id.ScrollView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(1, ViewActions.typeTextIntoFocusedView("yep"))
        );

        closeSoftKeyboard();


        onView(withId(R.id.ScrollView)).perform
                (RecyclerViewActions.actionOnItemAtPosition(2, ViewActions.longClick())
                );

        onView(withId(R.id.ScrollView)).perform(
                RecyclerViewActions.actionOnItemAtPosition(2, ViewActions.typeTextIntoFocusedView("yep"))
        );

        closeSoftKeyboard();


        onView(withId(R.id.touchExpander)).perform(
                swipeRight()
        );


    }



//
//    Before:
//    MyActivity activity = Robolectric.setupActivity(MyActivity.class);
//    assertThat(activity.getSomething()).isEqualTo("something");
//
//    After:
//            try(ActivityScenario<MyActivity> scenario = ActivityScenario.launch(MyActivity.class)) {
//        scenario.onActivity(activity -> {
//            assertThat(activity.getSomething()).isEqualTo("something");
//        });
//    }
//
//    Before:
//    ActivityController<MyActivity> controller = Robolectric.buildActivity(MyActivity.class);
//   controller.create().start().resume();  // Moves the activity state to State.RESUMED.
//   controller.pause();    // Moves the activity state to State.STARTED. (ON_PAUSE is an event).
//   controller.stop();     // Moves the activity state to State.CREATED. (ON_STOP is an event).
//   controller.destroy();  // Moves the activity state to State.DESTROYED.
//
//    After:
//            try(ActivityScenario<MyActivity> scenario = ActivityScenario.launch(MyActivity.class)) {
//        scenario.moveToState(State.RESUMED);    // Moves the activity state to State.RESUMED.
//        scenario.moveToState(State.STARTED);    // Moves the activity state to State.STARTED.
//        scenario.moveToState(State.CREATED);    // Moves the activity state to State.CREATED.
//        scenario.moveToState(State.DESTROYED);  // Moves the activity state to State.DESTROYED.
//    }

}