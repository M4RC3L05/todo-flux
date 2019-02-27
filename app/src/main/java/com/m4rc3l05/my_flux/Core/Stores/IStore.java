package com.m4rc3l05.my_flux.Core.Stores;

import com.m4rc3l05.my_flux.Core.Actions.BaseAction;

public interface IStore {
    void onDispath(BaseAction action);
}
