package com.rt.utility.dataservice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rt.utility.models.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaskDataService {

    private static TaskDataService taskDataService;
    private static String TAG = "TASK_DATA_SERVICE";

    private Context context;
    private SQLiteDatabase database;

    public static TaskDataService get(Context _context){
        if(taskDataService == null){
            taskDataService = new TaskDataService(_context);
        }

        return taskDataService;
    }

    private TaskDataService(Context _context){
        this.context = _context.getApplicationContext();
        this.database = new TaskDatabase(this.context).getWritableDatabase();
    }

    private static ContentValues getContentValues(Task task){
        ContentValues contentValues = new ContentValues();

        contentValues.put(TaskSchema.TaskTable.Cols.ID, task.GetId().toString());
        contentValues.put(TaskSchema.TaskTable.Cols.TITLE, task.GetName());
        contentValues.put(TaskSchema.TaskTable.Cols.COMPLETED, task.IsCompleted());
        contentValues.put(TaskSchema.TaskTable.Cols.DATE_ENTERED, task.GetEnteredDate().toString());

        return contentValues;
    }

    private TaskCursorWrapper queryTask(String where, String[] whereArgs){
        Cursor cursor = database.query(TaskSchema.TaskTable.NAME,
                null,
                where,
                whereArgs,
                null,
                null,
                null);

        return new TaskCursorWrapper(cursor);
    }

    public void insertTask(Task task){
        ContentValues cv = getContentValues(task);

        try{
            database.insert(TaskSchema.TaskTable.NAME, null, cv);
        }
        catch (Exception ex){
            Log.d(TAG, Objects.requireNonNull(ex.getMessage()));
        }
    }

    public void deleteTask(Task task){
        String id = task.GetId().toString();

        try{
            database.delete(TaskSchema.TaskTable.NAME, TaskSchema.TaskTable.Cols.ID + " = ?", new String[] { id });
        }
        catch(Exception ex){
            Log.d(TAG, Objects.requireNonNull(ex.getMessage()));
        }
    }

    public void updateTask(Task task){
        ContentValues cv = getContentValues(task);
        String id = task.GetId().toString();

        try{
            database.update(TaskSchema.TaskTable.NAME, cv, TaskSchema.TaskTable.Cols.ID + " = ?", new String[] { id });
        }
        catch(Exception ex){
            Log.d(TAG, Objects.requireNonNull(ex.getMessage()));
        }
    }

    public List<Task> getAllTasks(){
        List<Task> tasks = new ArrayList<>();
        TaskCursorWrapper cursor = queryTask(null, null);

        try{
            cursor.moveToFirst();

            while(!cursor.isAfterLast()){
                tasks.add(cursor.getTask());
                cursor.moveToNext();
            }
        }
        catch(Exception ex){
            Log.d(TAG, Objects.requireNonNull(ex.getMessage()));
            tasks = null;
        }
        finally {
            cursor.close();
        }

        return tasks;
    }

}
