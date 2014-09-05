/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.threetier.PlatypusConnection;
import com.eas.client.threetier.requests.ModuleStructureRequest;
import com.eas.client.threetier.requests.ResourceRequest;
import com.eas.concurrent.CallableConsumer;
import com.eas.util.FileUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class RemoteModulesProxy implements ModulesProxy {

    protected PlatypusConnection conn;
    protected String basePath;

    public RemoteModulesProxy(PlatypusConnection aConn) {
        super();
        conn = aConn;
        basePath = makePathInUserProfile(conn.getUrl().hashCode() + "");
    }

    @Override
    public String getLocalPath() {
        return basePath;
    }

    @Override
    public ModuleStructure getModule(String aName, Consumer<ModuleStructure> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (onSuccess != null) {
            requestModuleStructure(aName, (ModuleStructureRequest.Response structureResp) -> {
                try {
                    ModuleStructure structure = new ModuleStructure();
                    structure.getClientDependencies().addAll(structureResp.getClientDependencies());
                    structure.getServerDependencies().addAll(structureResp.getServerDependencies());
                    structure.getQueryDependencies().addAll(structureResp.getQueryDependencies());
                    boolean resource = structureResp.getStructure().isEmpty();
                    if (resource) {
                        // aName is the same with resource name
                        String cachePathName = constructResourcePath(aName);
                        File cachePath = new File(cachePathName);
                        syncResource(cachePath, aName, (Void aVoid) -> {
                            structure.getParts().addFile(cachePath);
                            onSuccess.accept(structure);
                        }, onFailure);
                    } else {
                        for (String resourceName : structureResp.getStructure()) {
                            String cachePathName = constructResourcePath(resourceName);
                            File cachePath = new File(cachePathName);
                            syncResource(cachePath, resourceName, (Void aVoid) -> {
                                synchronized (structure) {
                                    structure.getParts().addFile(cachePath);
                                    if (structure.getParts().getFiles().size() == structureResp.getStructure().size()) {
                                        onSuccess.accept(structure);
                                    }
                                }
                            }, onFailure);
                        }
                    }
                } catch (Exception ex) {
                    if (onFailure != null) {
                        onFailure.accept(ex);
                    }
                }
            }, onFailure);
            return null;
        } else {
            ModuleStructureRequest.Response structureResp = requestModuleStructure(aName, null, null);
            ModuleStructure structure = new ModuleStructure();
            structure.getClientDependencies().addAll(structureResp.getClientDependencies());
            structure.getServerDependencies().addAll(structureResp.getServerDependencies());
            structure.getQueryDependencies().addAll(structureResp.getQueryDependencies());
            boolean resource = structureResp.getStructure().isEmpty();
            if (resource) {
                String cachePathName = constructResourcePath(aName);
                File cachePath = new File(cachePathName);
                syncResource(cachePath, aName, null, null);
                structure.getParts().addFile(cachePath);
            } else {
                for (String resourceName : structureResp.getStructure()) {
                    String cachePathName = constructResourcePath(resourceName);
                    File cachePath = new File(cachePathName);
                    syncResource(cachePath, resourceName, null, null);
                    structure.getParts().addFile(cachePath);
                }
            }
            return structure;
        }
    }

    private void syncResource(File cachePath, String aName, Consumer<Void> onSuccess, Consumer<Exception> onFailure) throws Exception {
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
                        FileUtils.clearDirectory(cachePath);
                        cachePath.delete();
                    }
                }
                cachePath.createNewFile();
                try (OutputStream out = new BufferedOutputStream(new FileOutputStream(cachePath))) {
                    out.write(resourceResp.getContent());
                }
                cachePath.setLastModified(resourceResp.getTimeStamp().getTime());
            }
            return null;
        };
        if (onSuccess != null) {
            requestResource(localTimeStamp, aName, (ResourceRequest.Response resourceResp) -> {
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
            ResourceRequest.Response resourceResp = requestResource(localTimeStamp, aName, null, null);
            doWork.call(resourceResp);
        }
    }

    private ModuleStructureRequest.Response requestModuleStructure(String aName, Consumer<ModuleStructureRequest.Response> onSuccess, Consumer<Exception> onFailure) throws Exception {
        ModuleStructureRequest req = new ModuleStructureRequest(aName);
        if (onSuccess != null) {
            conn.enqueueRequest(req, onSuccess, onFailure);
            return null;
        } else {
            return conn.executeRequest(req);
        }
    }

    private ResourceRequest.Response requestResource(Date aTimeStamp, String aResourceName, Consumer<ResourceRequest.Response> onSuccess, Consumer<Exception> onFailure) throws Exception {
        ResourceRequest req = new ResourceRequest(aTimeStamp, aResourceName);
        if (onSuccess != null) {
            conn.enqueueRequest(req, onSuccess, onFailure);
            return null;
        } else {
            return conn.executeRequest(req);
        }
    }

    private String makePathInUserProfile(String aAppNameHash) {
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
        return path;
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
}
