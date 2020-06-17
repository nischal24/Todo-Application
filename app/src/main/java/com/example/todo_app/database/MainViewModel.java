package com.example.todo_app.tasks;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.todo_app.database.AppDatabase;
import com.example.todo_app.database.Repository;
import com.example.todo_app.database.TaskEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<TaskEntry>> task;
    Repository repository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppDatabase.getInstance(application);
        repository = new Repository(database);
        task = repository.getTasks();
    }

    public void insertTask(final TaskEntry task) {
        repository.insertTask(task);
    }

    public LiveData<List<TaskEntry>> getTask() {
        return task;
    }

    public LiveData<List<TaskEntry>> getByCategory(String category) {
        return repository.getCategory(category);
    }

    public LiveData<List<TaskEntry>> getByDate(String date) {
        return repository.getTasksByDate(date);
    }

    public LiveData<List<TaskEntry>> getTasksByCategoryByTodayDate(String category, String date) {
        return repository.getTasksByCategoryByTodayDate(category, date);
    }

    public LiveData<List<TaskEntry>> getTasksByPriorityByTodayDate(int p, String date) {
        return repository.getTasksByPriorityByTodayDate(p, date);
    }

    public LiveData<List<TaskEntry>> getTasksByPriorityByUpcomingDate(int p, String date) {
        return repository.getTasksByPriorityByUpcomingDate(p, date);
    }

    public LiveData<List<TaskEntry>> getTasksByDateNot(String date) {
        return repository.getTasksByDateNot(date);
    }

    public LiveData<List<TaskEntry>> getTasksByCategoryByUpcomingDate(String category, String date) {
        return repository.getTasksByCategoryByUpcomingDate(category, date);
    }

    public void deleteTask(final TaskEntry task) {
        repository.deleteTask(task);
    }

    public LiveData<List<TaskEntry>> getPriority(int p) {
        return repository.getPriority(p);
    }

    public void deleteAllTasks() {
        repository.deleteAllTask();
    }

}
