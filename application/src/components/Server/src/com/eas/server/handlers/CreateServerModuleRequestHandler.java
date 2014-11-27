/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.AppElementFiles;
import com.eas.server.SessionRequestHandler;
import com.eas.client.ServerModuleInfo;
import com.eas.client.cache.ScriptDocument;
import com.eas.client.login.AnonymousPlatypusPrincipal;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.scripts.ScriptedResource;
import com.eas.client.threetier.requests.CreateServerModuleRequest;
import com.eas.script.JsDoc;
import com.eas.script.ScriptUtils;
import com.eas.server.*;
import java.security.AccessControlException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.AuthPermission;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author pk, mg, ab
 */
public class CreateServerModuleRequestHandler extends SessionRequestHandler<CreateServerModuleRequest, CreateServerModuleRequest.Response> {

    public CreateServerModuleRequestHandler(PlatypusServerCore aServerCore, CreateServerModuleRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    protected void handle2(Session aSession, Consumer<CreateServerModuleRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        String moduleName = getRequest().getModuleName();
        if (moduleName == null || moduleName.isEmpty()) {
            onFailure.accept(new Exception("Module name is missing. Unnamed server modules are not allowed."));
        } else {
            Date clientModuleTime = getRequest().getTimeStamp();
            try {
                ScriptedResource._require(new String[]{moduleName}, new ConcurrentSkipListSet<>(), (Void v) -> {
                    try {
                        AppElementFiles files = serverCore.getIndexer().nameToFiles(moduleName);
                        JSObject jsConstr = ScriptUtils.lookupInGlobal(moduleName);
                        if (files != null && files.isModule() && jsConstr != null) {
                            Set<String> functionProps = new HashSet<>();
                            CreateServerModuleRequest.Response response = new CreateServerModuleRequest.Response(null);
                            Date serverModuleTime = files.getLastModified();
                            if (clientModuleTime == null || serverModuleTime.after(clientModuleTime)) {
                                ScriptDocument config = serverCore.getScriptsConfigs().get(moduleName, files);
                                checkPrincipalPermission(aSession, config.getModuleAllowedRoles(), moduleName);
                                // Let's check the if module is resident
                                JSObject moduleInstance = getServerCore().getSessionManager().getSystemSession().getModule(moduleName);
                                if (moduleInstance == null) {
                                    if (aSession.containsModule(moduleName)) {
                                        moduleInstance = aSession.getModule(moduleName);
                                    } else {
                                        if (config.hasModuleAnnotation(JsDoc.Tag.PUBLIC_TAG)) {
                                            moduleInstance = (JSObject) jsConstr.newObject(new Object[]{});
                                            // Let's decide if we have to register the module in user's session.
                                            if (!config.hasModuleAnnotation(JsDoc.Tag.STATELESS_TAG)) {
                                                aSession.registerModule(moduleInstance);
                                            }
                                            Logger.getLogger(CreateServerModuleRequestHandler.class.getName()).log(Level.FINE, "Created server module for script {0} with name {1}", new Object[]{getRequest().getModuleName(), moduleName});
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
                                response.setInfo(new ServerModuleInfo(moduleName, functionProps, true));
                                response.setTimeStamp(serverModuleTime);
                            }
                            onSuccess.accept(response);
                        } else {
                            onFailure.accept(new IllegalArgumentException(String.format("No module: %s, or it is not a module", moduleName)));
                        }
                    } catch (AccessControlException ex) {
                        CreateServerModuleRequest.Response response = new CreateServerModuleRequest.Response(new ServerModuleInfo(moduleName, Collections.emptySet(), false));
                        if (clientModuleTime == null) {
                            // If a client has no the resource, let's give it a chance to update the resource, when it will be permitted
                            response.setTimeStamp(new Date(0));
                        } else {
                            // Let's override client's resource timestamp to guarantee, that permitted == false will be accepted
                            response.setTimeStamp(new Date(clientModuleTime.getTime() + 1000));
                        }
                        onSuccess.accept(response);
                    } catch (Exception ex) {
                        onFailure.accept(ex);
                    }
                }, onFailure);
            } catch (Exception ex) {
                Logger.getLogger(CreateServerModuleRequestHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Checks module roles.
     *
     * @param aSession
     * @param anAllowedRoles
     * @param aModuleName
     * @throws AccessControlException
     */
    public static void checkPrincipalPermission(Session aSession, Set<String> anAllowedRoles, String aModuleName) throws AccessControlException {
        if (anAllowedRoles != null && !anAllowedRoles.isEmpty()) {
            try {
                PlatypusPrincipal principal = aSession.getPrincipal();
                if (principal == null || !principal.hasAnyRole(anAllowedRoles)) {
                    throw new AccessControlException(String.format("Access denied to %s module for '%s'.",//NOI18N
                            aModuleName,
                            principal != null ? principal.getName() : null), aSession.getPrincipal() instanceof AnonymousPlatypusPrincipal ? new AuthPermission("*") : null);
                }
            } catch (Exception ex) {
                throw new AccessControlException(ex.getMessage());
            }
        }
    }
}
