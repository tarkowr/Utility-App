package com.rt.utility.apps;

import android.os.Bundle;
import android.util.Log;
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
import com.rt.utility.dataservice.TaskDataService;
import com.rt.utility.helpers.JavaUtils;
import com.rt.utility.models.Task;

import java.util.ArrayList;
import java.util.List;

public class TaskManagerFragment extends Fragment {

    private List<Task> tasks;
    private TaskAdapter adapter;
    private RecyclerView taskList;
    private TaskDataService taskDataService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        tasks = new ArrayList<>();

        Log.d("Task Manager", "Getting data service");
        taskDataService = TaskDataService.get(getActivity());
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_manager_app, container, false);

        tasks = getAllTasks();

        Log.d("Task Manager", "" + tasks.size());

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
            String taskTitle = JavaUtils.GetWidgetText(addText);

            if(isValidTaskTitle(taskTitle)){
                Task task = new Task(taskTitle);
                tasks.add(task);
                taskDataService.insertTask(task);

                addText.setText(R.string.empty_string);
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
            taskDataService.updateTask(task);
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
            taskDataService.deleteTask(task);
            commitChanges();
        }
    };

    /*
    Get all saved tasks in the database
     */
    private List<Task> getAllTasks(){
        List<Task> _tasks = taskDataService.getAllTasks();

        if(_tasks == null){
            _tasks = new ArrayList<>();
        }

        return _tasks;
    }

    private Boolean isValidTaskTitle(String taskTitle){
        final int MIN_LENGTH = 1;
        final int MAX_LENGTH = 40;
        String regex = ".*[a-zA-Z0-9].*";

        if(JavaUtils.CheckIfEmptyString(taskTitle)){
            return false;
        }

        taskTitle = taskTitle.trim();
        int length = taskTitle.length();

        if(length < MIN_LENGTH || length > MAX_LENGTH){
            return false;
        }

        if(!taskTitle.matches(regex)){
            return false;
        }

        return true;
    }

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
