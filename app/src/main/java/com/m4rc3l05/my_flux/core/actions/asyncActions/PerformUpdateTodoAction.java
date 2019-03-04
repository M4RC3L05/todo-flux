package com.m4rc3l05.my_flux.core.actions.asyncActions;

import com.google.firebase.database.DatabaseReference;
import com.m4rc3l05.my_flux.core.Dispatcher;
import com.m4rc3l05.my_flux.core.actions.UpdateTodoAction;
import com.m4rc3l05.my_flux.core.models.Todo;
import com.m4rc3l05.my_flux.db.DBHelper;

public class PerformUpdateTodoAction extends BaseAsyncAction {
    private final Todo todo;
    private final DBHelper dbHelper;
    private final DatabaseReference databaseReference;

    private PerformUpdateTodoAction(Todo todo, DBHelper dbHelper, DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
        this.dbHelper = dbHelper;
        this.todo = todo;
    }

    public static PerformUpdateTodoAction create(Todo todo, DBHelper dbHelper, DatabaseReference databaseReference) {
        return new PerformUpdateTodoAction(todo, dbHelper, databaseReference);
    }

    @Override
    public void doWork(Dispatcher dispatcher) {

        if (!dbHelper.updateTodo(todo)) {
            this.__notify(false);
            return;
        }

        databaseReference
                .child(todo.get_id())
                .setValue(todo)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dispatcher.dispatch(
                                UpdateTodoAction.create(todo, todo.get_id())
                        );
                        this.__notify(true);
                    } else {
                        this.__notify(false);
                    }
                });
    }
}
