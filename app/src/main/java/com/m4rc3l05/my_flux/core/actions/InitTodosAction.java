package com.m4rc3l05.my_flux.core.actions;

import com.m4rc3l05.my_flux.models.Todo;

import java.util.List;

public class InitTodosAction extends BaseAction {
    public final List<Todo> todos;

    private InitTodosAction(List<Todo> todos) {
        this.todos = todos;
    }

    public static InitTodosAction create(List<Todo> todos) {
        return new InitTodosAction(todos);
    }
}
