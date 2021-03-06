package com.m4rc3l05.my_flux.core.actions.asyncActions;

import com.google.firebase.auth.FirebaseAuth;
import com.m4rc3l05.my_flux.core.Dispatcher;
import com.m4rc3l05.my_flux.core.actions.AuthErrorAction;
import com.m4rc3l05.my_flux.core.actions.AuthUserChangeAction;
import com.m4rc3l05.my_flux.core.actions.StartAuthAction;

public class PerformLoginAction extends BaseAsyncAction {
    private final String email;
    private final String password;
    private final FirebaseAuth fAuth;


    public PerformLoginAction(String email, String password, FirebaseAuth fAuth) {
        this.email = email;
        this.password = password;
        this.fAuth = fAuth;
    }

    public static PerformLoginAction create(String email, String password, FirebaseAuth fAuth) {
        return new PerformLoginAction(email, password, fAuth);
    }

    @Override
    public void doWork(Dispatcher dispatcher) {
        try {
            dispatcher.dispatch(StartAuthAction.create());
            fAuth.signInWithEmailAndPassword(this.email, this.password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dispatcher.dispatch(AuthUserChangeAction.create(fAuth.getCurrentUser()));
                            __notify(true);
                        } else {
                            dispatcher.dispatch(AuthErrorAction.create(task.getException().getMessage()));
                            __notify(false);
                        }
                    });
        } catch (Exception e) {
            dispatcher.dispatch(AuthErrorAction.create("Could not login"));
            __notify(false);
        }
    }
}
