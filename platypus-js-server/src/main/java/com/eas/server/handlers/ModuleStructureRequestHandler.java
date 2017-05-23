package com.eas.server.handlers;

import com.eas.server.RequestHandler;
import com.eas.client.ModuleStructure;
import com.eas.client.RemoteModulesProxy;
import com.eas.client.cache.ScriptDocument;
import com.eas.client.login.AnonymousPlatypusPrincipal;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.threetier.requests.ModuleStructureRequest;
import com.eas.script.Scripts;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import com.eas.util.JsonUtils;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public static final String ACCESS_DENIED_MSG = "Access denied to module '%s' [ %s ] for user %s";

    public ModuleStructureRequestHandler(PlatypusServerCore aServerCore, ModuleStructureRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    public void handle(Session aSession, Consumer<ModuleStructureRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        try {
            String moduleName = getRequest().getModuleName();
            if (moduleName == null || moduleName.isEmpty()) {
                moduleName = serverCore.getStartModuleName();
            }
            // Security check
            checkModuleRoles(moduleName);
            // Actual work
            serverCore.getModules().getModule(moduleName, Scripts.getSpace(), (ModuleStructure aStructure) -> {
                Path localAppPath = serverCore.getModules().getLocalPath();
                Set<String> structure = new HashSet<>();
                aStructure.getParts().getFiles().stream().forEach((File f) -> {
                    Path partPath = localAppPath.relativize(Paths.get(f.toURI()));
                    String resourceName = partPath.toString();
                    resourceName = resourceName.replace(File.separator, "/");
                    if (resourceName.startsWith("/")) {
                        resourceName = resourceName.substring(1);
                    }
                    structure.add(resourceName);
                });
                StringBuilder json = JsonUtils.o(new StringBuilder(RemoteModulesProxy.STRUCTURE_PROP_NAME), JsonUtils.as(structure.toArray(new String[]{})),
                        new StringBuilder(RemoteModulesProxy.CLIENT_DEPENDENCIES_PROP_NAME), JsonUtils.as(aStructure.getClientDependencies().toArray(new String[]{})),
                        new StringBuilder(RemoteModulesProxy.QUERY_DEPENDENCIES_PROP_NAME), JsonUtils.as(aStructure.getQueryDependencies().toArray(new String[]{})),
                        new StringBuilder(RemoteModulesProxy.SERVER_DEPENDENCIES_PROP_NAME), JsonUtils.as(aStructure.getServerDependencies().toArray(new String[]{}))
                );
                ModuleStructureRequest.Response resp = new ModuleStructureRequest.Response(json.toString());
                onSuccess.accept(resp);
            }, onFailure);
        } catch (Exception ex) {
            onFailure.accept(ex);
        }
    }

    private void checkModuleRoles(String aModuleName) throws Exception {
        ScriptDocument.ModuleDocument moduleDoc = serverCore.lookupModuleDocument(aModuleName);
        if (moduleDoc != null) {
            Set<String> rolesAllowed = moduleDoc.getAllowedRoles();
            PlatypusPrincipal principal = (PlatypusPrincipal) Scripts.getContext().getPrincipal();
            if (rolesAllowed != null && !principal.hasAnyRole(rolesAllowed)) {
                throw new AccessControlException(String.format(ACCESS_DENIED_MSG, aModuleName, getRequest().getModuleName(), principal.getName()), principal instanceof AnonymousPlatypusPrincipal ? new AuthPermission("*") : null);
            }
        }
    }
}
