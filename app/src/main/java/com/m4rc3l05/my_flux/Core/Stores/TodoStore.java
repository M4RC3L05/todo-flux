package com.m4rc3l05.my_flux.Core.Stores;

import com.m4rc3l05.my_flux.Core.Actions.AddTodoAction;
import com.m4rc3l05.my_flux.Core.Actions.BaseAction;
import com.m4rc3l05.my_flux.Core.Actions.InitTodosAction;
import com.m4rc3l05.my_flux.Core.Actions.RemoveTodoAction;
import com.m4rc3l05.my_flux.Core.Actions.ToggleDoneTodoAction;
import com.m4rc3l05.my_flux.Core.Actions.UndoRemoveTodoAction;
import com.m4rc3l05.my_flux.Core.Actions.UpdateTodoAction;
import com.m4rc3l05.my_flux.Core.Models.Todo;

import java.util.ArrayList;
import java.util.List;

public class TodoStore extends Store<TodoState> {

    private TodoStore() {
        super();
    }

    public static TodoStore create() {
        return new TodoStore();
    }

    @Override
    protected TodoState getInitialState() {
        return TodoState.create(new ArrayList<>(), true);
    }

    @Override
    public TodoState reduce(TodoState state, BaseAction action) {

        if (action instanceof AddTodoAction) {
            List<Todo> tmpTodos = new ArrayList<>();
            tmpTodos.add(((AddTodoAction) action).todo);
            tmpTodos.addAll(this._state.todos);

            return TodoState.create(tmpTodos, this._state.isLoading);

        } else if (action instanceof RemoveTodoAction) {
            List<Todo> tmpTodos = new ArrayList<>();

            for (Todo t: this._state.todos) {
                if (t.get_id().equals(((RemoveTodoAction) action).todoId)) continue;
                tmpTodos.add(t);
            }
            return TodoState.create(tmpTodos, this._state.isLoading);

        } else if (action instanceof UndoRemoveTodoAction) {
            List<Todo> tmpTodos = new ArrayList<>(this._state.todos);

            if (tmpTodos.size() <= 0)
                tmpTodos.add(((UndoRemoveTodoAction) action).todo);
            else
                tmpTodos.add(((UndoRemoveTodoAction) action).pos, ((UndoRemoveTodoAction) action).todo);

            return TodoState.create(tmpTodos,this._state.isLoading);
        } else if (action instanceof ToggleDoneTodoAction) {
            List<Todo> tmpTodos = new ArrayList<Todo>();

            for (Todo t: this._state.todos) {
                if (t.get_id().equals(((ToggleDoneTodoAction) action).todoId)) {
                    Todo newT = Todo.create(t.get_id(), t.get_text(), !t.is_isDone(), t.get_timestamp());
                    tmpTodos.add(newT);
                } else tmpTodos.add(t);
            }

            return TodoState.create(tmpTodos, this._state.isLoading);

        } else if(action instanceof UpdateTodoAction) {
            List<Todo> tmpTodos = new ArrayList<Todo>();

            for (Todo t: this._state.todos) {
                if (t.get_id().equals(((UpdateTodoAction) action).refId)) {
                    tmpTodos.add(((UpdateTodoAction) action).newTodo);
                } else tmpTodos.add(t);
            }

            return TodoState.create(tmpTodos,this._state.isLoading);
        } else if(action instanceof InitTodosAction) {
            return TodoState.create(((InitTodosAction) action).todos,false);
        } else return this._state;
    }
}
