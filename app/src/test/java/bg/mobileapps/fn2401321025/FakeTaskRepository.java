package bg.mobileapps.fn2401321025;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bg.mobileapps.fn2401321025.data.TaskRepository;
import bg.mobileapps.fn2401321025.data.local.Task;

/** Repository в паметта за тестовете на ViewModel-ите. */
public class FakeTaskRepository implements TaskRepository {

    public final List<Task> tasks = new ArrayList<>();
    public String lastQuery;
    public String suggestionToReturn;
    public int suggestionRequests;
    private final Map<String, MutableLiveData<List<Task>>> liveByQuery = new HashMap<>();
    private long nextId = 1;

    public Task add(String title, boolean done) {
        Task task = new Task();
        task.id = nextId++;
        task.title = title;
        task.done = done;
        tasks.add(task);
        publish();
        return task;
    }

    @Override
    public LiveData<List<Task>> observeTasks(@NonNull String query) {
        lastQuery = query;
        MutableLiveData<List<Task>> live = liveByQuery.get(query);
        if (live == null) {
            live = new MutableLiveData<>();
            liveByQuery.put(query, live);
        }
        publish();
        return live;
    }

    @Override
    public void loadTask(long id, @NonNull LoadCallback callback) {
        callback.onLoaded(find(id));
    }

    @Override
    public void save(@NonNull Task task, @Nullable Runnable onSaved) {
        if (task.id == 0) {
            task.id = nextId++;
            tasks.add(task);
        } else {
            tasks.remove(find(task.id));
            tasks.add(task);
        }
        publish();
        if (onSaved != null) {
            onSaved.run();
        }
    }

    @Override
    public void delete(@NonNull Task task) {
        tasks.remove(find(task.id));
        publish();
    }

    @Override
    public void restore(@NonNull Task task) {
        tasks.add(task);
        publish();
    }

    @Override
    public void setDone(long id, boolean done) {
        Task updated = find(id).copy();
        updated.done = done;
        tasks.set(tasks.indexOf(find(id)), updated);
        publish();
    }

    @Override
    public void deleteCompleted() {
        tasks.removeIf(task -> task.done);
        publish();
    }

    @Override
    public void fetchSuggestion(@NonNull SuggestionCallback callback) {
        suggestionRequests++;
        if (suggestionToReturn != null) {
            callback.onSuccess(suggestionToReturn);
        } else {
            callback.onError();
        }
    }

    public Task find(long id) {
        for (Task task : tasks) {
            if (task.id == id) {
                return task;
            }
        }
        return null;
    }

    private void publish() {
        for (Map.Entry<String, MutableLiveData<List<Task>>> entry : liveByQuery.entrySet()) {
            List<Task> matching = new ArrayList<>();
            for (Task task : tasks) {
                if (task.title.contains(entry.getKey())) {
                    matching.add(task);
                }
            }
            entry.getValue().setValue(matching);
        }
    }
}
