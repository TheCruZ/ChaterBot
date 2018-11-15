package org.ieszaidinvergeles.dam.chaterbot.SQLite;

import android.provider.BaseColumns;

public class Contract {

    private Contract() {
    }

    public static class chatsTable implements BaseColumns {
        public static final String TABLE_NAME = "chats";
        public static final String COLUMN_FROM = "who";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_WHEN = "whenhappens";

        public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_FROM + " TEXT," +
                COLUMN_MESSAGE + " TEXT," +
                COLUMN_WHEN + " Integer)";

        public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

}
