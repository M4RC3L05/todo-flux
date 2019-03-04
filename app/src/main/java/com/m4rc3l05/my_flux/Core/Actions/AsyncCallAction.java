package com.m4rc3l05.my_flux.Core.Actions;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.m4rc3l05.my_flux.DB.DBHelper;
import com.m4rc3l05.my_flux.Core.Dispatcher;
import com.m4rc3l05.my_flux.Core.Models.Todo;
import com.m4rc3l05.my_flux.Adapters.TodosRecyclerViewAdapter;

import java.util.List;



public class AsyncCallAction implements AsyncAction {
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


    private AsyncCallAction(Context ctx, TodosRecyclerViewAdapter todosRecyclerViewAdapter) {
        this.ctx = ctx;
        this.mAdapter = todosRecyclerViewAdapter;
    }

    public static AsyncCallAction create(Context ctx, TodosRecyclerViewAdapter mAdapter) {
        return new AsyncCallAction(ctx, mAdapter);
    }



    @Override
    public void doWork(Dispatcher dispatcher) {
        new Handler().postDelayed(() -> {
            try {
                List<Todo> todos = new AsyncCallAction.FetchTodos().execute(ctx).get();
                dispatcher.dispatch(InitTodosAction.create(todos));
                mAdapter.notifyDataSetChanged();
            } catch(Exception e) {

            }
        }, 2000);
    }
}
