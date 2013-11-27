/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.script;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author mg
 */
public class NativeJavaHostObject extends NativeJavaObject {

    protected ScriptableObject delegate;

    public NativeJavaHostObject(Scriptable scope, Object javaObject, Class<?> staticType) {
        super(scope, javaObject, staticType);
        delegate = new ScriptableObject() {
            @Override
            public String getClassName() {
                return NativeJavaHostObject.this.javaObject.getClass().getSimpleName();
            }
        };
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
        if (delegate.has(name, start)) {
            delegate.put(name, delegate, value);
        } else {
            if (super.has(name, start)) {
                super.put(name, start, value);
            } else {
                delegate.put(name, delegate, value);
            }
        }
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
        if (delegate.has(index, start)) {
            delegate.put(index, delegate, value);
        } else {
            if (super.has(index, start)) {
                super.put(index, start, value);
            } else {
                delegate.put(index, delegate, value);
            }
        }
    }

    @Override
    public Object get(String name, Scriptable start) {
        if (delegate.has(name, start)) {
            return delegate.get(name, start);
        } else {
            return super.get(name, start);
        }
    }

    @Override
    public Object get(int index, Scriptable start) {
        if (delegate.has(index, start)) {
            return delegate.get(index, start);
        } else {
            return super.get(index, start);
        }
    }

    @Override
    public boolean has(String name, Scriptable start) {
        boolean res = super.has(name, start);
        if (!res) {
            res = delegate.has(name, delegate);
        }
        return res;
    }

    @Override
    public boolean has(int index, Scriptable start) {
        boolean res = super.has(index, start);
        if (!res) {
            res = delegate.has(index, delegate);
        }
        return res;
    }

    @Override
    public Object[] getIds() {
        List<Object> ids = new ArrayList<>();
        ids.addAll(Arrays.asList(super.getIds()));
        ids.addAll(Arrays.asList(delegate.getIds()));
        return ids.toArray();
    }

    @Override
    public void delete(String name) {
        if (delegate.has(name, this)) {
            delegate.delete(name);
        }// super.delete will be no op. see NativeJavaObject
    }

    @Override
    public void delete(int index) {
        if (delegate.has(index, this)) {
            delegate.delete(index);
        }// super.delete will be no op. see NativeJavaObject
    }

    public ScriptableObject getDelegate() {
        return delegate;
    }

    public void defineProperty(String propertyName, Object delegateTo,
            Method getter, Method setter) {
        delegate.defineProperty(propertyName, delegateTo, getter, setter, 0);
    }

    public void defineProperty(String propertyName, Object value) {
        delegate.defineProperty(propertyName, value, 0);
    }
}
