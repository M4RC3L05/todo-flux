package com.m4rc3l05.my_flux.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

import com.m4rc3l05.my_flux.Core.Models.AuthFrase;
import com.m4rc3l05.my_flux.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LoginActivity extends AppCompatActivity {
    TextView authFrasesDisplay;
    Button btnLogin;
    TextView txtLoginSwitch;

    List<AuthFrase> authFrazes;
    Timer authFrasesTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.setUpDependencies();
        this.setUpUI();
        this.setUpListeners();
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

        this.btnLogin.setOnClickListener(e -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

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
    }
}
