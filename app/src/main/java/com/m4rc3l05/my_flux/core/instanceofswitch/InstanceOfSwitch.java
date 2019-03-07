package com.m4rc3l05.my_flux.core.instanceofswitch;

import java.util.HashMap;
import java.util.Map;

public class InstanceOfSwitch {
    public interface IOnMatch {
        Object onMatch();
    }

    private final HashMap<Class<?>, IOnMatch> _dicMatches;
    private IOnMatch _def;
    private final Object _valToMatch;

    private InstanceOfSwitch(Object _valToMatch) {
        this._valToMatch = _valToMatch;
        this._dicMatches = new HashMap<>();
    }

    public static InstanceOfSwitch of(Object _valToMatch) {
        return new InstanceOfSwitch(_valToMatch);
    }

    public InstanceOfSwitch ofType(Class<?> t, IOnMatch onMatch) {
        this._dicMatches.put(t, onMatch);
        return this;
    }

    public InstanceOfSwitch orElse(IOnMatch onMatch) {
        this._def = onMatch;
        return this;
    }

    public InstanceOfSwitchResultWrapper match() {
        for (Map.Entry<Class<?>,IOnMatch> entry : this._dicMatches.entrySet()) {
            Class<?> key = entry.getKey();
            IOnMatch val = entry.getValue();

            if (!this._valToMatch.getClass().equals(key)) continue;

            return InstanceOfSwitchResultWrapper.of(val.onMatch());
        }

        if (this._def != null) return InstanceOfSwitchResultWrapper.of(this._def.onMatch());

        return InstanceOfSwitchResultWrapper.of(null);
    }
}
