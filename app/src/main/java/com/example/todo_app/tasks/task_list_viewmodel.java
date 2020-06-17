package com.example.todo_app.tasks;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.todo_app.database.AppDatabase;
import com.example.todo_app.database.TaskListEntry;
import com.example.todo_app.database.TaskListRepository;

import java.util.List;

public class task_list_viewmodel extends AndroidViewModel {

    private LiveData<List<TaskListEntry>> taskentry;
    TaskListRepository taskListRepository;

    public task_list_viewmodel(@NonNull Application application) {
        super(application);
        AppDatabase appDatabase = AppDatabase.getInstance(application);
        taskListRepository = new TaskListRepository(appDatabase);
        taskentry = taskListRepository.getTaskList();

    }

    public LiveData<List<TaskListEntry>> getTaskList() {
        return taskListRepository.getTaskList();
    }

    public LiveData<List<TaskListEntry>> getCategory(String category) {
        return taskListRepository.getCategory(category);
    }

    public LiveData<List<TaskListEntry>> getPriorityList(int p) {
        return taskListRepository.getPriorityList(p);
    }

    public void insertTaskList(final TaskListEntry taskList) {
        taskListRepository.insertTaskList(taskList);
    }

    public void deleteTaskList(final TaskListEntry taskList) {
        taskListRepository.deleteTaskList(taskList);
    }

    public void deleteAllTaskList() {
        taskListRepository.deleteAllTaskList();
    }
}
