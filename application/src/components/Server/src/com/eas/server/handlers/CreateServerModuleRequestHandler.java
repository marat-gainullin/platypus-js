/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.AppCache;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.scripts.ScriptDocument;
import com.eas.client.scripts.SecuredJSConstructor;
import com.eas.client.scripts.store.Dom2ScriptDocument;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.CreateServerModuleRequest;
import com.eas.client.threetier.requests.CreateServerModuleResponse;
import com.eas.script.JsDoc;
import com.eas.script.ScriptUtils;
import com.eas.server.*;
import java.security.AccessControlException;
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
        // Let's check the module is already created
        JSObject serverModule = getServerCore().getSessionManager().getSystemSession().getModule(moduleName);
        if (serverModule == null) {
            serverModule = getSession().getModule(moduleName);
        }
        Set<String> functionProps = new HashSet<>();
        boolean permitted = true;
        try {
            if (serverModule == null) {
                serverModule = runModule(getServerCore().getDatabasesClient().getAppCache(), moduleName);
                Logger.getLogger(CreateServerModuleRequestHandler.class.getName()).log(Level.FINE, "Created server module for script {0} with id {1} on request {2}", new Object[]{getRequest().getModuleName(), moduleName, getRequest().getID()});
            }
            JSObject jsConstr = ScriptUtils.lookupInGlobal(moduleName);
            if(jsConstr != null && jsConstr instanceof SecuredJSConstructor){
                SecuredJSConstructor casted = (SecuredJSConstructor)jsConstr;
                checkPrincipalPermission(casted.getModuleAllowedRoles(), moduleName);
            }
            final JSObject funSource = serverModule;
            funSource.keySet().stream().forEach((String aKey) -> {
                Object oFun = funSource.getMember(aKey);
                if (oFun instanceof JSObject && ((JSObject) oFun).isFunction()) {
                    functionProps.add(aKey);
                }
            });
        } catch (AccessControlException ex) {
            permitted = false;
        }
        return new CreateServerModuleResponse(getRequest().getID(), moduleName, functionProps, permitted);
    }

    /**
     * Checks module roles.
     *
     * @param anAllowedRoles
     * @param aModuleName
     * @throws Exception
     */
    public void checkPrincipalPermission(Set<String> anAllowedRoles, String aModuleName) throws Exception {
        if (anAllowedRoles != null && !anAllowedRoles.isEmpty()) {
            PlatypusPrincipal principal = getServerCore().getPrincipal();
            if (principal == null || !principal.hasAnyRole(anAllowedRoles)) {
                throw new AccessControlException(String.format("Access denied to %s module for '%s'.",//NOI18N
                        aModuleName,
                        principal != null ? principal.getName() : null));
            }
        }
    }

    static JSObject runModule(AppCache aAppCache, String aModuleName) throws Exception {
        JSObject serverModule = null;
        ScriptDocument scriptDoc = null;
        ApplicationElement appElement = aAppCache.get(aModuleName);
        if (appElement != null && appElement.isModule()) {
            scriptDoc = Dom2ScriptDocument.transform(appElement.getContent());
        }
        if (scriptDoc != null) {
            if (!scriptDoc.hasModuleAnnotation(JsDoc.Tag.PUBLIC_TAG)) {
                throw new AccessControlException(String.format("Public access to module %s is denied.", aModuleName));//NOI18N
            }
            serverModule = ScriptUtils.createModule(aModuleName);
        } else {
            throw new IllegalArgumentException(String.format("No module: %s, or it is not a module", aModuleName));
        }
        return serverModule;
    }
}
