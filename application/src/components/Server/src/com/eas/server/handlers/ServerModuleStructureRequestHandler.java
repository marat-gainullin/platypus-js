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
import com.eas.client.threetier.requests.ServerModuleStructureRequest;
import com.eas.server.*;
import java.security.AccessControlException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import javax.security.auth.AuthPermission;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author pk, mg, ab
 */
public class ServerModuleStructureRequestHandler extends SessionRequestHandler<ServerModuleStructureRequest, ServerModuleStructureRequest.Response> {

    public ServerModuleStructureRequestHandler(PlatypusServerCore aServerCore, ServerModuleStructureRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    protected void handle2(Session aSession, Consumer<ServerModuleStructureRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        String moduleName = getRequest().getModuleName();
        if (moduleName == null || moduleName.isEmpty()) {
            onFailure.accept(new Exception("Module name is missing. Unnamed server modules are not allowed."));
        } else {
            Date clientModuleTime = getRequest().getTimeStamp();
            try {
                AppElementFiles files = serverCore.getIndexer().nameToFiles(moduleName);
                JSObject jsConstr = aSession.getSpace().lookupInGlobal(moduleName);
                if (files != null && files.isModule() && jsConstr != null) {
                    Set<String> functionProps = new HashSet<>();
                    ServerModuleStructureRequest.Response response = new ServerModuleStructureRequest.Response(null);
                    Date serverModuleTime = files.getLastModified();
                    if (clientModuleTime == null || serverModuleTime.after(clientModuleTime)) {
                        ScriptDocument config = serverCore.getScriptsConfigs().get(moduleName, files);
                        checkPrincipalPermission(aSession, config.getModuleAllowedRoles(), moduleName);
                        functionProps.addAll(config.getPropertyAllowedRoles().keySet());
                        response.setInfo(new ServerModuleInfo(moduleName, functionProps, true));
                        response.setTimeStamp(serverModuleTime);
                    }
                    onSuccess.accept(response);
                } else {
                    onFailure.accept(new IllegalArgumentException(String.format("No module: %s, or it is not a module", moduleName)));
                }
            } catch (AccessControlException ex) {
                if (ex.getPermission() instanceof AuthPermission) {
                    onFailure.accept(ex);
                } else {
                    ServerModuleStructureRequest.Response response = new ServerModuleStructureRequest.Response(new ServerModuleInfo(moduleName, Collections.emptySet(), false));
                    if (clientModuleTime == null) {
                        // If a client has no the resource, let's give it a chance to update the resource, when it will be permitted
                        response.setTimeStamp(new Date(0));
                    } else {
                        // Let's override client's resource timestamp to guarantee, that permitted == false will be accepted
                        response.setTimeStamp(new Date(clientModuleTime.getTime() + 1000));
                    }
                    onSuccess.accept(response);
                }
            } catch (Exception ex) {
                onFailure.accept(ex);
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
                PlatypusPrincipal principal = (PlatypusPrincipal) aSession.getSpace().getPrincipal();
                if (principal == null || !principal.hasAnyRole(anAllowedRoles)) {
                    throw new AccessControlException(String.format("Access denied to %s module for '%s'.",//NOI18N
                            aModuleName,
                            principal != null ? principal.getName() : null), principal instanceof AnonymousPlatypusPrincipal ? new AuthPermission("*") : null);
                }
            } catch (Exception ex) {
                if (ex instanceof AccessControlException) {
                    throw ex;
                } else {
                    throw new AccessControlException(ex.getMessage());
                }
            }
        }
    }
}
