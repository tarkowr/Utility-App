package com.rt.utility.dataservice;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.rt.utility.models.Task;

import java.util.Date;
import java.util.UUID;

public class TaskCursorWrapper extends CursorWrapper {
    public TaskCursorWrapper(Cursor cursor){
        super(cursor);
    }

    public Task getTask(){
        String id = getString(getColumnIndex(TaskSchema.TaskTable.Cols.ID));
        String title = getString(getColumnIndex(TaskSchema.TaskTable.Cols.TITLE));
        Boolean completed = (getInt(getColumnIndex(TaskSchema.TaskTable.Cols.COMPLETED)) != 0);
        Long entered = getLong(getColumnIndex(TaskSchema.TaskTable.Cols.DATE_ENTERED));

        Task task = new Task(UUID.fromString(id), title, new Date(entered), completed);
        return task;
    }

}
