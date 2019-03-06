package com.m4rc3l05.my_flux;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.m4rc3l05.my_flux.core.Dispatcher;
import com.m4rc3l05.my_flux.core.stores.AuthStore;
import com.m4rc3l05.my_flux.core.stores.LoginFormStore;
import com.m4rc3l05.my_flux.core.stores.RegisterFormStore;
import com.m4rc3l05.my_flux.core.stores.TodoFormStore;
import com.m4rc3l05.my_flux.core.stores.TodoStore;

public class RootApp extends Application {
    public RootApp() { }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase
                .getInstance()
                .setPersistenceEnabled(true);

        Container
                .getInstance()
                .registerSingleton(TodoFormStore.class.toString(), TodoFormStore::create)
                .registerSingleton(RegisterFormStore.class.toString(), RegisterFormStore::create)
                .registerSingleton(LoginFormStore.class.toString(), LoginFormStore::create)
                .registerSingleton(TodoStore.class.toString(), TodoStore::create)
                .registerSingleton(AuthStore.class.toString(), AuthStore::create)
                .registerSingleton(Dispatcher.class.toString(), Dispatcher::create);
    }
}
