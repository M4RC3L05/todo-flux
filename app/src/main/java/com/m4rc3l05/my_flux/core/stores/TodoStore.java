package com.m4rc3l05.my_flux.core.stores;

import com.m4rc3l05.my_flux.core.actions.AddTodoAction;
import com.m4rc3l05.my_flux.core.actions.BaseAction;
import com.m4rc3l05.my_flux.core.actions.InitTodosAction;
import com.m4rc3l05.my_flux.core.actions.RemoveTodoAction;
import com.m4rc3l05.my_flux.core.actions.StartPerformTodoAction;
import com.m4rc3l05.my_flux.core.actions.TodoActionError;
import com.m4rc3l05.my_flux.core.actions.UndoRemoveTodoAction;
import com.m4rc3l05.my_flux.core.actions.UpdateTodoAction;
import com.m4rc3l05.my_flux.core.actions.asyncActions.PerformAddTodoAction;
import com.m4rc3l05.my_flux.core.stores.states.TodoState;
import com.m4rc3l05.my_flux.models.Todo;

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
        return TodoState.create(new ArrayList<>(), true, false, null);
    }

    @Override
    public TodoState reduce(TodoState state, BaseAction action) {

        if (action instanceof AddTodoAction) {

            if (this._state.todos.contains(((AddTodoAction) action).todo)) return TodoState.create(this._state.todos, false, false, null);

            List<Todo> tmpTodos = new ArrayList<>();
            tmpTodos.add(((AddTodoAction) action).todo);
            tmpTodos.addAll(this._state.todos);

            return TodoState.create(tmpTodos, false, false, null);

        } else if (action instanceof RemoveTodoAction) {
            List<Todo> tmpTodos = new ArrayList<>();

            for (Todo t: this._state.todos) {
                if (t.get_id().equals(((RemoveTodoAction) action).todoId)) continue;
                tmpTodos.add(t);
            }
            return TodoState.create(tmpTodos, false, false, null);

        } else if (action instanceof UndoRemoveTodoAction) {
            List<Todo> tmpTodos = new ArrayList<>(this._state.todos);

            if (tmpTodos.size() <= 0)
                tmpTodos.add(((UndoRemoveTodoAction) action).todo);
            else
                tmpTodos.add(((UndoRemoveTodoAction) action).pos, ((UndoRemoveTodoAction) action).todo);

            return TodoState.create(tmpTodos,false, false, null);
        }  else if(action instanceof UpdateTodoAction) {
            List<Todo> tmpTodos = new ArrayList<Todo>();

            for (Todo t: this._state.todos) {
                if (t.get_id().equals(((UpdateTodoAction) action).refId)) {
                    tmpTodos.add(((UpdateTodoAction) action).newTodo);
                } else tmpTodos.add(t);
            }

            return TodoState.create(tmpTodos,false, false, null);
        } else if(action instanceof InitTodosAction) {
            return TodoState.create(((InitTodosAction) action).todos,false, false, null);
        } else if (action instanceof PerformAddTodoAction) {
            return TodoState.create(this._state.todos, false, true, null);
        } else if (action instanceof StartPerformTodoAction) {
            return TodoState.create(this._state.todos, this._state.isLoading, true, null);
        } else if (action instanceof TodoActionError) {
            return TodoState.create(this._state.todos, false, false, ((TodoActionError) action).error);
        } else return this._state;
    }
}
