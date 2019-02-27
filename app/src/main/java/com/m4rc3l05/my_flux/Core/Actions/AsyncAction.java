package com.m4rc3l05.my_flux.Core.Actions;

import com.m4rc3l05.my_flux.Core.Dispatcher;

public interface AsyncAction extends BaseAction {
    void doWork(Dispatcher dispatcher);
}
