package com.m4rc3l05.my_flux.core.actions.asyncActions;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAsyncAction implements AsyncAction {
    protected List<AsyncActionSubscription> listeners;

    protected BaseAsyncAction() {
        this.listeners = new ArrayList<>();
    }

    public BaseAsyncAction subscribe(AsyncActionSubscription sub) {
        if (!this.listeners.contains(sub)) this.listeners.add(sub);
        return this;
    }

    protected void __notify(boolean success) {
        for(AsyncActionSubscription sub: this.listeners) {
            sub.call(success);
        }
    }
}
