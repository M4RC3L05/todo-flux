package com.m4rc3l05.my_flux.core.actions.asyncActions;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.m4rc3l05.my_flux.core.actions.InitTodosAction;
import com.m4rc3l05.my_flux.db.DBHelper;
import com.m4rc3l05.my_flux.core.Dispatcher;
import com.m4rc3l05.my_flux.core.models.Todo;
import com.m4rc3l05.my_flux.adapters.TodosRecyclerViewAdapter;

import java.util.List;



public class PerformFetchAllTodos implements AsyncAction {
    private Context ctx;
    private TodosRecyclerViewAdapter mAdapter;

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


    private PerformFetchAllTodos(Context ctx, TodosRecyclerViewAdapter todosRecyclerViewAdapter) {
        this.ctx = ctx;
        this.mAdapter = todosRecyclerViewAdapter;
    }

    public static PerformFetchAllTodos create(Context ctx, TodosRecyclerViewAdapter mAdapter) {
        return new PerformFetchAllTodos(ctx, mAdapter);
    }



    @Override
    public void doWork(Dispatcher dispatcher) {
        new Handler().postDelayed(() -> {
            try {
                List<Todo> todos = new PerformFetchAllTodos.FetchTodos().execute(ctx).get();
                dispatcher.dispatch(InitTodosAction.create(todos));
                mAdapter.notifyDataSetChanged();
            } catch(Exception e) {

            }
        }, 2000);
    }
}
