package com.example.utility.dataservice;

public class UserSchema {
    public static final class UserTable {
        public static final String NAME = "users";

        public static final class Cols {
            public static final String UUID  = "id";
            public static final String USERNAME = "username";
            public static final String DATE_CREATED = "dateCreated";
        }
    }
}
