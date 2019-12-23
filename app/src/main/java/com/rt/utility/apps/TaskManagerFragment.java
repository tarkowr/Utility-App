package com.rt.utility.apps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rt.utility.R;
import com.rt.utility.models.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskManagerFragment extends Fragment {

    private List<Task> tasks;
    private TaskAdapter adapter;
    private RecyclerView taskList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        tasks = new ArrayList<>();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_manager_app, container, false);

        Button addBtn = view.findViewById(R.id.btn_add_task);
        addBtn.setOnClickListener(addTask);

        taskList = view.findViewById(R.id.task_list);
        taskList.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new TaskAdapter(tasks, checkedTask, deleteTask);
        taskList.setAdapter(adapter);

        return view;
    }

    /*
    Add button onClick event
     */
    private View.OnClickListener addTask = new View.OnClickListener() {
        public void onClick(View view) {
            view = getView();

            if(view == null){
                return;
            }

            EditText addText = view.findViewById(R.id.txt_new_task);
            String taskTitle = addText.getText().toString();

            if(!taskTitle.isEmpty() && taskTitle.trim().length() > 0){
                tasks.add(new Task(taskTitle));
                addText.setText("");
                commitChanges();
            }
        }
    };


    /*
    Checkbox onClick event
     */
    private OnItemClickListener checkedTask = new OnItemClickListener() {
        @Override
        public void onItemClick(Task task) {
            task.ToggleCompleted();
            commitChanges();
        }
    };

    /*
    Delete Task onClick event
     */
    private OnItemClickListener deleteTask = new OnItemClickListener() {
        @Override
        public void onItemClick(Task task) {
            tasks.remove(task);
            commitChanges();
        }
    };

    /*
    Update the ListView when the user adds new tasks
     */
    private void commitChanges(){
        adapter.notifyDataSetChanged();
    }

    /*
    Represents each view in the task recycler view
     */
    private class TaskHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle;
        private CheckBox checkBoxCompleted;
        private ImageView imgDelete;

        private TaskHolder(View taskContainerView){
            super(taskContainerView);
            txtTitle = taskContainerView.findViewById(R.id.task_title);
            checkBoxCompleted = taskContainerView.findViewById(R.id.task_complete);
            imgDelete = taskContainerView.findViewById(R.id.task_delete);
        }

        /*
        Binds data to each task in the task recycler view and attaches the onClick listeners
         */
        private void bindListApp(final Task task, final OnItemClickListener checkListener, final OnItemClickListener deleteListener) {
            txtTitle.setText(task.toString());

            checkBoxCompleted.setChecked(task.IsCompleted());
            checkBoxCompleted.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    checkListener.onItemClick(task);
                }
            });

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteListener.onItemClick(task);
                }
            });
        }
    }

    /*
    Interface between the task model and the task recycler view
     */
    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {

        private List<Task> tasks;
        private OnItemClickListener checkListener;
        private OnItemClickListener deleteListener;

        private TaskAdapter(List<Task> _tasks, OnItemClickListener _checkListener, OnItemClickListener _deleteListener){
            this.tasks = _tasks;
            this.checkListener = _checkListener;
            this.deleteListener = _deleteListener;
        }

        @NonNull
        @Override
        public TaskHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType){
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.task_list_item, viewGroup, false);
            return new TaskHolder(view);
        }

        @Override
        public void onBindViewHolder(TaskHolder taskHolder, int position){
            Task task = tasks.get(position);
            taskHolder.bindListApp(task, checkListener, deleteListener);
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }
    }

    /*
    Custom interface for task onClick events
     */
    private interface OnItemClickListener {
        void onItemClick(Task task);
    }
}
