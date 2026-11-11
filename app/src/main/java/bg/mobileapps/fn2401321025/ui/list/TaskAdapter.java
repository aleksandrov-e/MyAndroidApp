package bg.mobileapps.fn2401321025.ui.list;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
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
import bg.mobileapps.fn2401321025.domain.TaskCategory;
import bg.mobileapps.fn2401321025.ui.CategoryUi;

public class TaskAdapter extends ListAdapter<Task, TaskAdapter.TaskViewHolder> {

    public interface Listener {
        void onTaskClicked(Task task);

        void onTaskChecked(Task task, boolean done);
    }

    /** Прозрачност (от 255) на фона на етикета с категорията. */
    private static final int TAG_BACKGROUND_ALPHA = 36;

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
            binding.getRoot().setAlpha(task.done ? 0.6f : 1f);

            binding.textDescription.setText(task.description);
            binding.textDescription.setVisibility(
                    task.description.isEmpty() ? View.GONE : View.VISIBLE);

            bindCategory(context, task);
            bindDueDate(context, task);
            ViewCompat.setBackgroundTintList(binding.priorityDot, ColorStateList.valueOf(
                    ContextCompat.getColor(context, priorityColor(task.priority))));
            binding.priorityDot.setContentDescription(
                    context.getString(priorityLabel(task.priority)));

            // Махаме listener-а преди setChecked, за да не се вика при рециклиране.
            binding.checkDone.setOnCheckedChangeListener(null);
            binding.checkDone.setChecked(task.done);
            binding.checkDone.setOnCheckedChangeListener(
                    (button, checked) -> listener.onTaskChecked(task, checked));

            binding.getRoot().setOnClickListener(view -> listener.onTaskClicked(task));
        }

        private void bindCategory(Context context, Task task) {
            TaskCategory category = TaskCategory.fromId(task.category);
            int color = ContextCompat.getColor(context, CategoryUi.colorRes(category));

            binding.textCategory.setText(CategoryUi.labelRes(category));
            binding.textCategory.setTextColor(color);
            binding.iconCategory.setImageResource(CategoryUi.iconRes(category));
            binding.iconCategory.setImageTintList(ColorStateList.valueOf(color));
            ViewCompat.setBackgroundTintList(binding.tagCategory, ColorStateList.valueOf(
                    ColorUtils.setAlphaComponent(color, TAG_BACKGROUND_ALPHA)));
        }

        private void bindDueDate(Context context, Task task) {
            if (task.dueDate == null) {
                binding.iconDue.setVisibility(View.GONE);
                binding.textDue.setVisibility(View.GONE);
                return;
            }
            long today = DueDates.todayUtcMidnight(
                    System.currentTimeMillis(), TimeZone.getDefault());
            boolean overdue = !task.done && DueDates.isOverdue(task.dueDate, today);
            String date = DueDates.format(task.dueDate, Locale.getDefault());
            int color = MaterialColors.getColor(binding.textDue, overdue
                    ? androidx.appcompat.R.attr.colorError
                    : com.google.android.material.R.attr.colorOnSurfaceVariant);

            binding.iconDue.setVisibility(View.VISIBLE);
            binding.iconDue.setImageTintList(ColorStateList.valueOf(color));
            binding.textDue.setVisibility(View.VISIBLE);
            binding.textDue.setText(overdue
                    ? context.getString(R.string.due_overdue, date)
                    : date);
            binding.textDue.setTextColor(color);
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

        private static int priorityLabel(int priority) {
            switch (priority) {
                case Task.PRIORITY_HIGH:
                    return R.string.priority_high;
                case Task.PRIORITY_LOW:
                    return R.string.priority_low;
                default:
                    return R.string.priority_medium;
            }
        }
    }
}
