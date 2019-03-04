package com.m4rc3l05.my_flux.core.actions.asyncActions;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.m4rc3l05.my_flux.core.actions.AddTodoAction;
import com.m4rc3l05.my_flux.core.actions.StartPerformTodoAction;
import com.m4rc3l05.my_flux.core.Dispatcher;
import com.m4rc3l05.my_flux.core.models.Todo;
import com.m4rc3l05.my_flux.db.DBHelper;

import java.sql.Timestamp;
import java.util.UUID;

public class PerformAddTodoAction extends BaseAsyncAction {

    private final Todo todo;
    private final DBHelper dbHelper;
    private final DatabaseReference databaseReference;

    private PerformAddTodoAction(Todo todo, DBHelper dbHelper, DatabaseReference databaseReference) {
        this.todo = todo;
        this.dbHelper = dbHelper;
        this.databaseReference = databaseReference;
    }

    public static PerformAddTodoAction create(Todo todo, DBHelper dbHelper, DatabaseReference databaseReference) {
        return new PerformAddTodoAction(todo, dbHelper, databaseReference);
    }

    @Override
    public void doWork(Dispatcher dispatcher) {
        dispatcher.dispatch(StartPerformTodoAction.create());

        if (!dbHelper.addNewTodo(todo)) return;

        this.databaseReference
                .child(todo.get_id())
                .setValue(todo)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dispatcher.dispatch(AddTodoAction.create(todo));
                        this.__notify(true);
                    } else {
                        dbHelper.deleteTodo(todo.get_id());
                        this.__notify(false);
                    }
                });
    }
}
