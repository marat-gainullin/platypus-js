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
public class SyncJSObjectFacade extends JSObjectFacade {

    public SyncJSObjectFacade(JSObject aDelegate) {
        super(aDelegate);
    }

    @Override
    public synchronized Object call(Object thiz, Object... args) {
        return super.call(thiz, args);
    }

    @Override
    public synchronized Object eval(String s) {
        return super.eval(s);
    }

    @Override
    public synchronized String getClassName() {
        return super.getClassName();
    }

    @Override
    public synchronized Object getMember(String name) {
        Object result = super.getMember(name);
        result = jdk.nashorn.api.scripting.ScriptUtils.wrap(result);
        if (result instanceof JSObject && ((JSObject) result).isFunction()) {
            result = new SyncJSFunctionFacade((JSObject) result, this);
        }
        return result;
    }

    @Override
    public synchronized Object getSlot(int index) {
        Object result = super.getSlot(index);
        result = jdk.nashorn.api.scripting.ScriptUtils.wrap(result);
        if (result instanceof JSObject && ((JSObject) result).isFunction()) {
            result = new SyncJSFunctionFacade((JSObject) result, this);
        }
        return result;
    }

    @Override
    public synchronized boolean hasMember(String name) {
        return super.hasMember(name);
    }

    @Override
    public synchronized boolean hasSlot(int slot) {
        return super.hasSlot(slot);
    }

    @Override
    public synchronized boolean isArray() {
        return super.isArray();
    }

    @Override
    public synchronized boolean isFunction() {
        return super.isFunction();
    }

    @Override
    public synchronized boolean isInstance(Object instance) {
        return super.isInstance(instance);
    }

    @Override
    public synchronized boolean isInstanceOf(Object clazz) {
        return super.isInstanceOf(clazz);
    }

    @Override
    public synchronized boolean isStrictFunction() {
        return super.isStrictFunction();
    }

    @Override
    public synchronized Set<String> keySet() {
        return super.keySet();
    }

    @Override
    public synchronized Object newObject(Object... args) {
        return super.newObject(args);
    }

    @Override
    public synchronized void removeMember(String name) {
        super.removeMember(name);
    }

    @Override
    public synchronized void setMember(String name, Object value) {
        super.setMember(name, value);
    }

    @Override
    public synchronized void setSlot(int index, Object value) {
        super.setSlot(index, value);
    }

    @Override
    public synchronized double toNumber() {
        return super.toNumber();
    }
}
