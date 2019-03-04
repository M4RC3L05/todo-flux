package com.m4rc3l05.my_flux.core.actions;

import com.m4rc3l05.my_flux.core.models.Todo;

public class UpdateTodoAction implements BaseAction {
    public final Todo newTodo;
    public final String refId;

    private UpdateTodoAction(Todo newTodo, String refId) {
        this.newTodo = newTodo;
        this.refId = refId;
    }

    public static UpdateTodoAction create(Todo newTodo, String refId) {
        return new UpdateTodoAction(newTodo, refId);
    }
}
