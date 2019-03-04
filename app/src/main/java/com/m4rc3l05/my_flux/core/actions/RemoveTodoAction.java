package com.m4rc3l05.my_flux.core.actions;

public class RemoveTodoAction extends BaseAction {
    public final String todoId;

    private RemoveTodoAction(String todoId) {
        this.todoId = todoId;
    }

    public static RemoveTodoAction create(String todoId) {
        return new RemoveTodoAction(todoId);
    }
}
