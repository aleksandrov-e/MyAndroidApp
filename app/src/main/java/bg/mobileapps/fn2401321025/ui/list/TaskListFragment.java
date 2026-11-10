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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import bg.mobileapps.fn2401321025.R;
import bg.mobileapps.fn2401321025.data.local.Task;
import bg.mobileapps.fn2401321025.databinding.FragmentTaskListBinding;
import bg.mobileapps.fn2401321025.domain.TaskFilter;
import bg.mobileapps.fn2401321025.ui.ViewModelFactory;
import bg.mobileapps.fn2401321025.ui.edit.TaskEditFragment;

/** Първи екран: списък със задачите, търсене, филтър, изтриване с плъзгане. */
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
        attachSwipeToDelete(adapter);

        viewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> {
            adapter.submitList(tasks);
            binding.textEmpty.setVisibility(tasks.isEmpty() ? View.VISIBLE : View.GONE);
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

    private void openEditor(long taskId) {
        Bundle args = new Bundle();
        args.putLong(TaskEditFragment.ARG_TASK_ID, taskId);
        NavHostFragment.findNavController(this).navigate(R.id.action_list_to_edit, args);
    }

    private void attachSwipeToDelete(TaskAdapter adapter) {
        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                    @NonNull RecyclerView.ViewHolder viewHolder,
                    @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }
                deleteWithUndo(adapter.getCurrentList().get(position));
            }
        };
        new ItemTouchHelper(callback).attachToRecyclerView(binding.listTasks);
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
