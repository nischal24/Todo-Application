package com.example.todo_app.Adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo_app.R;
import com.example.todo_app.database.TaskListEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.TaskListViewHolder> implements Filterable {

    private List<TaskListEntry> taskListEntryList;
    private List<TaskListEntry> mTaskEntriesAll;
    Context context;

    public TaskListAdapter(Context context) {
        this.context = context;
    }

    List<TaskListEntry> getTasks() {
        return taskListEntryList;
    }

    @NonNull
    @Override
    public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.activity_task_list_layout, parent, false);
        return new TaskListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListViewHolder holder, int position) {
        // Determine the values of the wanted data
        final TaskListEntry taskListEntry = taskListEntryList.get(position);
        String category = taskListEntry.getCategory();
        String description = taskListEntry.getDescription();
        int priority = taskListEntry.getPriority();
        String updatedAt = taskListEntry.getUpdatedAt();

        //Set values
        holder.taskCategoryView.setText(category);
        holder.taskDescriptionView.setText(description);
        holder.updatedAtView.setText(updatedAt);

        // Programmatically set the text and color for the priority TextView
        String priorityString = "" + priority; // converts int to String
        holder.priorityView.setText(priorityString);

        GradientDrawable priorityCircle = (GradientDrawable) holder.priorityView.getBackground();
        // Get the appropriate background color based on the priority
        int priorityColor = getPriorityColor(priority);
        priorityCircle.setColor(priorityColor);
    }

    private int getPriorityColor(int priority) {
        int priorityColor = 0;

        switch (priority) {
            case 1:
                priorityColor = ContextCompat.getColor(context, R.color.materialRed);
                break;
            case 2:
                priorityColor = ContextCompat.getColor(context, R.color.materialOrange);
                break;
            case 3:
                priorityColor = ContextCompat.getColor(context, R.color.materialYellow);
                break;
            default:
                break;
        }
        return priorityColor;
    }

    @Override
    public int getItemCount() {
        if (taskListEntryList == null) {
            return 0;
        }
        return taskListEntryList.size();
    }

    public List<TaskListEntry> getTaskList() {
        return taskListEntryList;
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setTasks(List<TaskListEntry> taskEntries) {
        taskListEntryList = taskEntries;
        mTaskEntriesAll = new ArrayList<>(taskEntries);
        notifyDataSetChanged();
    }

    //Filter method
    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<TaskListEntry> filteredList = new ArrayList<>();
            if (charSequence.toString().isEmpty()) {
                filteredList.addAll(mTaskEntriesAll);
            } else {
                for (TaskListEntry task : mTaskEntriesAll) {
                    if (task.getDescription().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                        filteredList.add(task);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            try {
                taskListEntryList.clear();
                taskListEntryList.addAll((Collection<? extends TaskListEntry>) results.values);
                notifyDataSetChanged();
            } catch (Exception e) {

            }
        }
    };

    public class TaskListViewHolder extends RecyclerView.ViewHolder {

        // Class variables for the task description and priority TextViews
        TextView taskCategoryView;
        TextView taskDescriptionView;
        TextView updatedAtView;
        TextView priorityView;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public TaskListViewHolder(@NonNull View itemView) {
            super(itemView);
            taskCategoryView = itemView.findViewById(R.id.categoryLayout);
            taskDescriptionView = itemView.findViewById(R.id.taskDescription);
            updatedAtView = itemView.findViewById(R.id.taskUpdatedAt);
            priorityView = itemView.findViewById(R.id.priorityTextView);
        }
    }
}
