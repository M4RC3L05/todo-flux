package com.m4rc3l05.my_flux.core.stores;

import com.m4rc3l05.my_flux.core.actions.BaseAction;
import com.m4rc3l05.my_flux.core.actions.OnInputChangeEvent;
import com.m4rc3l05.my_flux.core.stores.states.RegisterFormState;

public class RegisterFormStore extends Store<RegisterFormState> {

    public static RegisterFormStore create() {
        return new RegisterFormStore();
    }

    @Override
    protected RegisterFormState getInitialState() {
        return RegisterFormState.create("", "", "");
    }

    @Override
    protected RegisterFormState reduce(RegisterFormState state, BaseAction action) {

        if (action instanceof OnInputChangeEvent && ((OnInputChangeEvent) action).context.equals("register_form")) {
            return ((OnInputChangeEvent) action).inputName.equals("username")
                    ? RegisterFormState.create(((OnInputChangeEvent) action).text, state.email, state.password)
                    : ((OnInputChangeEvent) action).inputName.equals("email")
                    ? RegisterFormState.create(state.username, ((OnInputChangeEvent) action).text, state.password)
                    : ((OnInputChangeEvent) action).inputName.equals("password")
                    ? RegisterFormState.create(state.username, state.email, ((OnInputChangeEvent) action).text)
                    : state;
        }

        return state;
    }
}
