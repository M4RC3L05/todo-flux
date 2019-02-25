package com.m4rc3l05.my_flux.Actions;

public class ToggleDoneTodoAction implements BaseAction {
    public final String todoId;

    private ToggleDoneTodoAction(String id) {
        this.todoId = id;
    }

    public static ToggleDoneTodoAction create(String id) {
        return new ToggleDoneTodoAction(id);
    }
}
