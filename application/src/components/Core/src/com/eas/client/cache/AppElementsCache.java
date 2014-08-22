package com.eas.client.cache;

import com.eas.client.AppCache;
import com.eas.client.ClientConstants;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.threetier.platypus.PlatypusNativeClient;
import com.eas.util.FileUtils;
import java.io.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public abstract class AppElementsCache extends FreqCache<String, ApplicationElement> implements AppCache {

    protected String basePath;

    public AppElementsCache(String aAppNameHash) throws Exception {
        this(aAppNameHash, null);
    }

    public AppElementsCache(String aAppNameHash, String aBasePath) throws Exception {
        super();
        if (aBasePath != null && !aBasePath.isEmpty()) {
            basePath = aBasePath;
        } else {
            basePath = basePathInUserProfile(aAppNameHash);
        }
    }

    public static String basePathInUserProfile(String aAppNameHash) throws Exception {
        //Make file cache directories
        String basePath = System.getProperty(ClientConstants.USER_HOME_PROP_NAME);
        if (!basePath.endsWith(File.separator)) {
            basePath += File.separator;
        }
        basePath += ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME;
        File newDir = new File(basePath);
        if (!newDir.exists()) {
            newDir.mkdir();
        }
        basePath += File.separator + ClientConstants.ENTITIES_CACHE_DIRECTORY_NAME;
        newDir = new File(basePath);
        if (!newDir.exists()) {
            newDir.mkdir();
        }
        basePath += File.separator + aAppNameHash;
        newDir = new File(basePath);
        if (!newDir.exists()) {
            newDir.mkdir();
        }
        return basePath;
    }

    /**
     * Generates path and creates it.
     *
     * @param aAppelementName Application element name.
     * @return Generated and created path name.
     */
    protected String generateAppElementPath(String aAppelementName) {
        String pathName = basePath + File.separator + String.valueOf(Math.abs(aAppelementName.hashCode()) % 100);
        File path = new File(pathName);
        if (!path.exists()) {
            path.mkdir();
        }
        pathName += File.separator + aAppelementName;
        pathName = pathName.replace('/', File.separatorChar);
        return pathName;
    }

    @Override
    public String translateScriptPath(ApplicationElement aAppElement) throws Exception {
        if (aAppElement != null) {
            if (aAppElement.getType() == ClientConstants.ET_RESOURCE) {
                return basePath + File.separator + aAppElement.getName().replace('/', File.separatorChar);
            } else if (aAppElement.isModule()) {
                return generateAppElementPath(aAppElement.getName()) + File.separator + aAppElement.getName() + "." + PlatypusFiles.JAVASCRIPT_EXTENSION;
            }
        }
        return "";
    }

    @Override
    public abstract boolean isActual(String aId, long aTxtContentLength, long aTxtCrc32, Consumer<Boolean> onSuccess, Consumer<Exception> onFailure) throws Exception;

    protected abstract ApplicationElement achieveAppElement(String aAppElementId, Consumer<ApplicationElement> onSuccess, Consumer<Exception> onFailure) throws Exception;

    /**
     * Revomes application element from both memory and file caches.
     *
     * @param aId Application element identifier.
     * @throws Exception
     */
    @Override
    public void remove(String aId) throws Exception {
        synchronized (lock) {
            super.remove(aId);
            removeFromFileCache(aId);
        }
    }

    @Override
    protected ApplicationElement getNewEntry(String aId, Consumer<ApplicationElement> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (onSuccess != null) {
            ApplicationElement appElement = getFromFileCache(aId);
            if (appElement != null) {
                isActual(appElement.getId(), appElement.getTxtContentLength(), appElement.getTxtCrc32(), (Boolean actual) -> {
                    if (actual) {
                        onSuccess.accept(appElement);
                    } else {
                        try {
                            achieveAppElement(aId, (ApplicationElement aAppElement) -> {
                                if (aAppElement != null) {
                                    putToFileCache(aAppElement);
                                }
                                onSuccess.accept(aAppElement);
                            }, onFailure);
                        } catch (Exception ex) {
                            if (onFailure != null) {
                                onFailure.accept(ex);
                            }
                        }
                    }
                }, onFailure);
            } else {
                achieveAppElement(aId, (ApplicationElement aAppElement) -> {
                    if (aAppElement != null) {
                        putToFileCache(aAppElement);
                    }
                    onSuccess.accept(aAppElement);
                }, onFailure);
            }
            return null;
        } else {
            ApplicationElement appElement = getFromFileCache(aId);
            if (appElement == null || !isActual(appElement.getId(), appElement.getTxtContentLength(), appElement.getTxtCrc32(), null, null)) {
                appElement = achieveAppElement(aId, null, null);
                if (appElement != null) {
                    putToFileCache(appElement);
                }
            }
            return appElement;
        }
    }

    @Override
    protected void putEntry(CacheEntry entry) {
        if (entry.value != null) {
            super.putEntry(entry);
        }
    }

    protected void putToFileCache(ApplicationElement aAppElement) {
        synchronized (lock) {
            if (aAppElement != null) {
                String appElementName = aAppElement.getId();
                if (aAppElement.getType() == ClientConstants.ET_RESOURCE) {
                    String fileName = basePath + File.separator + appElementName;
                    fileName = fileName.replace('/', File.separatorChar);
                    int lastSlash = fileName.lastIndexOf(File.separatorChar);
                    String dirName = fileName.substring(0, lastSlash);
                    (new File(dirName)).mkdirs();
                    File binaryFile = new File(fileName);
                    if (binaryFile.exists()) {
                        binaryFile.delete();
                    }
                    try {
                        binaryFile.createNewFile();
                        FileUtils.writeBytes(binaryFile, aAppElement.getBinaryContent());
                    } catch (IOException ex) {
                        if (binaryFile.exists()) {
                            binaryFile.delete();
                        }
                    }
                } else {
                    removeFromFileCache(appElementName);
                    try {
                        String entityPath = generateAppElementPath(appElementName);
                        File cachedEntityFile = new File(entityPath);
                        assert !cachedEntityFile.exists();
                        if (cachedEntityFile.mkdirs()) {
                            AppElementFiles.createAppElementFiles(new File(entityPath), aAppElement);
                        }
                    } catch (AppElementFilesException ex) {
                        Logger.getLogger(PlatypusNativeClient.class.getName()).log(Level.SEVERE, null, ex);
                        removeFromFileCache(appElementName);
                    }
                }
            }
        }
    }

    protected void removeFromFileCache(String aId) {
        if (aId != null) {
            try {
                String cachedPath = generateAppElementPath(aId);
                File cachedPathDirectory = new File(cachedPath);
                if (cachedPathDirectory.exists()) {
                    String[] filesNames = cachedPathDirectory.list();
                    for (String fileName : filesNames) {
                        File file = new File(cachedPath + File.separator + fileName);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                    cachedPathDirectory.delete();
                }
            } catch (Exception ex) {
                Logger.getLogger(PlatypusNativeClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    protected ApplicationElement getFromFileCache(String aAppElementName) throws Exception {
        synchronized (lock) {
            String resourcePath = basePath + File.separator + aAppElementName;
            if ((new File(resourcePath)).exists()) {// Seems it is a resource
                ApplicationElement appElement = new ApplicationElement();
                appElement.setId(aAppElementName);
                appElement.setType(ClientConstants.ET_RESOURCE);
                byte[] content = FileUtils.readBytes(new File(resourcePath));
                appElement.setBinaryContent(content);
                return appElement;
            } else {
                String appElementDirPath = generateAppElementPath(aAppElementName);
                File appElementDir = new File(appElementDirPath);
                if (appElementDir.exists()) {
                    AppElementFiles files = new AppElementFiles("");
                    for (String fName : appElementDir.list()) {
                        files.addFile(new File(appElementDir, fName));
                    }
                    return files.getApplicationElement();
                } else {
                    return null;
                }
            }
        }
    }
}
