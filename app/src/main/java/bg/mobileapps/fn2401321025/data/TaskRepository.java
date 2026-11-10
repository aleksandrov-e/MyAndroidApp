package bg.mobileapps.fn2401321025.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.List;

import bg.mobileapps.fn2401321025.data.local.Task;

/**
 * Единствената точка за достъп до данните. ViewModel-ите не знаят дали
 * данните идват от Room или от мрежата.
 */
public interface TaskRepository {

    LiveData<List<Task>> observeTasks(@NonNull String query);

    /** Зарежда задача по id; резултатът (или null) се връща на главната нишка. */
    void loadTask(long id, @NonNull LoadCallback callback);

    /** Създава задачата (id == 0) или я обновява; onSaved се вика на главната нишка. */
    void save(@NonNull Task task, @Nullable Runnable onSaved);

    void delete(@NonNull Task task);

    /** Връща изтрита задача обратно със същото id (за "Отмени"). */
    void restore(@NonNull Task task);

    void setDone(long id, boolean done);

    void deleteCompleted();

    /** Взима текст за нова задача от REST услугата. */
    void fetchSuggestion(@NonNull SuggestionCallback callback);

    interface LoadCallback {
        void onLoaded(@Nullable Task task);
    }

    interface SuggestionCallback {
        void onSuccess(@NonNull String text);

        void onError();
    }
}
