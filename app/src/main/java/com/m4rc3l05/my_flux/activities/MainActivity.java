package com.m4rc3l05.my_flux.activities;


import android.animation.Animator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.m4rc3l05.my_flux.Container;
import com.m4rc3l05.my_flux.core.actions.asyncActions.PerformDeleteTodoAction;
import com.m4rc3l05.my_flux.core.actions.asyncActions.PerformFetchAllTodos;
import com.m4rc3l05.my_flux.core.actions.asyncActions.PerformAddTodoAction;
import com.m4rc3l05.my_flux.core.stores.AuthStore;
import com.m4rc3l05.my_flux.core.Dispatcher;
import com.m4rc3l05.my_flux.core.IView;
import com.m4rc3l05.my_flux.models.Todo;
import com.m4rc3l05.my_flux.adapters.TodosRecyclerViewAdapter;
import com.m4rc3l05.my_flux.R;
import com.m4rc3l05.my_flux.core.stores.states.TodoState;
import com.m4rc3l05.my_flux.core.stores.TodoStore;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements IView {

    public Dispatcher dispatcher;
    public TodoStore todoStore;
    public AuthStore authStore;
    public RecyclerView recyclerView;
    public TodosRecyclerViewAdapter mAdapter;
    public RecyclerView.LayoutManager layoutManager;
    public ProgressBar loadingTodosSpinner;
    public EditText newTodoTextInput;
    public ConstraintLayout addNewTodoBtn;
    public FirebaseDatabase fDatabase;
    public DatabaseReference databaseReference;
    ProgressBar loaderIndicatorTodoAction;
    TextView btnAddTodoText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setUpDependencies();
        this.setUpUI();
        this.setUpListeners();

        this.dispatcher.dispatch(
                PerformFetchAllTodos.create(this.databaseReference)
                .subscribe(success -> mAdapter.notifyDataSetChanged())
        );

        this.render();
    }

    private void setUpDependencies() {
        this.dispatcher = Container.dispatcher;
        this.todoStore = Container.todoStore;
        this.authStore = Container.authStore;
        this.fDatabase = FirebaseDatabase.getInstance();
        this.fDatabase.setPersistenceEnabled(true);
        this.databaseReference = this.fDatabase.getReference("todos").child(authStore.getState().authUser.getUid());
        this.databaseReference.keepSynced(true);
    }

    private void setUpListeners() {
        this.newTodoTextInput.setOnEditorActionListener((v, actionId, event) -> {
            this.onAddNewTodo();
            return true;
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

            @Override
            public boolean isItemViewSwipeEnabled() {
                return !todoStore.getState().isLoading && !todoStore.getState().isPerformingAction;
            }
        });

        ith.attachToRecyclerView(this.recyclerView);
    }

    public void onDeleteTodo(@NonNull RecyclerView.ViewHolder viewHolder) {
        Todo todo = mAdapter.getTodoAt(viewHolder.getAdapterPosition());

        int pos = viewHolder.getAdapterPosition();

        this.dispatcher.dispatch(
            PerformDeleteTodoAction.create(todo, databaseReference, getApplicationContext())
                .subscribe(success -> {
                    if (!success) return;
                    mAdapter.notifyItemRemoved(pos);

                    Snackbar sb = Snackbar.make(findViewById(R.id.mainlayout), "Todo Deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo", v -> {
                                dispatcher.dispatch(
                                        PerformAddTodoAction.create(getApplicationContext(), todo, databaseReference)
                                            .subscribe(success1 -> {
                                                if (!success1) return;
                                                mAdapter.notifyItemInserted(pos);
                                            })
                                );
                            });

                    sb.getView().setBackgroundColor(Color.WHITE);
                    ((TextView) sb.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.BLACK);

                    sb.show();
                })
        );
    }

    public void setUpUI() {
        this.recyclerView = findViewById(R.id.recylerVTodos);
        this.newTodoTextInput = findViewById(R.id.txtTodoText);
        this.loadingTodosSpinner = findViewById(R.id.loadingTodosSpinner);
        this.addNewTodoBtn = findViewById(R.id.btn_addTodo);
        this.loaderIndicatorTodoAction = findViewById(R.id.loaderIndicatorTodoAction);
        this.btnAddTodoText = findViewById(R.id.btnAddTodoText);

        // this.recyclerView.setHasFixedSize(true);
        this.layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.mAdapter = TodosRecyclerViewAdapter.create(new ArrayList<>(), this.dispatcher, this);
        this.recyclerView.setAdapter(this.mAdapter);

        findViewById(R.id.cardViewAddTodoForm).setClipToOutline(true);
    }

    public void onAddNewTodo() {
        String text = newTodoTextInput.getText().toString().trim();

        if (text.trim().length() <= 0) return;

        Todo todoToInsert = Todo.create(UUID.randomUUID().toString(), text, false, "" + new Timestamp(System.currentTimeMillis()).getTime());

        this
            .dispatcher
            .dispatch(
                PerformAddTodoAction
                    .create(getApplicationContext(), todoToInsert, databaseReference)
                    .subscribe(success -> {
                        if (!success) return;

                        mAdapter.notifyItemInserted(0);
                        newTodoTextInput.setText("");
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(newTodoTextInput.getWindowToken(), 0);
                        this.recyclerView.smoothScrollToPosition(0);
                    })

            );
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.todoStore.unsubscribe(this);
        this.authStore.unsubscribe(this);

        this.dispatcher.unsubscribe(todoStore);
        this.dispatcher.unsubscribe(authStore);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.todoStore.subscribe(this);
        this.authStore.subscribe(this);

        this.dispatcher.subscribe(todoStore);
        this.dispatcher.subscribe(authStore);
    }

    public void render() {
        TodoState ts = this.todoStore.getState();

        this.mAdapter.setItems(ts.todos);

        this.loadingTodosSpinner.setVisibility(ts.isLoading ? View.VISIBLE : View.INVISIBLE);

        this.addNewTodoBtn.setEnabled(!ts.isLoading && !ts.isPerformingAction);
        this.newTodoTextInput.setEnabled(!ts.isLoading && !ts.isPerformingAction);

        this.loaderIndicatorTodoAction.setVisibility(ts.isPerformingAction ? View.VISIBLE : View.GONE);
        this.btnAddTodoText.setVisibility(ts.isPerformingAction ? View.GONE : View.VISIBLE);

        this.addNewTodoBtn.setPadding(ts.isPerformingAction ? 0 : 8, 0, ts.isPerformingAction ? 8 : 0, 0);
    }
}
