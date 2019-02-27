package com.m4rc3l05.my_flux.Core.Actions;

import com.m4rc3l05.my_flux.Core.Models.Todo;

public class AddTodoAction implements BaseAction {
    public final Todo todo;

    private AddTodoAction(Todo todo) {
        this.todo = todo;
    }

    public static AddTodoAction create(Todo todo) {
        return new AddTodoAction(todo);
    }
}
