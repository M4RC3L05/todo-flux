package com.m4rc3l05.my_flux.Core.Actions;

public class AuthErrorAction implements BaseAction {
    public final String error;

    private AuthErrorAction(String error) {
        this.error = error;
    }

    public static AuthErrorAction create(String error) {
        return new AuthErrorAction(error);
    }
}
