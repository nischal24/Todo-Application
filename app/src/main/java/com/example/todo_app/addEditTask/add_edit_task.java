package com.example.todo_app.addEditTask;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.todo_app.R;
import com.example.todo_app.database.TaskEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class add_edit_task extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    // Extra for the task ID to be received in the intent
    public static final String EXTRA_TASK_ID = "extraTaskId";

    // Extra for the task ID to be received after rotation
    public static final String INSTANCE_TASK_ID = "instanceTaskId";
    // Constants for priority
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_MEDIUM = 2;
    public static final int PRIORITY_LOW = 3;
    // Constant for default task id to be used when not in update mode
    private static final int DEFAULT_TASK_ID = -1;
    // Constant for logging
    private static final String TAG = add_edit_task.class.getSimpleName();
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    // Fields for views
    EditText mEditText;
    RadioGroup mRadioGroup;
    Button mButton;
    ImageButton micButton, calenderButton;
    EditText dateEditText;
    Spinner spinner;
    private String[] categories = {
            "Personal",
            "Shopping",
            "Wishlist",
            "Work",
            "Other"
    };
    public ArrayList<String> spinnerList = new ArrayList<>(Arrays.asList(categories));
    private int mTaskId = DEFAULT_TASK_ID;
    add_edit_task_viewmodel viewModel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_task);

        initViews();

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }

        Intent intent = getIntent();

        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            mButton.setText(R.string.update_button);

            if (mTaskId == DEFAULT_TASK_ID) {
                // populate the UI
                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);
                add_edit_task_viemodel_factory factory = new add_edit_task_viemodel_factory(getApplication(), mTaskId);
                viewModel = ViewModelProviders.of(this, factory).get(add_edit_task_viewmodel.class);

                viewModel.getTask().observe(this, new Observer<TaskEntry>() {
                    @Override
                    public void onChanged(TaskEntry taskEntry) {
                        viewModel.getTask().removeObserver(this);
                        populateUI(taskEntry);
                    }
                });
            }
        } else {
            add_edit_task_viemodel_factory factory = new add_edit_task_viemodel_factory(getApplication(), mTaskId);
            viewModel = ViewModelProviders.of(this, factory).get(add_edit_task_viewmodel.class);
        }
    }

    /**
     * populateUI would be called to populate the UI when in update mode
     *
     * @param task the taskEntry to populate the UI
     */
    private void populateUI(TaskEntry task) {
        if (task == null) {
            return;
        }
        mEditText.setText(task.getDescription());
        setPriorityInViews(task.getPriority());
        spinner.setSelection(spinnerList.indexOf(task.getCategory()));

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        String date = new String();
        date = task.getUpdatedAt();
        dateEditText.setText(date);
    }

    /**
     * initViews is called from onCreate to init the member variable views
     */
    private void initViews() {
        mEditText = findViewById(R.id.editTextTaskDescription);
        dateEditText = findViewById(R.id.editDate);
        mRadioGroup = findViewById(R.id.radioGroup);
        spinner = findViewById(R.id.spinner);
        micButton = findViewById(R.id.micButton);
        calenderButton = findViewById(R.id.calenderButton);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        dateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        calenderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        mButton = findViewById(R.id.saveButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClicked();
            }
        });

        //button click to display speech to text
        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                talk();
            }
        });
    }

    //Speech to Text method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    //get text array from voice intent
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //set to text view
                    mEditText.setText(result.get(0));
                }
                break;
            }
        }
    }

    private void talk() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Something");
        //Start intent
        try {
            //if there is no error show dialog
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            //if there is some error catch error and show
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //Date picker
    public void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }

    /**
     * getPriority is called whenever the selected priority needs to be retrieved
     */
    public int getPriorityFromViews() {
        int priority = 1;
        int checkedId = ((RadioGroup) findViewById(R.id.radioGroup)).getCheckedRadioButtonId();
        switch (checkedId) {
            case R.id.radButton1:
                priority = PRIORITY_HIGH;
                break;
            case R.id.radButton2:
                priority = PRIORITY_MEDIUM;
                break;
            case R.id.radButton3:
                priority = PRIORITY_LOW;
        }
        return priority;
    }

    /**
     * setPriority is called when we receive a task from MainActivity
     *
     * @param priority the priority value
     */
    public void setPriorityInViews(int priority) {
        switch (priority) {
            case PRIORITY_HIGH:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton1);
                break;
            case PRIORITY_MEDIUM:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton2);
                break;
            case PRIORITY_LOW:
                ((RadioGroup) findViewById(R.id.radioGroup)).check(R.id.radButton3);
        }
    }

    /**
     * onSaveButtonClicked is called when the "save" button is clicked.
     * It retrieves user input and inserts that new task data into the underlying database.
     */
    public void onSaveButtonClicked() {
        // Not yet implemented
        String description = mEditText.getText().toString();
        String category = spinner.getSelectedItem().toString();
        String sDate = dateEditText.getText().toString();
        int priority = getPriorityFromViews();

        if (description.trim().isEmpty()) {
            Toast.makeText(this, " Description is Missing", Toast.LENGTH_SHORT).show();
        } else if (sDate.isEmpty()) {
            Toast.makeText(this, " Date is Missing", Toast.LENGTH_SHORT).show();
        } else {
            TaskEntry todo = new TaskEntry(description, priority, sDate, category);
            if (mTaskId == DEFAULT_TASK_ID)
                viewModel.insertTask(todo);
            else {
                todo.setId(mTaskId);
                viewModel.updateTask(todo);
            }
            finish();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String currentDate = dayOfMonth + "-" + "0" + (month + 1) + "-" + year;
        dateEditText.setText(currentDate);
    }
}
