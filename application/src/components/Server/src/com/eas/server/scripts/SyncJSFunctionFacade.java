/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.scripts;

import com.eas.client.scripts.JSObjectFacade;
import java.util.Set;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class SyncJSFunctionFacade extends JSObjectFacade {

    protected final Object lock;

    public SyncJSFunctionFacade(JSObject aDelegate, Object aLock) {
        super(aDelegate);
        lock = aLock;
    }

    @Override
    public Object call(Object thiz, Object... args) {
        synchronized (lock) {
            return super.call(thiz, args);
        }
    }

    @Override
    public Object eval(String s) {
        synchronized (lock) {
            return super.eval(s);
        }
    }

    @Override
    public String getClassName() {
        synchronized (lock) {
            return super.getClassName();
        }
    }

    @Override
    public Object getMember(String name) {
        synchronized (lock) {
            return super.getMember(name);
        }
    }

    @Override
    public Object getSlot(int index) {
        synchronized (lock) {
            return super.getSlot(index);
        }
    }

    @Override
    public boolean hasMember(String name) {
        synchronized (lock) {
            return super.hasMember(name);
        }
    }

    @Override
    public boolean hasSlot(int slot) {
        synchronized (lock) {
            return super.hasSlot(slot);
        }
    }

    @Override
    public boolean isArray() {
        synchronized (lock) {
            return super.isArray();
        }
    }

    @Override
    public boolean isFunction() {
        synchronized (lock) {
            return super.isFunction();
        }
    }

    @Override
    public boolean isInstance(Object instance) {
        synchronized (lock) {
            return super.isInstance(instance);
        }
    }

    @Override
    public boolean isInstanceOf(Object clazz) {
        synchronized (lock) {
            return super.isInstanceOf(clazz);
        }
    }

    @Override
    public boolean isStrictFunction() {
        synchronized (lock) {
            return super.isStrictFunction();
        }
    }

    @Override
    public Set<String> keySet() {
        synchronized (lock) {
            return super.keySet();
        }
    }

    @Override
    public Object newObject(Object... args) {
        synchronized (lock) {
            return super.newObject(args);
        }
    }

    @Override
    public void removeMember(String name) {
        synchronized (lock) {
            super.removeMember(name);
        }
    }

    @Override
    public void setMember(String name, Object value) {
        synchronized (lock) {
            super.setMember(name, value);
        }
    }

    @Override
    public void setSlot(int index, Object value) {
        synchronized (lock) {
            super.setSlot(index, value);
        }
    }

    @Override
    public double toNumber() {
        synchronized (lock) {
            return super.toNumber();
        }
    }
}
