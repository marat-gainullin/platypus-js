/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.ClientConstants;
import com.eas.client.metadata.ApplicationElement;
import com.eas.util.FileUtils;
import com.eas.xml.dom.Source2XmlDom;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import sun.misc.UUEncoder;

/**
 *
 * @author vv
 */
public class AppElementFiles {

    private static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    private final String parentDirectoryAppElementId;
    private final Set<File> files = new HashSet<>();
    private final Set<String> filesExtensions = new HashSet<>();
    protected StringBuilder accumulator;

    public AppElementFiles(String aParentDirectoryAppElementId) {
        parentDirectoryAppElementId = aParentDirectoryAppElementId;
    }

    public String getParentDirectoryAppElementId() {
        return parentDirectoryAppElementId;
    }

    public ApplicationElement getApplicationElement() {
        accumulator = new StringBuilder();
        try {
            ApplicationElement appElement = new ApplicationElement();
            String fileName = getAppElementName();
            assert fileName != null && !fileName.isEmpty();
            appElement.setName(fileName);
            Integer appElementType = getAppElementType();
            if (appElementType != null) {
                appElement.setType(appElementType);
                String appElementId = getAppElementId(appElementType);
                if (appElementId != null && !appElementId.isEmpty()) {
                    appElement.setId(appElementId);
                    appElement.setParentId(parentDirectoryAppElementId);
                    appElement.setContent(getAppElementContent(appElementType));
                    String accumulatedText = accumulator.toString();
                    // The following trick is applicable only for FilesCache
                    // accumulatedText is changed every time the underliyng files change
                    // but it is not exact text representation of content DOM.
                    appElement.setTxtContentLength((long) accumulatedText.length());
                    appElement.setTxtCrc32(ApplicationElement.calcTxtCrc32(accumulatedText));
                    return appElement;
                } else {
                    Logger.getLogger(AppElementFiles.class.getName()).log(Level.WARNING, "Application element id is null or empty for file name: {0}.", fileName); //NOI18N
                }
            }
            return null;
        } finally {
            accumulator = null;
        }
    }

    public void addFile(File file) {
        files.add(file);
        if (!file.isDirectory()) {
            filesExtensions.add(FileUtils.getFileExtension(file));
        }
    }

    public Set<File> getFiles() {
        return Collections.unmodifiableSet(files);
    }

    private String getAppElementName() {
        if (files.size() > 0) {
            return FileUtils.removeExtension(files.iterator().next().getName());
        }
        return null;
    }

    public String getAppElementId(int appElementType) {
        if (appElementType == ClientConstants.ET_COMPONENT || appElementType == ClientConstants.ET_FORM || appElementType == ClientConstants.ET_REPORT) {
            return PlatypusFilesSupport.getAppElementIdByAnnotation(findFileByExtension(PlatypusFiles.JAVASCRIPT_EXTENSION));
        } else if (appElementType == ClientConstants.ET_QUERY) {
            return PlatypusFilesSupport.getAppElementIdByAnnotation(findFileByExtension(PlatypusFiles.SQL_EXTENSION));
        } else if (appElementType == ClientConstants.ET_CONNECTION) {
            return PlatypusFilesSupport.getAppElementIdForConnectionAppElement(findFileByExtension(PlatypusFiles.CONNECTION_EXTENSION));
        } else if (appElementType == ClientConstants.ET_DB_SCHEME) {
            return IDGenerator.genID().toString();
        }
        return null;
    }

    private Document getAppElementContent(int appElementType) {
        try {
            if (ClientConstants.ET_FOLDER == appElementType) {
                return null;
            } else {
                DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
                Document appElementDom = documentBuilder.newDocument();
		appElementDom.setXmlStandalone(true);
                Element rootTag = null;
                switch (appElementType) {
                    case ClientConstants.ET_COMPONENT:
                        rootTag = appElementDom.createElement(ApplicationElement.SCRIPT_ROOT_TAG_NAME);
                        break;
                    case ClientConstants.ET_FORM:
                        rootTag = appElementDom.createElement(ApplicationElement.FORM_ROOT_TAG_NAME);
                        break;
                    case ClientConstants.ET_REPORT:
                        rootTag = appElementDom.createElement(ApplicationElement.SCRIPT_ROOT_TAG_NAME);
                        break;
                    case ClientConstants.ET_QUERY:
                        rootTag = appElementDom.createElement(ApplicationElement.QUERY_ROOT_TAG_NAME);
                        break;
                    case ClientConstants.ET_CONNECTION:
                    case ClientConstants.ET_DB_SCHEME: {
                        String contentPart = FileUtils.readString(files.iterator().next(), PlatypusFiles.DEFAULT_ENCODING);
                        accumulator.append(contentPart);
                        return Source2XmlDom.transform(contentPart);
                    }
                }
                //Create childen elements
                for (File file : files) {
                    String ext = FileUtils.getFileExtension(file);
                    if (isAllowedFileExtension(appElementType, ext)) {
                        switch (ext) {
                            case PlatypusFiles.JAVASCRIPT_EXTENSION: {
                                assert rootTag != null;
                                Element jsSourceTag = appElementDom.createElement(ApplicationElement.SCRIPT_SOURCE_TAG_NAME);
                                String contentPart = FileUtils.readString(file, PlatypusFiles.DEFAULT_ENCODING);
                                accumulator.append(contentPart);
                                jsSourceTag.setTextContent(contentPart);
                                rootTag.appendChild(jsSourceTag);
                                break;
                            }
                            case PlatypusFiles.MODEL_EXTENSION: {
                                assert rootTag != null;
                                String contentPart = FileUtils.readString(file, PlatypusFiles.DEFAULT_ENCODING);
                                accumulator.append(contentPart);
                                Document modelDocument = Source2XmlDom.transform(contentPart);
                                Element modelElement = (Element) appElementDom.importNode(modelDocument.getDocumentElement(), true);
                                rootTag.appendChild(modelElement);
                                break;
                            }
                            case PlatypusFiles.FORM_EXTENSION: {
                                assert rootTag != null;
                                String contentPart = FileUtils.readString(file, PlatypusFiles.DEFAULT_ENCODING);
                                accumulator.append(contentPart);
                                Document layoutDocument = Source2XmlDom.transform(contentPart);
                                Element layoutRootElement = (Element) appElementDom.importNode(layoutDocument.getDocumentElement(), true);
                                rootTag.appendChild(layoutRootElement);
                                break;
                            }
                            case PlatypusFiles.REPORT_LAYOUT_EXTENSION: {
                                assert rootTag != null;
                                Element reportLayoutTag = appElementDom.createElement(ApplicationElement.XLS_LAYOUT_TAG_NAME);
                                UUEncoder encoder = new UUEncoder();
                                String contentPart = encoder.encodeBuffer(FileUtils.readBytes(file));
                                accumulator.append(contentPart);
                                reportLayoutTag.setTextContent(contentPart);
                                rootTag.appendChild(reportLayoutTag);
                                break;
                            }
                            case PlatypusFiles.SQL_EXTENSION: {
                                assert rootTag != null;
                                Element sqlSourceTag = appElementDom.createElement(ApplicationElement.SQL_TAG_NAME);
                                String contentPart = FileUtils.readString(file, PlatypusFiles.DEFAULT_ENCODING);
                                accumulator.append(contentPart);
                                sqlSourceTag.setTextContent(contentPart);
                                rootTag.appendChild(sqlSourceTag);
                                break;
                            }
                            case PlatypusFiles.DIALECT_EXTENSION: {
                                assert rootTag != null;
                                Element sqlDialectSourceTag = appElementDom.createElement(ApplicationElement.FULL_SQL_TAG_NAME);
                                String contentPart = FileUtils.readString(file, PlatypusFiles.DEFAULT_ENCODING);
                                accumulator.append(contentPart);
                                sqlDialectSourceTag.setTextContent(contentPart);
                                rootTag.appendChild(sqlDialectSourceTag);
                                break;
                            }
                            case PlatypusFiles.OUT_EXTENSION: {
                                assert rootTag != null;
                                String contentPart = FileUtils.readString(file, PlatypusFiles.DEFAULT_ENCODING);
                                accumulator.append(contentPart);
                                if (contentPart != null && !contentPart.isEmpty()) {
                                    Document hintsDoc = Source2XmlDom.transform(contentPart);
                                    Element hintsElement = (Element) appElementDom.importNode(hintsDoc.getDocumentElement(), true);
                                    rootTag.appendChild(hintsElement);
                                }
                                break;
                            }
                        }
                    } else {
                        Logger.getLogger(AppElementFiles.class.getName()).log(Level.WARNING, "Wrong file: {0} for application element with type: {1}", // NOI18N
                                new Object[]{file.getAbsolutePath(), Integer.valueOf(appElementType)});
                    }
                }
                appElementDom.appendChild(rootTag);
                return appElementDom;
            }

        } catch (ParserConfigurationException | IOException ex) {
            Logger.getLogger(AppElementFiles.class.getName()).log(Level.SEVERE, "Error building XML for application element", ex); //
        }
        return null;
    }

    public Integer getAppElementType() {
        if (hasExtension(PlatypusFiles.JAVASCRIPT_EXTENSION) && hasExtension(PlatypusFiles.MODEL_EXTENSION) && hasExtension(PlatypusFiles.FORM_EXTENSION)) {
            return ClientConstants.ET_FORM;
        } else if (hasExtension(PlatypusFiles.JAVASCRIPT_EXTENSION) && hasExtension(PlatypusFiles.MODEL_EXTENSION) && hasExtension(PlatypusFiles.REPORT_LAYOUT_EXTENSION)) {
            return ClientConstants.ET_REPORT;
        } else if (hasExtension(PlatypusFiles.JAVASCRIPT_EXTENSION) && hasExtension(PlatypusFiles.MODEL_EXTENSION)) {
            return ClientConstants.ET_COMPONENT;
        } else if (hasExtension(PlatypusFiles.SQL_EXTENSION) && hasExtension(PlatypusFiles.MODEL_EXTENSION)) {
            return ClientConstants.ET_QUERY;
        } else if (hasExtension(PlatypusFiles.CONNECTION_EXTENSION)) {
            return ClientConstants.ET_CONNECTION;
        } else if (hasExtension(PlatypusFiles.DB_SCHEME_EXTENSION)) {
            return ClientConstants.ET_DB_SCHEME;
        }
        return null;
    }

    public boolean hasExtension(String ext) {
        return filesExtensions.contains(ext);
    }

    private File findFileByExtension(String ext) {
        for (File file : files) {
            if (FileUtils.getFileExtension(file).equals(ext)) {
                return file;
            }
        }
        return null;
    }

    private File removeFileByExtension(String ext) {
        if (ext != null) {
            for (File file : files) {
                if (ext.equals(FileUtils.getFileExtension(file))) {
                    files.remove(file);
                    filesExtensions.remove(ext);
                    return file;
                }
            }
        }
        return null;
    }

    private boolean isAllowedFileExtension(int appElementType, String ext) {
        switch (appElementType) {
            case ClientConstants.ET_FOLDER:
                return true;
            case ClientConstants.ET_COMPONENT:
                return PlatypusFiles.JAVASCRIPT_EXTENSION.equals(ext) || PlatypusFiles.MODEL_EXTENSION.equals(ext);
            case ClientConstants.ET_FORM:
                return PlatypusFiles.JAVASCRIPT_EXTENSION.equals(ext) || PlatypusFiles.MODEL_EXTENSION.equals(ext) || PlatypusFiles.FORM_EXTENSION.equals(ext);
            case ClientConstants.ET_REPORT:
                return PlatypusFiles.JAVASCRIPT_EXTENSION.equals(ext) || PlatypusFiles.MODEL_EXTENSION.equals(ext) || PlatypusFiles.REPORT_LAYOUT_EXTENSION.equals(ext);
            case ClientConstants.ET_QUERY:
                return PlatypusFiles.SQL_EXTENSION.equals(ext) || PlatypusFiles.DIALECT_EXTENSION.equals(ext) || PlatypusFiles.MODEL_EXTENSION.equals(ext) || PlatypusFiles.OUT_EXTENSION.equals(ext);
            case ClientConstants.ET_CONNECTION:
                return PlatypusFiles.CONNECTION_EXTENSION.equals(ext);
            case ClientConstants.ET_DB_SCHEME:
                return PlatypusFiles.DB_SCHEME_EXTENSION.equals(ext);
        }
        throw new IllegalArgumentException("Unsupported Platypus application element type: " + appElementType); //NOI18N
    }

    public File removeFile(File aFile) {
        return removeFileByExtension(FileUtils.getFileExtension(aFile));
    }

    public boolean isEmpty() {
        return files.isEmpty();
    }
}
