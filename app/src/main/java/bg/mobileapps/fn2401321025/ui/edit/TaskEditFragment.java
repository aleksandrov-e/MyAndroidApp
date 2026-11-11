package bg.mobileapps.fn2401321025.ui.edit;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import bg.mobileapps.fn2401321025.R;
import bg.mobileapps.fn2401321025.data.local.Task;
import bg.mobileapps.fn2401321025.databinding.FragmentTaskEditBinding;
import bg.mobileapps.fn2401321025.domain.DueDates;
import bg.mobileapps.fn2401321025.domain.TaskCategory;
import bg.mobileapps.fn2401321025.ui.ViewModelFactory;

/** Втори екран: създаване на нова задача или редакция/изтриване на съществуваща. */
public class TaskEditFragment extends Fragment {

    public static final String ARG_TASK_ID = "taskId";
    public static final long NEW_TASK_ID = -1L;

    private static final String TAG_DATE_PICKER = "dueDatePicker";

    private FragmentTaskEditBinding binding;
    private TaskEditViewModel viewModel;
    private long taskId = NEW_TASK_ID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            taskId = getArguments().getLong(ARG_TASK_ID, NEW_TASK_ID);
        }
        viewModel = new ViewModelProvider(this, ViewModelFactory.from(requireContext()))
                .get(TaskEditViewModel.class);
        viewModel.start(taskId);

        ActionBar actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(isEditing() ? R.string.title_edit_task : R.string.title_new_task);
        }

        observeViewModel();
        setUpInputs();
        reattachDatePickerListener();

        if (isEditing()) {
            requireActivity().addMenuProvider(
                    new EditMenuProvider(), getViewLifecycleOwner(), Lifecycle.State.RESUMED);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private boolean isEditing() {
        return taskId > 0;
    }

    private void observeViewModel() {
        viewModel.getLoadedTask().observe(getViewLifecycleOwner(), event -> {
            Task task = event.getIfNotHandled();
            if (task != null) {
                binding.inputTitle.setText(task.title);
                binding.inputDescription.setText(task.description);
                binding.togglePriority.check(buttonFor(task.priority));
                binding.chipGroupCategory.check(chipFor(TaskCategory.fromId(task.category)));
            }
        });

        viewModel.getDueDate().observe(getViewLifecycleOwner(), dueDate -> {
            binding.buttonDueDate.setText(dueDate == null
                    ? getString(R.string.pick_due_date)
                    : DueDates.format(dueDate, Locale.getDefault()));
            binding.buttonClearDueDate.setVisibility(dueDate == null ? View.GONE : View.VISIBLE);
        });

        viewModel.getTitleError().observe(getViewLifecycleOwner(), errorRes ->
                binding.layoutTitle.setError(errorRes == null ? null : getString(errorRes)));

        viewModel.getSuggestionLoading().observe(getViewLifecycleOwner(), loading -> {
            binding.progressSuggestion.setVisibility(loading ? View.VISIBLE : View.INVISIBLE);
            binding.buttonSuggest.setEnabled(!loading);
        });

        viewModel.getSuggestion().observe(getViewLifecycleOwner(), event -> {
            String text = event.getIfNotHandled();
            if (text != null) {
                binding.inputTitle.setText(text);
                binding.inputTitle.setSelection(binding.inputTitle.length());
            }
        });

        viewModel.getMessage().observe(getViewLifecycleOwner(), event -> {
            Integer messageRes = event.getIfNotHandled();
            if (messageRes != null) {
                Snackbar.make(binding.getRoot(), messageRes, Snackbar.LENGTH_LONG).show();
            }
        });

        viewModel.getFinished().observe(getViewLifecycleOwner(), event -> {
            if (event.getIfNotHandled() != null) {
                NavHostFragment.findNavController(this).navigateUp();
            }
        });
    }

    private void setUpInputs() {
        binding.inputTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                viewModel.onTitleChanged();
            }

            @Override
            public void afterTextChanged(Editable text) {
            }
        });

        binding.buttonDueDate.setOnClickListener(v -> showDatePicker());
        binding.buttonClearDueDate.setOnClickListener(v -> viewModel.setDueDate(null));
        binding.buttonSuggest.setOnClickListener(v -> viewModel.requestSuggestion());
        binding.buttonSave.setOnClickListener(v -> viewModel.save(
                String.valueOf(binding.inputTitle.getText()),
                String.valueOf(binding.inputDescription.getText()),
                selectedPriority(),
                selectedCategory()));
    }

    private TaskCategory selectedCategory() {
        int checked = binding.chipGroupCategory.getCheckedChipId();
        if (checked == R.id.chip_category_study) {
            return TaskCategory.STUDY;
        }
        if (checked == R.id.chip_category_work) {
            return TaskCategory.WORK;
        }
        if (checked == R.id.chip_category_shopping) {
            return TaskCategory.SHOPPING;
        }
        return TaskCategory.PERSONAL;
    }

    private static int chipFor(TaskCategory category) {
        switch (category) {
            case STUDY:
                return R.id.chip_category_study;
            case WORK:
                return R.id.chip_category_work;
            case SHOPPING:
                return R.id.chip_category_shopping;
            default:
                return R.id.chip_category_personal;
        }
    }

    private void showDatePicker() {
        Long current = viewModel.getDueDate().getValue();
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.pick_due_date)
                .setSelection(current != null ? current : MaterialDatePicker.todayInUtcMilliseconds())
                .build();
        TaskEditViewModel model = viewModel;
        picker.addOnPositiveButtonClickListener(model::setDueDate);
        picker.show(getChildFragmentManager(), TAG_DATE_PICKER);
    }

    /** След завъртане диалогът се възстановява без listener-и – закачаме го отново. */
    @SuppressWarnings("unchecked")
    private void reattachDatePickerListener() {
        Fragment existing = getChildFragmentManager().findFragmentByTag(TAG_DATE_PICKER);
        if (existing instanceof MaterialDatePicker) {
            TaskEditViewModel model = viewModel;
            ((MaterialDatePicker<Long>) existing).addOnPositiveButtonClickListener(model::setDueDate);
        }
    }

    private int selectedPriority() {
        int checked = binding.togglePriority.getCheckedButtonId();
        if (checked == R.id.button_priority_high) {
            return Task.PRIORITY_HIGH;
        }
        if (checked == R.id.button_priority_low) {
            return Task.PRIORITY_LOW;
        }
        return Task.PRIORITY_MEDIUM;
    }

    private static int buttonFor(int priority) {
        switch (priority) {
            case Task.PRIORITY_HIGH:
                return R.id.button_priority_high;
            case Task.PRIORITY_LOW:
                return R.id.button_priority_low;
            default:
                return R.id.button_priority_medium;
        }
    }

    private void confirmDelete() {
        TaskEditViewModel model = viewModel;
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.delete_task)
                .setMessage(R.string.delete_task_message)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> model.delete())
                .show();
    }

    private class EditMenuProvider implements MenuProvider {

        @Override
        public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
            inflater.inflate(R.menu.menu_task_edit, menu);
        }

        @Override
        public boolean onMenuItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == R.id.action_delete) {
                confirmDelete();
                return true;
            }
            return false;
        }
    }
}
