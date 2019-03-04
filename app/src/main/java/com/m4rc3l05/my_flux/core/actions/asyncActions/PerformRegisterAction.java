package com.m4rc3l05.my_flux.core.actions.asyncActions;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.m4rc3l05.my_flux.core.actions.AuthErrorAction;
import com.m4rc3l05.my_flux.core.actions.AuthUserChangeAction;
import com.m4rc3l05.my_flux.core.actions.StartAuthAction;
import com.m4rc3l05.my_flux.core.Dispatcher;

import java.util.Objects;

public class PerformRegisterAction implements AsyncAction {

    private final String email;
    private final String username;
    private final String password;
    private final FirebaseAuth fAuth;

    private PerformRegisterAction(String email, String username, String password, FirebaseAuth fAuth) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.fAuth = fAuth;
    }

    public static PerformRegisterAction create(String email, String username, String password, FirebaseAuth fAuth) {
        return new PerformRegisterAction(email, username, password, fAuth);
    }

    @Override
    public void doWork(Dispatcher dispatcher) {
            try {
                dispatcher.dispatch(StartAuthAction.create());

                fAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                Objects.requireNonNull(fAuth.getCurrentUser())
                                        .updateProfile((new UserProfileChangeRequest.Builder()).setDisplayName(username).build())
                                        .addOnCompleteListener(task1 -> dispatcher.dispatch(AuthUserChangeAction.create(fAuth.getCurrentUser())));
                            } else
                                dispatcher.dispatch(AuthErrorAction.create(Objects.requireNonNull(task.getException()).getMessage()));
                        });
            } catch (Exception e) {
                dispatcher.dispatch(AuthErrorAction.create("Could not create account"));
            }
    }
}
