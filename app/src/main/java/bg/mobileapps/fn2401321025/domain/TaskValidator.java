package bg.mobileapps.fn2401321025.domain;

/** Правилата за валидна задача – чиста Java логика без Android зависимости. */
public final class TaskValidator {

    public static final int MAX_TITLE_LENGTH = 80;

    public enum TitleError { NONE, EMPTY, TOO_LONG }

    private TaskValidator() {
    }

    public static TitleError validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return TitleError.EMPTY;
        }
        if (title.trim().length() > MAX_TITLE_LENGTH) {
            return TitleError.TOO_LONG;
        }
        return TitleError.NONE;
    }
}
