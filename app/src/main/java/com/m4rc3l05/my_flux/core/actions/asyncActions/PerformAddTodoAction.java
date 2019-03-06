package com.m4rc3l05.my_flux.core.actions.asyncActions;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.m4rc3l05.my_flux.ConnectionUtils;
import com.m4rc3l05.my_flux.core.Dispatcher;
import com.m4rc3l05.my_flux.core.actions.AddTodoAction;
import com.m4rc3l05.my_flux.core.actions.StartPerformTodoAction;
import com.m4rc3l05.my_flux.core.actions.TodoActionError;
import com.m4rc3l05.my_flux.models.Todo;

public class PerformAddTodoAction extends BaseAsyncAction {

    private final Todo todo;
    private final Context ctx;
    private final DatabaseReference databaseReference;

    public PerformAddTodoAction(Context ctx, Todo todo, DatabaseReference databaseReference) {
        this.todo = todo;
        this.databaseReference = databaseReference;
        this.ctx = ctx;
    }

    public static PerformAddTodoAction create(Context ctx, Todo todo, DatabaseReference databaseReference) {
        return new PerformAddTodoAction(ctx, todo, databaseReference);
    }

    @Override
    public void doWork(Dispatcher dispatcher) {
        if (ConnectionUtils.isConnected(ctx)) dispatcher.dispatch(StartPerformTodoAction.create());

        this.databaseReference
                .child(todo.get_id())
                .setValue(todo)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dispatcher.dispatch(AddTodoAction.create(todo));
                        this.__notify(true);
                    } else {
                        dispatcher.dispatch(TodoActionError.create("Could not create to-do"));
                        this.__notify(false);
                    }
                });

        if (!ConnectionUtils.isConnected(ctx)) {
            dispatcher.dispatch(AddTodoAction.create(todo));
            this.__notify(true);
        }
    }
}
