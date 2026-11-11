package bg.mobileapps.fn2401321025.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import bg.mobileapps.fn2401321025.data.local.Task;

public class TaskStatsTest {

    @Test
    public void nullList_hasNoTasks() {
        TaskStats stats = TaskStats.from(null);

        assertEquals(0, stats.total);
        assertEquals(0, stats.percent());
        assertFalse(stats.allDone());
    }

    @Test
    public void emptyList_isNotAllDone() {
        assertFalse(TaskStats.from(Collections.emptyList()).allDone());
    }

    @Test
    public void countsFinishedTasks() {
        TaskStats stats = TaskStats.from(Arrays.asList(task(true), task(false), task(false)));

        assertEquals(3, stats.total);
        assertEquals(1, stats.done);
        assertEquals(33, stats.percent());
        assertFalse(stats.allDone());
    }

    @Test
    public void percent_isRounded() {
        assertEquals(67, new TaskStats(3, 2).percent());
    }

    @Test
    public void allTasksFinished_isAllDone() {
        TaskStats stats = TaskStats.from(Arrays.asList(task(true), task(true)));

        assertEquals(100, stats.percent());
        assertTrue(stats.allDone());
    }

    private static Task task(boolean done) {
        Task task = new Task();
        task.done = done;
        return task;
    }
}
