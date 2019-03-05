package com.m4rc3l05.my_flux.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.m4rc3l05.my_flux.Container;
import com.m4rc3l05.my_flux.R;
import com.m4rc3l05.my_flux.core.Dispatcher;
import com.m4rc3l05.my_flux.core.IView;
import com.m4rc3l05.my_flux.core.actions.AuthUserChangeAction;
import com.m4rc3l05.my_flux.core.actions.asyncActions.PerformRegisterAction;
import com.m4rc3l05.my_flux.core.stores.AuthStore;
import com.m4rc3l05.my_flux.core.stores.states.AuthState;
import com.m4rc3l05.my_flux.models.AuthFrase;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RegisterActivity extends AppCompatActivity implements IView {

    TextView authFrasesDisplay;
    Button btnRegister;
    EditText emailInput;
    EditText passwordInput;
    EditText usernameInput;
    TextView txtRegisterSwitch;
    TextView txtAuthErrorDisplay;

    List<AuthFrase> authFrazes;
    Timer authFrasesTimer;
    Dispatcher dispatcher;
    AuthStore authStore;

    FirebaseAuth fAuth;
    Container container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            this._goToTodosActivity();
        }

        setContentView(R.layout.activity_register);

        this.setUpDependencies();
        this.setUpUI();
        this.setUpListeners();
        this.render();
    }

    private void setUpDependencies() {
        this.authFrazes = new ArrayList<>();
        this.authFrazes
                .add(AuthFrase.create("Manage tasks", "#FF4DD9"));
        this.authFrazes
                .add(AuthFrase.create("Store notes","#00B9E5"));
        this.authFrazes
                .add(AuthFrase.create("Personal to-do keeper","#F5D22A"));
        this.authFrazes
                .add(AuthFrase.create("Save you thoughts", "#1DB954"));

        this.container = Container.getInstance();

        this.dispatcher = (Dispatcher) this.container.get(Dispatcher.class.toString());
        this.authStore =  (AuthStore) this.container.get(AuthStore.class.toString());
        this.fAuth = FirebaseAuth.getInstance();

        this.authFrasesTimer = new Timer();
    }

    private void setUpListeners() {

        this.authFrasesTimer.scheduleAtFixedRate(new TimerTask() {
            private int curr = 0;

            @Override
            public void run() {
                AuthFrase frase = authFrazes.get(curr);

                curr = (curr + 1) % authFrazes.size();

                runOnUiThread(() -> {

                    AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
                    alphaAnimation.setDuration(200);
                    alphaAnimation.setRepeatCount(1);
                    alphaAnimation.setRepeatMode(Animation.REVERSE);

                    alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            authFrasesDisplay.setTextColor(Color.parseColor(frase.color));
                            authFrasesDisplay.setText(frase.msg);
                        }
                    });

                    authFrasesDisplay.startAnimation(alphaAnimation);
                });
            }
        }, 0, 5000);

        this.btnRegister.setOnClickListener(e -> {
            this.dispatcher.dispatch(PerformRegisterAction.create(
                    this.emailInput.getText().toString(),
                    this.usernameInput.getText().toString(),
                    this.passwordInput.getText().toString(),
                    this.fAuth
            ));
        });

        this.txtRegisterSwitch.setOnClickListener(e -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.authFrasesTimer.cancel();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.authStore.unsubscribe(this);
        this.dispatcher.unsubscribe(this.authStore);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.authStore.subscribe(this);
        this.dispatcher.subscribe(this.authStore);

        this.dispatcher.dispatch(AuthUserChangeAction.create(fAuth.getCurrentUser()));
    }

    private void setUpUI() {
        this.authFrasesDisplay = findViewById(R.id.authFrasesDisplay);
        this.btnRegister = findViewById(R.id.btnRegister);
        this.passwordInput = findViewById(R.id.passwordInput);
        this.emailInput = findViewById(R.id.emailInput);
        this.usernameInput = findViewById(R.id.usernameInput);
        this.txtRegisterSwitch = findViewById(R.id.txtRegisterSwitch);
        this.txtAuthErrorDisplay = findViewById(R.id.txtAuthErrorDisplay);
    }

    private void _goToTodosActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void render() {
        AuthState authState = this.authStore.getState();

        if (!authState.isPerformAuth && authState.authUser != null) {
            this._goToTodosActivity();
            return;
        }

        this.btnRegister.setEnabled(!authState.isPerformAuth);
        this.usernameInput.setEnabled(!authState.isPerformAuth);
        this.emailInput.setEnabled(!authState.isPerformAuth);
        this.passwordInput.setEnabled(!authState.isPerformAuth);

        txtAuthErrorDisplay.setText(authState.error);

    }
}
