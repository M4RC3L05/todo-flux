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

    private final String todoText;
    private final DBHelper dbHelper;
    private final DatabaseReference databaseReference;
    private final Context context;

    private PerformAddTodoAction(Context ctx, String todoText, DBHelper dbHelper, DatabaseReference databaseReference) {
        this.todoText = todoText;
        this.dbHelper = dbHelper;
        this.databaseReference = databaseReference;
        this.context = ctx;
    }

    public static PerformAddTodoAction create(Context ctx, String todoText, DBHelper dbHelper, DatabaseReference databaseReference) {
        return new PerformAddTodoAction(ctx, todoText, dbHelper, databaseReference);
    }

    @Override
    public void doWork(Dispatcher dispatcher) {
        dispatcher.dispatch(StartPerformTodoAction.create());
        Todo todoToInsert = Todo.create(UUID.randomUUID().toString(), todoText, false, "" + new Timestamp(System.currentTimeMillis()).getTime());

        if (!dbHelper.addNewTodo(todoToInsert)) return;

        this.databaseReference
                .child(todoToInsert.get_id())
                .setValue(todoToInsert)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dispatcher.dispatch(AddTodoAction.create(todoToInsert));
                        this.__notify(true);
                    } else {
                        dbHelper.deleteTodo(todoToInsert.get_id());
                        this.__notify(false);
                    }
                });
    }
}
