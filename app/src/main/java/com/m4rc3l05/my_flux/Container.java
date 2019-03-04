package com.m4rc3l05.my_flux;

import com.m4rc3l05.my_flux.core.Dispatcher;
import com.m4rc3l05.my_flux.core.stores.AuthStore;
import com.m4rc3l05.my_flux.core.stores.TodoStore;

public class Container {
    public static final Dispatcher dispatcher = Dispatcher.create();
    public static final TodoStore todoStore = TodoStore.create();
    public static final AuthStore authStore = AuthStore.create();
}
