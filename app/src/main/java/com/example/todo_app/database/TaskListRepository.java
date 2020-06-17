package com.example.todo_app.database;

import androidx.lifecycle.LiveData;

import java.util.List;

public class TaskListRepository {
    TaskListDao taskListDao;

    public TaskListRepository(AppDatabase appDatabase) {
        taskListDao = appDatabase.taskListDao();
    }

    public LiveData<List<TaskListEntry>> getTaskList() {
        return taskListDao.loadAllTasksList();
    }

    public LiveData<List<TaskListEntry>> getCategory(String category) {
        return taskListDao.fetchTodoListByCategory(category);
    }

    public LiveData<List<TaskListEntry>> getPriorityList(int p) {
        return taskListDao.getHighPriorityList(p);
    }

    public LiveData<TaskListEntry> loadTaskListById(int id) {
        return taskListDao.loadTaskListById(id);
    }

    public void insertTaskList(final TaskListEntry taskList) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                taskListDao.insertTaskList(taskList);
            }
        });
    }

    public void updateTaskList(final TaskListEntry taskList) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                taskListDao.updateTaskList(taskList);
            }
        });
    }

    public void deleteTaskList(final TaskListEntry taskList) {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                taskListDao.deleteTaskList(taskList);
            }
        });
    }

    public void deleteAllTaskList() {
        AppDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase.databaseWriteExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        taskListDao.deleteAllTaskList();
                    }
                });
            }
        });
    }
}
