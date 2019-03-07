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
import com.m4rc3l05.my_flux.core.instanceofswitch.InstanceOfSwitch;
import com.m4rc3l05.my_flux.core.stores.states.TodoState;
import com.m4rc3l05.my_flux.models.Todo;

import java.util.ArrayList;
import java.util.List;

public class TodoStore extends Store<TodoState> {

    public static TodoStore create() {
        return new TodoStore();
    }

    @Override
    protected TodoState getInitialState() {
        return TodoState.create(new ArrayList<>(), true, false, null);
    }

    @Override
    public TodoState reduce(TodoState state, BaseAction action) {

        return (TodoState) InstanceOfSwitch
            .of(action)
            .ofType(AddTodoAction.class, () -> {
                if (state.todos.contains(((AddTodoAction) action).todo)) return TodoState.create(state.todos, false, false, null);

                List<Todo> tmpTodos = new ArrayList<>();
                tmpTodos.add(((AddTodoAction) action).todo);
                tmpTodos.addAll(state.todos);

                return TodoState.create(tmpTodos, false, false, null);
            })
            .ofType(RemoveTodoAction.class, () -> {
                List<Todo> tmpTodos = new ArrayList<>();

                for (Todo t: state.todos) {
                    if (t.get_id().equals(((RemoveTodoAction) action).todoId)) continue;
                    tmpTodos.add(t);
                }
                return TodoState.create(tmpTodos, false, false, null);
            })
            .ofType(UndoRemoveTodoAction.class, () -> {
                List<Todo> tmpTodos = new ArrayList<>(state.todos);

                if (tmpTodos.size() <= 0)
                    tmpTodos.add(((UndoRemoveTodoAction) action).todo);
                else
                    tmpTodos.add(((UndoRemoveTodoAction) action).pos, ((UndoRemoveTodoAction) action).todo);

                return TodoState.create(tmpTodos,false, false, null);
            })
            .ofType(UpdateTodoAction.class, () -> {
                List<Todo> tmpTodos = new ArrayList<Todo>();

                for (Todo t: state.todos) {
                    if (t.get_id().equals(((UpdateTodoAction) action).refId)) {
                        tmpTodos.add(((UpdateTodoAction) action).newTodo);
                    } else tmpTodos.add(t);
                }

                return TodoState.create(tmpTodos,false, false, null);
            })
            .ofType(InitTodosAction.class, () -> TodoState.create(((InitTodosAction) action).todos,false, false, null))
            .ofType(StartPerformTodoAction.class, () -> TodoState.create(state.todos, state.isLoading, true, null))
            .ofType(TodoActionError.class, () -> TodoState.create(state.todos, false, false, ((TodoActionError) action).error))
            .match()
            .getOrElse(state);
    }
}
