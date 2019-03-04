package com.m4rc3l05.my_flux.core.actions.asyncActions;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.m4rc3l05.my_flux.core.actions.BaseAction;
import com.m4rc3l05.my_flux.core.actions.InitTodosAction;
import com.m4rc3l05.my_flux.core.actions.StartPerformTodoAction;
import com.m4rc3l05.my_flux.db.DBHelper;
import com.m4rc3l05.my_flux.core.Dispatcher;
import com.m4rc3l05.my_flux.core.models.Todo;
import com.m4rc3l05.my_flux.adapters.TodosRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;



public class PerformFetchAllTodos extends BaseAsyncAction {
    private Context ctx;

    private static class FetchTodos extends AsyncTask<Context, Void, List<Todo>> {

        @Override
        protected List<Todo> doInBackground(Context... args) {
            try {
                return DBHelper.create(args[0]).getAllTodos();
            } catch (Exception e) {
                return null;
            }
        }
    }


    private PerformFetchAllTodos(Context ctx) {
        this.ctx = ctx;
    }

    public static PerformFetchAllTodos create(Context ctx) {
        return new PerformFetchAllTodos(ctx);
    }

    @Override
    public void doWork(Dispatcher dispatcher) {
        dispatcher.dispatch(StartPerformTodoAction.create());
        new Handler().postDelayed(() -> {
            try {
                List<Todo> todos = new PerformFetchAllTodos.FetchTodos().execute(ctx).get();
                dispatcher.dispatch(InitTodosAction.create(todos == null ? new ArrayList<>() : todos));
                this.__notify(true);
            } catch(Exception e) {
                this.__notify(false);
            }
        }, 2000);
    }
}
