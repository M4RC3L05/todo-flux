package com.m4rc3l05.my_flux.models;

public class AuthFrase {
    public final String msg;
    public final String color;


    private AuthFrase(String msg, String color) {
        this.msg = msg;
        this.color = color;
    }

    public static AuthFrase create(String msg, String color) {
        return new AuthFrase(msg, color);
    }
}
