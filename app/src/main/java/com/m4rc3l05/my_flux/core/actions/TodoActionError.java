package com.m4rc3l05.my_flux.core.actions;

public class TodoActionError extends BaseAction {
    public final String error;

    private TodoActionError(String error) {
        this.error = error;
    }

    public static TodoActionError create(String error) {
        return new TodoActionError(error);
    }
}
