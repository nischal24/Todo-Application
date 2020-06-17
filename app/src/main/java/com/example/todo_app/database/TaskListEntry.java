package com.example.todo_app.database;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "taskList")
public class TaskListEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String description;
    private String category;
    private int priority;
    @ColumnInfo(name = "updated_at")
    private String updatedAt;

    public TaskListEntry(int id, String description, String category, int priority, String updatedAt) {
        this.id = id;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.updatedAt = updatedAt;
    }

    @Ignore
    public TaskListEntry(String description, String category, int priority, String updatedAt) {
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.updatedAt = updatedAt;
    }

    @Ignore
    public TaskListEntry() {
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public int getPriority() {
        return priority;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

}
