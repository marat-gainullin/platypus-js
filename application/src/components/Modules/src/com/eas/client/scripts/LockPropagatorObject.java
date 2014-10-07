/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class LockPropagatorObject extends JSObjectFacade {

    protected Object lock;
    
    public LockPropagatorObject(JSObject aDelegate, Object aLock) {
        super(aDelegate);
        lock = aLock;
    }

    @Override
    public Object getMember(String name) {
        Object res = super.getMember(name);
        if (res instanceof JSObject && !(res instanceof JSObjectFacade) && ((JSObject) res).isFunction()) {
            res = new LockProviderFunction((JSObject)res, lock);
        }
        return res;
    }

    @Override
    public Object getSlot(int index) {
        Object res = super.getSlot(index);
        if (res instanceof JSObject && !(res instanceof JSObjectFacade) && ((JSObject) res).isFunction()) {
            res = new LockProviderFunction((JSObject)res, lock);
        }
        return res;
    }

}
