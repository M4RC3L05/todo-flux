package com.m4rc3l05.my_flux.core.actions;

import com.google.firebase.auth.FirebaseUser;

public class AuthUserChangeAction extends BaseAction {
    public final FirebaseUser user;

    private AuthUserChangeAction(FirebaseUser user) {
        this.user = user;
    }

    public static AuthUserChangeAction create(FirebaseUser user) {
        return new AuthUserChangeAction(user);
    }

}
