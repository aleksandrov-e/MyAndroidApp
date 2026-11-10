package bg.mobileapps.fn2401321025.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.Executor;

import bg.mobileapps.fn2401321025.data.local.Task;
import bg.mobileapps.fn2401321025.data.local.TaskDao;
import bg.mobileapps.fn2401321025.data.remote.TodoApi;
import bg.mobileapps.fn2401321025.data.remote.TodoDto;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DefaultTaskRepositoryTest {

    private static final Executor DIRECT = Runnable::run;

    private TaskDao dao;
    private TodoApi api;
    private Call<TodoDto> call;
    private DefaultTaskRepository repository;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        dao = mock(TaskDao.class);
        api = mock(TodoApi.class);
        call = mock(Call.class);
        when(api.randomTodo()).thenReturn(call);
        repository = new DefaultTaskRepository(dao, api, DIRECT, DIRECT);
    }

    @Test
    public void save_newTask_insertsAndSetsIdAndCreationTime() {
        when(dao.insert(any(Task.class))).thenReturn(7L);
        Task task = new Task();
        task.title = "Нова";
        boolean[] saved = {false};

        repository.save(task, () -> saved[0] = true);

        verify(dao).insert(task);
        verify(dao, never()).update(any(Task.class));
        assertEquals(7L, task.id);
        assertTrue(task.createdAt > 0);
        assertTrue(saved[0]);
    }

    @Test
    public void save_existingTask_updates() {
        Task task = new Task();
        task.id = 3;

        repository.save(task, null);

        verify(dao).update(task);
        verify(dao, never()).insert(any(Task.class));
    }

    @Test
    public void observeTasks_trimsQuery() {
        repository.observeTasks("  мляко ");

        verify(dao).observeTasks("мляко");
    }

    @Test
    public void loadTask_returnsTaskFromDao() {
        Task stored = new Task();
        when(dao.getById(5)).thenReturn(stored);
        Task[] loaded = new Task[1];

        repository.loadTask(5, task -> loaded[0] = task);

        assertSame(stored, loaded[0]);
    }

    @Test
    public void deleteRestoreAndDone_delegateToDao() {
        Task task = new Task();
        task.id = 9;

        repository.delete(task);
        repository.restore(task);
        repository.setDone(9, true);
        repository.deleteCompleted();

        verify(dao).delete(task);
        verify(dao).insert(task);
        verify(dao).setDone(9, true);
        verify(dao).deleteCompleted();
    }

    @Test
    public void fetchSuggestion_success_returnsTrimmedText() {
        TodoDto dto = new TodoDto();
        dto.todo = "  Learn Java ";
        respondWith(Response.success(dto));
        RecordingCallback callback = new RecordingCallback();

        repository.fetchSuggestion(callback);

        assertEquals("Learn Java", callback.text);
        assertEquals(0, callback.errors);
    }

    @Test
    public void fetchSuggestion_emptyBody_reportsError() {
        respondWith(Response.success(new TodoDto()));
        RecordingCallback callback = new RecordingCallback();

        repository.fetchSuggestion(callback);

        assertNull(callback.text);
        assertEquals(1, callback.errors);
    }

    @Test
    public void fetchSuggestion_httpError_reportsError() {
        respondWith(Response.error(500,
                ResponseBody.create("oops", MediaType.get("text/plain"))));
        RecordingCallback callback = new RecordingCallback();

        repository.fetchSuggestion(callback);

        assertEquals(1, callback.errors);
    }

    @Test
    public void fetchSuggestion_networkFailure_reportsError() {
        doAnswer(invocation -> {
            Callback<TodoDto> callback = invocation.getArgument(0);
            callback.onFailure(call, new IOException("no network"));
            return null;
        }).when(call).enqueue(any());
        RecordingCallback callback = new RecordingCallback();

        repository.fetchSuggestion(callback);

        assertEquals(1, callback.errors);
    }

    private void respondWith(Response<TodoDto> response) {
        doAnswer(invocation -> {
            Callback<TodoDto> callback = invocation.getArgument(0);
            callback.onResponse(call, response);
            return null;
        }).when(call).enqueue(any());
    }

    private static class RecordingCallback implements TaskRepository.SuggestionCallback {
        String text;
        int errors;

        @Override
        public void onSuccess(String value) {
            text = value;
        }

        @Override
        public void onError() {
            errors++;
        }
    }
}
