package com.m4rc3l05.my_flux.core.stores.states;

import com.google.firebase.auth.FirebaseUser;

public class AuthState {
    public final FirebaseUser authUser;
    public final boolean isPerformAuth;
    public final String error;

    public AuthState(FirebaseUser user, boolean isPerformAuth, String error) {
        this.authUser = user;
        this.isPerformAuth = isPerformAuth;
        this.error = error;
    }

    public static AuthState create(FirebaseUser user, boolean isPerformAuth, String error) {
        return new AuthState(user, isPerformAuth, error);
    }
}
