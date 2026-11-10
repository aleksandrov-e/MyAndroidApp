package bg.mobileapps.fn2401321025;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import bg.mobileapps.fn2401321025.ui.MainActivity;

/** Основният сценарий: списък -> нова задача -> запис -> задачата се вижда в списъка. */
@RunWith(AndroidJUnit4.class)
public class AddTaskFlowTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void addTask_appearsInList() {
        String title = "UI тест " + System.currentTimeMillis();

        onView(withId(R.id.fab_add)).perform(click());
        onView(withId(R.id.input_title)).perform(replaceText(title), closeSoftKeyboard());
        onView(withId(R.id.button_save)).perform(scrollTo(), click());

        onView(withId(R.id.list_tasks))
                .perform(RecyclerViewActions.scrollTo(hasDescendant(withText(title))));
        onView(withText(title)).check(matches(isDisplayed()));
    }

    @Test
    public void saveWithoutTitle_showsValidationError() {
        onView(withId(R.id.fab_add)).perform(click());
        onView(withId(R.id.button_save)).perform(scrollTo(), click());

        onView(withText(R.string.error_title_empty)).check(matches(isDisplayed()));
    }
}
