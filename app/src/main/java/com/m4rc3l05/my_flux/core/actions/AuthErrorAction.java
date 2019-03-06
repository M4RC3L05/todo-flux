package com.m4rc3l05.my_flux.core.actions;

public class AuthErrorAction extends BaseAction {
    public final String error;

    public AuthErrorAction(String error) {
        this.error = error;
    }

    public static AuthErrorAction create(String error) {
        return new AuthErrorAction(error);
    }
}
