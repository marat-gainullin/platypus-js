package com.eas.server.handlers;

import com.eas.client.RemoteServerModulesProxy;
import com.eas.server.RequestHandler;
import com.eas.client.cache.ScriptDocument;
import com.eas.client.login.AnonymousPlatypusPrincipal;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.threetier.requests.ServerModuleStructureRequest;
import com.eas.script.Scripts;
import com.eas.server.*;
import com.eas.util.JsonUtils;
import java.io.File;
import java.security.AccessControlException;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.function.Consumer;
import javax.security.auth.AuthPermission;

/**
 *
 * @author pk, mg, ab
 */
public class ServerModuleStructureRequestHandler extends RequestHandler<ServerModuleStructureRequest, ServerModuleStructureRequest.Response> {

    public ServerModuleStructureRequestHandler(PlatypusServerCore aServerCore, ServerModuleStructureRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    public void handle(Session aSession, Consumer<ServerModuleStructureRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        String moduleName = getRequest().getModuleName();
        if (moduleName == null || moduleName.isEmpty()) {
            onFailure.accept(new Exception("Module name is missing. Unnamed server modules are not allowed."));
        } else {
            Date clientModuleTime = getRequest().getTimeStamp();
            try {
                File file = serverCore.getIndexer().nameToFile(moduleName);
                ScriptDocument.ModuleDocument moduleDoc = serverCore.lookupModuleDocument(moduleName);
                if (file != null && moduleDoc != null) {
                    ServerModuleStructureRequest.Response response = new ServerModuleStructureRequest.Response(null);
                    Date serverModuleTime = new Date(file.lastModified());
                    if (clientModuleTime == null || serverModuleTime.after(clientModuleTime)) {
                        checkPrincipalPermission(moduleDoc.getAllowedRoles(), moduleName);
                        StringBuilder json = JsonUtils.o(RemoteServerModulesProxy.CREATE_MODULE_RESPONSE_FUNCTIONS_PROP, JsonUtils.as(moduleDoc.getFunctionProperties().toArray(new String[]{})).toString(),
                                RemoteServerModulesProxy.CREATE_MODULE_RESPONSE_IS_PERMITTED_PROP, String.valueOf(true)
                        );
                        response.setInfoJson(json.toString());
                        response.setTimeStamp(serverModuleTime);
                    }
                    onSuccess.accept(response);
                } else {
                    onFailure.accept(new IllegalArgumentException(String.format("No module %s, or it is not a module", moduleName)));
                }
            } catch (AccessControlException ex) {
                if (ex.getPermission() instanceof AuthPermission) {
                    onFailure.accept(ex);
                } else {
                    StringBuilder json = JsonUtils.o(RemoteServerModulesProxy.CREATE_MODULE_RESPONSE_FUNCTIONS_PROP, JsonUtils.a(Collections.emptySet().toArray(new String[]{})).toString(),
                            RemoteServerModulesProxy.CREATE_MODULE_RESPONSE_IS_PERMITTED_PROP, String.valueOf(false)
                    );
                    ServerModuleStructureRequest.Response response = new ServerModuleStructureRequest.Response(json.toString());
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
     * @param anAllowedRoles
     * @param aSubjectName
     * @throws AccessControlException
     */
    public static void checkPrincipalPermission(Set<String> anAllowedRoles, String aSubjectName) throws AccessControlException {
        if (anAllowedRoles != null && !anAllowedRoles.isEmpty()) {
            try {
                PlatypusPrincipal principal = (PlatypusPrincipal) Scripts.getContext().getPrincipal();
                if (principal == null || !principal.hasAnyRole(anAllowedRoles)) {
                    throw new AccessControlException(String.format("Access denied to %s for '%s'.",//NOI18N
                            aSubjectName,
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
