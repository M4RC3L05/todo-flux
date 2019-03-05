package com.m4rc3l05.my_flux.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.m4rc3l05.my_flux.Container;
import com.m4rc3l05.my_flux.R;
import com.m4rc3l05.my_flux.core.Dispatcher;
import com.m4rc3l05.my_flux.core.IView;
import com.m4rc3l05.my_flux.core.stores.AuthStore;
import com.m4rc3l05.my_flux.core.stores.states.AuthState;

public class UserProfileActivity extends AppCompatActivity implements IView {

    ImageView profileImageDisplay;
    TextView txtUsernameDisplay;
    Button btnLogout;

    Container container;
    AuthStore authStore;
    Dispatcher dispatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            this._goToLoginActivity();
        }

        setContentView(R.layout.activity_user_profile);

        this.setUpDependencies();
        this.setUpUI();
        this.setUpListeners();
        this.render();
    }

    private void setUpListeners() {
        this.btnLogout.setOnClickListener(e -> {
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT)
                    .show();
        });
    }

    private void setUpUI() {
        this.profileImageDisplay = findViewById(R.id.profileImageDisplay);
        this.txtUsernameDisplay = findViewById(R.id.txtUsernameDisplay);
        this.btnLogout = findViewById(R.id.btnLogout);
    }

    private void setUpDependencies() {
        this.container = Container.getInstance();
        this.authStore = (AuthStore) this.container.get(AuthStore.class.toString());
        this.authStore.unsubscribe(this);
        this.dispatcher = (Dispatcher) this.container.get(Dispatcher.class.toString());
        this.dispatcher.subscribe(authStore);
    }

    private void _goToLoginActivity() {
        Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.authStore.unsubscribe(this);

        this.dispatcher.unsubscribe(authStore);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.authStore.subscribe(this);

        this.dispatcher.subscribe(authStore);
    }

    @Override
    public void render() {
        AuthState authState = this.authStore.getState();


        if (authState.authUser.getPhotoUrl() == null) {
            this.profileImageDisplay.setImageDrawable(getDrawable(R.drawable.ic_user));
            this.profileImageDisplay.setColorFilter(Color.parseColor("#ffffff"));
        } else {
            this.profileImageDisplay.setImageURI(authState.authUser.getPhotoUrl());
            this.profileImageDisplay.clearColorFilter();
        }

        this.txtUsernameDisplay.setText(authState.authUser.getDisplayName());
    }
}
