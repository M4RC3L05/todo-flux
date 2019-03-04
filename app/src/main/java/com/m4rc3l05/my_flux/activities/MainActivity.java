package com.m4rc3l05.my_flux.activities;


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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.m4rc3l05.my_flux.Container;
import com.m4rc3l05.my_flux.core.actions.asyncActions.PerformDeleteTodoAction;
import com.m4rc3l05.my_flux.core.actions.asyncActions.PerformFetchAllTodos;
import com.m4rc3l05.my_flux.core.actions.asyncActions.PerformAddTodoAction;
import com.m4rc3l05.my_flux.core.stores.AuthStore;
import com.m4rc3l05.my_flux.db.DBHelper;
import com.m4rc3l05.my_flux.core.Dispatcher;
import com.m4rc3l05.my_flux.core.IView;
import com.m4rc3l05.my_flux.core.models.Todo;
import com.m4rc3l05.my_flux.adapters.TodosRecyclerViewAdapter;
import com.m4rc3l05.my_flux.R;
import com.m4rc3l05.my_flux.core.stores.TodoState;
import com.m4rc3l05.my_flux.core.stores.TodoStore;

import org.json.JSONObject;

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
    public DBHelper dbHelper;
    public ProgressBar loadingTodosSpinner;
    public EditText newTodoTextInput;
    public FrameLayout addNewTodoBtn;
    public FirebaseDatabase fDatabase;
    public DatabaseReference databaseReference;
    // public ImageButton scrollTopBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setUpDependencies();
        this.setUpUI();
        this.setUpListeners();

        this.dispatcher.dispatch(
                PerformFetchAllTodos.create(this)
                    .subscribe(success -> {
                        if (!success) return;

                        mAdapter.notifyDataSetChanged();
                    })
        );

        this.render();
    }

    private void setUpDependencies() {
        this.dbHelper = DBHelper.create(this);
        this.dispatcher = Container.dispatcher;
        this.todoStore = Container.todoStore;
        this.authStore = Container.authStore;
        this.fDatabase = FirebaseDatabase.getInstance();
        this.databaseReference = this.fDatabase.getReference("todos").child(authStore.getState().authUser.getUid());
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

        /* this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private boolean isAnimating = false;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE && recyclerView.computeVerticalScrollOffset() < 80) {
                    findViewById(R.id.cardViewAddTodoForm)
                            .animate()
                            .alpha(1)
                            .setInterpolator(t -> {
                                if ((t *= 2) < 1) {
                                    return (float) (0.5 * Math.pow(t, 4));
                                }

                                return (float) (1 - 0.5 * Math.abs(Math.pow(2 - t, 4)));
                            })
                            .setDuration(500)
                            .start();


                    // scrollTopBtn.setVisibility(View.GONE);
                    scrollTopBtn
                            .animate()
                            .scaleX(0f)
                            .scaleY(0f)
                            .setInterpolator(t -> {
                                if ((t *= 2) < 1) {
                                    return (float) (0.5 * Math.pow(t, 4));
                                }

                                return (float) (1 - 0.5 * Math.abs(Math.pow(2 - t, 4)));
                            })
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    scrollTopBtn.setVisibility(View.GONE);
                                }
                            })
                            .setDuration(500)
                            .start();
                } else {
                    findViewById(R.id.cardViewAddTodoForm)
                            .animate()
                            .alpha(0)
                            .setInterpolator(t -> {
                                if ((t *= 2) < 1) {
                                    return (float) (0.5 * Math.pow(t, 4));
                                }

                                return (float) (1 - 0.5 * Math.abs(Math.pow(2 - t, 4)));
                            })
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    isAnimating = true;
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    isAnimating = false;
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            })
                            .setDuration(500)
                            .start();

                    scrollTopBtn
                            .animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setInterpolator(t -> {
                                if ((t *= 2) < 1) {
                                    return (float) (0.5 * Math.pow(t, 4));
                                }

                                return (float) (1 - 0.5 * Math.abs(Math.pow(2 - t, 4)));
                            })
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    scrollTopBtn.setVisibility(View.VISIBLE);
                                }
                            })
                            .setDuration(500)
                            .start();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (recyclerView.computeVerticalScrollOffset() >= 80 && !isAnimating && findViewById(R.id.cardViewAddTodoForm).getAlpha() != 0f) {

                    findViewById(R.id.cardViewAddTodoForm)
                            .animate()
                            .alpha(0)
                            .setInterpolator(t -> {
                                if ((t *= 2) < 1) {
                                    return (float) (0.5 * Math.pow(t, 4));
                                }

                                return (float) (1 - 0.5 * Math.abs(Math.pow(2 - t, 4)));
                            })
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    isAnimating = true;
                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    isAnimating = false;
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            })
                            .setDuration(500)
                            .start();


                    scrollTopBtn
                            .animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .setInterpolator(t -> {
                                if ((t *= 2) < 1) {
                                    return (float) (0.5 * Math.pow(t, 4));
                                }

                                return (float) (1 - 0.5 * Math.abs(Math.pow(2 - t, 4)));
                            })
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationStart(Animator animation) {
                                    super.onAnimationStart(animation);
                                    scrollTopBtn.setVisibility(View.VISIBLE);
                                }
                            })
                            .setDuration(500)
                            .start();

                }


            }
        }); */

        /*this.scrollTopBtn.setOnClickListener(v -> {
            this.recyclerView.smoothScrollToPosition(0);
        });*/
    }

    public void onDeleteTodo(@NonNull RecyclerView.ViewHolder viewHolder) {
        Todo todo = mAdapter.getTodoAt(viewHolder.getAdapterPosition());

        int pos = viewHolder.getAdapterPosition();

        this.dispatcher.dispatch(
            PerformDeleteTodoAction.create(todo, databaseReference, dbHelper)
                .subscribe(success -> {
                    if (!success) return;
                    mAdapter.notifyItemRemoved(pos);

                    Snackbar sb = Snackbar.make(findViewById(R.id.mainlayout), "Todo Deleted", Snackbar.LENGTH_LONG)
                            .setAction("Undo", v -> {
                                dispatcher.dispatch(
                                        PerformAddTodoAction.create(todo, dbHelper, databaseReference)
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

        // this.recyclerView.setHasFixedSize(true);
        this.layoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(this.layoutManager);

        this.mAdapter = TodosRecyclerViewAdapter.create(new ArrayList<>(), this.dispatcher, this);
        this.recyclerView.setAdapter(this.mAdapter);
        // this.scrollTopBtn = findViewById(R.id.scrollTopBtn);

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
                    .create(todoToInsert, dbHelper, databaseReference)
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
        System.out.println(ts.isPerformingAction);

        this.mAdapter.setItems(ts.todos);

        this.loadingTodosSpinner.setVisibility(ts.isLoading ? View.VISIBLE : View.INVISIBLE);

        this.addNewTodoBtn.setEnabled(!ts.isLoading && !ts.isPerformingAction);
        this.newTodoTextInput.setEnabled(!ts.isLoading && !ts.isPerformingAction);
    }
}