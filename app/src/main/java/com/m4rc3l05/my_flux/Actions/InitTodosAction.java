package com.m4rc3l05.my_flux.Actions;

import com.m4rc3l05.my_flux.Models.Todo;

import java.util.List;

public class InitTodosAction implements BaseAction {
    public final List<Todo> todos;

    private InitTodosAction(List<Todo> todos) {
        this.todos = todos;
    }

    public static InitTodosAction create(List<Todo> todos) {
        return new InitTodosAction(todos);
    }
}
