package com.rt.utility.dataservice;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.rt.utility.models.User;

import java.util.Date;
import java.util.UUID;

public class UserCursorWrapper extends CursorWrapper {
    public UserCursorWrapper(Cursor cursor){
        super(cursor);
    }

    /*
    Returns user data from the User Table at the cursor's location (row)
    Referenced Android Programming by The Big Nerd Ranch Guide
     */
    public User getUser(){
        String id = getString(getColumnIndex(UserSchema.UserTable.Cols.UUID));
        String username = getString(getColumnIndex(UserSchema.UserTable.Cols.USERNAME));
        Long date = getLong(getColumnIndex(UserSchema.UserTable.Cols.DATE_CREATED));

        User user = new User(UUID.fromString(id));
        user.setCreatedDate(new Date(date));
        user.setUsername(username);

        return user;
    }
}
