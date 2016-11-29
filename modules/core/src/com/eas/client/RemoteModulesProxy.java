/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.cache.PlatypusFiles;
import com.eas.client.threetier.PlatypusConnection;
import com.eas.client.threetier.requests.ModuleStructureRequest;
import com.eas.client.threetier.requests.ResourceRequest;
import com.eas.concurrent.CallableConsumer;
import com.eas.script.Scripts;
import com.eas.util.FileUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author mg
 */
public class RemoteModulesProxy implements ModulesProxy {

    public static final String SERVER_DEPENDENCIES_PROP_NAME = "serverDependencies";
    public static final String QUERY_DEPENDENCIES_PROP_NAME = "queryDependencies";
    public static final String CLIENT_DEPENDENCIES_PROP_NAME = "clientDependencies";
    public static final String STRUCTURE_PROP_NAME = "structure";
    public static final String LENGTH_PROP_NAME = "length";

    protected PlatypusConnection conn;
    protected Path basePath;
    protected Map<String, File> id2files = new ConcurrentHashMap<>();

    public RemoteModulesProxy(PlatypusConnection aConn) {
        super();
        conn = aConn;
        basePath = makePathInUserProfile(Math.abs(conn.getUrl().hashCode()) + "");
    }

    @Override
    public Path getLocalPath() {
        return basePath;
    }

    @Override
    public ModuleStructure getModule(String aName, Scripts.Space aSpace, Consumer<ModuleStructure> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (onSuccess != null) {
            requestModuleStructure(aName, aSpace, (ModuleStructureRequest.Response structureResp) -> {
                try {
                    ModuleStructure structure = new ModuleStructure();
                    JSObject jsStructure = (JSObject) aSpace.parseJson(structureResp.getJson());
                    readCommons(jsStructure, structure);
                    JSObject jsParts = (JSObject) jsStructure.getMember(STRUCTURE_PROP_NAME);
                    int partsLength = JSType.toInteger(jsParts.getMember(LENGTH_PROP_NAME));
                    for (int i = 0; i < partsLength; i++) {
                        String resourceName = JSType.toString(jsParts.getSlot(i));
                        getResource(resourceName, aSpace, (File aSynced) -> {
                            structure.getParts().addFile(aSynced);
                            if (structure.getParts().getFiles().size() == partsLength) {
                                id2files.put(aName, structure.getParts().findFileByExtension(PlatypusFiles.JAVASCRIPT_EXTENSION));
                                onSuccess.accept(structure);
                            }
                        }, onFailure);
                    }
                } catch (Exception ex) {
                    if (onFailure != null) {
                        onFailure.accept(ex);
                    }
                }
            }, onFailure);
            return null;
        } else {
            ModuleStructureRequest.Response structureResp = requestModuleStructure(aName, null, null, null);
            ModuleStructure structure = new ModuleStructure();
            JSObject jsStructure = (JSObject) aSpace.parseJson(structureResp.getJson());
            readCommons(jsStructure, structure);
            JSObject jsParts = (JSObject) jsStructure.getMember(STRUCTURE_PROP_NAME);
            int partsLength = JSType.toInteger(jsParts.getMember(LENGTH_PROP_NAME));
            for (int i = 0; i < partsLength; i++) {
                String resourceName = JSType.toString(jsParts.getSlot(i));
                File synced = getResource(resourceName, aSpace, null, null);
                structure.getParts().addFile(synced);
            }
            id2files.put(aName, structure.getParts().findFileByExtension(PlatypusFiles.JAVASCRIPT_EXTENSION));
            return structure;
        }
    }

    @Override
    public File getResource(String aResourceName, Scripts.Space aSpace, Consumer<File> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (onSuccess != null) {
            String cachePathName = constructResourcePath(aResourceName);
            File cachePath = new File(cachePathName);
            syncResource(cachePath, aResourceName, aSpace, (Void aVoid) -> {
                onSuccess.accept(cachePath);
            }, onFailure);
            return null;
        } else {
            String cachePathName = constructResourcePath(aResourceName);
            File cachePath = new File(cachePathName);
            syncResource(cachePath, aResourceName, null, null, null);
            return cachePath;
        }
    }

    private void readCommons(JSObject jsStructure, ModuleStructure structure) {
        JSObject jsClientDependencies = (JSObject) jsStructure.getMember(CLIENT_DEPENDENCIES_PROP_NAME);
        int clientDepsLength = JSType.toInteger(jsClientDependencies.getMember(LENGTH_PROP_NAME));
        for (int i = 0; i < clientDepsLength; i++) {
            String dep = JSType.toString(jsClientDependencies.getSlot(i));
            structure.getClientDependencies().add(dep);
        }
        JSObject jsQueryDependencies = (JSObject) jsStructure.getMember(QUERY_DEPENDENCIES_PROP_NAME);
        int queryDepsLength = JSType.toInteger(jsQueryDependencies.getMember(LENGTH_PROP_NAME));
        for (int i = 0; i < queryDepsLength; i++) {
            String dep = JSType.toString(jsQueryDependencies.getSlot(i));
            structure.getQueryDependencies().add(dep);
        }
        JSObject jsServerDependencies = (JSObject) jsStructure.getMember(SERVER_DEPENDENCIES_PROP_NAME);
        int serverDepsLength = JSType.toInteger(jsServerDependencies.getMember(LENGTH_PROP_NAME));
        for (int i = 0; i < serverDepsLength; i++) {
            String dep = JSType.toString(jsServerDependencies.getSlot(i));
            structure.getServerDependencies().add(dep);
        }
    }

    private void syncResource(File cachePath, String aName, Scripts.Space aSpace, Consumer<Void> onSuccess, Consumer<Exception> onFailure) throws Exception {
        Date localTimeStamp = null;
        if (cachePath.exists() && cachePath.isFile()) {
            localTimeStamp = new Date(cachePath.lastModified());
        }
        CallableConsumer<Void, ResourceRequest.Response> doWork = (ResourceRequest.Response resourceResp) -> {
            if (resourceResp.getContent() != null) {
                cachePath.getParentFile().mkdirs();
                boolean deleted = cachePath.delete();
                if (!deleted) {
                    if (cachePath.isDirectory()) {
                        FileUtils.clearDirectory(cachePath, false);
                        cachePath.delete();
                    }
                }
                cachePath.createNewFile();
                try (OutputStream out = new BufferedOutputStream(new FileOutputStream(cachePath))) {
                    out.write(resourceResp.getContent());
                    out.flush();
                }
                cachePath.setLastModified(resourceResp.getTimeStamp().getTime());
            }
            return null;
        };
        if (onSuccess != null) {
            requestResource(localTimeStamp, aName, aSpace, (ResourceRequest.Response resourceResp) -> {
                try {
                    doWork.call(resourceResp);
                    try {
                        onSuccess.accept(null);
                    } catch (Exception ex) {
                        Logger.getLogger(RemoteModulesProxy.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (Exception ex) {
                    if (onFailure != null) {
                        onFailure.accept(ex);
                    }
                }
            }, onFailure);
        } else {
            ResourceRequest.Response resourceResp = requestResource(localTimeStamp, aName, null, null, null);
            doWork.call(resourceResp);
        }
    }

    private ModuleStructureRequest.Response requestModuleStructure(String aName, Scripts.Space aSpace, Consumer<ModuleStructureRequest.Response> onSuccess, Consumer<Exception> onFailure) throws Exception {
        ModuleStructureRequest req = new ModuleStructureRequest(aName);
        if (onSuccess != null) {
            conn.enqueueRequest(req, aSpace, onSuccess, onFailure);
            return null;
        } else {
            return conn.executeRequest(req);
        }
    }

    private ResourceRequest.Response requestResource(Date aTimeStamp, String aResourceName, Scripts.Space aSpace, Consumer<ResourceRequest.Response> onSuccess, Consumer<Exception> onFailure) throws Exception {
        ResourceRequest req = new ResourceRequest(aTimeStamp, aResourceName);
        if (onSuccess != null) {
            conn.enqueueRequest(req, aSpace, onSuccess, onFailure);
            return null;
        } else {
            return conn.executeRequest(req);
        }
    }

    private Path makePathInUserProfile(String aAppNameHash) {
        //Make file cache directories
        String path = System.getProperty(ClientConstants.USER_HOME_PROP_NAME);
        if (!path.endsWith(File.separator)) {
            path += File.separator;
        }
        path += ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME;
        File newDir = new File(path);
        if (!newDir.exists()) {
            newDir.mkdir();
        }
        path += File.separator + ClientConstants.ENTITIES_CACHE_DIRECTORY_NAME;
        newDir = new File(path);
        if (!newDir.exists()) {
            newDir.mkdir();
        }
        path += File.separator + aAppNameHash;
        newDir = new File(path);
        if (!newDir.exists()) {
            newDir.mkdir();
        }
        return Paths.get(newDir.toURI());
    }

    /**
     * Generates path for some platypus resource.
     *
     * @param aResourceName A resource name.
     * @return Generated path name.
     */
    protected String constructResourcePath(String aResourceName) {
        String pathName = basePath + File.separator + aResourceName;
        return pathName.replace('/', File.separatorChar);
    }

    @Override
    public File nameToFile(String aName) throws Exception {
        return id2files.get(aName);
    }

    @Override
    public String getDefaultModuleName(File aFile) {
        String defaultModuleName = basePath.relativize(Paths.get(aFile.toURI())).toString().replace(File.separator, "/");
        defaultModuleName = defaultModuleName.substring(0, defaultModuleName.length() - PlatypusFiles.JAVASCRIPT_FILE_END.length());
        return defaultModuleName;
    }

}
