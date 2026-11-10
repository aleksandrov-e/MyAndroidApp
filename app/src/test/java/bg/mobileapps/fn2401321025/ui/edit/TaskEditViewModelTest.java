package bg.mobileapps.fn2401321025.ui.edit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import bg.mobileapps.fn2401321025.FakeTaskRepository;
import bg.mobileapps.fn2401321025.R;
import bg.mobileapps.fn2401321025.data.local.Task;

public class TaskEditViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantExecutor = new InstantTaskExecutorRule();

    private FakeTaskRepository repository;
    private TaskEditViewModel viewModel;

    @Before
    public void setUp() {
        repository = new FakeTaskRepository();
        viewModel = new TaskEditViewModel(repository);
    }

    @Test
    public void save_withEmptyTitle_showsErrorAndSavesNothing() {
        viewModel.start(-1);

        viewModel.save("   ", "", Task.PRIORITY_LOW);

        assertEquals(Integer.valueOf(R.string.error_title_empty),
                viewModel.getTitleError().getValue());
        assertTrue(repository.tasks.isEmpty());
        assertNull(viewModel.getFinished().getValue());
    }

    @Test
    public void save_newTask_storesTrimmedValuesAndCloses() {
        viewModel.start(-1);
        viewModel.setDueDate(1_000L);

        viewModel.save("  Купи хляб ", " пълнозърнест ", Task.PRIORITY_HIGH);

        assertEquals(1, repository.tasks.size());
        Task saved = repository.tasks.get(0);
        assertEquals("Купи хляб", saved.title);
        assertEquals("пълнозърнест", saved.description);
        assertEquals(Task.PRIORITY_HIGH, saved.priority);
        assertEquals(Long.valueOf(1_000L), saved.dueDate);
        assertFalse(saved.done);
        assertNotNull(viewModel.getFinished().getValue());
    }

    @Test
    public void start_withExistingTask_loadsItOnce() {
        Task existing = repository.add("Стара задача", true);
        existing.dueDate = 5_000L;

        viewModel.start(existing.id);
        viewModel.start(existing.id);

        assertEquals("Стара задача", viewModel.getLoadedTask().getValue().peek().title);
        assertEquals(Long.valueOf(5_000L), viewModel.getDueDate().getValue());
    }

    @Test
    public void save_existingTask_updatesItAndKeepsDoneFlag() {
        Task existing = repository.add("Стара задача", true);
        viewModel.start(existing.id);

        viewModel.save("Нова задача", "", Task.PRIORITY_LOW);

        assertEquals(1, repository.tasks.size());
        Task saved = repository.find(existing.id);
        assertEquals("Нова задача", saved.title);
        assertTrue(saved.done);
    }

    @Test
    public void start_withMissingTask_closesScreen() {
        viewModel.start(42);

        assertNotNull(viewModel.getFinished().getValue());
    }

    @Test
    public void delete_removesTaskAndCloses() {
        Task existing = repository.add("За триене", false);
        viewModel.start(existing.id);

        viewModel.delete();

        assertTrue(repository.tasks.isEmpty());
        assertNotNull(viewModel.getFinished().getValue());
    }

    @Test
    public void onTitleChanged_clearsError() {
        viewModel.save("", "", Task.PRIORITY_LOW);

        viewModel.onTitleChanged();

        assertNull(viewModel.getTitleError().getValue());
    }

    @Test
    public void requestSuggestion_success_publishesText() {
        repository.suggestionToReturn = "Read a book";

        viewModel.requestSuggestion();

        assertEquals("Read a book", viewModel.getSuggestion().getValue().peek());
        assertEquals(Boolean.FALSE, viewModel.getSuggestionLoading().getValue());
        assertNull(viewModel.getMessage().getValue());
    }

    @Test
    public void requestSuggestion_failure_publishesErrorMessage() {
        repository.suggestionToReturn = null;

        viewModel.requestSuggestion();

        assertEquals(Integer.valueOf(R.string.suggestion_error),
                viewModel.getMessage().getValue().peek());
        assertEquals(Boolean.FALSE, viewModel.getSuggestionLoading().getValue());
        assertNull(viewModel.getSuggestion().getValue());
    }
}
