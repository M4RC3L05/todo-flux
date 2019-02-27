package com.m4rc3l05.my_flux.Core.Stores;

import com.m4rc3l05.my_flux.Core.Models.Todo;

import java.util.List;

public class TodoState {
    public final List<Todo> todos;
    public final boolean isLoading;

    private TodoState(List<Todo> todos, boolean isLoading) {
        this.todos = todos;
        this.isLoading = isLoading;
    }

    public static TodoState create(List<Todo> todos, boolean isLoading) {
        return new TodoState(todos, isLoading);
    }
}
