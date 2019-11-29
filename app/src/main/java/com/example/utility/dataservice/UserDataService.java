package com.example.utility.dataservice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.utility.models.User;

import java.util.UUID;

public class UserDataService {
    private static UserDataService userDataService;

    private Context context;
    private SQLiteDatabase database;

    public static UserDataService get(Context _context){
        if(userDataService == null){
            userDataService = new UserDataService(_context);
        }

        return userDataService;
    }

    private UserDataService(Context _context){
        this.context = _context.getApplicationContext();
        this.database = new UserDatabase(this.context).getWritableDatabase();
    }

    public void addUser(User user){
        ContentValues cv = getContentValues(user);
        database.insert(UserSchema.UserTable.NAME, null, cv);
    }

    public void updateUser(User user){
        ContentValues cv = getContentValues(user);
        String id = user.getId().toString();

        database.update(UserSchema.UserTable.NAME, cv, UserSchema.UserTable.Cols.UUID + " = ?", new String[] { id });
    }

    public User getDefaultUser(){
        UserCursorWrapper cursor = queryUser(null, null);

        try{
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getUser();
        }
        finally {
            cursor.close();
        }
    }

    public User getUser(UUID id){
        UserCursorWrapper cursor = queryUser(
                UserSchema.UserTable.Cols.UUID + " = ?",
                new String[] { id.toString() }
        );

        try{
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getUser();
        }
        finally {
            cursor.close();
        }
    }

    private static ContentValues getContentValues(User user){
        ContentValues values = new ContentValues();
        values.put(UserSchema.UserTable.Cols.UUID, user.getId().toString());
        values.put(UserSchema.UserTable.Cols.USERNAME, user.getUsername());
        values.put(UserSchema.UserTable.Cols.DATE_CREATED, user.getCreatedDate().getTime());
        return values;
    }

    private UserCursorWrapper queryUser(String where, String[] whereArgs){
        Cursor cursor = database.query(UserSchema.UserTable.NAME,
                null,
                where,
                whereArgs,
                null,
                null,
                null);

        return new UserCursorWrapper(cursor);
    }
}
