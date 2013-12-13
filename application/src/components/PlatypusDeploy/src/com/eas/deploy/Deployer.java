/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.deploy;

import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.compacts.CompactClob;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.ClientConstants;
import com.eas.client.DbClient;
import com.eas.client.cache.AppElementFiles;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.cache.PlatypusFilesSupport;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.model.store.Model2XmlDom;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.queries.SqlQuery;
import com.eas.client.settings.ConnectionSettings2XmlDom;
import com.eas.script.JsDoc;
import com.eas.util.FileUtils;
import com.eas.util.StringUtils;
import com.eas.xml.dom.XmlDom2String;
import java.io.*;
import java.util.*;
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
 * Implementation of deploying Platypus application elements from sources to
 * runtime database.
 *
 * @author vv
 */
public class Deployer extends BaseDeployer {

    public static final String ID_PARAMETER_NAME = "id";
    public static final char[] FORBIDDEN_CHARS = {'\\', '/'};
    protected static final String DELETE_ENTITIES_SQL = "DELETE FROM "
            + ClientConstants.T_MTD_ENTITIES;
    protected static final String SELECT_ENTITIES_SQL = "SELECT * FROM "
            + ClientConstants.T_MTD_ENTITIES;
    protected static final String SELECT_ENTITIES_MD_SQL = SELECT_ENTITIES_SQL
            + " WHERE 0 = 1";
    protected static final String CREATE_FILE_EXCEPTION_MSG = "Error creating file: ";
    protected static final String CHECK_REQUIRED_TAG_EXCEPTION_MSG = "In Platypus documents should be exactly one tag: ";
    protected static final String CHECK_OPTIONAL_TAG_EXCEPTION_MSG = "In Platypus documents should be one or zero tags: ";
    protected static final String WRONG_ROOT_ELEMENT_EXCEPTION_MSG = "Wrong root element: %s for application element id: %s";
    private static DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    private File sourcesRoot;
    // used by deploy
    private Map<String, ApplicationElement> appElements;
    // used by import
    private Map<String, Set<ApplicationElement>> appElementsTree;

    public Deployer(String aProjectPath) {
        super(aProjectPath);
        init();
    }

    public Deployer(File aProjectDir, DbClient aClient) {
        super(aProjectDir, aClient);
        init();
    }

    private void init() {
        sourcesRoot = new File(projectDir, PlatypusFiles.PLATYPUS_PROJECT_SOURCES_ROOT);
    }

    /**
     * Deploys Platypus application component to the database
     *
     */
    public void deploy() {
        synchronized (this) {
            if (busy) {
                Logger.getLogger(Deployer.class.getName()).log(Level.WARNING, LOCKED_MSG);
                return;
            }
            busy = true;
        }
        try {
            checkDbClient();
            out.println("Deploy to database started.."); // NOI18N
            appElements = new LinkedHashMap<>();

            //Prepare application elements items
            traverseSources(sourcesRoot, null);

            //Deploy application elements to the database
            SqlQuery deleteQuery = new SqlQuery(client, DELETE_ENTITIES_SQL);
            deleteQuery.compile().enqueueUpdate();

            SqlQuery entitiesQuery = new SqlQuery(client, SELECT_ENTITIES_MD_SQL);
            entitiesQuery.setEntityId(ClientConstants.T_MTD_ENTITIES);
            Rowset rs = entitiesQuery.compile().executeQuery();
            assert rs.isEmpty();
            AppElementRowsetIndexes ri = new AppElementRowsetIndexes(rs);
            int i = 0;
            for (String appElementId : appElements.keySet()) {
                ApplicationElement appElement = appElements.get(appElementId);
                Row appElementRow = new Row(rs.getFields());
                appElementRow.setColumnObject(ri.idColumnIndex, appElementId);
                appElementRow.setColumnObject(ri.parentIdColumnIndex, appElement.getParentId());
                appElementRow.setColumnObject(ri.nameColumnIndex, appElement.getName());
                appElementRow.setColumnObject(ri.typeColumnIndex, appElement.getType());
                String txtContent;
                if (appElement.getType() == ClientConstants.ET_RESOURCE) {
                    UUEncoder encoder = new UUEncoder();
                    txtContent = encoder.encodeBuffer(appElement.getBinaryContent());
                } else {
                    txtContent = XmlDom2String.transform(appElement.getContent());
                }
                appElementRow.setColumnObject(ri.contentTxtColumnIndex, txtContent);
                appElementRow.setColumnObject(ri.contentTxtSizeColumnIndex, txtContent.length());
                appElementRow.setColumnObject(ri.contentTxtCrcColumnIndex, ApplicationElement.calcTxtCrc32(txtContent));
                rs.insert(appElementRow, true);
                i++;
            }
            client.commit(null);
            out.println(String.format("Deploy completed. %d application elements deployed.", i)); // NOI18N
            out.println();
        } catch (Exception ex) {
            client.rollback(null);
            Logger.getLogger(Deployer.class.getName()).log(Level.SEVERE, null, ex);
            err.println("Application deploying error: " + ex.getMessage()); // NOI18N
        } finally {
            synchronized (this) {
                busy = false;
            }
        }
    }

    /**
     * Removes the project's contents from the database
     *
     */
    public void undeploy() {
        synchronized (this) {
            if (busy) {
                Logger.getLogger(Deployer.class.getName()).log(Level.WARNING, LOCKED_MSG);
                return;
            }
            busy = true;
        }
        try {
            out.println("Undeploy started.."); // NOI18N
            checkDbClient();
            SqlQuery deleteQuery = new SqlQuery(client, DELETE_ENTITIES_SQL);
            SqlCompiledQuery deleteCompiledQuery = deleteQuery.compile();
            deleteCompiledQuery.enqueueUpdate();
            client.commit(null);
            out.println("Unddeploy completed."); // NOI18N
        } catch (Exception ex) {
            client.rollback(null);
            Logger.getLogger(Deployer.class.getName()).log(Level.SEVERE, null, ex);
            err.println("Error undeploy application: " + ex.getMessage()); // NOI18N
        } finally {
            synchronized (this) {
                busy = false;
            }
        }
    }

    /**
     * Import Platypus application component from the database to the project's
     * sources
     *
     */
    public void importApplication() {
        synchronized (this) {
            if (busy) {
                Logger.getLogger(Deployer.class.getName()).log(Level.WARNING, LOCKED_MSG);
                return;
            }
            busy = true;
        }
        try {
            out.println("Import from database started.."); // NOI18N
            checkDbClient();

            //Get application elements entities 
            SqlQuery selectComponentEntitiesQuery = new SqlQuery(client, SELECT_ENTITIES_SQL);
            Rowset rs = selectComponentEntitiesQuery.compile().executeQuery();

            //Build application elements tree, find root node for the component
            appElementsTree = new HashMap<>();
            AppElementRowsetIndexes ri = new AppElementRowsetIndexes(rs);
            for (int i = 1; i <= rs.size(); i++) {
                Row row = rs.getRow(i);
                Object oId = row.getColumnObject(ri.idColumnIndex);
                if (oId != null) {
                    oId = oId.toString();// some old projects can have number ids
                }
                Object oParentId = row.getColumnObject(ri.parentIdColumnIndex);
                if (oParentId != null) {
                    oParentId = oParentId.toString();// some old projects can have number ids
                }
                String parentId = (String) oParentId;

                ApplicationElement appElement = new ApplicationElement();
                appElement.setId((String) oId);
                appElement.setParentId(parentId);
                appElement.setName((String) row.getColumnObject(ri.nameColumnIndex));
                appElement.setType(((Number) row.getColumnObject(ri.typeColumnIndex)).intValue());
                String txtContent = stringifyObject(row.getColumnObject(ri.contentTxtColumnIndex));
                if (appElement.getType() == ClientConstants.ET_RESOURCE) {
                    UUDecoder decoder = new UUDecoder();
                    appElement.setBinaryContent(decoder.decodeBuffer(txtContent));
                } else {
                    appElement.setTxtContent(txtContent);
                }
                appElement.setTxtCrc32((row.getColumnObject(ri.contentTxtCrcColumnIndex) != null) ? ((Number) row.getColumnObject(ri.contentTxtCrcColumnIndex)).longValue() : 0l);
                Set<ApplicationElement> children = appElementsTree.get(parentId);
                if (children == null) {
                    children = new HashSet<>();
                    appElementsTree.put(appElement.getParentId(), children);
                }
                children.add(appElement);
            }
            FileUtils.clearDirectory(sourcesRoot);

            //Save application elements sources tree to disk
            traverseAppElements(sourcesRoot, null);
            out.println(String.format("Import completed. %d application elements imported.", rs.size())); // NOI18N
            out.println();
        } catch (Exception ex) {
            Logger.getLogger(Deployer.class.getName()).log(Level.SEVERE, null, ex);
            err.println("Error import component: " + ex.getMessage());
        } finally {
            synchronized (this) {
                busy = false;
            }
        }
    }

    private String stringifyObject(Object o) {
        if (o == null) {
            return null;
        } else if (o instanceof String) {
            return (String) o;
        } else if (o instanceof CompactClob) {
            return ((CompactClob) o).getData();
        } else {
            throw new IllegalArgumentException("Unsupported string object type: " + o.getClass().getName()); // NOI18N
        }
    }

    private void traverseAppElements(File parentDirectory, String parentId) {
        Set<ApplicationElement> children = appElementsTree.get(parentId);
        if (children != null) {
            for (ApplicationElement appElement : children) {
                try {
                    if (appElement.getType() == ClientConstants.ET_FOLDER) {
                        File directory = new File(parentDirectory, StringUtils.replaceUnsupportedSymbolsinFileNames(appElement.getName()));
                        if (directory.mkdir()) {
                            traverseAppElements(directory, appElement.getId());
                        } else {
                            throw new DeployException("Cant' create directory: " + appElement.getName() + " at path: " + parentDirectory.getAbsolutePath()); //NOI18N
                        }
                    } else if (appElement.getType() == ClientConstants.ET_RESOURCE) {
                        String path = sourcesRoot.getPath() + File.separator + appElement.getId();
                        path = path.replace('/', File.separatorChar);
                        File file = new File(path);
                        if (file.createNewFile()) {
                            FileUtils.writeBytes(file, appElement.getBinaryContent());
                        } else {
                            throw new DeployException(CREATE_FILE_EXCEPTION_MSG + file.getAbsolutePath());
                        }
                    } else {
                        createAppElementFiles(parentDirectory, appElement);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Deployer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (DeployException ex) {
                    Logger.getLogger(Deployer.class.getName()).log(Level.WARNING, getAppElementErrorMessage(appElement, ex.getMessage()), ex); // NOI18N
                    err.println(getAppElementErrorMessage(appElement, ex.getMessage())); // NOI18N
                }
            }
        }
    }

    private String getAppElementErrorMessage(ApplicationElement appElement, String errorMsg) {
        return String.format("File or directory can not be created: %s for application element id: %s name: %s", errorMsg, appElement.getId(), appElement.getName()); // NOI18N
    }

    private void createAppElementFiles(File parentDirectory, ApplicationElement appElement) throws DeployException {
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
                            throw new DeployException(String.format(WRONG_ROOT_ELEMENT_EXCEPTION_MSG, rootElement.getNodeName(), appElement.getId()));
                        }
                        break;
                    case ClientConstants.ET_FORM:
                        if (ApplicationElement.FORM_ROOT_TAG_NAME.equals(rootElement.getNodeName())) {
                            createFormFiles(parentDirectory, appElement, rootElement, documentBuilder);
                        } else {
                            throw new DeployException(String.format(WRONG_ROOT_ELEMENT_EXCEPTION_MSG, rootElement.getNodeName(), appElement.getId()));
                        }
                        break;
                    case ClientConstants.ET_REPORT:
                        if (ApplicationElement.SCRIPT_ROOT_TAG_NAME.equals(rootElement.getNodeName())) {
                            createReportFiles(parentDirectory, appElement, rootElement, documentBuilder);
                        } else {
                            throw new DeployException(String.format(WRONG_ROOT_ELEMENT_EXCEPTION_MSG, rootElement.getNodeName(), appElement.getId()));
                        }
                        break;
                    case ClientConstants.ET_QUERY:
                        if (ApplicationElement.QUERY_ROOT_TAG_NAME.equals(rootElement.getNodeName())) {
                            createQueryFiles(parentDirectory, appElement, rootElement, documentBuilder);
                        } else {
                            throw new DeployException("Wrong root element: " + rootElement.getNodeName() + " for application element id: " + appElement.getId()); // NOI18N  
                        }
                        break;
                    case ClientConstants.ET_CONNECTION:
                        rootElement.setAttribute(ConnectionSettings2XmlDom.NAME_ATTR_NAME, appElement.getId());
                        saveXml2File(parentDirectory, appElement.getName(), PlatypusFiles.CONNECTION_EXTENSION, rootElement, documentBuilder);
                        break;
                    case ClientConstants.ET_DB_SCHEME:
                        saveXml2File(parentDirectory, appElement.getName(), PlatypusFiles.DB_SCHEME_EXTENSION, rootElement, documentBuilder);
                        break;
                    default:
                        throw new DeployException("Unknown type: " + appElement.getType() + " for application element id: " + appElement.getId()); // NOI18N
                }
            } else {
                throw new DeployException("Broken content for the application element id:" + appElement.getId()); // NOI18N
            }
        } catch (IOException ex) {
            Logger.getLogger(Deployer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Deployer.class.getName()).log(Level.SEVERE, "Error creating files for application element", ex); // NOI18N
        }
    }

    private static Element getRootElement(Document appElementDocument) throws DeployException {
        NodeList roots = appElementDocument.getChildNodes();
        if (roots.getLength() != 1) {
            throw new DeployException("In Platypus documents should be exactly one root tag."); // NOI18N
        }
        return (Element) roots.item(0);
    }

    private static void createModuleFiles(File parentDirectory, ApplicationElement appElement, Element rootNode, DocumentBuilder documentBuilder) throws DeployException, IOException {
        saveTextContentFile(parentDirectory, appElement, PlatypusFiles.JAVASCRIPT_EXTENSION, ApplicationElement.SCRIPT_SOURCE_TAG_NAME, rootNode);
        saveExportedElementFile(parentDirectory, appElement.getName(), PlatypusFiles.MODEL_EXTENSION, Model2XmlDom.DATAMODEL_TAG_NAME, rootNode, documentBuilder);
    }

    private static void createFormFiles(File parentDirectory, ApplicationElement appElement, Element rootNode, DocumentBuilder documentBuilder) throws DeployException, IOException {
        createModuleFiles(parentDirectory, appElement, rootNode, documentBuilder);
        saveExportedElementFile(parentDirectory, appElement.getName(), PlatypusFiles.FORM_EXTENSION, ApplicationElement.LAYOUT_TAG_NAME, rootNode, documentBuilder);
    }

    private static void createReportFiles(File parentDirectory, ApplicationElement appElement, Element rootNode, DocumentBuilder documentBuilder) throws DeployException, IOException {
        createModuleFiles(parentDirectory, appElement, rootNode, documentBuilder);
        NodeList layoutNodes = rootNode.getElementsByTagName(ApplicationElement.XLS_LAYOUT_TAG_NAME);
        if (layoutNodes.getLength() != 1) {
            throw new DeployException(CHECK_REQUIRED_TAG_EXCEPTION_MSG + ApplicationElement.XLS_LAYOUT_TAG_NAME);
        }
        String ext = layoutNodes.item(0).getAttributes().getNamedItem(ApplicationElement.EXT_TAG_ATTRIBUTE_NAME) != null ? layoutNodes.item(0).getAttributes().getNamedItem(ApplicationElement.EXT_TAG_ATTRIBUTE_NAME).getNodeValue() : PlatypusFiles.REPORT_LAYOUT_EXTENSION;
        String aName = checkFileName(appElement.getName());
        File reportLayoutFile = new File(parentDirectory, addExtension(aName, ext));
        if (reportLayoutFile.createNewFile()) {
            UUDecoder decoder = new UUDecoder();
            FileUtils.writeBytes(reportLayoutFile, decoder.decodeBuffer(layoutNodes.item(0).getTextContent()));
        } else {
            throw new DeployException(CREATE_FILE_EXCEPTION_MSG + reportLayoutFile.getAbsolutePath());
        }
    }

    private static String checkFileName(String aFileName) {
        String aName = StringUtils.replaceUnsupportedSymbolsinFileNames(aFileName);
        if (aFileName.compareToIgnoreCase(aName) != 0) {
            Logger.getLogger(Deployer.class.getName()).log(Level.INFO, "File name was changed from {0} to {1}",
                    new Object[]{aFileName, aName});
        }
        return aName;
    }

    private static String checkAnnotation(String aContent, String aAnnotationValue) {
        String aName = PlatypusFilesSupport.getAnnotationValue(aContent, JsDoc.Tag.NAME_TAG);
        if (aName != null && !aName.isEmpty()) {
            return aContent;
        } else {
            return PlatypusFilesSupport.replaceAnnotationValue(aContent, JsDoc.Tag.NAME_TAG, aAnnotationValue);
        }
    }

    private static void createQueryFiles(File parentDirectory, ApplicationElement appElement, Element rootNode, DocumentBuilder documentBuilder) throws DeployException, IOException {
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
            throw new DeployException(CHECK_REQUIRED_TAG_EXCEPTION_MSG + ApplicationElement.SQL_TAG_NAME);
        }
        rootNode.removeChild(sqlSourceNodes.item(0));
        NodeList dialectSourceNodes = rootNode.getElementsByTagName(ApplicationElement.FULL_SQL_TAG_NAME);
        if (sqlSourceNodes.getLength() > 1) {
            throw new DeployException(CHECK_OPTIONAL_TAG_EXCEPTION_MSG + ApplicationElement.FULL_SQL_TAG_NAME);
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
        saveExportedElementFile(parentDirectory, appElement.getName(), PlatypusFiles.MODEL_EXTENSION, Model2XmlDom.DATAMODEL_TAG_NAME, rootNode, documentBuilder);
    }

    private static void saveTextContentFile(File parentDirectory, ApplicationElement aAppElement, String ext, String tagName, Element node) throws DeployException, IOException {
        NodeList contentNodes = node.getElementsByTagName(tagName);
        Node contentNode = contentNodes.item(0);
        if (contentNode != null) {
            String aContent = checkAnnotation(contentNode.getTextContent(), aAppElement.getId());
            saveFile(parentDirectory, aAppElement.getName(), ext, aContent);
        }
    }

    private static void saveFile(File aParentDirectory, String aFileName, String aExt, String aContent) throws IOException, DeployException {
        String aName = checkFileName(aFileName);
        File file = new File(aParentDirectory, addExtension(aName, aExt));
        if (file.createNewFile()) {
            FileUtils.writeString(file, aContent, PlatypusFiles.DEFAULT_ENCODING);
        } else {
            throw new DeployException(CREATE_FILE_EXCEPTION_MSG + file.getAbsolutePath());
        }
    }

    private static void saveExportedElementFile(File parentDirectory, String name, String ext, String tagName, Element node, DocumentBuilder documentBuilder) throws DeployException, IOException {
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

    private void saveXml2File(File parentDirectory, String fileName, String aExt, Element rootElement, DocumentBuilder documentBuilder) throws IOException, DeployException {
        Document doc = documentBuilder.newDocument();
        doc.setXmlStandalone(true);
        doc.adoptNode(rootElement);
        doc.appendChild(rootElement);
        saveFile(parentDirectory, fileName, aExt, XmlDom2String.transform(doc));
    }

    private static String addExtension(String name, String ext) {
        return name + FileUtils.EXTENSION_SEPARATOR + ext;
    }

    private void traverseSources(File dir, String aParentDirectoryAppElementId) throws IOException {
        assert dir.isDirectory();
        Map<String, AppElementFiles> appElementsFileGroups = new HashMap<>();
        for (String filename : dir.list()) {
            File child = new File(dir, filename);
            if (!child.isHidden()) {
                if (child.isDirectory()) {
                    String directoryAppElementId = IDGenerator.genID().toString();
                    ApplicationElement directoryAppElement = new ApplicationElement();
                    directoryAppElement.setId(directoryAppElementId);
                    directoryAppElement.setName(child.getName());
                    directoryAppElement.setParentId(aParentDirectoryAppElementId);
                    directoryAppElement.setType(ClientConstants.ET_FOLDER);
                    appElements.put(directoryAppElement.getId(), directoryAppElement);
                    traverseSources(child, directoryAppElementId);
                } else if (PlatypusFiles.isPlatypusProjectFile(child)) {
                    String groupName = FileUtils.removeExtension(child.getName());
                    AppElementFiles fg = appElementsFileGroups.get(groupName);
                    if (fg == null) {
                        fg = new AppElementFiles(aParentDirectoryAppElementId);
                        appElementsFileGroups.put(groupName, fg);
                    }
                    fg.addFile(child);
                } else {
                    ApplicationElement appElement = appElementByFile(child, aParentDirectoryAppElementId);
                    appElements.put(appElement.getId(), appElement);
                }
            }
        }
        for (String key : appElementsFileGroups.keySet()) {
            AppElementFiles fg = appElementsFileGroups.get(key);
            ApplicationElement appElement = fg.getApplicationElement();
            if (appElement != null) {
                appElements.put(appElement.getId(), appElement);
            } else {
                for (File child : fg.getFiles()) {
                    appElement = appElementByFile(child, fg.getParentDirectoryAppElementId());
                    appElements.put(appElement.getId(), appElement);
                }
            }
        }
    }

    private ApplicationElement appElementByFile(File child, String aParentDirectoryAppElementId) throws IOException {
        String path = child.getPath();
        assert path.startsWith(sourcesRoot.getPath());
        String resId = path.substring(sourcesRoot.getPath().length()).replace(File.separatorChar, '/');
        if (resId.startsWith("/")) {
            resId = resId.substring(1);
        }
        ApplicationElement appElement = new ApplicationElement();
        appElement.setId(resId);
        appElement.setName(child.getName());
        appElement.setType(ClientConstants.ET_RESOURCE);
        appElement.setParentId(aParentDirectoryAppElementId);
        appElement.setBinaryContent(FileUtils.readBytes(child));
        return appElement;
    }

    private static class AppElementRowsetIndexes {

        final int idColumnIndex;
        final int parentIdColumnIndex;
        final int nameColumnIndex;
        final int typeColumnIndex;
        final int contentTxtColumnIndex;
        final int contentTxtSizeColumnIndex;
        final int contentTxtCrcColumnIndex;

        AppElementRowsetIndexes(Rowset rs) {
            idColumnIndex = rs.getFields().find(ClientConstants.F_MDENT_ID);
            parentIdColumnIndex = rs.getFields().find(ClientConstants.F_MDENT_PARENT_ID);
            nameColumnIndex = rs.getFields().find(ClientConstants.F_MDENT_NAME);
            typeColumnIndex = rs.getFields().find(ClientConstants.F_MDENT_TYPE);
            contentTxtColumnIndex = rs.getFields().find(ClientConstants.F_MDENT_CONTENT_TXT);
            contentTxtSizeColumnIndex = rs.getFields().find(ClientConstants.F_MDENT_CONTENT_TXT_SIZE);
            contentTxtCrcColumnIndex = rs.getFields().find(ClientConstants.F_MDENT_CONTENT_TXT_CRC32);
        }
    }
}
