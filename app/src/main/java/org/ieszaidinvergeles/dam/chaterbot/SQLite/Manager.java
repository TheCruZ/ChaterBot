package org.ieszaidinvergeles.dam.chaterbot.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.ieszaidinvergeles.dam.chaterbot.Message;

import java.util.ArrayList;
import java.util.List;

public class Manager {

    Helper h;
    SQLiteDatabase db;
    boolean closed = false;

    public Manager(Context c) {
        this(c, true);
    }

    public Manager(Context c, boolean write) {
        h = new Helper(c);
        if (write)
            db = h.getWritableDatabase();
        else
            db = h.getReadableDatabase();
    }

    public void close() {
        h.close();
        closed = true;
    }

    public long insert(Message m) {
        ContentValues c=new ContentValues();
        c.put(Contract.chatsTable.COLUMN_FROM,m.getFrom());
        c.put(Contract.chatsTable.COLUMN_MESSAGE,m.getMessage());
        c.put(Contract.chatsTable.COLUMN_WHEN,m.getWhen());
        return db.insert(Contract.chatsTable.TABLE_NAME, null, c);
    }

    public int delete(Message m) {
        return delete(m.getId());
    }

    public int delete(long id) {
        String condicion = Contract.chatsTable._ID + " = ?";
        String[] argumentos = new String[]{id + ""};
        return db.delete(Contract.chatsTable.TABLE_NAME, condicion, argumentos);
    }

    public List<Message> getLastMessages() {
        List<Message> messages = new ArrayList<>();
        Cursor cursor = db.query(Contract.chatsTable.TABLE_NAME, null, null, null, null, null, Contract.chatsTable.COLUMN_WHEN);
        while (cursor.moveToNext()) {
            messages.add(getFila(cursor));
        }
        cursor.close();
        return messages;
    }

    public static Message getFila(Cursor c) {
        Message co = new Message();
        co.setId(c.getLong(c.getColumnIndex(Contract.chatsTable._ID)));
        co.setFrom(c.getString(c.getColumnIndex(Contract.chatsTable.COLUMN_FROM)));
        co.setMessage(c.getString(c.getColumnIndex(Contract.chatsTable.COLUMN_MESSAGE)));
        co.setWhen(c.getLong(c.getColumnIndex(Contract.chatsTable.COLUMN_WHEN)));
        return co;
    }
}
