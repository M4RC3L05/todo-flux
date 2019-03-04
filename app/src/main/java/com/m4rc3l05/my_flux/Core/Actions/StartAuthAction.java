package com.m4rc3l05.my_flux.Core.Actions;

import com.google.firebase.auth.FirebaseUser;

public class StartAuthAction implements BaseAction {

    private StartAuthAction() {
    }

    public static StartAuthAction create() {
        return new StartAuthAction();
    }

}
