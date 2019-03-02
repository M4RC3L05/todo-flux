package com.m4rc3l05.my_flux.Core.Stores;

import com.m4rc3l05.my_flux.Core.Actions.BaseAction;
import com.m4rc3l05.my_flux.Core.IView;

import java.util.ArrayList;
import java.util.List;

public abstract class Store<T> {

    private final List<IView> _views;
    protected T _state;

    protected Store() {
        this._views = new ArrayList<IView>();
        this._state = this.getInitialState();
    }

    protected T getInitialState() {
        return null;
    }

    public T getState() {
        return this._state;
    }

    protected T reduce(T state, BaseAction action) {
        return state;
    }

    private void _notify() {
        for (IView v: this._views) {
            if (v == null) continue;
            v.render();
        }
    }

    public void subscribe(IView view) {
        if(this._views.contains(view)) return;

        this._views.add(view);
    }

    public void onDispatch(BaseAction action) {
        T state = this._state;
        T newState = this.reduce(state, action);

        if (newState.equals(state)) return;

        this._state = newState;
        this._notify();
    }
}
