/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.script.ScriptUtils;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class LockProviderConstructor extends JSObjectFacade {

    public LockProviderConstructor(JSObject aDelegate) {
        super(aDelegate);
        assert super.isFunction() : "LockProviderConstructor instance should only wrap a function.";
    }

    @Override
    public synchronized Object newObject(Object... args) {
        ObjectLock lock = new ObjectLock(String.valueOf(super.getMember("name")));
        Object oldLock = ScriptUtils.getLock();
        ScriptUtils.setLock(lock);
        try {
            Object res = super.newObject(args);
            return new LockPropagatorObject((JSObject) res, lock);
        } finally {
            ScriptUtils.setLock(oldLock);
        }
    }

    @Override
    public synchronized Object call(Object thiz, Object... args) {
        return super.call(thiz, args);
    }

}
