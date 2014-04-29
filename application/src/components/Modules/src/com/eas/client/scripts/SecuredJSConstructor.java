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

import com.eas.client.login.PrincipalHost;
import java.util.Map;
import java.util.Set;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class SecuredJSConstructor extends SecuredJSObjectFacade {

    public SecuredJSConstructor(JSObject aDelegate, String aAppElementId, Set<String> aModuleAllowedRoles, Map<String, Set<String>> aPropertiesAllowedRoles, PrincipalHost aPrincipalHost) {
        super(aDelegate, aAppElementId, aModuleAllowedRoles, aPropertiesAllowedRoles, aPrincipalHost);
    }

    @Override
    public Object call(Object thiz, Object... args) {
        checkModulePermissions();
        Object res = super.call(thiz, args);
        if (res instanceof JSObject) {
            return new SecuredJSObject((JSObject) res, appElementId, moduleAllowedRoles, propertiesAllowedRoles, principalHost);
        } else {
            return res;
        }
    }

    @Override
    public Object newObject(Object... args) {
        checkModulePermissions();
        Object res = super.newObject(args);
        if (res instanceof JSObject) {
            return new SecuredJSObject((JSObject) res, appElementId, moduleAllowedRoles, propertiesAllowedRoles, principalHost);
        } else {
            return res;
        }
    }

}
