package com.m4rc3l05.my_flux.core.actions.asyncActions;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.m4rc3l05.my_flux.core.actions.BaseAction;
import com.m4rc3l05.my_flux.core.actions.InitTodosAction;
import com.m4rc3l05.my_flux.core.actions.StartPerformTodoAction;
import com.m4rc3l05.my_flux.core.Dispatcher;
import com.m4rc3l05.my_flux.core.models.Todo;
import com.m4rc3l05.my_flux.adapters.TodosRecyclerViewAdapter;

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


    private PerformFetchAllTodos(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    public static PerformFetchAllTodos create(DatabaseReference databaseReference) {
        return new PerformFetchAllTodos(databaseReference);
    }

    @Override
    public void doWork(Dispatcher dispatcher) {
        dispatcher.dispatch(StartPerformTodoAction.create());
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

                    }
                });
    }
}
