/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project;

import com.eas.client.AppElementFiles;
import com.eas.client.DatabaseMdCache;
import com.eas.client.DatabasesClient;
import com.eas.client.ScriptedDatabasesClient;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.cache.PlatypusIndexer;
import com.eas.client.forms.Forms;
import com.eas.client.queries.LocalQueriesProxy;
import com.eas.client.resourcepool.BearResourcePool;
import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.application.indexer.PlatypusPathRecognizer;
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
import com.eas.util.ListenerRegistration;
import java.awt.Component;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import jdk.nashorn.api.scripting.JSObject;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.db.explorer.DatabaseException;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.spi.java.classpath.ClassPathFactory;
import org.netbeans.spi.java.classpath.ClassPathImplementation;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.PathResourceImplementation;
import org.netbeans.spi.java.classpath.support.PathResourceBase;
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
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.lookup.Lookups;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

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
    protected static final Scripts.Space jsSpace = initScriptSpace();

    static Scripts.Space initScriptSpace() {
        try {
            ScriptEngine jsEngine = new ScriptEngineManager().getEngineByName("nashorn");
            Bindings bindings = jsEngine.getBindings(ScriptContext.ENGINE_SCOPE);
            Scripts.Space space = new Scripts.Space(jsEngine);
            bindings.put("space", space);
            Object global = jsEngine.eval("load('classpath:com/eas/designer/explorer/designer-js.js', space);", bindings);
            space.setGlobal(global);
            Scripts.LocalContext context = Scripts.createContext(space);
            EventQueue.invokeLater(()->{
                Scripts.setContext(context);
                Forms.initContext(context);
            });
            return space;
        } catch (ScriptException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public static Scripts.Space getJsSpace() {
        return jsSpace;
    }

    protected Lookup pLookup;
    protected ProjectState state;
    protected final FileObject projectDir;
    protected ScriptedDatabasesClient basesProxy;
    protected PlatypusIndexer indexer;
    protected LocalQueriesProxy queries;
    protected RequestProcessor.Task connecting2Db;
    protected PlatypusProjectInformation info;
    protected PlatypusProjectSettingsImpl settings;
    private boolean autoDeployEnabled;
    private ClassPath sourceRoot;
    private final Set<PlatypusProject.ClientChangeListener> clientListeners = new HashSet<>();
    private final Set<PlatypusProject.QueriesChangeListener> queriesListeners = new HashSet<>();
    private final SearchFilter searchFilter;

    public PlatypusProjectImpl(FileObject aProjectDir, ProjectState aState) throws Exception {
        super();
        DataNode.setShowFileExtensions(false);
        state = aState;
        projectDir = aProjectDir;
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
        indexer = (String aAppElementName) -> {
            if (aAppElementName != null && !aAppElementName.isEmpty()) {
                FileObject fo = IndexerQuery.appElementId2File(PlatypusProjectImpl.this, aAppElementName);
                if (fo != null) {
                    DataObject dObject = DataObject.find(fo);
                    if (dObject != null) {
                        AppElementFiles files = new AppElementFiles();
                        dObject.files().stream().forEach((dofo) -> {
                            files.addFile(FileUtil.toFile(dofo));
                        });
                        return files;
                    }
                }
            }
            return null;
        };
        basesProxy = new ScriptedDatabasesClient(settings.getDefaultDataSourceName(), indexer, false, BearResourcePool.DEFAULT_MAXIMUM_SIZE) {

            @Override
            protected JSObject createModule(String aModuleName, Scripts.Space aSpace) throws Exception {
                return createLocalEngineModule(aModuleName);
            }
        };
        queries = new LocalQueriesProxy(basesProxy, indexer) {

            @Override
            protected JSObject createModule(String aModuleName, Scripts.Space aSpace) throws Exception {
                return createLocalEngineModule(aModuleName);
            }

        };
        basesProxy.setQueries(queries);
    }

    protected JSObject createLocalEngineModule(String aModuleName) throws Exception {
        FileObject jsFo = IndexerQuery.appElementId2File(PlatypusProjectImpl.this, aModuleName);
        if (jsFo != null) {
            jsSpace.exec(jsFo.toURL());
            return jsSpace.createModule(aModuleName);
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

    public void disconnectedFromDatasource(final String aDatasourceName) throws Exception {
        if (ModelInspector.isInstance()) {
            ModelInspector mi = ModelInspector.getInstance();
            mi.setNodesReflector(null);
            mi.setViewData(null);
        }
        DatabaseMdCache mdCache = basesProxy.getDbMetadataCache(aDatasourceName);
        if (mdCache != null) {
            mdCache.clear();
        }
        if (aDatasourceName == null ? basesProxy.getDefaultDatasourceName() == null : aDatasourceName.equals(basesProxy.getDefaultDatasourceName())) {
            mdCache = basesProxy.getDbMetadataCache(null);
            if (mdCache != null) {
                mdCache.clear();
            }
        }
        fireClientDisconnected(aDatasourceName);
        StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(PlatypusProjectActions.class, "LBL_Connection_Lost", aDatasourceName)); // NOI18N
    }

    @Override
    public void disconnectFormDb(final String aDatasourceName) throws InterruptedException, ExecutionException {
        if (connecting2Db != null && !connecting2Db.isFinished()) {
            connecting2Db.waitFinished();
        }
        String datasourceId = aDatasourceName;
        if (datasourceId == null) {
            datasourceId = settings.getDefaultDataSourceName();
        }
        DatabaseConnection conn = DatabaseConnections.lookup(datasourceId);
        if (conn != null) {
            ConnectionManager.getDefault().disconnect(conn);
        }
        if (ModelInspector.isInstance()) {
            ModelInspector mi = ModelInspector.getInstance();
            mi.setNodesReflector(null);
            mi.setViewData(null);
        }
        StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(PlatypusProjectActions.class, "LBL_Connection_Lost", datasourceId)); // NOI18N                    
    }

    private synchronized void fireClientConnected(final String aDatasourceName) {
        clientListeners.stream().forEach((onChange) -> {
            onChange.connected(aDatasourceName);
        });
    }

    private synchronized void fireClientDisconnected(final String aDatasourceName) {
        clientListeners.stream().forEach((onChange) -> {
            onChange.disconnected(aDatasourceName);
        });
    }

    private synchronized void fireClientDefaultDatasourceChanged(final String aOldDatasourceName, final String aNewDatasourceName) {
        clientListeners.stream().forEach((onChange) -> {
            onChange.defaultDatasourceNameChanged(aOldDatasourceName, aNewDatasourceName);
        });
    }

    @Override
    public synchronized void fireQueriesChanged() {
        queries.clearCachedQueries();
        queriesListeners.stream().forEach((onChange) -> {
            onChange.changed();
        });
    }

    @Override
    public synchronized void fireQueryChanged(String aQueryName) {
        queries.clearCachedQuery(aQueryName);
        queriesListeners.stream().forEach((onChange) -> {
            onChange.changed();
        });
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
        DatabaseMdCache mdCache = basesProxy.getDbMetadataCache(datasourceName);
        if (mdCache != null) {
            mdCache.clear();
            mdCache.fillTablesCacheByConnectionSchema(true);
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
        return getDirectory(PlatypusFiles.PLATYPUS_PROJECT_APP_ROOT);
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

    private final class PlatypusProjectOpenedClosedHook extends ProjectOpenedHook implements DatabaseConnectionsListener {

        protected ListenerRegistration datasourceListener;

        @Override
        protected void projectOpened() {
            try {
                datasourceListener = DatabaseConnections.getDefault().addListener(this);
                Logger.getLogger(PlatypusProjectImpl.class.getName()).log(Level.INFO, "Project opened");
                sourceRoot = ClassPath.getClassPath(getSrcRoot(), PlatypusPathRecognizer.SOURCE_CP);
                GlobalPathRegistry.getDefault().register(PlatypusPathRecognizer.SOURCE_CP, new ClassPath[]{sourceRoot});
                startConnecting2db(getSettings().getDefaultDataSourceName());
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
                Logger.getLogger(PlatypusProjectImpl.class.getName()).log(Level.INFO, "Project closed");
                GlobalPathRegistry.getDefault().unregister(PlatypusPathRecognizer.SOURCE_CP, new ClassPath[]{sourceRoot});
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
            if (PlatypusPathRecognizer.SOURCE_CP.equals(type)) {
                return getSourceClassPath();
            }
            return null;
        }

        public synchronized ClassPath getSourceClassPath() {
            if (sourceClassPath == null) {
                sourceClassPath = ClassPathFactory.createClassPath(new PlatypusClassPathImplementation());
            }
            return sourceClassPath;
        }

    }

    private final class PlatypusClassPathImplementation implements ClassPathImplementation {

        private final PropertyChangeSupport support = new PropertyChangeSupport(this);
        private List<PathResourceImplementation> resources;

        @Override
        public synchronized List<? extends PathResourceImplementation> getResources() {
            try {
                if (resources != null) {
                    return resources;
                }
                if (resources == null) {
                    resources = new ArrayList<>();
                    resources.add(new PathResourceImpl(new URL[]{getSrcRoot().toURL()}));
                }
                return resources;
            } catch (Exception ex) {
                Logger.getLogger(PlatypusProjectImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener listener) {
            support.addPropertyChangeListener(listener);
        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener listener) {
            support.removePropertyChangeListener(listener);
        }
    }

    private static final class PathResourceImpl extends PathResourceBase {

        private final URL[] roots;

        public PathResourceImpl(URL[] aRoots) {
            super();
            roots = aRoots;
        }

        @Override
        public URL[] getRoots() {
            return roots;
        }

        @Override
        public ClassPathImplementation getContent() {
            return null;
        }
    }
}
