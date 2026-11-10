package bg.mobileapps.fn2401321025.domain;

import java.util.ArrayList;
import java.util.List;

import bg.mobileapps.fn2401321025.data.local.Task;

/** Филтърът от чиповете над списъка. */
public enum TaskFilter {
    ALL,
    ACTIVE,
    DONE;

    public boolean matches(Task task) {
        switch (this) {
            case ACTIVE:
                return !task.done;
            case DONE:
                return task.done;
            default:
                return true;
        }
    }

    public List<Task> apply(List<Task> tasks) {
        List<Task> result = new ArrayList<>();
        if (tasks == null) {
            return result;
        }
        for (Task task : tasks) {
            if (matches(task)) {
                result.add(task);
            }
        }
        return result;
    }
}
