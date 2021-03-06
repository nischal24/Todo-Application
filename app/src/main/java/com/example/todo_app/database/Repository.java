package com.example.todo_app.database;

import androidx.lifecycle.LiveData;

import java.util.List;

public class Repository {
    TaskDao dao;

    public Repository(AppDatabase appDatabase) {
        dao = appDatabase.taskDao();
    }

    public LiveData<List<TaskEntry>> getTasks() {
        return dao.loadAllTasks();
    }

    public LiveData<List<TaskEntry>> getCategory(String category) {
        return dao.fetchTodoListByCategory(category);
    }

    public LiveData<TaskEntry> getTaskById(int taskId) {
        return dao.loadTaskById(taskId);
    }

    public LiveData<List<TaskEntry>> getPriority(int p) {
        return dao.getHighPriority(p);
    }

    public LiveData<List<TaskEntry>> getTasksByDate(String date) {
        return dao.getTasksByDate(date);
    }

    public LiveData<List<TaskEntry>> getTasksByDateNot(String date) {
        return dao.getTasksByDateNot(date);
    }

    public LiveData<List<TaskEntry>> getTasksByCategoryByUpcomingDate(String category, String date) {
        return dao.getTasksByCategoryByUpcomingDate(category, date);
    }

    public LiveData<List<TaskEntry>> getTasksByPriorityByUpcomingDate(int p, String date) {
        return dao.getTasksByPriorityByUpcomingDate(p, date);
    }

    public LiveData<List<TaskEntry>> getTasksByCategoryByTodayDate(String category, String date) {
        return dao.getTasksByCategoryByTodayDate(category, date);
    }

    public LiveData<List<TaskEntry>> getTasksByPriorityByTodayDate(int p, String date) {
        return dao.getTasksByPriorityByTodayDate(p, date);
    }

    public void updateTask(final TaskEntry task) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.update(task);
            }
        });
    }

    public void deleteTask(final TaskEntry task) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.deleteTask(task);
            }
        });
    }

    public void deleteAllTask() {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.deleteAllTask();
            }
        });
    }

    public void insertTask(final TaskEntry task) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dao.insertTask(task);
            }
        });
    }
}
