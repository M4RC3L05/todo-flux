package com.m4rc3l05.my_flux.core.stores.states;

public class RegisterFormState {
    public final String username;
    public final String email;
    public final String password;

    public RegisterFormState(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public static RegisterFormState create(String username, String email, String password) {
        return  new RegisterFormState(username, email, password);
    }
}
