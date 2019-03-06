package com.m4rc3l05.my_flux.core.actions;

public class OnInputChangeEvent extends BaseAction {
    public final String text;
    public final String inputName;
    public final String context;

    public OnInputChangeEvent(String text, String inputName, String context) {
        this.text = text;
        this.context = context;
        this.inputName = inputName;
    }

    public static OnInputChangeEvent create(String text, String inputName, String context) {
        return new OnInputChangeEvent(text, inputName, context);
    }
}
