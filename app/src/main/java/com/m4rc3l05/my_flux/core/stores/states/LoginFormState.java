package com.m4rc3l05.my_flux.core.stores.states;

public class LoginFormState {
    public final String email;
    public final String password;

    public LoginFormState(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static LoginFormState create(String email, String password) {
        return new LoginFormState(email, password);
    }
}
