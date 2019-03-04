package com.m4rc3l05.my_flux.core.actions.asyncActions;

import com.m4rc3l05.my_flux.core.actions.BaseAction;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAsyncAction extends BaseAction implements AsyncAction {
    protected List<AsyncActionSubscription> listeners;
    protected boolean hasNotify;

    protected BaseAsyncAction() {
        this.listeners = new ArrayList<>();
        hasNotify = false;
    }

    public BaseAsyncAction subscribe(AsyncActionSubscription sub) {
        if (!this.listeners.contains(sub)) this.listeners.add(sub);
        return this;
    }

    protected void __notify(boolean success) {
        if (this.hasNotify) return;
        
        for(AsyncActionSubscription sub: this.listeners) {
            sub.call(success);
        }

        this.hasNotify = true;
    }
}
