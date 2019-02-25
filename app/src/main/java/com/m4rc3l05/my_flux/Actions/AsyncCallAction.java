package com.m4rc3l05.my_flux.Actions;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import com.m4rc3l05.my_flux.DBHelper;
import com.m4rc3l05.my_flux.Dispatcher;
import com.m4rc3l05.my_flux.Models.Todo;
import com.m4rc3l05.my_flux.MyAdapter;

import java.util.List;


class FetchTodos extends AsyncTask<Context, Void, List<Todo>> {

    @Override
    protected List<Todo> doInBackground(Context... args) {
        try {
            return DBHelper.create(args[0]).getAllTodos();
        } catch (Exception e) {
            return null;
        }
    }
}
public class AsyncCallAction implements AsyncAction {
    private Context ctx;
    private MyAdapter mAdapter;


    private AsyncCallAction(Context ctx, MyAdapter myAdapter) {
        this.ctx = ctx;
        this.mAdapter = myAdapter;
    }

    public static AsyncCallAction create(Context ctx, MyAdapter mAdapter) {
        return new AsyncCallAction(ctx, mAdapter);
    }

    @Override
    public void doWork(Dispatcher dispatcher) {
        new Handler().postDelayed(() -> {
            try {
                List<Todo> todos = new FetchTodos().execute(ctx).get();
                dispatcher.dispatch(InitTodosAction.create(todos));
                mAdapter.notifyDataSetChanged();
            } catch(Exception e) {

            }
        }, 2000);
    }
}
