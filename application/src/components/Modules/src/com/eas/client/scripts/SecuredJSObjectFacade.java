/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.login.PrincipalHost;
import java.security.AccessControlException;
import java.util.Map;
import java.util.Set;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class SecuredJSObjectFacade extends JSObjectFacade {

    protected Map<String, Set<String>> propertiesAllowedRoles;
    // configuration
    protected String appElementId;
    protected Set<String> moduleAllowedRoles;
    /**
     * Current principal provider
     */
    protected PrincipalHost principalHost;

    public SecuredJSObjectFacade(JSObject aDelegate, String aAppElementId, Set<String> aModuleAllowedRoles, Map<String, Set<String>> aPropertiesAllowedRoles, PrincipalHost aPrincipalHost) {
        super(aDelegate);
        appElementId = aAppElementId;
        moduleAllowedRoles = aModuleAllowedRoles;
        propertiesAllowedRoles = aPropertiesAllowedRoles;
        principalHost = aPrincipalHost;
    }

    protected PlatypusPrincipal getPrincipal() {
        if (principalHost != null) {
            return principalHost.getPrincipal();
        }
        return null;
    }

    /**
     * Checks module access roles.
     *
     */
    protected void checkModulePermissions() throws AccessControlException {
        if (moduleAllowedRoles != null && !moduleAllowedRoles.isEmpty()) {
            try {
                PlatypusPrincipal principal = getPrincipal();
                if (principal == null || !principal.hasAnyRole(moduleAllowedRoles)) {
                    throw new AccessControlException(String.format("Access denied to %s module for '%s'.",//NOI18N
                            appElementId,
                            principal != null ? principal.getName() : null));
                }
            } catch (Exception ex) {
                throw new AccessControlException(ex.getMessage());
            }
        }
    }

    /**
     * Checks module instance property access roles.
     *
     * @param aName A property name access is checked for.
     */
    protected void checkPropertyPermission(String aName) throws AccessControlException {
        try {
            PlatypusPrincipal principal = getPrincipal();
            if (propertiesAllowedRoles != null && propertiesAllowedRoles.get(aName) != null && !propertiesAllowedRoles.get(aName).isEmpty()) {
                if (principal != null && principal.hasAnyRole(propertiesAllowedRoles.get(aName))) {
                    return;
                }
                throw new AccessControlException(String.format("Access denied to %s function in %s module for '%s'.",//NOI18N
                        aName,
                        appElementId,
                        principal != null ? principal.getName() : null));
            } else {
                checkModulePermissions();
            }
        } catch (Exception ex) {
            if (ex instanceof AccessControlException) {
                throw (AccessControlException) ex;
            } else {
                throw new AccessControlException(ex.getMessage());
            }
        }
    }
}
