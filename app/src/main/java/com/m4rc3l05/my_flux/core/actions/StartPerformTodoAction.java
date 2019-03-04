package com.m4rc3l05.my_flux.core.actions;

public class StartPerformTodoAction implements BaseAction {

    private StartPerformTodoAction() {

    }

    public static StartPerformTodoAction create() {
        return new StartPerformTodoAction();
    }
}
