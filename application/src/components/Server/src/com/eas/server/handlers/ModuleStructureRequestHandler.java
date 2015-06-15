/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.server.RequestHandler;
import com.eas.client.AppElementFiles;
import com.eas.client.ModuleStructure;
import com.eas.client.RemoteModulesProxy;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.cache.ScriptDocument;
import com.eas.client.login.AnonymousPlatypusPrincipal;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.threetier.requests.ModuleStructureRequest;
import com.eas.script.Scripts;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import com.eas.util.JSONUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.AccessControlException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import javax.security.auth.AuthPermission;

/**
 *
 * @author mg
 */
public class ModuleStructureRequestHandler extends RequestHandler<ModuleStructureRequest, ModuleStructureRequest.Response> {

    public static final String ACCESS_DENIED_MSG = "Access denied to application element '%s' [ %s ] for user %s";

    public ModuleStructureRequestHandler(PlatypusServerCore aServerCore, ModuleStructureRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    public void handle(Session aSession, Consumer<ModuleStructureRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        try {
            String moduleOrResourceName = getRequest().getModuleOrResourceName();
            if (moduleOrResourceName == null || moduleOrResourceName.isEmpty()) {
                moduleOrResourceName = serverCore.getDefaultAppElement();
            }
            AppElementFiles files = serverCore.getIndexer().nameToFiles(moduleOrResourceName);
            if (files != null) {
                // Security check
                checkModuleRoles(moduleOrResourceName, files);
                // Actual work
                serverCore.getModules().getModule(moduleOrResourceName, Scripts.getSpace(), (ModuleStructure aStructure) -> {
                    String localPath = serverCore.getModules().getLocalPath();
                    Set<String> structure = new HashSet<>();
                    aStructure.getParts().getFiles().stream().forEach((File f) -> {
                        String resourceName = f.getPath().substring(localPath.length());
                        resourceName = resourceName.replace("\\", "/");
                        if (resourceName.startsWith("/")) {
                            resourceName = resourceName.substring(1);
                        }
                        structure.add(resourceName);
                    });
                    StringBuilder json = JSONUtils.o(new StringBuilder(RemoteModulesProxy.STRUCTURE_PROP_NAME), JSONUtils.as(structure.toArray(new String[]{})),
                            new StringBuilder(RemoteModulesProxy.CLIENT_DEPENDENCIES_PROP_NAME), JSONUtils.as(aStructure.getClientDependencies().toArray(new String[]{})),
                            new StringBuilder(RemoteModulesProxy.QUERY_DEPENDENCIES_PROP_NAME), JSONUtils.as(aStructure.getQueryDependencies().toArray(new String[]{})),
                            new StringBuilder(RemoteModulesProxy.SERVER_DEPENDENCIES_PROP_NAME), JSONUtils.as(aStructure.getServerDependencies().toArray(new String[]{}))
                    );
                    ModuleStructureRequest.Response resp = new ModuleStructureRequest.Response(json.toString());
                    onSuccess.accept(resp);
                }, onFailure);
            } else {
                onFailure.accept(new FileNotFoundException(moduleOrResourceName));
            }
        } catch (Exception ex) {
            onFailure.accept(ex);
        }
    }

    private void checkModuleRoles(String aModuleName, AppElementFiles aAppElementFiles) throws Exception {
        if (aAppElementFiles != null && aAppElementFiles.hasExtension(PlatypusFiles.JAVASCRIPT_EXTENSION)) {
            ScriptDocument jsDoc = serverCore.getScriptsConfigs().get(aModuleName, aAppElementFiles);
            Set<String> rolesAllowed = jsDoc.getModuleAllowedRoles();
            PlatypusPrincipal principal = (PlatypusPrincipal) Scripts.getContext().getPrincipal();
            if (rolesAllowed != null && !principal.hasAnyRole(rolesAllowed)) {
                throw new AccessControlException(String.format(ACCESS_DENIED_MSG, aModuleName, getRequest().getModuleOrResourceName(), principal.getName()), principal instanceof AnonymousPlatypusPrincipal ? new AuthPermission("*") : null);
            }
        }
    }
}
