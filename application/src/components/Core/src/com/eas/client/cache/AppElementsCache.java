/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.eas.client.AppCache;
import com.eas.client.Client;
import com.eas.client.ClientConstants;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.threetier.PlatypusNativeClient;
import com.eas.util.FileUtils;
import com.eas.xml.dom.XmlDom2String;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public abstract class AppElementsCache<T extends Client> extends FreqCache<String, ApplicationElement> implements AppCache {

    public static final String PROPERTY_REQUIRED_MSG = "Property %s is required";
    public static final String APP_ELEMENT_PROPERTIES_FILE_NAME = "entity.properties";
    public static final String APP_ELEMENT_TXT_CONTENT_FILE_NAME = "entity.txt";
    public static final String APP_ELEMENT_BIN_CONTENT_FILE_NAME = "entity.bin";
    protected T client = null;
    protected String CACHED_ENTITIES_PATH;

    public AppElementsCache(T aClient) throws Exception {
        super();
        client = aClient;
        initializeFileCache();
    }

    protected void initializeFileCache() throws Exception {
        //Make file cache directories
        CACHED_ENTITIES_PATH = System.getProperty(ClientConstants.USER_HOME_PROP_NAME);
        if (!CACHED_ENTITIES_PATH.endsWith(File.separator)) {
            CACHED_ENTITIES_PATH += File.separator;
        }
        CACHED_ENTITIES_PATH += ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME;
        File newDir = new File(CACHED_ENTITIES_PATH);
        if (!newDir.exists()) {
            newDir.mkdir();
        }
        CACHED_ENTITIES_PATH += File.separator + ClientConstants.ENTITIES_CACHE_DIRECTORY_NAME;
        newDir = new File(CACHED_ENTITIES_PATH);
        if (!newDir.exists()) {
            newDir.mkdir();
        }
        String url = client.getSettings().getUrl();
        String urlHash = String.valueOf(Math.abs(url.hashCode()));
        String connectionPath = "app_" + urlHash;
        CACHED_ENTITIES_PATH += File.separator + connectionPath;
        newDir = new File(CACHED_ENTITIES_PATH);
        if (!newDir.exists()) {
            newDir.mkdir();
        }
    }

    /**
     * Generates path and creates it.
     *
     * @param aId Application element id.
     * @return Generated and created path name.
     */
    protected String generatePath(String aId) {
        String pathName = CACHED_ENTITIES_PATH + File.separator + aId.hashCode();//String.valueOf(aId % 100);
        File path = new File(pathName);
        if (!path.exists()) {
            path.mkdir();
        }
        pathName += File.separator + aId;
        pathName = pathName.replace('/', File.separatorChar);
        return pathName;
    }

    @Override
    public abstract boolean isActual(String aId, long aAppElementTxtContentLength, long aAppElementTxtCrc32) throws Exception;

    protected abstract ApplicationElement achieveAppElement(String aId) throws Exception;

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
    protected ApplicationElement getNewEntry(String aId) throws Exception {
        ApplicationElement appElement = getFromFileCache(aId);
        if (appElement == null || !isActual(appElement.getId(), appElement.getTxtContentLength(), appElement.getTxtCrc32())) {
            appElement = achieveAppElement(aId);
            if (appElement != null) {
                putToFileCache(appElement);
            }
        }
        return appElement;
    }

    @Override
    protected void putEntry(CacheEntry entry) {
        if (entry.value != null) {
            super.putEntry(entry);
        }
    }

    private void string2File(File aFile, String aValue) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        byte[] buffer = aValue.getBytes("UTF-16LE");
        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(aFile))) {
            out.write(buffer);
            out.flush();
        }
    }

    protected void putToFileCache(ApplicationElement aAppElement) {
        synchronized (lock) {
            if (aAppElement != null) {
                String entityId = aAppElement.getId();
                try {
                    removeFromFileCache(entityId);
                    String entityDirectory = generatePath(entityId);
                    File cachedEntityFile = new File(entityDirectory);
                    File propsFile = new File(entityDirectory + File.separator + APP_ELEMENT_PROPERTIES_FILE_NAME);
                    File txtFile = new File(entityDirectory + File.separator + APP_ELEMENT_TXT_CONTENT_FILE_NAME);
                    File binaryFile = new File(entityDirectory + File.separator + APP_ELEMENT_BIN_CONTENT_FILE_NAME);
                    assert !propsFile.exists();
                    assert !txtFile.exists();
                    assert !binaryFile.exists();
                    assert !cachedEntityFile.exists();
                    if (cachedEntityFile.mkdirs()
                            && propsFile.createNewFile()
                            && txtFile.createNewFile()
                            && binaryFile.createNewFile()) {
                        StringBuilder propsString = new StringBuilder();
                        propsString.append(ClientConstants.F_MDENT_ID);
                        propsString.append("=");
                        propsString.append(entityId);
                        propsString.append(ClientConstants.CRLF);
                        String lName = aAppElement.getName();
                        if (lName != null) {
                            propsString.append(ClientConstants.F_MDENT_NAME);
                            propsString.append("=");
                            propsString.append(lName);
                            propsString.append(ClientConstants.CRLF);
                        }
                        propsString.append(ClientConstants.F_MDENT_ORDER);
                        propsString.append("=");
                        propsString.append(String.valueOf(aAppElement.getOrder()));
                        propsString.append(ClientConstants.CRLF);
                        propsString.append(ClientConstants.F_MDENT_TYPE);
                        propsString.append("=");
                        propsString.append(String.valueOf(aAppElement.getType()));
                        propsString.append(ClientConstants.CRLF);
                        if (aAppElement.getParentId() != null) {
                            propsString.append(ClientConstants.F_MDENT_PARENT_ID);
                            propsString.append("=");
                            propsString.append(String.valueOf(aAppElement.getParentId()));
                            propsString.append(ClientConstants.CRLF);
                        }

                        string2File(propsFile, propsString.toString());

                        if (aAppElement.getType() == ClientConstants.ET_RESOURCE) {
                            FileUtils.writeBytes(binaryFile, aAppElement.getBinaryContent());
                        } else {
                            if (aAppElement.getContent() != null) {
                                string2File(txtFile, XmlDom2String.transform(aAppElement.getContent()));
                            }
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusNativeClient.class.getName()).log(Level.SEVERE, null, ex);
                    removeFromFileCache(entityId);
                }
            }
        }
    }

    protected void removeFromFileCache(String aId) {
        if (aId != null) {
            try {
                String cachedPath = generatePath(aId);
                File cachedPathDirectory = new File(cachedPath);
                File propsFile = new File(cachedPath + File.separator + APP_ELEMENT_PROPERTIES_FILE_NAME);
                File txtFile = new File(cachedPath + File.separator + APP_ELEMENT_TXT_CONTENT_FILE_NAME);
                File binaryFile = new File(cachedPath + File.separator + APP_ELEMENT_BIN_CONTENT_FILE_NAME);
                if (txtFile.exists()) {
                    txtFile.delete();
                }
                if (binaryFile.exists()) {
                    binaryFile.delete();
                }
                if (propsFile.exists()) {
                    propsFile.delete();
                }
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

    private String file2String(File aFile) throws FileNotFoundException, IOException {
        byte[] buffer = new byte[(int) aFile.length()];
        try (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(aFile))) {
            int read = bin.read(buffer);
            assert read == buffer.length;
        }
        return new String(buffer, "UTF-16LE");
    }

    protected ApplicationElement getFromFileCache(String aId) throws Exception {
        synchronized (lock) {
            ApplicationElement appElement = null;
            try {
                String entityDirectoryPath = generatePath(aId);
                File cachedEntityDirectory = new File(entityDirectoryPath);
                File propsFile = new File(entityDirectoryPath + File.separator + APP_ELEMENT_PROPERTIES_FILE_NAME);
                File txtFile = new File(entityDirectoryPath + File.separator + APP_ELEMENT_TXT_CONTENT_FILE_NAME);
                File binaryFile = new File(entityDirectoryPath + File.separator + APP_ELEMENT_BIN_CONTENT_FILE_NAME);
                if (cachedEntityDirectory.exists() && cachedEntityDirectory.isDirectory()
                        && propsFile.exists() && !propsFile.isDirectory()
                        && txtFile.exists() && !txtFile.isDirectory()
                        && binaryFile.exists() && !binaryFile.isDirectory()
                        && propsFile.length() <= Integer.MAX_VALUE
                        && txtFile.length() <= Integer.MAX_VALUE
                        && binaryFile.length() <= Integer.MAX_VALUE) {
                    // properties
                    String propsString = file2String(propsFile);
                    String[] propsStrings = propsString.split("\n");
                    if (propsStrings != null) {
                        appElement = new ApplicationElement();
                        boolean wasId = false;
                        boolean wasType = false;
                        boolean wasName = false;
                        for (int i = 0; i < propsStrings.length; i++) {
                            if (propsStrings[i] != null && !propsStrings[i].isEmpty()) {
                                String propString = propsStrings[i];
                                propString = propString.replaceAll("\r", "");

                                int equIndex = propString.indexOf("=");
                                if (equIndex != -1) {
                                    String lKey = propString.substring(0, equIndex);
                                    String lValue = propString.substring(equIndex + 1, propString.length());
                                    if (lKey != null && lValue != null) {
                                        lKey = lKey.trim();
                                        lValue = lValue.trim();
                                        if (ClientConstants.F_MDENT_ID.equalsIgnoreCase(lKey)) {
                                            appElement.setId(lValue);
                                            wasId = true;
                                        } else if (ClientConstants.F_MDENT_TYPE.equalsIgnoreCase(lKey)) {
                                            appElement.setType(Integer.valueOf(lValue));
                                            wasType = true;
                                        } else if (ClientConstants.F_MDENT_NAME.equalsIgnoreCase(lKey)) {
                                            appElement.setName(lValue);
                                            wasName = true;
                                        } else if (ClientConstants.F_MDENT_ORDER.equalsIgnoreCase(lKey)) {
                                            appElement.setOrder(Double.valueOf(lValue));
                                        } else if (ClientConstants.F_MDENT_PARENT_ID.equalsIgnoreCase(lKey)) {
                                            appElement.setParentId(lValue);
                                        }
                                    }
                                }
                            }
                        }
                        if (!wasId) {
                            throw new IllegalStateException(String.format(PROPERTY_REQUIRED_MSG, ClientConstants.F_MDENT_ID));
                        }
                        if (!wasType) {
                            throw new IllegalStateException(String.format(PROPERTY_REQUIRED_MSG, ClientConstants.F_MDENT_TYPE));
                        }
                        if (!wasName) {
                            throw new IllegalStateException(String.format(PROPERTY_REQUIRED_MSG, ClientConstants.F_MDENT_NAME));
                        }
                    }
                    // content
                    if (appElement.getType() == ClientConstants.ET_RESOURCE) {
                        appElement.setBinaryContent(FileUtils.readBytes(binaryFile));
                    } else {
                        appElement.setTxtContent(file2String(txtFile));
                    }
                }
            } catch (IOException | NumberFormatException | IllegalStateException ex) {
                appElement = null;
                Logger.getLogger(PlatypusNativeClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            return appElement;
        }
    }
}
