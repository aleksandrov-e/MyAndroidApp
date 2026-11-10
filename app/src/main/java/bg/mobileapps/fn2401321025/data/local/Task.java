package bg.mobileapps.fn2401321025.data.local;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

/** Една задача – ред в таблицата "tasks". */
@Entity(tableName = "tasks")
public class Task {

    public static final int PRIORITY_LOW = 0;
    public static final int PRIORITY_MEDIUM = 1;
    public static final int PRIORITY_HIGH = 2;

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String title = "";

    @NonNull
    public String description = "";

    public int priority = PRIORITY_MEDIUM;

    /** Краен срок като UTC полунощ в милисекунди или null, ако няма срок. */
    @Nullable
    public Long dueDate;

    public boolean done;

    public long createdAt;

    public Task copy() {
        Task copy = new Task();
        copy.id = id;
        copy.title = title;
        copy.description = description;
        copy.priority = priority;
        copy.dueDate = dueDate;
        copy.done = done;
        copy.createdAt = createdAt;
        return copy;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Task)) {
            return false;
        }
        Task task = (Task) other;
        return id == task.id
                && priority == task.priority
                && done == task.done
                && createdAt == task.createdAt
                && title.equals(task.title)
                && description.equals(task.description)
                && Objects.equals(dueDate, task.dueDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, priority, dueDate, done, createdAt);
    }
}
