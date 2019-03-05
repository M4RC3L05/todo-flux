package com.m4rc3l05.my_flux.core;

import com.m4rc3l05.my_flux.core.actions.BaseAction;
import com.m4rc3l05.my_flux.core.actions.asyncActions.AsyncAction;
import com.m4rc3l05.my_flux.core.stores.Store;

import java.util.ArrayList;
import java.util.List;

public class Dispatcher {
    private final List<Store> _stores;

    private Dispatcher() {
        this._stores = new ArrayList<>();
    }

    public static Dispatcher create() {
        return new Dispatcher();
    }

    public void subscribe(Store store) {
        if (this._stores.contains(store)) return;
        this._stores.add(store);
    }

    public void unsubscribe(Store cls) {
        this._stores.remove(cls);
    }

    public void dispatch(BaseAction action) {
        if(action instanceof AsyncAction) {
            ((AsyncAction) action).doWork(this);
            return;
        }

        for (Store s: this._stores) {
            s.onDispatch(action);
        }
    }
}
