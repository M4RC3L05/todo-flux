package com.m4rc3l05.my_flux.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.m4rc3l05.my_flux.core.models.Todo;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper instance;

    public static final String TABLE_NAME = "todos";
    public static final String COL__ID = "id";
    public static final String COL__TEXT = "text";
    public static final String COL__IS_DONE = "isDone";
    public static final String COL__CREATED_AT = "created_at";

    private DBHelper(Context context) {
        super(context, DBHelper.TABLE_NAME, null, 1);
    }

    public static DBHelper create(Context context) {
        if (DBHelper.instance != null) return DBHelper.instance;

        synchronized (DBHelper.class) {
            if (instance == null) {
                instance = new DBHelper(context);
            }
        }

        return DBHelper.instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTbale = "CREATE TABLE " + DBHelper.TABLE_NAME + " (" +
                "id string PRIMARY KEY," +
                "text TEXT NOT NULL," +
                "isDone INTEGER NOT NULL DEFAULT 0," +
                "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ")";

        db.execSQL(createTbale);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBHelper.TABLE_NAME);
    }

    public boolean addNewTodo(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COL__ID, todo.get_id());
        contentValues.put(DBHelper.COL__TEXT, todo.get_text());
        contentValues.put(DBHelper.COL__IS_DONE, todo.is_isDone() ? 1 : 0);
        contentValues.put(DBHelper.COL__CREATED_AT, todo.get_timestamp());

        long res = db.insert(DBHelper.TABLE_NAME, null, contentValues);

        return res != -1;
    }

    public Cursor getTodos() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT *  FROM " + DBHelper.TABLE_NAME + " ORDER BY created_at DESC", null);
        return c;
    }

    public boolean deleteTodo(String id) {
        SQLiteDatabase db = this.getWritableDatabase();

        long res = db.delete(DBHelper.TABLE_NAME, "id='" + id + "'", null);

        return res != -1;
    }

    public boolean onToggleComplete(String todoId, boolean isDone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COL__IS_DONE, isDone ? 1 : 0);

        long res = db.update(DBHelper.TABLE_NAME, contentValues, "id='" + todoId + "'", null);

        return res != -1;
    }

    public boolean updateTodoText(Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COL__TEXT, todo.get_text());

        long res = db.update(DBHelper.TABLE_NAME, contentValues, "id='" + todo.get_id() + "'", null);

        return res != -1;
    }

    public List<Todo> getAllTodos() {
        Cursor todosCursor = this.getTodos();
        List<Todo> todosFromDB = new ArrayList<>();

        while(todosCursor.moveToNext()) {
            todosFromDB.add(Todo.create(
                    todosCursor.getString(todosCursor.getColumnIndex(DBHelper.COL__ID)),
                    todosCursor.getString(todosCursor.getColumnIndex(DBHelper.COL__TEXT)),
                    Integer.parseInt(todosCursor.getString(todosCursor.getColumnIndex(DBHelper.COL__IS_DONE))) == 1,
                    todosCursor.getString(todosCursor.getColumnIndex(DBHelper.COL__CREATED_AT))
            ));
        }

        return todosFromDB;
    }
}
