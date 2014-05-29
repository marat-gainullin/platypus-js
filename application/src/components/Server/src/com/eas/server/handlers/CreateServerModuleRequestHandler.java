/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.scripts.PlatypusScriptedResource;
import com.eas.client.scripts.SecuredJSConstructor;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.CreateServerModuleRequest;
import com.eas.client.threetier.requests.CreateServerModuleResponse;
import com.eas.script.JsDoc;
import com.eas.script.ScriptUtils;
import com.eas.server.*;
import java.security.AccessControlException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author pk, mg, ab
 */
public class CreateServerModuleRequestHandler extends SessionRequestHandler<CreateServerModuleRequest> {

    public CreateServerModuleRequestHandler(PlatypusServerCore server, Session session, CreateServerModuleRequest rq) {
        super(server, session, rq);
    }

    @Override
    public Response handle2() throws Exception {
        String moduleName = getRequest().getModuleName();
        if (moduleName == null || moduleName.isEmpty()) {
            throw new Exception("Module name is missing. Unnamed server modules are not allowed.");
        }
        PlatypusScriptedResource.executeScriptResource(moduleName);
        Set<String> functionProps = new HashSet<>();
        try {
            JSObject jsConstr = ScriptUtils.lookupInGlobal(moduleName);
            if (jsConstr != null) {
                if (jsConstr instanceof SecuredJSConstructor) {
                    SecuredJSConstructor sjsConstr = (SecuredJSConstructor) jsConstr;
                    // Let's check the if module is resident
                    JSObject moduleInstance = getServerCore().getSessionManager().getSystemSession().getModule(moduleName);
                    if (moduleInstance != null) {
                        // Resident module roles need to be checked against a current user.
                        checkPrincipalPermission(getServerCore(), sjsConstr.getModuleAllowedRoles(), moduleName);
                    } else {
                        if (getSession().containsModule(moduleName)) {
                            moduleInstance = getSession().getModule(moduleName);
                        } else {
                            if (sjsConstr.hasModuleAnnotation(JsDoc.Tag.PUBLIC_TAG)) {
                                moduleInstance = (JSObject) sjsConstr.newObject(new Object[]{});
                                // Let's decide if we have to register the module in user's session.
                                if (!sjsConstr.hasModuleAnnotation(JsDoc.Tag.STATELESS_TAG)) {
                                    getSession().registerModule(moduleInstance);
                                }
                                Logger.getLogger(CreateServerModuleRequestHandler.class.getName()).log(Level.FINE, "Created server module for script {0} with name {1} on request {2}", new Object[]{getRequest().getModuleName(), moduleName, getRequest().getID()});
                            } else {
                                throw new AccessControlException(String.format("Public access to module %s is denied.", moduleName));//NOI18N
                            }
                        }
                    }
                    final JSObject funSource = moduleInstance;
                    funSource.keySet().stream().forEach((String aKey) -> {
                        Object oFun = funSource.getMember(aKey);
                        if (oFun instanceof JSObject && ((JSObject) oFun).isFunction()) {
                            functionProps.add(aKey);
                        }
                    });
                    return new CreateServerModuleResponse(getRequest().getID(), moduleName, functionProps, true);
                } else {
                    throw new AccessControlException(String.format("Access to unsecured module %s is denied.", moduleName));//NOI18N
                }
            } else {
                throw new IllegalArgumentException(String.format("No module: %s, or it is not a module", moduleName));
            }
        } catch (AccessControlException ex) {
            return new CreateServerModuleResponse(getRequest().getID(), moduleName, Collections.emptySet(), false);
        }
    }

    /**
     * Checks module roles.
     *
     * @param aServerCore
     * @param anAllowedRoles
     * @param aModuleName
     * @throws Exception
     */
    public static void checkPrincipalPermission(PlatypusServerCore aServerCore, Set<String> anAllowedRoles, String aModuleName) throws Exception {
        if (anAllowedRoles != null && !anAllowedRoles.isEmpty()) {
            PlatypusPrincipal principal = aServerCore.getPrincipal();
            if (principal == null || !principal.hasAnyRole(anAllowedRoles)) {
                throw new AccessControlException(String.format("Access denied to %s module for '%s'.",//NOI18N
                        aModuleName,
                        principal != null ? principal.getName() : null));
            }
        }
    }
}
