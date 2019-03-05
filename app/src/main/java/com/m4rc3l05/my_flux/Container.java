package com.m4rc3l05.my_flux;

import com.m4rc3l05.my_flux.core.Dispatcher;
import com.m4rc3l05.my_flux.core.stores.AuthStore;
import com.m4rc3l05.my_flux.core.stores.TodoStore;

import java.util.HashMap;
import java.util.function.Supplier;

public class Container {
    private static Container instance;
    private final HashMap<String, Object> instances;

    public interface ISingletonGetter extends IObjectGetter {
    }

    public interface IObjectGetter {
        Object get();
    }

    private Container() {
        this.instances = new HashMap<>();
    }

    public static Container getInstance(){
        if(instance == null){
            synchronized (Container.class) {
                if(instance == null){
                    instance = new Container();
                }
            }
        }
        return instance;
    }

    public Container registerSingleton(String cls, ISingletonGetter action) {
        this.instances.put(cls, action.get());
        return this;
    }

    public Object get(String cls) {
        Object obj = this.instances.get(cls);

        if (obj instanceof IObjectGetter) return ((IObjectGetter) obj).get();

        return obj;
    }

    public Container register(String cls, IObjectGetter obj) {
        this.instances.put(cls, obj);
        return this;
    }
}
