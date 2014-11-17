/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.server.SessionRequestHandler;
import com.eas.client.AppElementFiles;
import com.eas.client.ModuleStructure;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.cache.ScriptDocument;
import com.eas.client.threetier.requests.ModuleStructureRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.AccessControlException;
import java.util.Set;
import java.util.function.Consumer;

/**
 *
 * @author mg
 */
public class ModuleStructureRequestHandler extends SessionRequestHandler<ModuleStructureRequest, ModuleStructureRequest.Response> {

    public static final String ACCESS_DENIED_MSG = "Access denied to application element '%s' [ %s ] for user %s";

    public ModuleStructureRequestHandler(PlatypusServerCore aServerCore, ModuleStructureRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    public void handle2(Session aSession, Consumer<ModuleStructureRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        try {
            String moduleOrResourceName = getRequest().getModuleOrResourceName();
            if (moduleOrResourceName == null || moduleOrResourceName.isEmpty()) {
                moduleOrResourceName = serverCore.getDefaultAppElement();
            }
            AppElementFiles files = serverCore.getIndexer().nameToFiles(moduleOrResourceName);
            if (files != null) {
                // Security check
                checkModuleRoles(aSession, moduleOrResourceName, files);
                // Actual work
                serverCore.getModules().getModule(moduleOrResourceName, (ModuleStructure aStructure) -> {
                    ModuleStructureRequest.Response resp = new ModuleStructureRequest.Response();
                    String localPath = serverCore.getModules().getLocalPath();
                    aStructure.getParts().getFiles().stream().forEach((File f) -> {
                        String resourceName = f.getPath().substring(localPath.length());
                        resourceName = resourceName.replace("\\", "/");
                        if (resourceName.startsWith("/")) {
                            resourceName = resourceName.substring(1);
                        }
                        resp.getStructure().add(resourceName);
                    });
                    resp.getClientDependencies().addAll(aStructure.getClientDependencies());
                    resp.getServerDependencies().addAll(aStructure.getServerDependencies());
                    resp.getQueryDependencies().addAll(aStructure.getQueryDependencies());
                    onSuccess.accept(resp);
                }, onFailure);
            } else {
                onFailure.accept(new FileNotFoundException(moduleOrResourceName));
            }
        } catch (Exception ex) {
            onFailure.accept(ex);
        }
    }

    private void checkModuleRoles(Session aSession, String aModuleName, AppElementFiles aAppElementFiles) throws Exception {
        if (aAppElementFiles.hasExtension(PlatypusFiles.JAVASCRIPT_EXTENSION)) {
            ScriptDocument jsDoc = serverCore.getScriptsConfigs().get(aModuleName, aAppElementFiles);
            Set<String> rolesAllowed = jsDoc.getModuleAllowedRoles();
            if (rolesAllowed != null && !aSession.getPrincipal().hasAnyRole(rolesAllowed)) {
                throw new AccessControlException(String.format(ACCESS_DENIED_MSG, aModuleName, getRequest().getModuleOrResourceName(), aSession.getPrincipal().getName()));
            }
        }
    }
}
