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
public class LockProviderFunction extends JSObjectFacade {

    protected final Object lock;

    public LockProviderFunction(JSObject aDelegate, Object aLock) {
        super(aDelegate);
        lock = aLock;
    }

    @Override
    public Object call(Object thiz, Object... args) {
        final Object outerLock = ScriptUtils.getLock();
        if (outerLock != null) {
            synchronized (outerLock) {
                return super.call(thiz, args);
            }
        } else {
            synchronized (lock) {
                ScriptUtils.setLock(lock);
                try {
                    return super.call(thiz, args);
                } finally {
                    ScriptUtils.setLock(null);
                }
            }
        }
    }

    @Override
    public Object newObject(Object... args) {
        final Object outerLock = ScriptUtils.getLock();
        if (outerLock != null) {
            synchronized (outerLock) {
                return super.newObject(args);
            }
        } else {
            synchronized (lock) {
                ScriptUtils.setLock(lock);
                try {
                    return super.newObject(args);
                } finally {
                    ScriptUtils.setLock(null);
                }
            }
        }
    }

}
