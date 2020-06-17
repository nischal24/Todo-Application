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
public interface TaskListDao {
    @Query("select * from taskList")
    public LiveData<List<TaskListEntry>> loadAllTasksList();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertTaskList(TaskListEntry taskList);

    @Update
    public void updateTaskList(TaskListEntry taskList);

    @Delete
    public void deleteTaskList(TaskListEntry taskList);

    @Query("delete from taskList")
    public void deleteAllTaskList();

    @Query("select * from taskList where id =:id")
    public LiveData<TaskListEntry> loadTaskListById(int id);

    @Query("SELECT * FROM taskList WHERE category = :category")
    public LiveData<List<TaskListEntry>> fetchTodoListByCategory(String category);

    @Query("select * from taskList where priority =:p")
    public LiveData<List<TaskListEntry>> getHighPriorityList(int p);

}
