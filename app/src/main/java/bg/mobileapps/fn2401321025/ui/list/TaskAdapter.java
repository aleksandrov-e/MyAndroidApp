package bg.mobileapps.fn2401321025.ui.list;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;

import java.util.Locale;
import java.util.TimeZone;

import bg.mobileapps.fn2401321025.R;
import bg.mobileapps.fn2401321025.data.local.Task;
import bg.mobileapps.fn2401321025.databinding.ItemTaskBinding;
import bg.mobileapps.fn2401321025.domain.DueDates;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {

    public interface Listener {
        void onTaskClicked(Task task);

        void onTaskChecked(Task task, boolean done);
    }

    private static final DiffUtil.ItemCallback<Task> DIFF = new DiffUtil.ItemCallback<Task>() {
        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.equals(newItem);
        }
    };

    private final Listener listener;

    public TaskAdapter(Listener listener) {
        super(DIFF);
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskBinding binding = ItemTaskBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new TaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        private final ItemTaskBinding binding;

        TaskViewHolder(ItemTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Task task, Listener listener) {
            Context context = binding.getRoot().getContext();

            binding.textTitle.setText(task.title);
            int flags = binding.textTitle.getPaintFlags();
            binding.textTitle.setPaintFlags(task.done
                    ? flags | Paint.STRIKE_THRU_TEXT_FLAG
                    : flags & ~Paint.STRIKE_THRU_TEXT_FLAG);
            binding.textTitle.setAlpha(task.done ? 0.5f : 1f);

            binding.textDescription.setText(task.description);
            binding.textDescription.setVisibility(
                    task.description.isEmpty() ? View.GONE : View.VISIBLE);

            bindDueDate(context, task);
            binding.priorityStripe.setBackgroundColor(
                    ContextCompat.getColor(context, priorityColor(task.priority)));

            // Махаме listener-а преди setChecked, за да не се вика при рециклиране.
            binding.checkDone.setOnCheckedChangeListener(null);
            binding.checkDone.setChecked(task.done);
            binding.checkDone.setOnCheckedChangeListener(
                    (button, checked) -> listener.onTaskChecked(task, checked));

            binding.getRoot().setOnClickListener(view -> listener.onTaskClicked(task));
        }

        private void bindDueDate(Context context, Task task) {
            if (task.dueDate == null) {
                binding.textDue.setVisibility(View.GONE);
                return;
            }
            long today = DueDates.todayUtcMidnight(
                    System.currentTimeMillis(), TimeZone.getDefault());
            boolean overdue = !task.done && DueDates.isOverdue(task.dueDate, today);
            String date = DueDates.format(task.dueDate, Locale.getDefault());

            binding.textDue.setVisibility(View.VISIBLE);
            binding.textDue.setText(context.getString(
                    overdue ? R.string.due_overdue : R.string.due_on, date));
            binding.textDue.setTextColor(MaterialColors.getColor(binding.textDue, overdue
                    ? androidx.appcompat.R.attr.colorError
                    : com.google.android.material.R.attr.colorOnSurfaceVariant));
        }

        private static int priorityColor(int priority) {
            switch (priority) {
                case Task.PRIORITY_HIGH:
                    return R.color.priority_high;
                case Task.PRIORITY_LOW:
                    return R.color.priority_low;
                default:
                    return R.color.priority_medium;
            }
        }
    }
}
