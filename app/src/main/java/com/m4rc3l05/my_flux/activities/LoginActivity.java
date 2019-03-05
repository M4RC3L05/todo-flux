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
import com.m4rc3l05.my_flux.core.actions.asyncActions.PerformLoginAction;
import com.m4rc3l05.my_flux.core.stores.AuthStore;
import com.m4rc3l05.my_flux.core.stores.states.AuthState;
import com.m4rc3l05.my_flux.models.AuthFrase;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity implements IView {
    TextView authFrasesDisplay;
    Button btnLogin;
    TextView txtLoginSwitch;
    EditText passwordInput;
    EditText emailInput;
    TextView txtAuthErrorDisplay;

    List<AuthFrase> authFrazes;
    Timer authFrasesTimer;
    AuthStore authStore;
    Dispatcher dispatcher;
    FirebaseAuth fAuth;
    Container container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            this._goToTodosActivity();
        }

        setContentView(R.layout.activity_login);

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
        this.authStore = (AuthStore) this.container.get(AuthStore.class.toString());
        this.dispatcher = (Dispatcher) this.container.get(Dispatcher.class.toString());
        this.fAuth = FirebaseAuth.getInstance();
    }

    private void setUpListeners() {

        authFrasesTimer = new Timer();

        authFrasesTimer.scheduleAtFixedRate(new TimerTask() {
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

        this.btnLogin.setOnClickListener(e -> this.dispatcher.dispatch(PerformLoginAction.create(this.emailInput.getText().toString(), this.passwordInput.getText().toString(), fAuth)));

        this.txtLoginSwitch.setOnClickListener(e -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        authFrasesTimer.cancel();
    }

    private void setUpUI() {
        this.authFrasesDisplay = findViewById(R.id.authFrasesDisplay);
        this.btnLogin = findViewById(R.id.btnLogin);
        this.txtLoginSwitch = findViewById(R.id.txtLoginSwitch);
        this.passwordInput = findViewById(R.id.passwordInput);
        this.emailInput = findViewById(R.id.emailInput);
        this.txtAuthErrorDisplay = findViewById(R.id.txtAuthErrorDisplay);
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


    private void _goToTodosActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void render() {
        AuthState authState = this.authStore.getState();
        System.out.println("update " + authState.authUser);

        if (!authState.isPerformAuth && authState.authUser != null) {
            this._goToTodosActivity();
            return;
        }

        this.emailInput.setEnabled(!authState.isPerformAuth);
        this.passwordInput.setEnabled(!authState.isPerformAuth);
        this.btnLogin.setEnabled(!authState.isPerformAuth);

        this.txtAuthErrorDisplay.setText(authState.error);
    }
}
