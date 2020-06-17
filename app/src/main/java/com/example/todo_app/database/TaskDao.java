package com.example.todo_app.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("select * from task order by priority")
    LiveData<List<TaskEntry>> loadAllTasks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTask(TaskEntry task);

    @Update
    void update(TaskEntry task);

    @Delete
    void deleteTask(TaskEntry task);

    @Query("delete from task")
    void deleteAllTask();

    @Query("SELECT * FROM task WHERE category = :category")
    LiveData<List<TaskEntry>> fetchTodoListByCategory(String category);

    @Query("select * from task WHERE updated_at = :date")
    LiveData<List<TaskEntry>> getTasksByDate(String date);

    @Query("select * from task WHERE category = :category and updated_at = :date")
    LiveData<List<TaskEntry>> getTasksByCategoryByTodayDate(String category, String date);

    @Query("select * from task WHERE priority = :p and updated_at = :date")
    LiveData<List<TaskEntry>> getTasksByPriorityByTodayDate(int p, String date);

    @Query("select * from task WHERE updated_at != :date")
    LiveData<List<TaskEntry>> getTasksByDateNot(String date);

    @Query("select * from task WHERE category = :category and updated_at != :date")
    LiveData<List<TaskEntry>> getTasksByCategoryByUpcomingDate(String category, String date);

    @Query("select * from task WHERE priority = :p and updated_at != :date")
    LiveData<List<TaskEntry>> getTasksByPriorityByUpcomingDate(int p, String date);

    @Query("select * from task where priority =:p")
    LiveData<List<TaskEntry>> getHighPriority(int p);

    @Query("select * from task where id =:id")
    LiveData<TaskEntry> loadTaskById(int id);

}
