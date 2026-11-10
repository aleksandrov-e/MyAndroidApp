package bg.mobileapps.fn2401321025.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    /** Незавършените първи, после по приоритет, най-близък срок и дата на създаване. */
    @Query("SELECT * FROM tasks "
            + "WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' "
            + "ORDER BY done ASC, priority DESC, "
            + "CASE WHEN dueDate IS NULL THEN 1 ELSE 0 END, dueDate ASC, createdAt DESC")
    LiveData<List<Task>> observeTasks(String query);

    @Query("SELECT * FROM tasks WHERE id = :id")
    Task getById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Task task);

    @Update
    int update(Task task);

    @Delete
    int delete(Task task);

    @Query("UPDATE tasks SET done = :done WHERE id = :id")
    void setDone(long id, boolean done);

    @Query("DELETE FROM tasks WHERE done = 1")
    int deleteCompleted();
}
