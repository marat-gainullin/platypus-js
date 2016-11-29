/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import java.util.Collection;
import java.util.Set;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class JSObjectFacade implements JSObject {

    protected final JSObject delegate;

    public JSObjectFacade(JSObject aDelegate) {
        super();
        assert !(aDelegate instanceof JSObjectFacade) : "Delegate is not allowed to be a JSObjectFacade instance.";
        delegate = aDelegate;
    }

    public JSObject getDelegate() {
        return delegate;
    }

    @Override
    public Object call(Object thiz, Object... args) {
        return delegate.call(thiz, args);
    }

    @Override
    public Object newObject(Object... args) {
        return delegate.newObject(args);
    }

    @Override
    public Object eval(String s) {
        return delegate.eval(s);
    }

    @Override
    public Object getMember(String name) {
        return delegate.getMember(name);
    }

    @Override
    public Object getSlot(int index) {
        return delegate.getSlot(index);
    }

    @Override
    public boolean hasMember(String name) {
        return delegate.hasMember(name);
    }

    @Override
    public boolean hasSlot(int slot) {
        return delegate.hasSlot(slot);
    }

    @Override
    public void removeMember(String name) {
        delegate.removeMember(name);
    }

    @Override
    public void setMember(String name, Object value) {
        delegate.setMember(name, value);
    }

    @Override
    public void setSlot(int index, Object value) {
        delegate.setSlot(index, value);
    }

    @Override
    public Set<String> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<Object> values() {
        return delegate.values();
    }

    @Override
    public boolean isInstance(Object instance) {
        return delegate.isInstance(instance);
    }

    @Override
    public boolean isInstanceOf(Object clazz) {
        return delegate.isInstanceOf(clazz);
    }

    @Override
    public String getClassName() {
        return delegate.getClassName();
    }

    @Override
    public boolean isFunction() {
        return delegate.isFunction();
    }

    @Override
    public boolean isStrictFunction() {
        return delegate.isStrictFunction();
    }

    @Override
    public boolean isArray() {
        return delegate.isArray();
    }

    @Override
    public double toNumber() {
        return delegate.toNumber();
    }
}
