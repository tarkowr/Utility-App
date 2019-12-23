package com.rt.utility.dataservice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.rt.utility.models.User;

import java.util.UUID;

public class UserDataService {
    private static UserDataService userDataService;

    private Context context;
    private SQLiteDatabase database;

    /*
    Ensures only one instance of this class exists during the app's lifecycle
    Referenced Android Programming by The Big Nerd Ranch Guide
     */
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

    /*
    Inserts a user into the User Table
    Learned how to insert a row into a SQLite db from Android Programming by The Big Nerd Ranch Guide
     */
    public void addUser(User user){
        ContentValues cv = getContentValues(user);
        database.insert(UserSchema.UserTable.NAME, null, cv);
    }

    /*
    Updates a user's information in the User Table
    Learned how to update a row in a SQLite db from Android Programming by The Big Nerd Ranch Guide
     */
    public void updateUser(User user){
        ContentValues cv = getContentValues(user);
        String id = user.getId().toString();

        database.update(UserSchema.UserTable.NAME, cv, UserSchema.UserTable.Cols.UUID + " = ?", new String[] { id });
    }

    /*
    Returns the first (default) user in the User Table
     */
    public User getDefaultUser(){
        UserCursorWrapper cursor = queryUser(null, null);

        try{
            if(cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getUser();
        }
        catch (Exception ex){
            return null;
        }
        finally {
            cursor.close();
        }
    }

    /*
    Returns a user from the User Table by ID
    Learned how to use the cursor returned from a query to return a row from Android Programming by The Big Nerd Ranch Guide
     */
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

    /*
    Maps the user object to User Table columns
    Learned how to map an object's properties to a SQLite Table's columns with ContentValues from Android Programming by The Big Nerd Ranch Guide
     */
    private static ContentValues getContentValues(User user){
        ContentValues values = new ContentValues();
        values.put(UserSchema.UserTable.Cols.UUID, user.getId().toString());
        values.put(UserSchema.UserTable.Cols.USERNAME, user.getUsername());
        values.put(UserSchema.UserTable.Cols.DATE_CREATED, user.getCreatedDate().getTime());
        return values;
    }

    /*
    Queries the User Table for users with SQL query
    Learned how to query a Table in SQLite with SELECT, WHERE, GROUP BY, etc. clauses from Android Programming by The Big Nerd Ranch Guide
     */
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
