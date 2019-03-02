package com.m4rc3l05.my_flux.Core.Stores;

import com.m4rc3l05.my_flux.Core.Actions.BaseAction;

public interface IStore<S> {
    void reduce(S state, BaseAction action);
    S initialState();
}
