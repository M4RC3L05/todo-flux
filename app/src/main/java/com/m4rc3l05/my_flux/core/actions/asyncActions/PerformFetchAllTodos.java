package com.m4rc3l05.my_flux.core.actions.asyncActions;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.m4rc3l05.my_flux.ConnectionUtils;
import com.m4rc3l05.my_flux.core.Dispatcher;
import com.m4rc3l05.my_flux.core.actions.InitTodosAction;
import com.m4rc3l05.my_flux.core.actions.StartPerformTodoAction;
import com.m4rc3l05.my_flux.core.actions.TodoActionError;
import com.m4rc3l05.my_flux.models.Todo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class PerformFetchAllTodos extends BaseAsyncAction {
    private DatabaseReference databaseReference;
    private Context ctx;


    public PerformFetchAllTodos(Context ctx, DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
        this.ctx = ctx;
    }

    public static PerformFetchAllTodos create(Context ctx, DatabaseReference databaseReference) {
        return new PerformFetchAllTodos(ctx, databaseReference);
    }

    @Override
    public void doWork(Dispatcher dispatcher) {
        if (ConnectionUtils.isConnected(ctx)) dispatcher.dispatch(StartPerformTodoAction.create());

        this.databaseReference
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Map<String, String> todos = (Map<String, String>) dataSnapshot.getValue();
                        List<Todo> allTodos = new ArrayList<>();


                        if (todos == null || todos.values().size() <= 0) {
                            dispatcher.dispatch(
                                    InitTodosAction.create(allTodos)
                            );

                            __notify(true);
                            return;
                        }

                        JSONArray jsonArray = new JSONArray(todos.values());

                        for(int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject json = jsonArray.getJSONObject(i);
                                allTodos.add(Todo.create(
                                        json.getString("_id"),
                                        json.getString("_text"),
                                        json.getBoolean("_isDone"),
                                        json.getString("_timestamp")
                                ));
                            } catch (JSONException e) {

                            }
                        }

                        Collections.sort(allTodos, (o1, o2) -> (new Date(Long.parseLong(o1.get_timestamp()))).before(new Date(Long.parseLong(o2.get_timestamp()))) ? 1 : (new Date(Long.parseLong(o1.get_timestamp()))).after(new Date(Long.parseLong(o2.get_timestamp()))) ? -1 : 0);

                        dispatcher.dispatch(
                                InitTodosAction.create(allTodos)
                        );

                        __notify(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        dispatcher.dispatch(TodoActionError.create("Could not fetch to-dos."));
                        __notify(false);
                    }
                });
    }
}
