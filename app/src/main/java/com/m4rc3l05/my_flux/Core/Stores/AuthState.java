package com.m4rc3l05.my_flux.Core.Stores;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class AuthState {
    public final FirebaseUser authUser;
    public final boolean isPerformAuth;
    public final String error;

    private AuthState(FirebaseUser user, boolean isPerformAuth, String error) {
        this.authUser = user;
        this.isPerformAuth = isPerformAuth;
        this.error = error;
    }

    public static AuthState create(FirebaseUser user, boolean isPerformAuth, String error) {
        return new AuthState(user, isPerformAuth, error);
    }
}
