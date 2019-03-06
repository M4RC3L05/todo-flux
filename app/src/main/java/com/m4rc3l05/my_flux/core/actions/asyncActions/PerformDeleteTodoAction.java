package com.m4rc3l05.my_flux.core.actions.asyncActions;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.m4rc3l05.my_flux.ConnectionUtils;
import com.m4rc3l05.my_flux.core.Dispatcher;
import com.m4rc3l05.my_flux.core.actions.RemoveTodoAction;
import com.m4rc3l05.my_flux.core.actions.StartPerformTodoAction;
import com.m4rc3l05.my_flux.core.actions.TodoActionError;
import com.m4rc3l05.my_flux.models.Todo;

public class PerformDeleteTodoAction extends BaseAsyncAction {
    private final Todo todo;
    private final DatabaseReference databaseReference;
    private final Context ctx;

    public PerformDeleteTodoAction(Todo todo, DatabaseReference databaseReference, Context ctx) {
        this.todo = todo;
        this.databaseReference = databaseReference;
        this.ctx = ctx;
    }

    public static PerformDeleteTodoAction create(Todo todo, DatabaseReference databaseReference, Context ctx) {
        return new PerformDeleteTodoAction(todo, databaseReference, ctx);
    }

    @Override
    public void doWork(Dispatcher dispatcher) {
        if (ConnectionUtils.isConnected(ctx)) dispatcher.dispatch(StartPerformTodoAction.create());

        databaseReference
                .child(todo.get_id())
                .setValue(null)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dispatcher.dispatch(RemoveTodoAction.create(todo.get_id()));
                        this.__notify(true);
                    } else {
                        dispatcher.dispatch(TodoActionError.create("Could not delete to-do"));
                        this.__notify(false);
                    }
                });

        if (!ConnectionUtils.isConnected(ctx)) {
            dispatcher.dispatch(RemoveTodoAction.create(todo.get_id()));
            this.__notify(true);
        }
    }
}
