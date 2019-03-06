package com.m4rc3l05.my_flux.core.stores;

import com.m4rc3l05.my_flux.core.actions.AuthErrorAction;
import com.m4rc3l05.my_flux.core.actions.AuthUserChangeAction;
import com.m4rc3l05.my_flux.core.actions.BaseAction;
import com.m4rc3l05.my_flux.core.actions.StartAuthAction;
import com.m4rc3l05.my_flux.core.stores.states.AuthState;


public class AuthStore extends Store<AuthState> {

    public static AuthStore create() {
        return new AuthStore();
    }

    @Override
    protected AuthState getInitialState() {
        return AuthState.create(null, false, null);
    }

    @Override
    protected AuthState reduce(AuthState state, BaseAction action) {
        if (action instanceof StartAuthAction) {
            return AuthState.create(state.authUser, true, null);
        } else if (action instanceof AuthUserChangeAction) {
            return AuthState.create(((AuthUserChangeAction) action).user, false, state.error);
        } else if (action instanceof AuthErrorAction) {
            return AuthState.create(null, false, ((AuthErrorAction) action).error);
        }else {
            return state;
        }
    }
}
