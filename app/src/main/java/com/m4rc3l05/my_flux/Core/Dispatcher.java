package com.m4rc3l05.my_flux.Core;

import com.m4rc3l05.my_flux.Core.Actions.AsyncAction;
import com.m4rc3l05.my_flux.Core.Actions.BaseAction;
import com.m4rc3l05.my_flux.Core.Stores.IStore;

import java.util.ArrayList;
import java.util.List;

public class Dispatcher {
    private final List<IStore> _stores;

    private Dispatcher() {
        this._stores = new ArrayList<IStore>();
    }

    public static Dispatcher create() {
        return new Dispatcher();
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
