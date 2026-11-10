package bg.mobileapps.fn2401321025.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import bg.mobileapps.fn2401321025.data.local.Task;

public class TaskFilterTest {

    private final Task active = task(1, false);
    private final Task done = task(2, true);
    private final List<Task> tasks = Arrays.asList(active, done);

    @Test
    public void all_keepsEverything() {
        assertEquals(tasks, TaskFilter.ALL.apply(tasks));
    }

    @Test
    public void active_keepsOnlyUnfinished() {
        assertEquals(Arrays.asList(active), TaskFilter.ACTIVE.apply(tasks));
    }

    @Test
    public void done_keepsOnlyFinished() {
        assertEquals(Arrays.asList(done), TaskFilter.DONE.apply(tasks));
    }

    @Test
    public void nullList_givesEmptyList() {
        assertTrue(TaskFilter.ACTIVE.apply(null).isEmpty());
    }

    private static Task task(long id, boolean done) {
        Task task = new Task();
        task.id = id;
        task.title = "Задача " + id;
        task.done = done;
        return task;
    }
}
