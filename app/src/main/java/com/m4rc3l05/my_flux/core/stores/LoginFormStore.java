package com.m4rc3l05.my_flux.core.stores;

import com.m4rc3l05.my_flux.core.actions.BaseAction;
import com.m4rc3l05.my_flux.core.actions.OnInputChangeEvent;
import com.m4rc3l05.my_flux.core.stores.states.LoginFormState;

public class LoginFormStore extends Store<LoginFormState> {

    private LoginFormStore() {
        super();
    }

    public static LoginFormStore create() {
        return new LoginFormStore();
    }

    @Override
    protected LoginFormState getInitialState() {
        return LoginFormState.create("", "");
    }

    @Override
    protected LoginFormState reduce(LoginFormState state, BaseAction action) {
        if (action instanceof OnInputChangeEvent && ((OnInputChangeEvent) action).context.equals("login_form")) {
            return ((OnInputChangeEvent) action).inputName.equals("email")
                    ? LoginFormState.create(((OnInputChangeEvent) action).text, this._state.password)
                    : LoginFormState.create(this._state.email, ((OnInputChangeEvent) action).text);
        }

        return this._state;
    }
}
