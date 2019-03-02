package com.m4rc3l05.my_flux.Core.Stores;

import com.google.firebase.auth.FirebaseUser;

public class AuthState {
    public final FirebaseUser authUser;
    public final boolean isPerformAuth;

    private AuthState(FirebaseUser user, boolean isPerformAuth) {
        this.authUser = user;
        this.isPerformAuth = isPerformAuth;
    }

    public static AuthState create(FirebaseUser user, boolean isPerformAuth) {
        return new AuthState(user, isPerformAuth);
    }
}
