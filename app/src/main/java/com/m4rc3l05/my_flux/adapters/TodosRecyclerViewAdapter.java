package com.m4rc3l05.my_flux.adapters;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.m4rc3l05.my_flux.Container;
import com.m4rc3l05.my_flux.core.actions.UpdateTodoAction;
import com.m4rc3l05.my_flux.activities.MainActivity;
import com.m4rc3l05.my_flux.core.actions.asyncActions.PerformUpdateTodoAction;
import com.m4rc3l05.my_flux.db.DBHelper;
import com.m4rc3l05.my_flux.core.Dispatcher;
import com.m4rc3l05.my_flux.core.models.Todo;
import com.m4rc3l05.my_flux.R;

import java.util.List;
import java.util.Objects;

public class TodosRecyclerViewAdapter extends android.support.v7.widget.RecyclerView.Adapter<TodosRecyclerViewAdapter.MyViewHolder> {
    private List<Todo> _todos;
    private Dispatcher _dispatcher;
    private Context ctx;

    private TodosRecyclerViewAdapter(List<Todo> todos, Dispatcher dispatcher, Context ctx) {
        this._todos = todos;
        this._dispatcher = dispatcher;
        this.ctx = ctx;
    }

    public static TodosRecyclerViewAdapter create(List<Todo> todos, Dispatcher dispatcher, Context ctx) {
        return new TodosRecyclerViewAdapter(todos, dispatcher, ctx);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private View view;
        private CardView cardView;
        private ImageView completedMark;

        public MyViewHolder(View tv) {
            super(tv);
            this.view = tv;
            this.completedMark = tv.findViewById(R.id.completedMark);
            this.cardView = tv.findViewById(R.id.card_view);
            this.textView = tv.findViewById(R.id.todotext);
        }

        public void bindData(Todo todo, int pos) {

            // this.cardView.setCardBackgroundColor(todo.is_isDone() ? Color.parseColor("#66bb6a") : Color.parseColor("#FFFFFF"));
            this.completedMark.setColorFilter(Color.parseColor("#1DB954"));
            this.completedMark.setVisibility(todo.is_isDone() ? View.VISIBLE : View.GONE);
            this.textView.setText(todo.get_text());

            if (todo.is_isDone()) this.textView.setPaintFlags(Paint.ANTI_ALIAS_FLAG | Paint.STRIKE_THRU_TEXT_FLAG);
            else this.textView.setPaintFlags(Paint.ANTI_ALIAS_FLAG);

            this.view.setOnLongClickListener(v -> {
                Todo updatedTodo = Todo.create(todo.get_id(), todo.get_text(), !todo.is_isDone(), todo.get_timestamp());
                DBHelper dbHelper = DBHelper.create(ctx);

                _dispatcher.dispatch(
                        PerformUpdateTodoAction.create(updatedTodo, dbHelper, FirebaseDatabase.getInstance().getReference("todos").child(Container.authStore.getState().authUser.getUid()))
                            .subscribe(success -> {
                                if (!success) return;
                                notifyItemChanged(getAdapterPosition());
                            })
                );

                return true;
            });

            this.view.setOnClickListener(v -> {
                Dialog d = new Dialog(ctx);
                LayoutInflater inflater = ((MainActivity) ctx).getLayoutInflater();
                View view = inflater.inflate(R.layout.edit_todo_dialog, null);
                Button editBtn = view.findViewById(R.id.confirmEditTodoBtn);
                EditText todoText = view.findViewById(R.id.newTodoTextInput);
                todoText.setText(todo.get_text());

                editBtn.setOnClickListener(l -> {
                    String text = todoText.getText().toString().trim();

                    if (text.length() <= 0) return;

                    Todo updatedTodo = Todo.create(todo.get_id(), text, todo.is_isDone(), todo.get_timestamp());

                    _dispatcher.dispatch(
                            PerformUpdateTodoAction.create(updatedTodo, DBHelper.create(ctx), FirebaseDatabase.getInstance().getReference("todos").child(Container.authStore.getState().authUser.getUid()))
                                .subscribe(success -> {
                                    if (!success) return;

                                    todoText.setText("");
                                    notifyItemChanged(getAdapterPosition());
                                    InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow((Objects.requireNonNull(((MainActivity) ctx).getCurrentFocus())).getWindowToken(), 0);
                                    d.cancel();
                                })
                    );

                });

                d.setContentView(view);

                Objects.requireNonNull(d
                        .getWindow())
                        .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                d.show();


                todoText.requestFocus();
            });
        }

    }

    @NonNull
    @Override
    public TodosRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View tv = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.recycler_todo_item, viewGroup, false);

        MyViewHolder vh = new MyViewHolder(tv);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, int i) {
        viewHolder.itemView.setTag(viewHolder);
        viewHolder.bindData(this._todos.get(i), i);
    }



    @Override
    public int getItemCount() {
        return this._todos.size();
    }

    public void setItems(List<Todo> todos) {
        this._todos = todos;
    }

    public Todo getTodoAt(int pos) {
        return this._todos.get(pos);
    }
}