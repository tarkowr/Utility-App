package com.rt.utility.dataservice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabase extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "utility_user.db";

    public UserDatabase(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    /*
    Creates the User Table in the utility DB and specifies the User Table columns
    Learned how to programmatically create a SQLite db from Android Programming by The Big Nerd Ranch Guide
     */
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + UserSchema.UserTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                UserSchema.UserTable.Cols.UUID + ", " +
                UserSchema.UserTable.Cols.USERNAME + ", " +
                UserSchema.UserTable.Cols.DATE_CREATED + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){ return;}
}
