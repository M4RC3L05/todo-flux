package com.m4rc3l05.my_flux.Stores;

import com.m4rc3l05.my_flux.Actions.BaseAction;
import com.m4rc3l05.my_flux.IView;

import java.util.ArrayList;
import java.util.List;

public abstract class Store implements IStore {


    private final List<IView> _views;

    Store() {
        this._views = new ArrayList<IView>();
    }

    @Override
    public void onDispath(BaseAction action) {
        this._notify();
    }

    protected void _notify() {
        for (IView v: this._views) {
            if (v == null) continue;
            v.render();
        }
    }

    public void subscribe(IView view) {
        if(this._views.contains(view)) return;

        this._views.add(view);
    }
}
