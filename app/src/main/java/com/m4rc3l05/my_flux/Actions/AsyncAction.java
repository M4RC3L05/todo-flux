package com.m4rc3l05.my_flux.Actions;

import com.m4rc3l05.my_flux.Dispatcher;

public interface AsyncAction extends BaseAction {
    void doWork(Dispatcher dispatcher);
}
