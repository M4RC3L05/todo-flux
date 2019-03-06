package com.m4rc3l05.my_flux.core.actions;

import com.m4rc3l05.my_flux.models.Todo;

public class AddTodoAction extends BaseAction {
    public final Todo todo;

    public AddTodoAction(Todo todo) {
        this.todo = todo;
    }

    public static AddTodoAction create(Todo todo) {
        return new AddTodoAction(todo);
    }
}
