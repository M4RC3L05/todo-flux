package com.m4rc3l05.my_flux.core.stores.states;

import com.m4rc3l05.my_flux.models.Todo;

import java.util.List;

public class TodoState {
    public final List<Todo> todos;
    public final boolean isLoading;
    public final boolean isPerformingAction;

    private TodoState(List<Todo> todos, boolean isLoading, boolean isPerformingAction) {
        this.todos = todos;
        this.isLoading = isLoading;
        this.isPerformingAction = isPerformingAction;
    }

    public static TodoState create(List<Todo> todos, boolean isLoading, boolean isPerformingAction) {
        return new TodoState(todos, isLoading, isPerformingAction);
    }
}
