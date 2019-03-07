package com.m4rc3l05.my_flux.core.stores;

import com.m4rc3l05.my_flux.core.actions.BaseAction;
import com.m4rc3l05.my_flux.core.actions.OnInputChangeEvent;
import com.m4rc3l05.my_flux.core.instanceofswitch.InstanceOfSwitch;
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

        return (RegisterFormState) InstanceOfSwitch
            .of(action)
            .ofType(OnInputChangeEvent.class, () -> {
                if (!((OnInputChangeEvent) action).context.equals("register_form")) return state;

                return ((OnInputChangeEvent) action).inputName.equals("username")
                        ? RegisterFormState.create(((OnInputChangeEvent) action).text, state.email, state.password)
                        : ((OnInputChangeEvent) action).inputName.equals("email")
                        ? RegisterFormState.create(state.username, ((OnInputChangeEvent) action).text, state.password)
                        : ((OnInputChangeEvent) action).inputName.equals("password")
                        ? RegisterFormState.create(state.username, state.email, ((OnInputChangeEvent) action).text)
                        : state;
            })
            .orElse(() -> state)
            .match()
            .getOrElse(state);
    }
}
