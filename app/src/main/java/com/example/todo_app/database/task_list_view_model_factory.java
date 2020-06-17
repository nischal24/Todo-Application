package com.example.todo_app.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.todo_app.tasks.task_list_viewmodel;

public class task_list_view_model_factory extends ViewModelProvider.NewInstanceFactory {
    Application application;

    public task_list_view_model_factory(Application application) {
        this.application = application;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new task_list_viewmodel(application);
    }
}
