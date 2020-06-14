package com.example.todo_app.tasks;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo_app.R;
import com.example.todo_app.database.TaskEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>  implements Filterable {
    // Constant for date format
    //private static final String DATE_FORMAT = "dd-MM-yyy";

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    // Class variables for the List that holds task data and the Context
    private List<TaskEntry> mTaskEntries;
    private List<TaskEntry> mTaskEntriesAll;
    private List<Integer> selectedItems;
    private Context mContext;
    // Date formatter
   // private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    MainViewModel viewModel;

    /**
     * Constructor for the TaskAdapter that initializes the Context.
     *
     * @param context  the current Context
     * @param listener the ItemClickListener
     */
    public TaskAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
        selectedItems=new ArrayList<Integer>();
    }

    public List<TaskEntry> getTask() {
        return mTaskEntries;
    }

    /**
     * Called when ViewHolders are created to fill a RecyclerView.
     *
     * @return A new TaskViewHolder that holds the view for each task
     */
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.activity_task_layout, parent, false);
        return new TaskViewHolder(view);
    }

    /**
     * Called by the RecyclerView to display data at a specified position in the Cursor.
     *
     * @param holder   The ViewHolder to bind Cursor data to
     * @param position The position of the data in the Cursor
     */
    @Override
    public void onBindViewHolder(@NonNull final TaskViewHolder holder, final int position) {
        // Determine the values of the wanted data
        final TaskEntry taskEntry = mTaskEntries.get(position);
        String category = taskEntry.getCategory();
        String description = taskEntry.getDescription();
        int priority = taskEntry.getPriority();
        //String updatedAt = dateFormat.format(taskEntry.getUpdatedAt());
        String updatedAt = taskEntry.getUpdatedAt();

        //Set values
        holder.taskCategoryView.setText(category);
        holder.taskDescriptionView.setText(description);
        holder.updatedAtView.setText(updatedAt);


        holder.taskCheckboxView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked)
        {
            selectedItems.add(position);
            ////                    holder.taskCheckboxView.setSelected(false);

            ////                    holder.taskCheckboxView.toggle();

        }

            }
        });

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
                priorityColor = ContextCompat.getColor(mContext, R.color.materialRed);
                break;
            case 2:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialOrange);
                break;
            case 3:
                priorityColor = ContextCompat.getColor(mContext, R.color.materialYellow);
                break;
            default:
                break;
        }
        return priorityColor;
    }

    @Override
    public int getItemCount() {
        if (mTaskEntries == null) {
            return 0;
        }
        return mTaskEntries.size();
    }

    public List<TaskEntry> getTasks() {
        return mTaskEntries;
    }
    public List<Integer> getSelectedItems()
    {
        return selectedItems;
    }

    /**
     * When data changes, this method updates the list of taskEntries
     * and notifies the adapter to use the new values on it
     */
    public void setTasks(List<TaskEntry> taskEntries) {
        mTaskEntries = taskEntries;
        mTaskEntriesAll = taskEntries;
        notifyDataSetChanged();
    }


    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }


    @Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<TaskEntry> filteredList = new ArrayList<>();
            if (charSequence.toString().isEmpty()) {
                filteredList.addAll(mTaskEntriesAll);
            } else {
                for (TaskEntry task : mTaskEntriesAll) {
                    if (task.toString().toLowerCase().contains(charSequence.toString().toLowerCase())) {
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
            mTaskEntriesAll.clear();
            mTaskEntriesAll.addAll((Collection<? extends TaskEntry>) results.values);
            notifyDataSetChanged();
        }
    };

    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Class variables for the task description and priority TextViews
        TextView taskCategoryView;
        TextView taskDescriptionView;
        TextView updatedAtView;
        TextView priorityView;
       public CheckBox taskCheckboxView;

        /**
         * Constructor for the TaskViewHolders.
         *
         * @param itemView The view inflated in onCreateViewHolder
         */
        public TaskViewHolder(View itemView) {
            super(itemView);
            taskCategoryView=itemView.findViewById(R.id.categoryLayout);
            taskDescriptionView = itemView.findViewById(R.id.taskDescription);
            updatedAtView = itemView.findViewById(R.id.taskUpdatedAt);
            priorityView = itemView.findViewById(R.id.priorityTextView);
            taskCheckboxView =itemView.findViewById(R.id.taskCheckbox);
            taskCheckboxView.setOnCheckedChangeListener(null);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            int elementId = mTaskEntries.get(getAdapterPosition()).getId();
            if (mItemClickListener !=null || elementId != RecyclerView.NO_POSITION)
            {
                mItemClickListener.onItemClickListener(elementId);
            }
        }
    }
}