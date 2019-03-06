package com.m4rc3l05.my_flux.core.stores;

import com.m4rc3l05.my_flux.core.actions.BaseAction;
import com.m4rc3l05.my_flux.core.actions.OnInputChangeEvent;
import com.m4rc3l05.my_flux.core.stores.states.TodoFormState;

public class TodoFormStore extends Store<TodoFormState> {

    public static TodoFormStore create() {
        return new TodoFormStore();
    }

    @Override
    protected TodoFormState getInitialState() {
        return TodoFormState.create("");
    }

    @Override
    protected TodoFormState reduce(TodoFormState state, BaseAction action) {
        if (action instanceof OnInputChangeEvent && ((OnInputChangeEvent) action).context.equals("todo_form"))
            return ((OnInputChangeEvent) action).inputName.equals("todoText")
                ? TodoFormState.create(((OnInputChangeEvent) action).text)
                : state;

        return state;
    }
}
