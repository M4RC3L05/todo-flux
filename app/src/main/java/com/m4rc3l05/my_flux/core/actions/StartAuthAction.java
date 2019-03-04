package com.m4rc3l05.my_flux.core.actions;

public class StartAuthAction implements BaseAction {

    private StartAuthAction() {
    }

    public static StartAuthAction create() {
        return new StartAuthAction();
    }

}
