package bg.mobileapps.fn2401321025.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;

import bg.mobileapps.fn2401321025.data.local.Task;
import bg.mobileapps.fn2401321025.data.local.TaskDao;
import bg.mobileapps.fn2401321025.data.remote.TodoApi;
import bg.mobileapps.fn2401321025.data.remote.TodoDto;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Room за локалните данни, Retrofit за REST заявката. */
public class DefaultTaskRepository implements TaskRepository {

    private final TaskDao taskDao;
    private final TodoApi todoApi;
    private final Executor ioExecutor;
    private final Executor mainExecutor;

    public DefaultTaskRepository(
            TaskDao taskDao, TodoApi todoApi, Executor ioExecutor, Executor mainExecutor) {
        this.taskDao = taskDao;
        this.todoApi = todoApi;
        this.ioExecutor = ioExecutor;
        this.mainExecutor = mainExecutor;
    }

    @Override
    public LiveData<List<Task>> observeTasks(@NonNull String query) {
        return taskDao.observeTasks(query.trim());
    }

    @Override
    public void loadTask(long id, @NonNull LoadCallback callback) {
        ioExecutor.execute(() -> {
            Task task = taskDao.getById(id);
            mainExecutor.execute(() -> callback.onLoaded(task));
        });
    }

    @Override
    public void save(@NonNull Task task, @Nullable Runnable onSaved) {
        ioExecutor.execute(() -> {
            if (task.id == 0) {
                if (task.createdAt == 0) {
                    task.createdAt = System.currentTimeMillis();
                }
                task.id = taskDao.insert(task);
            } else {
                taskDao.update(task);
            }
            if (onSaved != null) {
                mainExecutor.execute(onSaved);
            }
        });
    }

    @Override
    public void delete(@NonNull Task task) {
        ioExecutor.execute(() -> taskDao.delete(task));
    }

    @Override
    public void restore(@NonNull Task task) {
        ioExecutor.execute(() -> taskDao.insert(task));
    }

    @Override
    public void setDone(long id, boolean done) {
        ioExecutor.execute(() -> taskDao.setDone(id, done));
    }

    @Override
    public void deleteCompleted() {
        ioExecutor.execute(taskDao::deleteCompleted);
    }

    @Override
    public void fetchSuggestion(@NonNull SuggestionCallback callback) {
        todoApi.randomTodo().enqueue(new Callback<TodoDto>() {
            @Override
            public void onResponse(
                    @NonNull Call<TodoDto> call, @NonNull Response<TodoDto> response) {
                TodoDto body = response.body();
                if (response.isSuccessful() && body != null && body.todo != null
                        && !body.todo.trim().isEmpty()) {
                    callback.onSuccess(body.todo.trim());
                } else {
                    callback.onError();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TodoDto> call, @NonNull Throwable error) {
                callback.onError();
            }
        });
    }
}
