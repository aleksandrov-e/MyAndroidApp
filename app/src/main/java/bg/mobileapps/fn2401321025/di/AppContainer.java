package bg.mobileapps.fn2401321025.di;

import android.content.Context;

import androidx.core.content.ContextCompat;
import androidx.room.Room;

import java.util.concurrent.Executors;

import bg.mobileapps.fn2401321025.data.DefaultTaskRepository;
import bg.mobileapps.fn2401321025.data.TaskRepository;
import bg.mobileapps.fn2401321025.data.local.AppDatabase;
import bg.mobileapps.fn2401321025.data.remote.TodoApi;
import bg.mobileapps.fn2401321025.ui.ViewModelFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Ръчен dependency injection: тук се създават базата, REST клиентът и Repository-то
 * и се подават надолу към ViewModel-ите през {@link ViewModelFactory}.
 */
public class AppContainer {

    private static final String BASE_URL = "https://dummyjson.com/";
    private static final String DATABASE_NAME = "tasks.db";

    private final TaskRepository taskRepository;
    private final ViewModelFactory viewModelFactory;

    public AppContainer(Context context) {
        Context appContext = context.getApplicationContext();

        AppDatabase database =
                Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                        .addMigrations(AppDatabase.MIGRATION_1_2)
                        .build();

        TodoApi todoApi = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TodoApi.class);

        taskRepository = new DefaultTaskRepository(
                database.taskDao(),
                todoApi,
                Executors.newSingleThreadExecutor(),
                ContextCompat.getMainExecutor(appContext));
        viewModelFactory = new ViewModelFactory(taskRepository);
    }

    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    public ViewModelFactory getViewModelFactory() {
        return viewModelFactory;
    }
}
