package bg.mobileapps.fn2401321025.ui;

import androidx.annotation.Nullable;

/** Еднократно събитие в LiveData – не се обработва повторно след завъртане на екрана. */
public class Event<T> {

    private final T content;
    private boolean handled;

    public Event(T content) {
        this.content = content;
    }

    @Nullable
    public T getIfNotHandled() {
        if (handled) {
            return null;
        }
        handled = true;
        return content;
    }

    public T peek() {
        return content;
    }
}
