package bg.mobileapps.fn2401321025.ui.list;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import java.util.List;

import bg.mobileapps.fn2401321025.data.TaskRepository;
import bg.mobileapps.fn2401321025.data.local.Task;
import bg.mobileapps.fn2401321025.domain.TaskFilter;
import bg.mobileapps.fn2401321025.domain.TaskStats;

/** Състоянието на екрана със списъка: търсене + филтър върху задачите от Repository-то. */
public class TaskListViewModel extends ViewModel {

    private final TaskRepository repository;

    private final MutableLiveData<String> query = new MutableLiveData<>("");
    private final MutableLiveData<TaskFilter> filter = new MutableLiveData<>(TaskFilter.ALL);
    private final MediatorLiveData<List<Task>> tasks = new MediatorLiveData<>();

    private final LiveData<TaskStats> stats;

    public TaskListViewModel(TaskRepository repository) {
        this.repository = repository;

        // Напредъкът се смята върху всички задачи, независимо от търсенето и филтъра.
        stats = Transformations.map(repository.observeTasks(""), TaskStats::from);

        LiveData<List<Task>> searched = Transformations.switchMap(query, repository::observeTasks);
        tasks.addSource(searched, list -> tasks.setValue(currentFilter().apply(list)));
        tasks.addSource(filter, selected -> {
            List<Task> list = searched.getValue();
            if (list != null) {
                tasks.setValue(selected.apply(list));
            }
        });
    }

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    public LiveData<TaskStats> getStats() {
        return stats;
    }

    public LiveData<TaskFilter> getFilter() {
        return filter;
    }

    public String getQuery() {
        String value = query.getValue();
        return value == null ? "" : value;
    }

    public void setQuery(String text) {
        String value = text == null ? "" : text;
        if (!value.equals(query.getValue())) {
            query.setValue(value);
        }
    }

    public void setFilter(@NonNull TaskFilter selected) {
        if (selected != filter.getValue()) {
            filter.setValue(selected);
        }
    }

    public void setDone(@NonNull Task task, boolean done) {
        if (task.done != done) {
            repository.setDone(task.id, done);
        }
    }

    public void delete(@NonNull Task task) {
        repository.delete(task);
    }

    public void restore(@NonNull Task task) {
        repository.restore(task);
    }

    public void deleteCompleted() {
        repository.deleteCompleted();
    }

    private TaskFilter currentFilter() {
        TaskFilter value = filter.getValue();
        return value == null ? TaskFilter.ALL : value;
    }
}
