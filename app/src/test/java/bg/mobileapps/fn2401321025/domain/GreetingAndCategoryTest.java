package bg.mobileapps.fn2401321025.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GreetingAndCategoryTest {

    @Test
    public void greeting_followsTimeOfDay() {
        assertEquals(Greeting.EVENING, Greeting.forHour(0));
        assertEquals(Greeting.EVENING, Greeting.forHour(4));
        assertEquals(Greeting.MORNING, Greeting.forHour(5));
        assertEquals(Greeting.MORNING, Greeting.forHour(11));
        assertEquals(Greeting.DAY, Greeting.forHour(12));
        assertEquals(Greeting.DAY, Greeting.forHour(17));
        assertEquals(Greeting.EVENING, Greeting.forHour(18));
        assertEquals(Greeting.EVENING, Greeting.forHour(23));
    }

    @Test
    public void category_roundTripsThroughId() {
        for (TaskCategory category : TaskCategory.values()) {
            assertEquals(category, TaskCategory.fromId(category.id));
        }
    }

    @Test
    public void unknownCategoryId_fallsBackToPersonal() {
        assertEquals(TaskCategory.PERSONAL, TaskCategory.fromId(99));
        assertEquals(TaskCategory.PERSONAL, TaskCategory.fromId(-1));
    }
}
