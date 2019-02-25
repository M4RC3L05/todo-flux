package com.m4rc3l05.my_flux;


import android.animation.Animator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.m4rc3l05.my_flux.Actions.AddTodoAction;
import com.m4rc3l05.my_flux.Actions.AsyncCallAction;
import com.m4rc3l05.my_flux.Actions.RemoveTodoAction;
import com.m4rc3l05.my_flux.Actions.UndoRemoveTodoAction;
import com.m4rc3l05.my_flux.Models.Todo;
import com.m4rc3l05.my_flux.Stores.TodoState;
import com.m4rc3l05.my_flux.Stores.TodoStore;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements IView {

    public Dispatcher dispatcher;
    public TodoStore todoStore;
    public RecyclerView recyclerView;
    public MyAdapter mAdapter;
    public RecyclerView.LayoutManager layoutManager;
    public DBHelper dbHelper;
    public ProgressBar loadingTodosSpinner;
    public EditText newTodoTextInput;
    public FrameLayout addNewTodoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.dbHelper = DBHelper.create(this);
        this.dispatcher = Dispatcher.getInstance();
        this.todoStore = TodoStore.getInstance();
        dispatcher.subscribe(todoStore);
        todoStore.subscribe(this);

        this.setUpUI();
        this.setUpListeners();
        this.dispatcher.dispatch(AsyncCallAction.create(this, this.mAdapter));
        this.render();
    }

    private void setUpListeners() {
        this.newTodoTextInput.setOnEditorActionListener((v, actionId, event) -> {
            this.onAddNewTodo();
            return true;
        });

        this.newTodoTextInput.addTextChangedListener(new TextWatcher() {
            private boolean isOpen = false;


            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String currText = s.toString();

                if (currText.length() <= 0) {
                    addNewTodoBtn
                            .animate()
                            .setInterpolator(t -> {
                                if ((t *= 2) < 1) {
                                    return (float) (0.5 * Math.pow(t, 4));
                                }

                                return (float) (1 - 0.5 * Math.abs(Math.pow(2 - t, 4)));
                            })
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    isOpen = false;
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    isOpen = false;
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                    isOpen = false;
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            })
                            .setDuration(600)
                            .translationX(newTodoTextInput.getWidth());
                } else if(!isOpen) {
                    addNewTodoBtn
                            .animate()
                            .setInterpolator(t -> {
                                if ((t *= 2) < 1) {
                                    return (float) (0.5 * Math.pow(t, 4));
                                }

                                return (float) (1 - 0.5 * Math.abs(Math.pow(2 - t, 4)));
                            })
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    isOpen = true;
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    isOpen = true;
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                    isOpen = false;
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            })
                            .setDuration(600)
                            .translationX(0f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        this.addNewTodoBtn.setOnClickListener(l -> this.onAddNewTodo());

        ItemTouchHelper ith = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(MainActivity.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addActionIcon(R.drawable.ic_trash)
                        .addSwipeRightLabel("Delete")
                        .setSwipeRightLabelColor(Color.RED)
                        .setIconHorizontalMargin((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, getApplicationContext().getResources().getDisplayMetrics()))
                        .create()
                        .decorate();

                ColorDrawable cd = new ColorDrawable(Color.parseColor("#000000"));
                cd.setAlpha((int)(255 - ((Math.abs(dX) / recyclerView.getWidth()) * 255)));
                cd.setBounds(0, viewHolder.itemView.getTop(), viewHolder.itemView.getWidth(), viewHolder.itemView.getBottom());

                cd.draw(c);

                viewHolder.itemView.setAlpha(1-(Math.abs(dX) / recyclerView.getWidth()));

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                onDeleteTodo(viewHolder);
            }
        });

        ith.attachToRecyclerView(this.recyclerView);
    }

    public void onDeleteTodo(@NonNull RecyclerView.ViewHolder viewHolder) {
        Todo todo = mAdapter.getTodoAt(viewHolder.getAdapterPosition());
        int pos = viewHolder.getAdapterPosition();

        if (!dbHelper.deleteTodo(todo.get_id())) return;

        dispatcher.dispatch(RemoveTodoAction.create(todo.get_id()));
        mAdapter.notifyItemRemoved(pos);

        Snackbar sb = Snackbar.make(findViewById(R.id.mainlayout), "Todo Deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", v -> {

                    if (!dbHelper.addNewTodo(todo)) return;
                    dispatcher.dispatch(UndoRemoveTodoAction.create(todo, pos));
                    mAdapter.notifyItemInserted(pos);
                });

        sb.getView().setBackgroundColor(Color.WHITE);
        ((TextView) sb.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.BLACK);

        sb.show();
    }

    public void setUpUI() {
        this.recyclerView = findViewById(R.id.recylerVTodos);
        this.newTodoTextInput = findViewById(R.id.txtTodoText);
        this.loadingTodosSpinner = findViewById(R.id.loadingTodosSpinner);
        this.addNewTodoBtn = findViewById(R.id.btn_addTodo);

        // this.recyclerView.setHasFixedSize(true);
        this.layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.mAdapter = MyAdapter.create(new ArrayList<>(), this.dispatcher, this);
        this.recyclerView.setAdapter(this.mAdapter);

        findViewById(R.id.cardViewAddTodoForm).setClipToOutline(true);
    }

    public void onAddNewTodo() {
        String text = newTodoTextInput.getText().toString();

        if (text.length() <= 0) return;

        Todo todoToInsert = Todo.create(UUID.randomUUID().toString(), text, false, "" + new Timestamp(System.currentTimeMillis()).getTime());

        if (!dbHelper.addNewTodo(todoToInsert)) return;

        this.dispatcher.dispatch(AddTodoAction.create(todoToInsert));
        mAdapter.notifyItemInserted(0);
        newTodoTextInput.setText("");
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(newTodoTextInput.getWindowToken(), 0);
        this.recyclerView.smoothScrollToPosition(0);
    }

    public void render() {
        TodoState ts = this.todoStore.getState();

        this.mAdapter.setItems(ts.todos);


        this.loadingTodosSpinner.setVisibility(ts.isLoading ? View.VISIBLE : View.INVISIBLE);
    }
}
