package com.m4rc3l05.my_flux;

import com.m4rc3l05.my_flux.Actions.AsyncAction;
import com.m4rc3l05.my_flux.Actions.BaseAction;
import com.m4rc3l05.my_flux.Stores.IStore;

import java.util.ArrayList;
import java.util.List;

public class Dispatcher {
    private final List<IStore> _stores;

    private static final Dispatcher instance = new Dispatcher();

    private Dispatcher() {
        this._stores = new ArrayList<IStore>();
    }

    public static Dispatcher getInstance() {
        return instance;
    }

    public void subscribe(IStore store) {
        if (this._stores.contains(store)) return;
        this._stores.add(store);
    }

    public void dispatch(BaseAction action) {
        if(action instanceof AsyncAction) {
            ((AsyncAction) action).doWork(this);
            return;
        }

        for (IStore s: this._stores) {
            s.onDispath(action);
        }
    }
}
