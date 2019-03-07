package com.m4rc3l05.my_flux.core.instanceofswitch;

public class InstanceOfSwitchResultWrapper<T> {
    private final T _value;

    private InstanceOfSwitchResultWrapper(T _value) {
        this._value = _value;
    }

    public static <S> InstanceOfSwitchResultWrapper of(S _value) {
        return new InstanceOfSwitchResultWrapper(_value);
    }

    public Object getOrElse(Object elseVal) {
        if (this._value == null) return elseVal;

        return this._value;
    }
}
