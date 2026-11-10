package bg.mobileapps.fn2401321025.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import bg.mobileapps.fn2401321025.domain.TaskValidator.TitleError;

public class TaskValidatorTest {

    @Test
    public void nullTitle_isEmpty() {
        assertEquals(TitleError.EMPTY, TaskValidator.validateTitle(null));
    }

    @Test
    public void blankTitle_isEmpty() {
        assertEquals(TitleError.EMPTY, TaskValidator.validateTitle("   \n"));
    }

    @Test
    public void normalTitle_isValid() {
        assertEquals(TitleError.NONE, TaskValidator.validateTitle("Купи хляб"));
    }

    @Test
    public void titleAtLimit_isValid() {
        String title = repeat('a', TaskValidator.MAX_TITLE_LENGTH);
        assertEquals(TitleError.NONE, TaskValidator.validateTitle(title));
    }

    @Test
    public void titleOverLimit_isTooLong() {
        String title = repeat('a', TaskValidator.MAX_TITLE_LENGTH + 1);
        assertEquals(TitleError.TOO_LONG, TaskValidator.validateTitle(title));
    }

    @Test
    public void surroundingSpaces_doNotCountTowardsLimit() {
        String title = "  " + repeat('a', TaskValidator.MAX_TITLE_LENGTH) + "  ";
        assertEquals(TitleError.NONE, TaskValidator.validateTitle(title));
    }

    private static String repeat(char symbol, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(symbol);
        }
        return builder.toString();
    }
}
