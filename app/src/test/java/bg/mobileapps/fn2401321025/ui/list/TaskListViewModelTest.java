package bg.mobileapps.fn2401321025.ui.list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import bg.mobileapps.fn2401321025.FakeTaskRepository;
import bg.mobileapps.fn2401321025.data.local.Task;
import bg.mobileapps.fn2401321025.domain.TaskFilter;

public class TaskListViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutor = new InstantTaskExecutorRule();

    private FakeTaskRepository repository;
    private TaskListViewModel viewModel;
    private List<Task> shown = new ArrayList<>();

    @Before
    public void setUp() {
        repository = new FakeTaskRepository();
        repository.add("Купи мляко", false);
        repository.add("Изпрати доклада", true);
        viewModel = new TaskListViewModel(repository);
        viewModel.getTasks().observeForever(tasks -> shown = tasks);
    }

    @Test
    public void showsAllTasksByDefault() {
        assertEquals(2, shown.size());
    }

    @Test
    public void activeFilter_hidesFinishedTasks() {
        viewModel.setFilter(TaskFilter.ACTIVE);

        assertEquals(1, shown.size());
        assertEquals("Купи мляко", shown.get(0).title);
    }

    @Test
    public void doneFilter_showsOnlyFinishedTasks() {
        viewModel.setFilter(TaskFilter.DONE);

        assertEquals(1, shown.size());
        assertTrue(shown.get(0).done);
    }

    @Test
    public void query_isPassedToRepository() {
        viewModel.setQuery("доклада");

        assertEquals("доклада", repository.lastQuery);
        assertEquals(1, shown.size());
        assertEquals("Изпрати доклада", shown.get(0).title);
    }

    @Test
    public void filterIsKeptWhenQueryChanges() {
        viewModel.setFilter(TaskFilter.ACTIVE);
        viewModel.setQuery("доклада");

        assertTrue(shown.isEmpty());
    }

    @Test
    public void setDone_updatesTaskAndList() {
        Task task = repository.find(1);

        viewModel.setDone(task, true);
        viewModel.setFilter(TaskFilter.ACTIVE);

        assertTrue(repository.find(1).done);
        assertTrue(shown.isEmpty());
    }

    @Test
    public void delete_thenRestore_bringsTaskBack() {
        Task task = repository.find(1);

        viewModel.delete(task);
        assertEquals(1, shown.size());

        viewModel.restore(task);
        assertEquals(2, shown.size());
    }

    @Test
    public void deleteCompleted_removesOnlyFinishedTasks() {
        viewModel.deleteCompleted();

        assertEquals(1, shown.size());
        assertFalse(shown.get(0).done);
    }
}
