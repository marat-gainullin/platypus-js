/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.client.cache.ScriptDocument;
import com.eas.client.login.PrincipalHost;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class SecuredJSObject extends SecuredJSObjectFacade {

    public SecuredJSObject(JSObject aDelegate, String aAppElementId, PrincipalHost aPrincipalHost, ScriptDocument aConfig) {
        super(aDelegate, aAppElementId, aPrincipalHost, aConfig);
    }

    @Override
    public synchronized Object getMember(String name) {
        Object res = super.getMember(name);
        if (res instanceof JSObject && ((JSObject) res).isFunction()) {
            return new SecuredJSFunction(name, (JSObject) res, moduleName, principalHost, config);
        } else {
            checkPropertyPermission(name);
            return res;
        }
    }

    @Override
    public synchronized void setMember(String name, Object value) {
        if (value instanceof JSObject && ((JSObject) value).isFunction()) {
            super.setMember(name, value);
        } else {
            checkPropertyPermission(name);
            super.setMember(name, value);
        }
    }

}
