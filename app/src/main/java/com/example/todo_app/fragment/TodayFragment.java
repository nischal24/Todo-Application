package com.example.todo_app.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo_app.R;
import com.example.todo_app.addEditTask.add_edit_task;
import com.example.todo_app.database.TaskEntry;
import com.example.todo_app.database.TaskListEntry;
import com.example.todo_app.database.task_list_view_model_factory;
import com.example.todo_app.tasks.MainViewModel;
import com.example.todo_app.tasks.TaskAdapter;
import com.example.todo_app.tasks.task_list_viewmodel;
import com.example.todo_app.view.activity.CompletedTask;
import com.example.todo_app.view.activity.LoginActivity;
import com.example.todo_app.view.activity.MainActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class TodayFragment extends Fragment implements TaskAdapter.ItemClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
    //    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private TaskAdapter mAdapter;
    MainViewModel viewModel;
    task_list_viewmodel taskListViewmodel;
    private ImageButton delete;
    private Button complete;
    private Spinner spinner;

    private String[] categories = {
            "All",
            "Personal",
            "Shopping",
            "Wishlist",
            "Work",
            "Other"
    };
    Context context;
    ArrayList<String> spinnerList = new ArrayList<>(Arrays.asList(categories));

    public TodayFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_today, null);
        setup(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    //Menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.todo_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void setup(View view) {

        delete = view.findViewById(R.id.deleteImage);
        spinner = view.findViewById(R.id.mainSpinner);
        complete = view.findViewById(R.id.completeButton);

        task_list_view_model_factory factory = new task_list_view_model_factory(getActivity().getApplication());
        taskListViewmodel = ViewModelProviders.of(this, factory).get(task_list_viewmodel.class);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(0);
         /*
         Set the Floating Action Button (FAB) to its corresponding View.
         */
        FloatingActionButton fabButton = view.findViewById(R.id.floating_button);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = view.findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new TaskAdapter(context, this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);
        //Delete method called when user clicks delete button
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialogueBox();
            }
        });
        //Complete task method called when user clicks complete button
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedComplete();
            }
        });


        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item, or to move them into completed task.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                int position = viewHolder.getAdapterPosition();
                final TaskEntry task = mAdapter.getTask().get(position);
                switch (swipeDir) {
                    //delete files when user swipes left
                    case ItemTouchHelper.LEFT:
                        String deletedFile = task.getDescription();
                        viewModel.deleteTask(task);
                        Snackbar.make(mRecyclerView, deletedFile, Snackbar.LENGTH_LONG).setAction("Undo Delete", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewModel.insertTask(task);
                            }
                        }).show();
                        break;
                    // move files to completed when user swipes right
                    case ItemTouchHelper.RIGHT:
                        viewModel.deleteTask(task);
                        String desc = task.getDescription();
                        String cat = task.getCategory();
                        int prior = task.getPriority();
                        String date = task.getUpdatedAt();
                        final TaskListEntry taskListEntry = new TaskListEntry(desc, cat, prior, date);
                        taskListViewmodel.insertTaskList(taskListEntry);
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(context, R.color.materialRed))
                        .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_sweep)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(context, R.color.materialGreen))
                        .addSwipeRightActionIcon(R.drawable.ic_baseline_check_24)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(mRecyclerView);
             /*
        Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
        to launch the AddTaskActivity.
         */

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new intent to start an AddTaskActivity
                Intent addTaskIntent = new Intent(context, add_edit_task.class);
                startActivity(addTaskIntent);
            }
        });
        setUpViewModel();
    }

    @Override
    public void onItemClickListener(int itemId) {
        // Launch AddTaskActivity adding the itemId as an extra in the intent
        Intent intent = new Intent(context, add_edit_task.class);
        intent.putExtra(add_edit_task.EXTRA_TASK_ID, itemId);
        startActivity(intent);

    }

    // Called when user clicks complete button removes the task and send them to completed activity
    private void clickedComplete() {
        TaskListEntry entry = new TaskListEntry();
        int checkValue = mAdapter.getCheckValue();
        List<Integer> selected = mAdapter.getSelectedItems();
        for (int a : selected) {
            TaskEntry task = mAdapter.getTask().get(a);
            if (checkValue == 1) {
                viewModel.deleteTask(task);
            }
            String desc = task.getDescription();
            String cat = task.getCategory();
            int prior = task.getPriority();
            String date = task.getUpdatedAt();

            TaskListEntry taskListEntry = new TaskListEntry(desc, cat, prior, date);
            taskListViewmodel.insertTaskList(taskListEntry);
        }
        selected.clear();
    }

    //Menu Options selected method
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.down:
                return true;
            case R.id.allPriority:
                setUpViewModel();
                return true;
            case R.id.highPriority:
                getPriorityList(1);
                return true;
            case R.id.mediumPriority:
                getPriorityList(2);
                return true;
            case R.id.lowPriority:
                getPriorityList(3);
                return true;
            case R.id.completedTask:
                Intent intent = new Intent(context, CompletedTask.class);
                startActivity(intent);
                return true;
            case R.id.logOut:
                Intent intent1 = new Intent(context, LoginActivity.class);
                startActivity(intent1);
                return true;
            case R.id.exit:
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //method to call specific data from database and send to adapter
    private void setUpViewModel() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = dateFormat.format(date);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getByDate(strDate).observe((LifecycleOwner) context, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(List<TaskEntry> taskEntries) {
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                mAdapter.setTasks(taskEntries);
            }
        });
    }

    //method for delete dialogue box
    public void deleteDialogueBox() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Are you sure, You wanted to make decision");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        viewModel.deleteAllTasks();
                        Toast.makeText(context, "All Task has been deleted", Toast.LENGTH_LONG).show();
                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //method for Spinner item selection
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            setUpViewModel();
        } else {
            String value = parent.getItemAtPosition(position).toString();
            loadFiltered(value);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void loadFiltered(String value) {
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        if (value == "All") {
            setUpViewModel();
        } else {
            Date date = Calendar.getInstance().getTime();
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String strDate = dateFormat.format(date);
            viewModel.getTasksByCategoryByTodayDate(value, strDate).observe(this, new Observer<List<TaskEntry>>() {
                @Override
                public void onChanged(List<TaskEntry> taskEntries) {
                    Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                    mAdapter.setTasks(taskEntries);
                }
            });
        }
    }

    //method displays according to priority selected
    private void getPriorityList(int value) {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = dateFormat.format(date);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getTasksByPriorityByTodayDate(value, strDate).observe(this, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(List<TaskEntry> taskEntries) {
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                mAdapter.setTasks(taskEntries);
            }
        });
    }
}
