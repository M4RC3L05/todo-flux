package com.m4rc3l05.my_flux.core.actions;

import com.google.firebase.database.DatabaseReference;
import com.m4rc3l05.my_flux.core.Dispatcher;
import com.m4rc3l05.my_flux.core.actions.asyncActions.BaseAsyncAction;
import com.m4rc3l05.my_flux.core.models.Todo;
import com.m4rc3l05.my_flux.db.DBHelper;

public class PerformDeleteTodoAction extends BaseAsyncAction {
    private final Todo todo;
    private final DatabaseReference databaseReference;
    private final DBHelper dbHelper;

    private PerformDeleteTodoAction(Todo todo, DatabaseReference databaseReference, DBHelper dbHelper) {
        this.todo = todo;
        this.databaseReference = databaseReference;
        this.dbHelper = dbHelper;
    }

    public static PerformDeleteTodoAction create(Todo todo, DatabaseReference databaseReference, DBHelper dbHelper) {
        return new PerformDeleteTodoAction(todo, databaseReference, dbHelper);
    }

    @Override
    public void doWork(Dispatcher dispatcher) {
        if (!dbHelper.deleteTodo(todo.get_id())) {
            this.__notify(false);
            return;
        }

        databaseReference
                .child(todo.get_id())
                .setValue(null)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dispatcher.dispatch(RemoveTodoAction.create(todo.get_id()));
                        this.__notify(true);
                    } else {
                        this.__notify(false);
                    }
                });

        dispatcher.dispatch(RemoveTodoAction.create(todo.get_id()));
    }
}
