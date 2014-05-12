/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.cache;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.ClientConstants;
import com.eas.client.metadata.ApplicationElement;
import com.eas.script.JsDoc;
import com.eas.util.FileUtils;
import com.eas.util.StringUtils;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.misc.UUDecoder;
import sun.misc.UUEncoder;

/**
 *
 * @author vv
 */
public class AppElementFiles {

    public final static String DATAMODEL_TAG_NAME = "datamodel";
    public final static String CHECK_OPTIONAL_TAG_EXCEPTION_MSG = "In Platypus documents should be one or zero tags: ";
    public final static String CHECK_REQUIRED_TAG_EXCEPTION_MSG = "In Platypus documents should be exactly one tag: ";
    public final static String WRONG_ROOT_ELEMENT_EXCEPTION_MSG = "Wrong root element: %s for application element id: %s";
    public final static String CREATE_FILE_EXCEPTION_MSG = "Error creating file: ";
    private static final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    private final String parentDirectoryAppElementId;
    private final Set<File> files = new HashSet<>();
    private final Set<String> filesExtensions = new HashSet<>();
    protected StringBuilder accumulator;

    public AppElementFiles(String aParentDirectoryAppElementId) {
        super();
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
                    accumulatedText = accumulatedText.replaceAll("\r", "\n");// mac style
                    accumulatedText = accumulatedText.replaceAll(ClientConstants.CRLF, "\n");// windows style
                    // WARNING! The accumulatedText is changed every time the underliyng files change
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
                            case PlatypusFiles.REPORT_LAYOUT_EXTENSION:
                            case PlatypusFiles.REPORT_LAYOUT_EXTENSION_X: {
                                assert rootTag != null;
                                Element reportLayoutTag = appElementDom.createElement(ApplicationElement.XLS_LAYOUT_TAG_NAME);
                                reportLayoutTag.setAttribute(ApplicationElement.EXT_TAG_ATTRIBUTE_NAME, FileUtils.getFileExtension(file));
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
        } else if (hasExtension(PlatypusFiles.JAVASCRIPT_EXTENSION) && hasExtension(PlatypusFiles.MODEL_EXTENSION) && (hasExtension(PlatypusFiles.REPORT_LAYOUT_EXTENSION) || hasExtension(PlatypusFiles.REPORT_LAYOUT_EXTENSION_X))) {
            return ClientConstants.ET_REPORT;
        } else if (hasExtension(PlatypusFiles.JAVASCRIPT_EXTENSION) && hasExtension(PlatypusFiles.MODEL_EXTENSION)) {
            return ClientConstants.ET_COMPONENT;
        } else if (hasExtension(PlatypusFiles.SQL_EXTENSION) && hasExtension(PlatypusFiles.MODEL_EXTENSION)) {
            return ClientConstants.ET_QUERY;
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
                return PlatypusFiles.JAVASCRIPT_EXTENSION.equals(ext) || PlatypusFiles.MODEL_EXTENSION.equals(ext) || PlatypusFiles.REPORT_LAYOUT_EXTENSION.equals(ext) || PlatypusFiles.REPORT_LAYOUT_EXTENSION_X.equals(ext);
            case ClientConstants.ET_QUERY:
                return PlatypusFiles.SQL_EXTENSION.equals(ext) || PlatypusFiles.DIALECT_EXTENSION.equals(ext) || PlatypusFiles.MODEL_EXTENSION.equals(ext) || PlatypusFiles.OUT_EXTENSION.equals(ext);
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

    public static void createAppElementFiles(File parentDirectory, ApplicationElement appElement) throws AppElementFilesException {
        try {
            assert appElement.getType() != ClientConstants.ET_FOLDER;
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            Document appElementDocument = appElement.getContent();
            if (appElementDocument != null) {
                Element rootElement = getRootElement(appElementDocument);
                switch (appElement.getType()) {
                    case ClientConstants.ET_COMPONENT:
                        if (ApplicationElement.SCRIPT_ROOT_TAG_NAME.equals(rootElement.getNodeName())) {
                            createModuleFiles(parentDirectory, appElement, rootElement, documentBuilder);
                        } else {
                            throw new AppElementFilesException(String.format(WRONG_ROOT_ELEMENT_EXCEPTION_MSG, rootElement.getNodeName(), appElement.getId()));
                        }
                        break;
                    case ClientConstants.ET_FORM:
                        if (ApplicationElement.FORM_ROOT_TAG_NAME.equals(rootElement.getNodeName())) {
                            createFormFiles(parentDirectory, appElement, rootElement, documentBuilder);
                        } else {
                            throw new AppElementFilesException(String.format(WRONG_ROOT_ELEMENT_EXCEPTION_MSG, rootElement.getNodeName(), appElement.getId()));
                        }
                        break;
                    case ClientConstants.ET_REPORT:
                        if (ApplicationElement.SCRIPT_ROOT_TAG_NAME.equals(rootElement.getNodeName())) {
                            createReportFiles(parentDirectory, appElement, rootElement, documentBuilder);
                        } else {
                            throw new AppElementFilesException(String.format(WRONG_ROOT_ELEMENT_EXCEPTION_MSG, rootElement.getNodeName(), appElement.getId()));
                        }
                        break;
                    case ClientConstants.ET_QUERY:
                        if (ApplicationElement.QUERY_ROOT_TAG_NAME.equals(rootElement.getNodeName())) {
                            createQueryFiles(parentDirectory, appElement, rootElement, documentBuilder);
                        } else {
                            throw new AppElementFilesException("Wrong root element: " + rootElement.getNodeName() + " for application element id: " + appElement.getId()); // NOI18N  
                        }
                        break;
                    case ClientConstants.ET_DB_SCHEME:
                        saveXml2File(parentDirectory, appElement.getName(), PlatypusFiles.DB_SCHEME_EXTENSION, rootElement, documentBuilder);
                        break;
                    default:
                        throw new AppElementFilesException("Unknown type: " + appElement.getType() + " for application element id: " + appElement.getId()); // NOI18N
                }
            } else {
                throw new AppElementFilesException("Broken content for the application element id:" + appElement.getId()); // NOI18N
            }
        } catch (IOException ex) {
            Logger.getLogger(AppElementFiles.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(AppElementFiles.class.getName()).log(Level.SEVERE, "Error creating files for application element", ex); // NOI18N
        }
    }

    private static Element getRootElement(Document appElementDocument) throws AppElementFilesException {
        NodeList roots = appElementDocument.getChildNodes();
        if (roots.getLength() != 1) {
            throw new AppElementFilesException("Should be exactly one root tag in Platypus documents."); // NOI18N
        }
        return (Element) roots.item(0);
    }

    private static void createModuleFiles(File parentDirectory, ApplicationElement appElement, Element rootNode, DocumentBuilder documentBuilder) throws AppElementFilesException, IOException {
        saveTextContentFile(parentDirectory, appElement, PlatypusFiles.JAVASCRIPT_EXTENSION, ApplicationElement.SCRIPT_SOURCE_TAG_NAME, rootNode);
        saveExportedElementFile(parentDirectory, appElement.getName(), PlatypusFiles.MODEL_EXTENSION, DATAMODEL_TAG_NAME, rootNode, documentBuilder);
    }

    private static void createFormFiles(File parentDirectory, ApplicationElement appElement, Element rootNode, DocumentBuilder documentBuilder) throws AppElementFilesException, IOException {
        createModuleFiles(parentDirectory, appElement, rootNode, documentBuilder);
        saveExportedElementFile(parentDirectory, appElement.getName(), PlatypusFiles.FORM_EXTENSION, ApplicationElement.LAYOUT_TAG_NAME, rootNode, documentBuilder);
    }

    private static void createReportFiles(File parentDirectory, ApplicationElement appElement, Element rootNode, DocumentBuilder documentBuilder) throws AppElementFilesException, IOException {
        createModuleFiles(parentDirectory, appElement, rootNode, documentBuilder);
        NodeList layoutNodes = rootNode.getElementsByTagName(ApplicationElement.XLS_LAYOUT_TAG_NAME);
        if (layoutNodes.getLength() != 1) {
            throw new AppElementFilesException(CHECK_REQUIRED_TAG_EXCEPTION_MSG + ApplicationElement.XLS_LAYOUT_TAG_NAME);
        }
        String ext = layoutNodes.item(0).getAttributes().getNamedItem(ApplicationElement.EXT_TAG_ATTRIBUTE_NAME) != null ? layoutNodes.item(0).getAttributes().getNamedItem(ApplicationElement.EXT_TAG_ATTRIBUTE_NAME).getNodeValue() : PlatypusFiles.REPORT_LAYOUT_EXTENSION;
        String aName = checkFileName(appElement.getName());
        File reportLayoutFile = new File(parentDirectory, addExtension(aName, ext));
        if (reportLayoutFile.createNewFile()) {
            UUDecoder decoder = new UUDecoder();
            FileUtils.writeBytes(reportLayoutFile, decoder.decodeBuffer(layoutNodes.item(0).getTextContent()));
        } else {
            throw new AppElementFilesException(CREATE_FILE_EXCEPTION_MSG + reportLayoutFile.getAbsolutePath());
        }
    }

    private static void createQueryFiles(File parentDirectory, ApplicationElement appElement, Element rootNode, DocumentBuilder documentBuilder) throws AppElementFilesException, IOException {
        saveTextContentFile(parentDirectory, appElement, PlatypusFiles.SQL_EXTENSION, ApplicationElement.SQL_TAG_NAME, rootNode);
        NodeList contentNodes = rootNode.getElementsByTagName(ApplicationElement.FULL_SQL_TAG_NAME);
        Node contentNode = contentNodes.item(0);
        if (contentNode != null && !contentNode.getTextContent().trim().equals("")) {
            saveTextContentFile(parentDirectory, appElement, PlatypusFiles.DIALECT_EXTENSION, ApplicationElement.FULL_SQL_TAG_NAME, rootNode);
        } else {
            saveFile(parentDirectory, appElement.getName(), PlatypusFiles.DIALECT_EXTENSION, "");
        }
        //Remove Sql and Sql dialect nodes and export XML
        NodeList sqlSourceNodes = rootNode.getElementsByTagName(ApplicationElement.SQL_TAG_NAME);
        if (sqlSourceNodes.getLength() != 1) {
            throw new AppElementFilesException(CHECK_REQUIRED_TAG_EXCEPTION_MSG + ApplicationElement.SQL_TAG_NAME);
        }
        rootNode.removeChild(sqlSourceNodes.item(0));
        NodeList dialectSourceNodes = rootNode.getElementsByTagName(ApplicationElement.FULL_SQL_TAG_NAME);
        if (sqlSourceNodes.getLength() > 1) {
            throw new AppElementFilesException(CHECK_OPTIONAL_TAG_EXCEPTION_MSG + ApplicationElement.FULL_SQL_TAG_NAME);
        }
        Node dialectNode = dialectSourceNodes.item(0);
        if (dialectNode != null) {
            rootNode.removeChild(dialectSourceNodes.item(0));
        }
        contentNodes = rootNode.getElementsByTagName(ApplicationElement.OUTPUT_FIELDS_TAG_NAME);
        contentNode = contentNodes.item(0);
        if (contentNode != null) {
            saveExportedElementFile(parentDirectory, appElement.getName(), PlatypusFiles.OUT_EXTENSION, ApplicationElement.OUTPUT_FIELDS_TAG_NAME, rootNode, documentBuilder);
        } else {
            saveFile(parentDirectory, appElement.getName(), PlatypusFiles.OUT_EXTENSION, PlatypusFiles.OUT_EMPTY_CONTENT);
        }
        saveExportedElementFile(parentDirectory, appElement.getName(), PlatypusFiles.MODEL_EXTENSION, DATAMODEL_TAG_NAME, rootNode, documentBuilder);
    }

    private static String checkFileName(String aFileName) {
        String aName = StringUtils.replaceUnsupportedSymbolsinFileNames(aFileName);
        if (aFileName.compareToIgnoreCase(aName) != 0) {
            Logger.getLogger(AppElementFiles.class.getName()).log(Level.INFO, "File name was changed from {0} to {1}",
                    new Object[]{aFileName, aName});
        }
        return aName;
    }

    private static void saveExportedElementFile(File parentDirectory, String name, String ext, String tagName, Element node, DocumentBuilder documentBuilder) throws AppElementFilesException, IOException {
        Document doc = documentBuilder.newDocument();
        doc.setXmlStandalone(true);
        NodeList importNodes = node.getElementsByTagName(tagName);
        Node importNode = importNodes.item(0);
        if (importNode != null) {
            doc.adoptNode(importNode);
            doc.appendChild(importNode);
            saveFile(parentDirectory, name, ext, XmlDom2String.transform(doc));
        }
    }

    private static void saveXml2File(File parentDirectory, String fileName, String aExt, Element rootElement, DocumentBuilder documentBuilder) throws IOException, AppElementFilesException {
        Document doc = documentBuilder.newDocument();
        doc.setXmlStandalone(true);
        doc.adoptNode(rootElement);
        doc.appendChild(rootElement);
        saveFile(parentDirectory, fileName, aExt, XmlDom2String.transform(doc));
    }

    private static void saveTextContentFile(File parentDirectory, ApplicationElement aAppElement, String ext, String tagName, Element node) throws AppElementFilesException, IOException {
        NodeList contentNodes = node.getElementsByTagName(tagName);
        Node contentNode = contentNodes.item(0);
        if (contentNode != null) {
            String aContent = checkAnnotation(contentNode.getTextContent(), aAppElement.getId());
            saveFile(parentDirectory, aAppElement.getName(), ext, aContent);
        }
    }

    private static void saveFile(File aParentDirectory, String aFileName, String aExt, String aContent) throws IOException, AppElementFilesException {
        String aName = checkFileName(aFileName);
        File file = new File(aParentDirectory, addExtension(aName, aExt));
        if (file.createNewFile()) {
            FileUtils.writeString(file, aContent, PlatypusFiles.DEFAULT_ENCODING);
        } else {
            throw new AppElementFilesException(CREATE_FILE_EXCEPTION_MSG + file.getAbsolutePath());
        }
    }

    private static String checkAnnotation(String aContent, String aAnnotationValue) {
        String aName = PlatypusFilesSupport.getAnnotationValue(aContent, JsDoc.Tag.NAME_TAG);
        if (aName != null && !aName.isEmpty()) {
            return aContent;
        } else {
            return PlatypusFilesSupport.replaceAnnotationValue(aContent, JsDoc.Tag.NAME_TAG, aAnnotationValue);
        }
    }

    private static String addExtension(String name, String ext) {
        return name + FileUtils.EXTENSION_SEPARATOR + ext;
    }

}
