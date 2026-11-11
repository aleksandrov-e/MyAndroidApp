package bg.mobileapps.fn2401321025.domain;

import java.util.List;

import bg.mobileapps.fn2401321025.data.local.Task;

/** Броят на всички и на завършените задачи – за лентата с напредъка. */
public final class TaskStats {

    public final int total;
    public final int done;

    public TaskStats(int total, int done) {
        this.total = total;
        this.done = done;
    }

    public static TaskStats from(List<Task> tasks) {
        if (tasks == null) {
            return new TaskStats(0, 0);
        }
        int done = 0;
        for (Task task : tasks) {
            if (task.done) {
                done++;
            }
        }
        return new TaskStats(tasks.size(), done);
    }

    /** Процент завършени, закръглен до цяло число; 0, когато няма задачи. */
    public int percent() {
        return total == 0 ? 0 : Math.round(done * 100f / total);
    }

    public boolean allDone() {
        return total > 0 && done == total;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof TaskStats)) {
            return false;
        }
        TaskStats stats = (TaskStats) other;
        return total == stats.total && done == stats.done;
    }

    @Override
    public int hashCode() {
        return 31 * total + done;
    }
}
