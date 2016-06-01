/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project;

import com.eas.client.MetadataCache;
import com.eas.client.DatabasesClient;
import com.eas.client.ScriptedDatabasesClient;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.cache.PlatypusIndexer;
import com.eas.client.queries.LocalQueriesProxy;
import com.eas.client.resourcepool.BearResourcePool;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.application.indexer.PlatypusPathRecognizer;
import com.eas.designer.application.platform.PlatformHomePathException;
import com.eas.designer.application.platform.PlatypusPlatform;
import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.application.project.PlatypusProjectInformation;
import com.eas.designer.application.utils.DatabaseConnections;
import com.eas.designer.application.utils.DatabaseConnectionsListener;
import com.eas.designer.explorer.j2ee.PlatypusWebModule;
import com.eas.designer.explorer.j2ee.PlatypusWebModuleManager;
import com.eas.designer.explorer.model.windows.ModelInspector;
import com.eas.designer.explorer.project.ui.PlatypusProjectCustomizerProvider;
import com.eas.script.Scripts;
import com.eas.util.BinaryUtils;
import com.eas.util.FileUtils;
import com.eas.util.ListenerRegistration;
import com.eas.xml.dom.Source2XmlDom;
import java.awt.Component;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.xml.parsers.ParserConfigurationException;
import jdk.nashorn.api.scripting.JSObject;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.db.explorer.DatabaseException;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.support.ClassPathSupport;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.netbeans.spi.queries.FileEncodingQueryImplementation;
import org.netbeans.spi.search.SearchFilterDefinition;
import org.netbeans.spi.search.SearchInfoDefinition;
import org.netbeans.spi.search.SearchInfoDefinitionFactory;
import org.netbeans.spi.search.SubTreeSearchOptions;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataNode;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author mg
 */
public class PlatypusProjectImpl implements PlatypusProject {

    public final RequestProcessor RP = new RequestProcessor(PlatypusProjectImpl.class);

    public static class PlatypusFilesEncodingQuery extends FileEncodingQueryImplementation {

        @Override
        public Charset getEncoding(FileObject fo) {
            return Charset.forName(PlatypusFiles.DEFAULT_ENCODING);
        }
    }

    static {
        Scripts.setOnlySpace(initScriptSpace());
        PlatypusPlatform.checkPlatypusJsUpdates();
    }

    static Scripts.Space initScriptSpace() {
        try {
            ScriptEngine jsEngine = Scripts.getEngine();
            ScriptContext jsContext = new SimpleScriptContext();
            Bindings bindings = jsEngine.createBindings();
            jsContext.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
            Scripts.Space space = new Scripts.Space(jsContext);
            bindings.put("space", space);

            Object global = jsEngine.eval("load('classpath:com/eas/designer/explorer/designer-js.js', space);", jsContext);

            space.setGlobal(global);
            return space;
        } catch (ScriptException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static Scripts.Space getJsSpace() {
        return Scripts.getSpace();
    }

    protected Lookup pLookup;
    protected ProjectState state;
    protected final FileObject projectDir;
    protected final Path apiPath;
    protected ScriptedDatabasesClient basesProxy;
    protected PlatypusIndexer indexer;
    protected LocalQueriesProxy queries;
    protected RequestProcessor.Task connecting2Db;
    protected PlatypusProjectInformation info;
    protected PlatypusProjectSettingsImpl settings;
    private boolean autoDeployEnabled;
    private final Set<PlatypusProject.ClientChangeListener> clientListeners = new HashSet<>();
    private final Set<PlatypusProject.QueriesChangeListener> queriesListeners = new HashSet<>();
    private final SearchFilter searchFilter;

    public PlatypusProjectImpl(FileObject aProjectDir, ProjectState aState) throws Exception {
        super();
        DataNode.setShowFileExtensions(false);
        state = aState;
        projectDir = aProjectDir;
        apiPath = Paths.get(projectDir.toURI()).resolve(WEB_INF_DIRECTORY).resolve(CLASSES_DIRECTORY_NAME);
        settings = new PlatypusProjectSettingsImpl(aProjectDir);
        settings.getChangeSupport().addPropertyChangeListener((final PropertyChangeEvent evt) -> {
            if (evt.getNewValue() == null ? evt.getOldValue() != null : !evt.getNewValue().equals(evt.getOldValue())) {
                state.markModified();
                if (PlatypusProjectSettingsImpl.DEFAULT_DATA_SOURCE_ELEMENT_KEY.equals(evt.getPropertyName())) {
                    EventQueue.invokeLater(() -> {
                        try {
                            if (evt.getNewValue() != null) {
                                basesProxy.setDefaultDatasourceName((String) evt.getNewValue(), false);
                                startConnecting2db(null);
                            } else {
                                basesProxy.setDefaultDatasourceName((String) evt.getNewValue());
                                fireClientDefaultDatasourceChanged((String) evt.getOldValue(), (String) evt.getNewValue());
                            }
                        } catch (Exception ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    });
                }
            }
        });
        info = new PlatypusProjectInformationImpl(this);
        searchFilter = new SearchFilter(this);
        pLookup = Lookups.fixed(
                new PlatypusProjectOpenedClosedHook(),
                new PlatypusClassPathProvider(),
                new PlatypusProjectView(this),
                info,
                new PlatypusProjectActions(this),
                new PlatypusProjectDiskOperations(this),
                new PlatypusPrivilegedTemplates(),
                new PlatypusProjectCustomizerProvider(this),
                new PlatypusFilesEncodingQuery(),
                new PlatypusWebModule(this),
                new PlatypusWebModuleManager(this),
                getSearchInfoDescription());
        indexer = new PlatypusIndexer() {
            @Override
            public File nameToFile(String aAppElementName) throws Exception {
                if (aAppElementName != null && !aAppElementName.isEmpty()) {
                    FileObject fo = IndexerQuery.appElementId2File(PlatypusProjectImpl.this, aAppElementName);
                    if (fo != null) {
                        return FileUtil.toFile(fo);
                    }
                }
                return null;
            }

            @Override
            public String getDefaultModuleName(File file) {
                return null;
            }
        };
        basesProxy = new ScriptedDatabasesClient(settings.getDefaultDataSourceName(), indexer, false, BearResourcePool.DEFAULT_MAXIMUM_SIZE) {

            @Override
            protected JSObject createModule(String aModuleName) throws Exception {
                return createLocalEngineModule(aModuleName);
            }

            @Override
            public void tableAdded(String aDatasourceName, String aTable) throws Exception {
                super.tableAdded(aDatasourceName, aTable);
                fireQueriesChanged();
            }

            @Override
            public void tableChanged(String aDatasourceName, String aTable) throws Exception {
                super.tableChanged(aDatasourceName, aTable);
                fireQueriesChanged();
            }

            @Override
            public void tableRemoved(String aDatasourceName, String aTable) throws Exception {
                super.tableRemoved(aDatasourceName, aTable);
                fireQueriesChanged();
            }

        };
        queries = new LocalQueriesProxy(basesProxy, indexer);
        basesProxy.setQueries(queries);
    }

    protected JSObject createLocalEngineModule(String aModuleName) throws Exception {
        FileObject jsFo = IndexerQuery.appElementId2File(PlatypusProjectImpl.this, aModuleName);
        if (jsFo != null) {
            Scripts.getSpace().exec(aModuleName, jsFo.toURL());
            return Scripts.getSpace().createModule(aModuleName);
        } else {
            return null;
        }
    }

    @Override
    public LocalQueriesProxy getQueries() {
        return queries;
    }

    @Override
    public PlatypusIndexer getIndexer() {
        return indexer;
    }

    @Override
    public boolean isDbConnected(String aDatasourceName) {
        String datasourceName = aDatasourceName;
        if (datasourceName == null) {
            datasourceName = settings.getDefaultDataSourceName();
        }
        DatabaseConnection conn = DatabaseConnections.lookup(datasourceName);
        return conn != null && conn.getJDBCConnection() != null;
    }

    @Override
    public DatabasesClient getBasesProxy() {
        return basesProxy;
    }

    @Override
    public final void startConnecting2db(final String aDatasourceName) {
        if (connecting2Db == null) {
            connecting2Db = RP.create(() -> {
                try {
                    connect2db(aDatasourceName);
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusProjectImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
            String datasourceName = aDatasourceName;
            if (datasourceName == null) {
                datasourceName = settings.getDefaultDataSourceName();
            }
            final ProgressHandle ph = ProgressHandleFactory.createHandle(NbBundle.getMessage(PlatypusProjectImpl.class, "LBL_Connecting_Progress", datasourceName), connecting2Db); // NOI18N  
            connecting2Db.addTaskListener((org.openide.util.Task task) -> {
                ph.finish();
            });
            ph.start();
            connecting2Db.schedule(0);
        }
    }

    public void disconnectedFromDatasource(final String aDatasourceName) {
        fireClientDisconnected(aDatasourceName);
        StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(PlatypusProjectActions.class, "LBL_Connection_Lost", aDatasourceName)); // NOI18N
    }

    @Override
    public void disconnectFormDb(final String aDatasourceName) throws InterruptedException, ExecutionException {
        if (connecting2Db != null && !connecting2Db.isFinished()) {
            connecting2Db.waitFinished();
        }
        String datasourceName = aDatasourceName;
        if (datasourceName == null) {
            datasourceName = settings.getDefaultDataSourceName();
        }
        DatabaseConnection conn = DatabaseConnections.lookup(datasourceName);
        if (conn != null) {
            ConnectionManager.getDefault().disconnect(conn);
        }
        if (ModelInspector.isInstance()) {
            ModelInspector mi = ModelInspector.getInstance();
            mi.setNodesReflector(null);
            mi.setViewData(null);
        }
        StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(PlatypusProjectActions.class, "LBL_Connection_Lost", datasourceName)); // NOI18N                    
    }

    private synchronized void fireClientConnected(final String aDatasourceName) {
        EventQueue.invokeLater(() -> {
            for (ClientChangeListener l : clientListeners.toArray(new ClientChangeListener[]{})) {
                l.connected(aDatasourceName);
            }
            fireQueriesChanged();
        });
    }

    private synchronized void fireClientDisconnected(final String aDatasourceName) {
        EventQueue.invokeLater(() -> {
            try {
                if (ModelInspector.isInstance()) {
                    ModelInspector mi = ModelInspector.getInstance();
                    mi.setNodesReflector(null);
                    mi.setViewData(null);
                }
                MetadataCache mdCache = basesProxy.getMetadataCache(aDatasourceName);
                if (mdCache != null) {
                    mdCache.clear();
                }
                if (aDatasourceName == null ? basesProxy.getDefaultDatasourceName() == null : aDatasourceName.equals(basesProxy.getDefaultDatasourceName())) {
                    mdCache = basesProxy.getMetadataCache(null);
                    if (mdCache != null) {
                        mdCache.clear();
                    }
                }
                for (ClientChangeListener l : clientListeners.toArray(new ClientChangeListener[]{})) {
                    l.disconnected(aDatasourceName);
                }
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            fireQueriesChanged();
        });
    }

    private synchronized void fireClientDefaultDatasourceChanged(final String aOldDatasourceName, final String aNewDatasourceName) {
        EventQueue.invokeLater(() -> {
            for (ClientChangeListener l : clientListeners.toArray(new ClientChangeListener[]{})) {
                l.defaultDatasourceNameChanged(aOldDatasourceName, aNewDatasourceName);
            }
            fireQueriesChanged();
        });
    }

    @Override
    public synchronized void fireQueriesChanged() {
        queries.clearCachedQueries();
        for (QueriesChangeListener l : queriesListeners.toArray(new QueriesChangeListener[]{})) {
            l.changed();
        }
    }

    @Override
    public synchronized void fireQueryChanged(String aQueryName) {
        queries.clearCachedQuery(aQueryName);
        for (QueriesChangeListener l : queriesListeners.toArray(new QueriesChangeListener[]{})) {
            l.changed();
        }
    }

    @Override
    public InputOutput getOutputWindowIO() {
        return IOProvider.getDefault().getIO(getDisplayName(), false);
    }

    public void connectedToDatasource(final String aDatasourceName) throws Exception {
        connecting2Db = null;
        assert basesProxy != null;
        final DatabaseConnection conn = DatabaseConnections.lookup(aDatasourceName);
        assert conn != null : "Already connected datasource disappeared";
        String datasourceName = (aDatasourceName == null ? basesProxy.getDefaultDatasourceName() != null : !aDatasourceName.equals(basesProxy.getDefaultDatasourceName())) ? aDatasourceName : null;
        MetadataCache mdCache = basesProxy.getMetadataCache(datasourceName);
        if (mdCache != null) {
            mdCache.clear();
            mdCache.fillTablesCacheByConnectionSchema();
        }
        String dbConnectingCompleteMsg = NbBundle.getMessage(PlatypusProjectImpl.class, "LBL_Connecting_Complete", aDatasourceName); // NOI18N
        StatusDisplayer.getDefault().setStatusText(dbConnectingCompleteMsg);
        getOutputWindowIO().getOut().println(dbConnectingCompleteMsg);
        fireClientConnected(aDatasourceName);
    }

    private void connect2db(String aDatasourceName) throws Exception {
        try {
            String datasourceName = aDatasourceName;
            if (datasourceName == null) {
                datasourceName = settings.getDefaultDataSourceName();
            }
            DatabaseConnection conn = DatabaseConnections.lookup(datasourceName);
            if (conn != null) {
                conn.getPassword();//Restore the connection's saved password. 
                if (conn.getJDBCConnection(true) == null) {
                    ConnectionManager.getDefault().connect(conn);
                } else {
                    connectedToDatasource(datasourceName);
                }
            } else {
                String dbUnableToConnectMsg = NbBundle.getMessage(PlatypusProjectImpl.class, "LBL_DatasourceNameMissing"); // NOI18N
                Logger.getLogger(PlatypusProjectImpl.class.getName()).log(Level.INFO, dbUnableToConnectMsg);
                StatusDisplayer.getDefault().setStatusText(dbUnableToConnectMsg);
                getOutputWindowIO().getErr().println(dbUnableToConnectMsg);
            }
            connecting2Db = null;
        } catch (DatabaseException ex) {
            connecting2Db = null;
            Logger.getLogger(PlatypusProjectImpl.class.getName()).log(Level.INFO, null, ex);
            String dbUnableToConnectMsg = NbBundle.getMessage(PlatypusProjectImpl.class, "LBL_UnableToConnect") + ": " + ex.getMessage(); // NOI18N
            StatusDisplayer.getDefault().setStatusText(dbUnableToConnectMsg);
            getOutputWindowIO().getErr().println(dbUnableToConnectMsg);
        }
    }

    @Override
    public Component generateDbPlaceholder(final String aDatasourceName) throws Exception {
        byte[] htmlData = BinaryUtils.readStream(PlatypusProjectImpl.class.getResourceAsStream(NbBundle.getMessage(PlatypusProjectImpl.class, "dbPlaceholder")), -1);  // NOI18N 
        String html = new String(htmlData, PlatypusUtils.COMMON_ENCODING_NAME);
        URL urlBase = PlatypusProjectImpl.class.getResource("");
        html = html.replaceAll("noDatabaseBaseHref", urlBase.toString());
        String datasourceName = aDatasourceName;
        if (datasourceName == null) {
            datasourceName = settings.getDefaultDataSourceName();
        }
        if (datasourceName == null || datasourceName.isEmpty()) {
            datasourceName = NbBundle.getMessage(PlatypusProjectImpl.class, "LBL_DatasourceNameMissing");
        }
        html = html.replaceAll("\\$\\{datasourceName\\}", datasourceName);
        JEditorPane htmlPage = new JEditorPane("text/html", html);
        htmlPage.addHyperlinkListener((HyperlinkEvent e) -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED && e.getDescription().equals("platypus://start_connecting")) { // NOI18N
                startConnecting2db(aDatasourceName);
            }
        });
        htmlPage.setEditable(false);
        return new JScrollPane(htmlPage);
    }

    @Override
    public Component generateDbValidatePlaceholder() throws Exception {
        byte[] htmlData = BinaryUtils.readStream(PlatypusProjectImpl.class.getResourceAsStream(NbBundle.getMessage(PlatypusProjectImpl.class, "dbValidatePlaceholder")), -1);  // NOI18N 
        String html = new String(htmlData, PlatypusUtils.COMMON_ENCODING_NAME);
        URL urlBase = PlatypusProjectImpl.class.getResource("");
        html = html.replaceAll("dbValidateBaseHref", urlBase.toString());
        JEditorPane htmlPage = new JEditorPane("text/html", html);
        htmlPage.setEditable(false);

        return new JScrollPane(htmlPage);
    }

    @Override
    public synchronized ListenerRegistration addClientChangeListener(final PlatypusProject.ClientChangeListener onChange) {
        clientListeners.add(onChange);
        return () -> {
            synchronized (PlatypusProjectImpl.this) {
                clientListeners.remove(onChange);
            }
        };
    }

    @Override
    public synchronized ListenerRegistration addQueriesChangeListener(final PlatypusProject.QueriesChangeListener onChange) {
        queriesListeners.add(onChange);
        return () -> {
            synchronized (PlatypusProjectImpl.this) {
                queriesListeners.remove(onChange);
            }
        };
    }

    @Override
    public FileObject getProjectDirectory() {
        return projectDir;
    }

    @Override
    public Lookup getLookup() {
        return pLookup;
    }

    @Override
    public PlatypusProjectSettingsImpl getSettings() {
        return settings;
    }

    @Override
    public void save() throws Exception {
        getSettings().save();
    }

    @Override
    public boolean isAutoDeployEnabled() {
        return autoDeployEnabled;
    }

    @Override
    public synchronized void setAutoDeployEnabled(boolean isEnabled) {
        autoDeployEnabled = isEnabled;
    }

    @Override
    public String getDisplayName() {
        return settings.getDisplayName();
    }

    @Override
    public ProjectState getState() {
        return state;
    }

    @Override
    public final FileObject getSrcRoot() {
        return settings.getSourcePath() != null ? getDirectory(settings.getSourcePath()) : getProjectDirectory();
    }

    @Override
    public final FileObject getApiRoot() throws IllegalStateException {
        FileObject webInfDir = getDirectory(WEB_INF_DIRECTORY);
        FileObject classes = webInfDir.getFileObject(CLASSES_DIRECTORY_NAME);
        if (classes == null) {
            try {
                classes = webInfDir.createFolder(CLASSES_DIRECTORY_NAME);
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }
        return classes;
    }

    @Override
    public final FileObject getLibRoot() throws IllegalStateException {
        FileObject webInfDir = getDirectory(WEB_INF_DIRECTORY);
        FileObject libDir = webInfDir.getFileObject(LIB_DIRECTORY_NAME);
        if (libDir == null) {
            try {
                libDir = webInfDir.createFolder(LIB_DIRECTORY_NAME);
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }
        return libDir;
    }

    private FileObject getDirectory(String name) {
        FileObject directory = projectDir.getFileObject(name);
        if (directory == null) {
            try {
                directory = projectDir.createFolder(name);
            } catch (IOException ex) {
                Logger.getLogger(PlatypusProjectImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return directory;
    }

    @Override
    public PlatypusProjectInformation getProjectInfo() {
        return info;
    }

    @Override
    public SubTreeSearchOptions getSubTreeSearchOptions() {
        return new SubTreeSearchOptions() {
            @Override
            public List<SearchFilterDefinition> getFilters() {
                return Arrays.asList(new SearchFilterDefinition[]{searchFilter});
            }
        };
    }

    @Override
    public RequestProcessor getRequestProcessor() {
        return RP;
    }

    private SearchInfoDefinition getSearchInfoDescription() {
        return SearchInfoDefinitionFactory.createSearchInfo(
                getProjectDirectory(),
                new SearchFilterDefinition[]{
                    searchFilter
                });
    }

    /**
     * Synchronous platypus.js integrating.
     * 
     * @throws IOException 
     */
    public void forceUpdatePlatypusRuntime() throws IOException {
        if (needUpdatePlatypusRuntime()) {
            getOutputWindowIO().getOut().println(NbBundle.getMessage(PlatypusProjectImpl.class, "LBL_Platform_Integrating_Progress_Started", getDisplayName()));
            try {
                clearPlatypusRuntime();
                copyPlatypusRuntime();
                getOutputWindowIO().getOut().println(NbBundle.getMessage(PlatypusProjectImpl.class, "LBL_Platform_Integrating_Progress_Finished", getDisplayName()));
            } catch (IOException ex) {
                getOutputWindowIO().getErr().println(NbBundle.getMessage(PlatypusProjectImpl.class, "LBL_Platform_Integrating_Progress_Failed", getDisplayName(), ex.toString()));
                throw ex;
            }
        }
    }

    protected boolean needUpdatePlatypusRuntime() throws IOException {
        if (settings.isAutoUpdatePlatypusJs()) {
            try {
                String projectVersionValue = settings.getPlatypusJsVersion();
                String platformVersionValue = readPlatypusJsVersion();
                return (projectVersionValue == null ? platformVersionValue != null : !projectVersionValue.equals(platformVersionValue));
            } catch (PlatformHomePathException | SAXException | ParserConfigurationException ex) {
                getOutputWindowIO().getErr().println(ex.getMessage());
                return false;
            }
        } else {
            return false;
        }
    }

    protected String readPlatypusJsVersion() throws IOException, PlatformHomePathException, SAXException, ParserConfigurationException {
        FileObject platformVersion = FileUtil.toFileObject(PlatypusPlatform.getPlatformVersion());
        if (platformVersion != null) {
            Document platformVersionDoc = Source2XmlDom.transform(platformVersion.asText());
            NodeList platformVersionElements = platformVersionDoc.getDocumentElement().getElementsByTagName(PlatypusPlatform.VERSION_TAG_NAME);
            if (platformVersionElements.getLength() == 1) {
                Element platformVersionElement = (Element) platformVersionElements.item(0);
                if (platformVersionElement != null) {
                    return platformVersionElement.getAttribute(PlatypusPlatform.VERSION_ATTRIBUTE_NAME);
                }
            }
        }
        return null;
    }

    protected void copyPlatypusRuntime() throws IOException {
        try {
            prepareJarsJSes(true);
            preparePlatypusWebClient(true);
            String platformVersion = readPlatypusJsVersion();
            settings.setPlatypusJsVersion(platformVersion);
            settings.save();
        } catch (PlatformHomePathException | SAXException | ParserConfigurationException ex) {
            throw new IOException(ex);
        }
    }

    private void prepareJarsJSes(boolean forceOverwrite) throws IOException {
        FileObject libsDir = getLibRoot();
        if (libsDir.getChildren().length == 0 || forceOverwrite) {
            copyBinJars(libsDir);
            copyLibJars(libsDir);
            copyExtJars(libsDir);
        }
        FileObject classesDir = getApiRoot();
        if (classesDir.getChildren().length == 0 || forceOverwrite) {
            copyApiJs(classesDir);
        }
    }

    private void copyJars(FileObject libsDir, FileObject sourceDir) throws IOException {
        for (FileObject fo : sourceDir.getChildren()) {
            if (fo.isData() && PlatypusPlatform.JAR_FILE_EXTENSION.equalsIgnoreCase(fo.getExt())) {
                FileObject alreadyFO = libsDir.getFileObject(fo.getName(), fo.getExt());
                if (alreadyFO != null) {// overwrite file
                    try (OutputStream out = alreadyFO.getOutputStream()) {
                        Files.copy(FileUtil.toFile(fo).toPath(), out);
                    }
                } else {// copy file
                    FileUtil.copyFile(fo, libsDir, fo.getName());
                }
            }
        }
    }

    private void copyBinJars(FileObject libsDir) throws IOException {
        try {
            FileObject platformBinDir = FileUtil.toFileObject(PlatypusPlatform.getPlatformBinDirectory());
            copyJars(libsDir, platformBinDir);
        } catch (PlatformHomePathException ex) {
            throw new IOException(ex);//Should not happen
        }
    }

    private void copyExtJars(FileObject libsDir) throws IOException {
        try {
            FileObject platformBinDir = FileUtil.toFileObject(PlatypusPlatform.getPlatformExtDirectory());
            copyJars(libsDir, platformBinDir);
        } catch (PlatformHomePathException ex) {
            throw new IOException(ex);//Should not happen
        }
    }

    private void copyLibJars(FileObject libsDir) throws IOException {
        try {
            Set<File> jdbcDriverFiles = new HashSet<>();
            for (String clazz : GeneralResourceProvider.driversClasses) {
                File jdbcDriver = PlatypusPlatform.findThirdpartyJar(clazz);
                if (jdbcDriver != null) {
                    FileObject jdbcDriverFo = FileUtil.toFileObject(jdbcDriver);
                    Enumeration<? extends FileObject> jdbcDriversEnumeration = jdbcDriverFo.getParent().getChildren(false);
                    while (jdbcDriversEnumeration.hasMoreElements()) {
                        FileObject fo = jdbcDriversEnumeration.nextElement();
                        jdbcDriverFiles.add(FileUtil.toFile(fo));
                    }
                }
            }
            FileObject platformLibDir = FileUtil.toFileObject(PlatypusPlatform.getPlatformLibDirectory());
            Enumeration<? extends FileObject> filesEnumeration = platformLibDir.getChildren(true);
            while (filesEnumeration.hasMoreElements()) {
                FileObject fo = filesEnumeration.nextElement();
                if (!fo.isFolder() && PlatypusPlatform.JAR_FILE_EXTENSION.equalsIgnoreCase(fo.getExt())
                        && !jdbcDriverFiles.contains(FileUtil.toFile(fo))) {
                    Logger.getLogger(PlatypusProjectImpl.class.getName()).log(Level.INFO, "Copying lib: {0}", fo.getPath());
                    FileObject alreadyFO = libsDir.getFileObject(fo.getName(), fo.getExt());
                    if (alreadyFO != null) {// overwrite file
                        try (OutputStream out = alreadyFO.getOutputStream()) {
                            Files.copy(FileUtil.toFile(fo).toPath(), out);
                        }
                    } else {// copy file
                        FileUtil.copyFile(fo, libsDir, fo.getName());
                    }
                } else {
                    Logger.getLogger(PlatypusProjectImpl.class.getName()).log(Level.INFO, "Skipped while copying libs: {0}", fo.getPath());
                }
            }
        } catch (PlatformHomePathException ex) {
            throw new IOException(ex);
        }
    }

    private void copyApiJs(FileObject clasessDir) throws IOException {
        try {
            copyContent(FileUtil.toFileObject(PlatypusPlatform.getPlatformApiDirectory()), clasessDir);
        } catch (PlatformHomePathException ex) {
            throw new IOException(ex);
        }
    }

    private void preparePlatypusWebClient(boolean forceOverwrite) throws IOException {
        try {
            FileObject webContentDir = getDirectory(WEB_DIRECTORY);
            FileObject pwcDir = webContentDir.getFileObject(PLATYPUS_WEB_CLIENT_DIR_NAME);
            if (pwcDir == null) {
                pwcDir = webContentDir.createFolder(PLATYPUS_WEB_CLIENT_DIR_NAME);
            }
            FileObject pwcSourceDir = FileUtil.toFileObject(PlatypusPlatform.getPlatformBinDirectory()).getFileObject(PLATYPUS_WEB_CLIENT_DIR_NAME);
            if (pwcSourceDir == null) {
                throw new IllegalStateException(String.format(NbBundle.getMessage(PlatypusProjectImpl.class, "MSG_Platypus_Web_Client_Not_Found"), PlatypusPlatform.getPlatformBinDirectory().getAbsolutePath()));//NOI18N
            }
            if (!pwcSourceDir.isFolder()) {
                throw new IllegalStateException(NbBundle.getMessage(PlatypusProjectImpl.class, "MSG_Platypus_Web_Client_Dir"));//NOI18N
            }
            if (pwcDir.getChildren().length == 0 || forceOverwrite) {
                copyContent(pwcSourceDir, pwcDir);
            }
        } catch (PlatformHomePathException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * Recursively copies directory's content.
     *
     * @param sourceDir
     * @param destDir
     * @throws IOException if some I/O problem occurred.
     */
    protected void copyContent(FileObject sourceDir, FileObject destDir) throws IOException {
        assert sourceDir.isFolder() && destDir.isFolder();
        for (FileObject sourceFo : sourceDir.getChildren()) {
            if (sourceFo.isFolder()) {
                FileObject targetFolder = destDir.getFileObject(sourceFo.getNameExt());
                if (targetFolder == null) {
                    targetFolder = destDir.createFolder(sourceFo.getNameExt());
                }
                copyContent(sourceFo, targetFolder);
            } else {
                FileObject alreadyFO = destDir.getFileObject(sourceFo.getName(), sourceFo.getExt());
                if (alreadyFO != null) {// overwrite file
                    try (OutputStream out = alreadyFO.getOutputStream()) {
                        Files.copy(FileUtil.toFile(sourceFo).toPath(), out);
                    }
                } else {// copy file
                    FileUtil.copyFile(sourceFo, destDir, sourceFo.getName());
                }
            }
        }
    }

    public void clearPlatypusRuntime() throws IOException {
        FileObject libsDir = getLibRoot();
        FileUtils.clearDirectory(FileUtil.toFile(libsDir), true);// servlet files
        FileObject classesDir = getApiRoot();
        FileUtils.clearDirectory(FileUtil.toFile(classesDir), true);// js api files
        FileObject webContentDir = projectDir.getFileObject(WEB_DIRECTORY);
        if (webContentDir != null && webContentDir.isFolder()) {
            FileObject pwcDir = webContentDir.getFileObject(PLATYPUS_WEB_CLIENT_DIR_NAME);
            if (pwcDir != null && pwcDir.isFolder()) {
                FileUtils.clearDirectory(FileUtil.toFile(pwcDir), true);// browser client
            }
        }
    }

    private final class PlatypusProjectOpenedClosedHook extends ProjectOpenedHook implements DatabaseConnectionsListener {

        protected ListenerRegistration datasourceListener;
        protected ClassPath sourceCp;
        protected ClassPath apiCp;

        @Override
        protected void projectOpened() {
            try {
                datasourceListener = DatabaseConnections.getDefault().addListener(this);
                apiCp = ClassPath.getClassPath(getApiRoot(), PlatypusPathRecognizer.API_CP);
                sourceCp = ClassPath.getClassPath(getSrcRoot(), PlatypusPathRecognizer.SOURCE_CP);
                GlobalPathRegistry.getDefault().register(PlatypusPathRecognizer.API_CP, new ClassPath[]{apiCp});
                GlobalPathRegistry.getDefault().register(PlatypusPathRecognizer.SOURCE_CP, new ClassPath[]{sourceCp});
                GlobalPathRegistry.getDefault().register(ClassPath.SOURCE, new ClassPath[]{apiCp, sourceCp});
                Logger.getLogger(PlatypusProjectImpl.class.getName()).log(Level.INFO, "Project {0} opened", getDisplayName());
                settings.load();
                startConnecting2db(getSettings().getDefaultDataSourceName());
                // WARNING!!! To avoid a peek and hard work of the filsystem, don't call it here
                //updatePlatypusRuntime();
            } catch (Exception ex) {
                Logger.getLogger(PlatypusProjectImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        protected void projectClosed() {
            try {
                if (datasourceListener != null) {
                    datasourceListener.remove();
                }
                GlobalPathRegistry.getDefault().unregister(PlatypusPathRecognizer.API_CP, new ClassPath[]{apiCp});
                GlobalPathRegistry.getDefault().unregister(PlatypusPathRecognizer.SOURCE_CP, new ClassPath[]{sourceCp});
                GlobalPathRegistry.getDefault().unregister(ClassPath.SOURCE, new ClassPath[]{apiCp, sourceCp});
                Logger.getLogger(PlatypusProjectImpl.class.getName()).log(Level.INFO, "Project {0} closed", getDisplayName());
            } catch (Exception ex) {
                Logger.getLogger(PlatypusProjectImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void connected(DatabaseConnection aConnection) {
            try {
                connectedToDatasource(aConnection.getDisplayName());
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        @Override
        public void disconnected(DatabaseConnection aConnection) {
            try {
                disconnectedFromDatasource(aConnection.getDisplayName());
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private final class PlatypusClassPathProvider implements ClassPathProvider {

        private ClassPath sourceClassPath;
        private ClassPath apiClassPath;

        /**
         * Find some kind of a classpath for a given file or default classpath.
         *
         * @param file a file somewhere, or a source root, or null for default
         * classpath
         * @param type a classpath type
         * @return an appropriate classpath, or null for no answer
         */
        @Override
        public ClassPath findClassPath(FileObject file, String type) {
            if (PlatypusPathRecognizer.SOURCE_CP.equals(type) || ClassPath.SOURCE.equals(type)) {
                return getSourceClassPath();
            } else if (PlatypusPathRecognizer.API_CP.equals(type)) {
                return getApiClassPath();
            }
            return null;
        }

        public synchronized ClassPath getSourceClassPath() {
            if (sourceClassPath == null) {
                sourceClassPath = ClassPathSupport.createClassPath(getSrcRoot());
            }
            return sourceClassPath;
        }

        public synchronized ClassPath getApiClassPath() {
            if (apiClassPath == null) {
                apiClassPath = ClassPathSupport.createClassPath(getApiRoot());
            }
            return apiClassPath;
        }

    }
}
