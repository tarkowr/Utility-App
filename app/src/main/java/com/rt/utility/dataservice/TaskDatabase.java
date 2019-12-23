package com.rt.utility.dataservice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskDatabase extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "utility_task.db";

    public TaskDatabase(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + TaskSchema.TaskTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                TaskSchema.TaskTable.Cols.ID + ", " +
                TaskSchema.TaskTable.Cols.TITLE + ", " +
                TaskSchema.TaskTable.Cols.COMPLETED + ", " +
                TaskSchema.TaskTable.Cols.DATE_ENTERED + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){ return;}
}
