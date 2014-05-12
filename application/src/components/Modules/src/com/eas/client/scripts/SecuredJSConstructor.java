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

import com.eas.client.AppCache;
import com.eas.client.login.PrincipalHost;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class SecuredJSConstructor extends SecuredJSObjectFacade {

    protected AppCache cache;
    protected long sourceLength;
    protected long sourceCRC32;

    public SecuredJSConstructor(JSObject aDelegate, String aAppElementId, long aSourceLength, long aSourceCRC32, AppCache aCache, Set<String> aModuleAllowedRoles, Map<String, Set<String>> aPropertiesAllowedRoles, PrincipalHost aPrincipalHost) {
        super(aDelegate, aAppElementId, aModuleAllowedRoles, aPropertiesAllowedRoles, aPrincipalHost);
        cache = aCache;
        sourceLength = aSourceLength;
        sourceCRC32 = aSourceCRC32;
    }

    public void replace(JSObject aDelegate, long aSourceLength, long aSourceCRC32, Set<String> aModuleAllowedRoles, Map<String, Set<String>> aPropertiesAllowedRoles) {
        delegate = aDelegate;
        moduleAllowedRoles = aModuleAllowedRoles;
        propertiesAllowedRoles = aPropertiesAllowedRoles;
        sourceLength = aSourceLength;
        sourceCRC32 = aSourceCRC32;
    }

    public Set<String> getModuleAllowedRoles() {
        return moduleAllowedRoles;
    }

    @Override
    public Object call(Object thiz, Object... args) {
        checkAutoUpdate();
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
        checkAutoUpdate();
        checkModulePermissions();
        Object res = super.newObject(args);
        if (res instanceof JSObject) {
            return new SecuredJSObject((JSObject) res, appElementId, moduleAllowedRoles, propertiesAllowedRoles, principalHost);
        } else {
            return res;
        }
    }

    private void checkAutoUpdate() {
        try {
            if (!cache.isActual(appElementId, sourceLength, sourceCRC32)) {
                cache.remove(appElementId);
                PlatypusScriptedResource.executeScriptResource(appElementId, this);
            }
        } catch (Exception ex) {
            Logger.getLogger(SecuredJSConstructor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
