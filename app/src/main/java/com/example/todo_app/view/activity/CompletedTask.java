package com.example.todo_app.view.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo_app.R;
import com.example.todo_app.database.TaskListDao;
import com.example.todo_app.database.TaskListEntry;
import com.example.todo_app.tasks.TaskListAdapter;
import com.example.todo_app.tasks.task_list_viewmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompletedTask extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private RecyclerView mRecyclerView;
    private TaskListAdapter mAdapter;
    private task_list_viewmodel taskListViewmodel;
    private ImageButton delete;
    private Spinner spinner;
    private String[] categories = {
            "All",
            "Personal",
            "Shopping",
            "Wishlist",
            "Work",
            "Other"
    };
    ArrayList<String> spinnerList = new ArrayList<>(Arrays.asList(categories));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_task);

        delete = findViewById(R.id.deleteImage);
        spinner = findViewById(R.id.mainSpinner);

        // Set the RecyclerView to its corresponding view
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new TaskListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        setUpViewModel();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(CompletedTask.this, android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(0);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialogueBox();
            }
        });

          /*
        Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
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
                TaskListEntry task = mAdapter.getTaskList().get(position);
                taskListViewmodel.deleteTaskList(task);
            }
        }).attachToRecyclerView(mRecyclerView);
    }
    //Menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.todo_menu, menu);
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
        return true;
    }

    private void setUpViewModel() {
        taskListViewmodel = ViewModelProviders.of(CompletedTask.this).get(task_list_viewmodel.class);
        taskListViewmodel.getTaskList().observe((LifecycleOwner) CompletedTask.this, new Observer<List<TaskListEntry>>() {
            @Override
            public void onChanged(List<TaskListEntry> taskListEntries) {
                mAdapter.setTasks(taskListEntries);
            }
        });
    }

    //Delete dialogue box
    public void deleteDialogueBox() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure, You wanted to make decision");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        taskListViewmodel.deleteAllTaskList();
                        Toast.makeText(CompletedTask.this, "All Task has been deleted", Toast.LENGTH_LONG).show();
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

    //Spinner filter method
    private void loadFiltered(String value) {
        taskListViewmodel = ViewModelProviders.of(CompletedTask.this).get(task_list_viewmodel.class);
        if (value == "All") {
            setUpViewModel();
        } else {
            taskListViewmodel.getCategory(value).observe((LifecycleOwner) CompletedTask.this, new Observer<List<TaskListEntry>>() {
                @Override
                public void onChanged(List<TaskListEntry> taskListEntries) {
                    mAdapter.setTasks(taskListEntries);
                }
            });
        }
    }

    //Priority method
    private void getPriorityList(int value) {
        taskListViewmodel = ViewModelProviders.of(CompletedTask.this).get(task_list_viewmodel.class);
        taskListViewmodel.getPriorityList(value).observe((LifecycleOwner) CompletedTask.this, new Observer<List<TaskListEntry>>() {
            @Override
            public void onChanged(List<TaskListEntry> taskListEntries) {
                mAdapter.setTasks(taskListEntries);
            }
        });
    }

    //Spinner method to select item
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            setUpViewModel();
        } else {
            String value = parent.getItemAtPosition(position).toString();
            Toast.makeText(CompletedTask.this, "item selected" + value, Toast.LENGTH_LONG).show();
            loadFiltered(value);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

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
            case R.id.logOut:
                Intent intent1 = new Intent(CompletedTask.this, LoginActivity.class);
                startActivity(intent1);
            case R.id.exit:
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}