package com.m4rc3l05.my_flux;

import com.m4rc3l05.my_flux.Core.Dispatcher;
import com.m4rc3l05.my_flux.Core.Stores.TodoStore;

public class Container {
    public static final Dispatcher dispatcher = Dispatcher.create();
    public static final TodoStore todoStore = TodoStore.create();
}
