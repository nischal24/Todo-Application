package com.example.todo_app.addEditTask;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

//import com.example.todo_app.add_edit_task_viewmodel;


public class add_edit_task_viemodel_factory extends ViewModelProvider.NewInstanceFactory {
    Application application;
    int taskId;

    public add_edit_task_viemodel_factory(Application application, int taskId) {
        this.application = application;
        this.taskId = taskId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new add_edit_task_viewmodel(application,taskId);
    }
}
