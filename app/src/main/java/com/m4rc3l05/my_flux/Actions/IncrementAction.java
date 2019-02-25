package com.m4rc3l05.my_flux.Actions;

public class IncrementAction implements BaseAction {

    private IncrementAction() {
    }

    public static IncrementAction create() {
        return new IncrementAction();
    }
}
