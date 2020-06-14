package com.example.todo_app.addEditTask;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.todo_app.database.AppDatabase;
import com.example.todo_app.database.Repository;
import com.example.todo_app.database.TaskEntry;

class add_edit_task_viewmodel extends AndroidViewModel {

    private LiveData<TaskEntry> task;
    Repository repository;

    public add_edit_task_viewmodel(Application application, int taskId) {
        super(application);
        AppDatabase database=AppDatabase.getInstance(application);
        repository=new Repository(database);
        if(taskId != -1)
            task=repository.getTaskById(taskId);
    }

    public LiveData<TaskEntry> getTask() {
        return task;
    }

    public void insertTask(final TaskEntry task)
    {
        repository.insertTask(task);
    }

    public void updateTask(final TaskEntry task)
    {
       repository.updateTask(task);
    }


}
