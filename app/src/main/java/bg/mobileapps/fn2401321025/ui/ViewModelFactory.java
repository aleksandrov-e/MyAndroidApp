package bg.mobileapps.fn2401321025.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import bg.mobileapps.fn2401321025.TasksApp;
import bg.mobileapps.fn2401321025.data.TaskRepository;
import bg.mobileapps.fn2401321025.ui.edit.TaskEditViewModel;
import bg.mobileapps.fn2401321025.ui.list.TaskListViewModel;

/** Създава ViewModel-ите и им подава Repository-то през конструктора. */
public class ViewModelFactory implements ViewModelProvider.Factory {

    private final TaskRepository repository;

    public ViewModelFactory(TaskRepository repository) {
        this.repository = repository;
    }

    public static ViewModelFactory from(Context context) {
        TasksApp app = (TasksApp) context.getApplicationContext();
        return app.getContainer().getViewModelFactory();
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TaskListViewModel.class)) {
            return (T) new TaskListViewModel(repository);
        }
        if (modelClass.isAssignableFrom(TaskEditViewModel.class)) {
            return (T) new TaskEditViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel: " + modelClass.getName());
    }
}
