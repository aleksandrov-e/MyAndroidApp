package bg.mobileapps.fn2401321025.ui.edit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import bg.mobileapps.fn2401321025.R;
import bg.mobileapps.fn2401321025.data.TaskRepository;
import bg.mobileapps.fn2401321025.data.local.Task;
import bg.mobileapps.fn2401321025.domain.TaskCategory;
import bg.mobileapps.fn2401321025.domain.TaskValidator;
import bg.mobileapps.fn2401321025.ui.Event;

/** Логиката на екрана за добавяне/редакция: зареждане, валидация, запис, REST предложение. */
public class TaskEditViewModel extends ViewModel {

    private final TaskRepository repository;

    private final MutableLiveData<Event<Task>> loadedTask = new MutableLiveData<>();
    private final MutableLiveData<Long> dueDate = new MutableLiveData<>(null);
    private final MutableLiveData<Integer> titleError = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> suggestionLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Event<String>> suggestion = new MutableLiveData<>();
    private final MutableLiveData<Event<Integer>> message = new MutableLiveData<>();
    private final MutableLiveData<Event<Boolean>> finished = new MutableLiveData<>();

    private boolean started;
    @Nullable
    private Task original;

    public TaskEditViewModel(TaskRepository repository) {
        this.repository = repository;
    }

    /** Вика се от фрагмента; при завъртане на екрана не зарежда повторно. */
    public void start(long taskId) {
        if (started) {
            return;
        }
        started = true;
        if (taskId <= 0) {
            return;
        }
        repository.loadTask(taskId, task -> {
            if (task == null) {
                finished.setValue(new Event<>(true));
                return;
            }
            original = task;
            dueDate.setValue(task.dueDate);
            loadedTask.setValue(new Event<>(task));
        });
    }

    public LiveData<Event<Task>> getLoadedTask() {
        return loadedTask;
    }

    public LiveData<Long> getDueDate() {
        return dueDate;
    }

    /** Ресурс със съобщение за грешка в заглавието или null, ако е валидно. */
    public LiveData<Integer> getTitleError() {
        return titleError;
    }

    public LiveData<Boolean> getSuggestionLoading() {
        return suggestionLoading;
    }

    public LiveData<Event<String>> getSuggestion() {
        return suggestion;
    }

    public LiveData<Event<Integer>> getMessage() {
        return message;
    }

    public LiveData<Event<Boolean>> getFinished() {
        return finished;
    }

    public void setDueDate(@Nullable Long utcMidnightMillis) {
        dueDate.setValue(utcMidnightMillis);
    }

    public void onTitleChanged() {
        if (titleError.getValue() != null) {
            titleError.setValue(null);
        }
    }

    public void save(String title, String description, int priority, TaskCategory category) {
        TaskValidator.TitleError error = TaskValidator.validateTitle(title);
        if (error != TaskValidator.TitleError.NONE) {
            titleError.setValue(error == TaskValidator.TitleError.EMPTY
                    ? R.string.error_title_empty
                    : R.string.error_title_too_long);
            return;
        }
        titleError.setValue(null);

        Task task = original != null ? original.copy() : new Task();
        task.title = title.trim();
        task.description = description == null ? "" : description.trim();
        task.priority = priority;
        task.category = category.id;
        task.dueDate = dueDate.getValue();
        repository.save(task, () -> finished.setValue(new Event<>(true)));
    }

    public void delete() {
        if (original != null) {
            repository.delete(original);
        }
        finished.setValue(new Event<>(true));
    }

    public void requestSuggestion() {
        if (Boolean.TRUE.equals(suggestionLoading.getValue())) {
            return;
        }
        suggestionLoading.setValue(true);
        repository.fetchSuggestion(new TaskRepository.SuggestionCallback() {
            @Override
            public void onSuccess(@NonNull String text) {
                suggestionLoading.setValue(false);
                suggestion.setValue(new Event<>(text));
            }

            @Override
            public void onError() {
                suggestionLoading.setValue(false);
                message.setValue(new Event<>(R.string.suggestion_error));
            }
        });
    }
}
