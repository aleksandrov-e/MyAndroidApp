package bg.mobileapps.fn2401321025.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import bg.mobileapps.fn2401321025.R;
import bg.mobileapps.fn2401321025.data.local.Task;
import bg.mobileapps.fn2401321025.databinding.FragmentTaskListBinding;
import bg.mobileapps.fn2401321025.domain.Greeting;
import bg.mobileapps.fn2401321025.domain.TaskFilter;
import bg.mobileapps.fn2401321025.domain.TaskStats;
import bg.mobileapps.fn2401321025.ui.ViewModelFactory;
import bg.mobileapps.fn2401321025.ui.edit.TaskEditFragment;

/** Първи екран: поздрав и напредък, списък със задачите, търсене, филтър, изтриване. */
public class TaskListFragment extends Fragment implements TaskAdapter.Listener {

    private FragmentTaskListBinding binding;
    private TaskListViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this, ViewModelFactory.from(requireContext()))
                .get(TaskListViewModel.class);

        TaskAdapter adapter = new TaskAdapter(this);
        binding.listTasks.setAdapter(adapter);
        new ItemTouchHelper(new SwipeToDeleteCallback(requireContext(),
                position -> deleteWithUndo(adapter.getCurrentList().get(position))))
                .attachToRecyclerView(binding.listTasks);

        bindHeader();
        viewModel.getStats().observe(getViewLifecycleOwner(), this::showStats);

        viewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> {
            adapter.submitList(tasks);
            showEmptyState(tasks.isEmpty());
        });

        binding.fabAdd.setOnClickListener(v -> openEditor(TaskEditFragment.NEW_TASK_ID));

        binding.chipGroupFilter.check(chipFor(viewModel.getFilter().getValue()));
        binding.chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                viewModel.setFilter(filterFor(checkedIds.get(0)));
            }
        });

        requireActivity().addMenuProvider(
                new ListMenuProvider(), getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.listTasks.setAdapter(null);
        binding = null;
    }

    @Override
    public void onTaskClicked(Task task) {
        openEditor(task.id);
    }

    @Override
    public void onTaskChecked(Task task, boolean done) {
        viewModel.setDone(task, done);
    }

    private void bindHeader() {
        Calendar now = Calendar.getInstance();
        binding.textGreeting.setText(greetingText(
                Greeting.forHour(now.get(Calendar.HOUR_OF_DAY))));

        String date = new SimpleDateFormat("EEEE, d MMMM", Locale.getDefault())
                .format(new Date(now.getTimeInMillis()));
        binding.textDate.setText(capitalize(date));
    }

    private void showStats(TaskStats stats) {
        if (stats.total == 0) {
            binding.textProgress.setText(R.string.progress_no_tasks);
        } else if (stats.allDone()) {
            binding.textProgress.setText(R.string.progress_all_done);
        } else {
            binding.textProgress.setText(
                    getString(R.string.progress_done_of, stats.done, stats.total));
        }
        binding.textPercent.setText(getString(R.string.progress_percent, stats.percent()));
        binding.progressTasks.setProgressCompat(stats.percent(), true);
    }

    private void showEmptyState(boolean empty) {
        binding.emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        if (!empty) {
            return;
        }
        TaskStats stats = viewModel.getStats().getValue();
        boolean noTasksAtAll = stats == null || stats.total == 0;
        binding.textEmptyTitle.setText(noTasksAtAll
                ? R.string.empty_title : R.string.empty_filtered_title);
        binding.textEmptyMessage.setText(noTasksAtAll
                ? R.string.empty_message : R.string.empty_filtered_message);
    }

    private int greetingText(Greeting greeting) {
        switch (greeting) {
            case MORNING:
                return R.string.greeting_morning;
            case DAY:
                return R.string.greeting_day;
            default:
                return R.string.greeting_evening;
        }
    }

    private static String capitalize(String text) {
        if (text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase(Locale.getDefault()) + text.substring(1);
    }

    private void openEditor(long taskId) {
        Bundle args = new Bundle();
        args.putLong(TaskEditFragment.ARG_TASK_ID, taskId);
        NavHostFragment.findNavController(this).navigate(R.id.action_list_to_edit, args);
    }

    private void deleteWithUndo(Task task) {
        // Локална променлива, за да не държи Snackbar-ът препратка към фрагмента.
        TaskListViewModel model = viewModel;
        model.delete(task);
        Snackbar.make(binding.getRoot(), R.string.task_deleted, Snackbar.LENGTH_LONG)
                .setAnchorView(binding.fabAdd)
                .setAction(R.string.undo, v -> model.restore(task))
                .show();
    }

    private void confirmDeleteCompleted() {
        TaskListViewModel model = viewModel;
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_completed)
                .setMessage(R.string.delete_completed_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> model.deleteCompleted())
                .show();
    }

    private static int chipFor(@Nullable TaskFilter filter) {
        if (filter == TaskFilter.ACTIVE) {
            return R.id.chip_active;
        }
        if (filter == TaskFilter.DONE) {
            return R.id.chip_done;
        }
        return R.id.chip_all;
    }

    private static TaskFilter filterFor(int chipId) {
        if (chipId == R.id.chip_active) {
            return TaskFilter.ACTIVE;
        }
        if (chipId == R.id.chip_done) {
            return TaskFilter.DONE;
        }
        return TaskFilter.ALL;
    }

    private class ListMenuProvider implements MenuProvider {

        @Override
        public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
            inflater.inflate(R.menu.menu_task_list, menu);

            MenuItem searchItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setQueryHint(getString(R.string.search_hint));

            String current = viewModel.getQuery();
            if (!current.isEmpty()) {
                searchItem.expandActionView();
                searchView.setQuery(current, false);
                searchView.clearFocus();
            }

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String text) {
                    searchView.clearFocus();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String text) {
                    viewModel.setQuery(text);
                    return true;
                }
            });
        }

        @Override
        public boolean onMenuItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.action_delete_completed) {
                confirmDeleteCompleted();
                return true;
            }
            return false;
        }
    }
}
