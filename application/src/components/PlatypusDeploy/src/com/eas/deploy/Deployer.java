/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.deploy;

import com.eas.client.cache.AppElementFilesException;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.compacts.CompactClob;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.ClientConstants;
import com.eas.client.DatabasesClient;
import com.eas.client.cache.AppElementFiles;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.queries.SqlCompiledQuery;
import com.eas.client.queries.SqlQuery;
import com.eas.util.FileUtils;
import com.eas.util.StringUtils;
import com.eas.xml.dom.Source2XmlDom;
import com.eas.xml.dom.XmlDom2String;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private final File sourcesRoot;
    // used by deploy
    private Map<String, ApplicationElement> appElements;
    // used by import
    private Map<String, Set<ApplicationElement>> appElementsTree;

    public Deployer(File aProjectDir, DatabasesClient aClient) {
        super(aClient);
        sourcesRoot = new File(aProjectDir, PlatypusFiles.PLATYPUS_PROJECT_APP_ROOT);
    }

    void initApp() throws Exception {
        DatabasesClient.initApplication(client.obtainDataSource(null));
    }

    void initUsersSpace() throws Exception {
        DatabasesClient.initUsersSpace(client.obtainDataSource(null));
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
            out.println("Deploy to database started.."); // NOI18N
            initApp();
            appElements = new LinkedHashMap<>();

            //Prepare application elements items
            traverseSources(sourcesRoot, null);

            //Deploy application elements to the database
            SqlQuery deleteQuery = new SqlQuery(client, DELETE_ENTITIES_SQL);
            SqlCompiledQuery compiledDeleteQuery = deleteQuery.compile();
            compiledDeleteQuery.enqueueUpdate();

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
                appElementRow.setColumnObject(ri.contentTxtSizeColumnIndex, appElement.getTxtContentLength());
                appElementRow.setColumnObject(ri.contentTxtCrcColumnIndex, appElement.getTxtCrc32());
                rs.insert(appElementRow, true);
                i++;
            }
            Map<String, List<Change>> changeLogs = new HashMap<>();
            List<Change> commonLog = new ArrayList<>();
            commonLog.addAll(compiledDeleteQuery.getFlow().getChangeLog());
            commonLog.addAll(rs.getFlowProvider().getChangeLog());
            changeLogs.put(null, commonLog);
            client.commit(changeLogs);
            out.println(String.format("Deploy completed. %d application elements deployed.", i)); // NOI18N
            out.println();
        } catch (Exception ex) {
            client.rollback();
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
            SqlQuery deleteQuery = new SqlQuery(client, DELETE_ENTITIES_SQL);
            SqlCompiledQuery deleteCompiledQuery = deleteQuery.compile();
            deleteCompiledQuery.enqueueUpdate();
            Map<String, List<Change>> changeLogs = new HashMap<>();
            changeLogs.put(null, deleteCompiledQuery.getFlow().getChangeLog());
            client.commit(changeLogs);
            out.println("Unddeploy completed."); // NOI18N
        } catch (Exception ex) {
            client.rollback();
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
            //Get application elements entities 
            SqlQuery applicationElementsQuery = new SqlQuery(client, SELECT_ENTITIES_SQL);
            Rowset rs = applicationElementsQuery.compile().executeQuery();

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
                    appElement.setContent(Source2XmlDom.transform(txtContent));
                    appElement.setTxtContentLength((row.getColumnObject(ri.contentTxtSizeColumnIndex) != null) ? ((Number) row.getColumnObject(ri.contentTxtSizeColumnIndex)).longValue() : 0l);
                    appElement.setTxtCrc32((row.getColumnObject(ri.contentTxtCrcColumnIndex) != null) ? ((Number) row.getColumnObject(ri.contentTxtCrcColumnIndex)).longValue() : 0l);
                }
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
            err.println("Import error occured: " + ex.getMessage());
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
                            throw new AppElementFilesException("Cant' create directory: " + appElement.getName() + " at path: " + parentDirectory.getAbsolutePath()); //NOI18N
                        }
                    } else if (appElement.getType() == ClientConstants.ET_RESOURCE) {
                        String path = sourcesRoot.getPath() + File.separator + appElement.getId();
                        path = path.replace('/', File.separatorChar);
                        File file = new File(path);
                        if (file.createNewFile()) {
                            FileUtils.writeBytes(file, appElement.getBinaryContent());
                        } else {
                            throw new AppElementFilesException(AppElementFiles.CREATE_FILE_EXCEPTION_MSG + file.getAbsolutePath());
                        }
                    } else {
                        AppElementFiles.createAppElementFiles(parentDirectory, appElement);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(Deployer.class.getName()).log(Level.SEVERE, null, ex);
                } catch (AppElementFilesException ex) {
                    Logger.getLogger(Deployer.class.getName()).log(Level.WARNING, getAppElementErrorMessage(appElement, ex.getMessage()), ex); // NOI18N
                    err.println(getAppElementErrorMessage(appElement, ex.getMessage())); // NOI18N
                }
            }
        }
    }

    private String getAppElementErrorMessage(ApplicationElement appElement, String errorMsg) {
        return String.format("File or directory can not be created: %s for application element id: %s name: %s", errorMsg, appElement.getId(), appElement.getName()); // NOI18N
    }

    private void traverseSources(File dir, String aParentDirectoryAppElementId) throws IOException {
        if (!dir.exists() || !dir.isDirectory()) {
            throw new FileNotFoundException(dir.getPath() + " - it must exist and it must be a directory.");
        }
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
