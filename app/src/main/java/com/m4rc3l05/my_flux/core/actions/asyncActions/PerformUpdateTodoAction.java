package com.m4rc3l05.my_flux.core.actions.asyncActions;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.m4rc3l05.my_flux.ConnectionUtils;
import com.m4rc3l05.my_flux.core.Dispatcher;
import com.m4rc3l05.my_flux.core.actions.StartPerformTodoAction;
import com.m4rc3l05.my_flux.core.actions.UpdateTodoAction;
import com.m4rc3l05.my_flux.models.Todo;

public class PerformUpdateTodoAction extends BaseAsyncAction {
    private final Todo todo;
    private final Context ctx;
    private final DatabaseReference databaseReference;

    private PerformUpdateTodoAction(Todo todo, Context ctx, DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
        this.ctx = ctx;
        this.todo = todo;
    }

    public static PerformUpdateTodoAction create(Todo todo, Context ctx, DatabaseReference databaseReference) {
        return new PerformUpdateTodoAction(todo, ctx, databaseReference);
    }

    @Override
    public void doWork(Dispatcher dispatcher) {
        if (ConnectionUtils.isConnected(ctx)) dispatcher.dispatch(StartPerformTodoAction.create());

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

        if (!ConnectionUtils.isConnected(ctx)) {
            dispatcher.dispatch(UpdateTodoAction.create(todo, todo.get_id()));
            this.__notify(true);
        }
    }
}
