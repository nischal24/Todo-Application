package com.example.todo_app.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo_app.view.activity.MainActivity;
import com.example.todo_app.R;
import com.example.todo_app.addEditTask.add_edit_task;
import com.example.todo_app.database.TaskEntry;
import com.example.todo_app.tasks.MainViewModel;
import com.example.todo_app.tasks.TaskAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TodayFragment extends Fragment implements TaskAdapter.ItemClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    // Member variables for the adapter and RecyclerView
    private RecyclerView mRecyclerView;
//    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
    private TaskAdapter mAdapter;
    MainViewModel viewModel;
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
        View view=inflater.inflate(R.layout.fragment_today,null);
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
        inflater.inflate(R.menu.todo_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void setup(View view) {

        delete=view.findViewById(R.id.deleteImage);
        spinner=view.findViewById(R.id.mainSpinner);
        complete=view.findViewById(R.id.completeButton);


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
        mAdapter = new TaskAdapter(context,this);
        mRecyclerView.setAdapter(mAdapter);

        DividerItemDecoration decoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(decoration);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialogueBox();
            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedComplete();
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
                int position=viewHolder.getAdapterPosition();
                TaskEntry task=mAdapter.getTask().get(position);
                viewModel.deleteTask(task);
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
        Intent intent=new Intent(context, add_edit_task.class);
        intent.putExtra(add_edit_task.EXTRA_TASK_ID,itemId);
        startActivity(intent);

    }
    private void clickedComplete() {
        List<Integer> selected=mAdapter.getSelectedItems();
        for(int a:selected)
        {
            TaskEntry task=mAdapter.getTask().get(a);
            viewModel.deleteTask(task);

            String desc=task.getDescription();
            String cat=task.getCategory();
            int prior=task.getPriority();
//            Date date=task.getUpdatedAt();

//            TaskListEntry taskListEntry=new TaskListEntry(desc,cat,prior,date);
            //          task_list_viewmodel.insertTaskList(taskListEntry);

            Toast.makeText(context,"pos"+desc,Toast.LENGTH_SHORT).show();


        }
        selected.clear();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.down:
                Toast.makeText(context,"down",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.allPriority:
                Toast.makeText(context,"all priority",Toast.LENGTH_SHORT).show();
                setUpViewModel();
                return true;
            case R.id.highPriority:
                Toast.makeText(context,"high priority",Toast.LENGTH_SHORT).show();
                getPriorityList(1);
                return true;
            case R.id.mediumPriority:
                Toast.makeText(context,"medium priority",Toast.LENGTH_SHORT).show();
                getPriorityList(2);
                return true;
            case R.id.lowPriority:
                Toast.makeText(context,"low priority",Toast.LENGTH_SHORT).show();
                getPriorityList(3);
                return true;
            case R.id.completedTask:
                Toast.makeText(context,"priority",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.exit:
                Toast.makeText(context,"exit",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //
    private void setUpViewModel() {

        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String strDate = dateFormat.format(date);

        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getByDate(strDate).observe((LifecycleOwner) context, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(List<TaskEntry> taskEntries) {
                Log.d(TAG,"Updating list of tasks from LiveData in ViewModel");
                mAdapter.setTasks(taskEntries);
            }
        });
    }
    public void deleteDialogueBox()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage("Are you sure, You wanted to make decision");
        alertDialogBuilder.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        viewModel.deleteAllTasks();
                        Toast.makeText(context,"All Task has been deleted",Toast.LENGTH_LONG).show();
                    }
                });

        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    //Spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            setUpViewModel();
        } else {
            String value = parent.getItemAtPosition(position).toString();
            Toast.makeText(context,"item selected"+value,Toast.LENGTH_LONG).show();
            loadFiltered(value);
        }

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void loadFiltered(String value)
    {
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        if(value=="All")
        {
            setUpViewModel();
        }
        else
        {
            viewModel.getByCategory(value).observe(this, new Observer<List<TaskEntry>>() {
                @Override
                public void onChanged(List<TaskEntry> taskEntries) {
                    Log.d(TAG,"Updating list of tasks from LiveData in ViewModel");
                    mAdapter.setTasks(taskEntries);
                }
            });
        }
    }
    private void getPriorityList(int value)
    {
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getPriority(value).observe(this, new Observer<List<TaskEntry>>() {
            @Override
            public void onChanged(List<TaskEntry> taskEntries) {
                Log.d(TAG,"Updating list of tasks from LiveData in ViewModel");
                mAdapter.setTasks(taskEntries);
            }
        });
    }
}
