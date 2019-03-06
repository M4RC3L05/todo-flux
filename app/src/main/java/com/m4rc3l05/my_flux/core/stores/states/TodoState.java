package com.m4rc3l05.my_flux.core.stores.states;

import com.m4rc3l05.my_flux.models.Todo;

import java.util.List;

public class TodoState {
    public final List<Todo> todos;
    public final boolean isLoading;
    public final boolean isPerformingAction;
    public final String error;

    public TodoState(List<Todo> todos, boolean isLoading, boolean isPerformingAction, String error) {
        this.todos = todos;
        this.isLoading = isLoading;
        this.isPerformingAction = isPerformingAction;
        this.error = error;
    }

    public static TodoState create(List<Todo> todos, boolean isLoading, boolean isPerformingAction, String error) {
        return new TodoState(todos, isLoading, isPerformingAction, error);
    }
}
