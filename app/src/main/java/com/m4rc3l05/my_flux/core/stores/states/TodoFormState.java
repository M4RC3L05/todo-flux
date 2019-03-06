package com.m4rc3l05.my_flux.core.stores.states;

public class TodoFormState {
    public final String todoText;

    public TodoFormState(String todoText) {
        this.todoText = todoText;
    }

    public static TodoFormState create(String todoText) {
        return new TodoFormState(todoText);
    }
}
