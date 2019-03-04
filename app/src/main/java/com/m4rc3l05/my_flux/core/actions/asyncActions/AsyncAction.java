package com.m4rc3l05.my_flux.core.actions.asyncActions;

import com.m4rc3l05.my_flux.core.actions.BaseAction;
import com.m4rc3l05.my_flux.core.Dispatcher;

public interface AsyncAction extends BaseAction {
    void doWork(Dispatcher dispatcher);
}
