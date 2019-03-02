package com.m4rc3l05.my_flux.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.m4rc3l05.my_flux.Container;
import com.m4rc3l05.my_flux.Core.Actions.AuthUserChangeAction;
import com.m4rc3l05.my_flux.Core.Actions.StartAuthAction;
import com.m4rc3l05.my_flux.Core.Dispatcher;
import com.m4rc3l05.my_flux.Core.IView;
import com.m4rc3l05.my_flux.Core.Models.AuthFrase;
import com.m4rc3l05.my_flux.Core.Stores.AuthState;
import com.m4rc3l05.my_flux.Core.Stores.AuthStore;
import com.m4rc3l05.my_flux.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class RegisterActivity extends AppCompatActivity implements IView {

    TextView authFrasesDisplay;
    Button btnRegister;
    EditText emailInput;
    EditText passwordInput;
    EditText usernameInput;

    List<AuthFrase> authFrazes;
    Timer authFrasesTimer;
    Dispatcher dispatcher;
    AuthStore authStore;

    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.dispatcher = Container.dispatcher;
        this.authStore = Container.authStore;

        this.authStore.subscribe(this);
        this.dispatcher.subscribe(authStore);

        /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }*/

        setContentView(R.layout.activity_register);

        this.authFrazes = new ArrayList<>();
        this.authFrazes
                .add(AuthFrase.create("Manage tasks", "#FF4DD9"));
        this.authFrazes
                .add(AuthFrase.create("Store notes","#00B9E5"));
        this.authFrazes
                .add(AuthFrase.create("Personal to-do keeper","#F5D22A"));
        this.authFrazes
                .add(AuthFrase.create("Save you thoughts", "#1DB954"));

        this.setUpUI();
        this.setUpListeners();
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

        this.btnRegister.setOnClickListener(e -> {
            this.dispatcher.dispatch(StartAuthAction.create());

            String email = this.emailInput.getText().toString();
            String password = this.passwordInput.getText().toString();

            fAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            Objects.requireNonNull(fAuth.getCurrentUser())
                                    .updateProfile((new UserProfileChangeRequest.Builder()).setDisplayName(usernameInput.getText().toString()).build())
                                    .addOnCompleteListener(task1 -> this.dispatcher.dispatch(AuthUserChangeAction.create(fAuth.getCurrentUser())));
                        } else {
                            this.dispatcher.dispatch(AuthUserChangeAction.create(fAuth.getCurrentUser()));
                        }
                    });
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        authFrasesTimer.cancel();
    }

    private void setUpUI() {
        this.authFrasesDisplay = findViewById(R.id.authFrasesDisplay);
        this.btnRegister = findViewById(R.id.btnRegister);
        this.passwordInput = findViewById(R.id.passwordInput);
        this.emailInput = findViewById(R.id.emailInput);
        this.usernameInput = findViewById(R.id.usernameInput);

        this.fAuth = FirebaseAuth.getInstance();
    }

    private void _goToTodosActivity() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void render() {
        AuthState authState = this.authStore.getState();

        this.btnRegister.setActivated(!authState.isPerformAuth);

        if (!authState.isPerformAuth && authState.authUser != null) {
            this._goToTodosActivity();
            return;
        }

    }
}
