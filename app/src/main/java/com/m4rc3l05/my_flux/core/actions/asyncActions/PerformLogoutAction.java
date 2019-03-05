package com.m4rc3l05.my_flux.core.actions.asyncActions;

import com.google.firebase.auth.FirebaseAuth;
import com.m4rc3l05.my_flux.core.Dispatcher;
import com.m4rc3l05.my_flux.core.actions.AuthUserChangeAction;
import com.m4rc3l05.my_flux.core.actions.StartAuthAction;

public class PerformLogoutAction extends BaseAsyncAction {

    private final FirebaseAuth fAuth;

    private PerformLogoutAction(FirebaseAuth fAuth) {
        super();

        this.fAuth = fAuth;
    }

    public static PerformLogoutAction create(FirebaseAuth fAuth) {
        return new PerformLogoutAction(fAuth);
    }

    @Override
    public void doWork(Dispatcher dispatcher) {
        dispatcher.dispatch(StartAuthAction.create());
        fAuth.signOut();
        dispatcher.dispatch(AuthUserChangeAction.create(null));
        this.__notify(true);
    }
}
