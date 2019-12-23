package com.rt.utility.dataservice;

public class TaskSchema {
    public static final class TaskTable {
        public static final String NAME = "tasks";

        public static final class Cols {
            public static final String ID  = "id";
            public static final String TITLE = "title";
            public static final String COMPLETED = "completed";
            public static final String DATE_ENTERED = "dateEntered";
        }
    }
}
