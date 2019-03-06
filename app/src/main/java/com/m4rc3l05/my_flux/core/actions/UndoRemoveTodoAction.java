package com.m4rc3l05.my_flux.core.actions;

import com.m4rc3l05.my_flux.models.Todo;

public class UndoRemoveTodoAction extends BaseAction {
    public final Todo todo;
    public final int pos;

    public UndoRemoveTodoAction(Todo todo, int pos) {
        this.todo = todo;
        this.pos = pos;
    }

    public static UndoRemoveTodoAction create(Todo todo, int pos) {
        return new UndoRemoveTodoAction(todo, pos);
    }
}
