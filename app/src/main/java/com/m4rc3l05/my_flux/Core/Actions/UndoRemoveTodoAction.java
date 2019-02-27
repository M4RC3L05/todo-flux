package com.m4rc3l05.my_flux.Core.Actions;

import com.m4rc3l05.my_flux.Core.Models.Todo;

public class UndoRemoveTodoAction implements BaseAction {
    public final Todo todo;
    public final int pos;

    private UndoRemoveTodoAction(Todo todo, int pos) {
        this.todo = todo;
        this.pos = pos;
    }

    public static UndoRemoveTodoAction create(Todo todo, int pos) {
        return new UndoRemoveTodoAction(todo, pos);
    }
}
