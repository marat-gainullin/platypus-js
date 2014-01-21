/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project;

import com.eas.client.AppCache;
import com.eas.client.Client;
import com.eas.client.DatabasesClient;
import com.eas.client.ScriptedDatabasesClient;
import com.eas.client.application.ClientCompiledScriptDocuments;
import com.eas.client.cache.FilesAppCache;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.login.PrincipalHost;
import com.eas.client.login.SystemPlatypusPrincipal;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.client.scripts.CompiledScriptDocuments;
import com.eas.client.scripts.CompiledScriptDocumentsHost;
import com.eas.client.scripts.ScriptRunner;
import com.eas.deploy.DbMigrator;
import com.eas.deploy.Deployer;
import com.eas.designer.application.HandlerRegistration;
import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.application.indexer.PlatypusPathRecognizer;
import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.application.project.PlatypusProjectInformation;
import com.eas.designer.explorer.j2ee.PlatypusWebModule;
import com.eas.designer.explorer.j2ee.PlatypusWebModuleManager;
import com.eas.designer.explorer.model.windows.ModelInspector;
import com.eas.designer.explorer.project.ui.PlatypusProjectCustomizerProvider;
import com.eas.script.ScriptUtils;
import com.eas.util.BinaryUtils;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.mozilla.javascript.Context;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
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
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;
import org.openide.util.lookup.Lookups;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author mg
 */
public class PlatypusProjectImpl implements PlatypusProject {

    public final RequestProcessor RP = new RequestProcessor(PlatypusProjectImpl.class);
    public static final String CLIENT_PROPERTY_NAME = "client";

    public static class PlatypusFilesEncodingQuery extends FileEncodingQueryImplementation {

        @Override
        public Charset getEncoding(FileObject fo) {
            return Charset.forName(PlatypusFiles.DEFAULT_ENCODING);
        }
    }
    protected Lookup pLookup;
    protected ProjectState state;
    protected final FileObject projectDir;
    protected final FileObject localProjectFile; // project's settings file
    protected ScriptedDatabasesClient client;
    protected RequestProcessor.Task connecting2Db;
    protected PlatypusProjectInformation info;
    protected PlatypusProjectSettingsImpl settings;
    private boolean autoDeployEnabled;
    private Deployer deployer;
    private DbMigrator migrator;
    private ClassPath sourceRoot;
    private final Set<Runnable> clientListeners = new HashSet<>();
    private final SearchFilter searchFilter;

    public PlatypusProjectImpl(FileObject aProjectDir, ProjectState aState) throws Exception {
        super();
        DataNode.setShowFileExtensions(false);
        state = aState;
        projectDir = aProjectDir;
        localProjectFile = aProjectDir.getFileObject(PlatypusProjectSettingsImpl.PLATYPUS_SETTINGS_FILE);
        settings = new PlatypusProjectSettingsImpl(aProjectDir);
        settings.getChangeSupport().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                state.markModified();
            }
        });
        info = new PlatypusProjectInformationImpl(this);
        searchFilter = new SearchFilter(this);
        pLookup = Lookups.fixed(
                new PlatypusOpenedHook(),
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
        client = new ScriptedDatabasesClient(new FilesAppCache(projectDir.getPath()), settings.getAppSettings().getDefaultDatasource(), true) {
            protected CompiledScriptDocuments documents = new ClientCompiledScriptDocuments(this);

            @Override
            protected ScriptRunner createModule(Context cx, String aModuleName) throws Exception {
                ScriptRunner created = new ScriptRunner(aModuleName, (Client) this, ScriptUtils.getScope(), new PrincipalHost() {
                    @Override
                    public PlatypusPrincipal getPrincipal() {
                        return new SystemPlatypusPrincipal();
                    }
                }, new CompiledScriptDocumentsHost() {
                    @Override
                    public CompiledScriptDocuments getDocuments() {
                        return documents;
                    }

                    @Override
                    public void defineJsClass(String string, ApplicationElement ae) {
                        throw new UnsupportedOperationException("Javascript classes defining is not supported within a designer."); //To change body of generated methods, choose Tools | Templates.
                    }
                }, new Object[]{});
                return created;
            }
        };
    }

    @Override
    public boolean isDbConnected(String aDatasourceId) {
        try {
            return GeneralResourceProvider.getInstance().isDatasourceConnected(aDatasourceId);
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
            return false;
        }
    }

    @Override
    public DatabasesClient getClient() {
        return client;
    }

    @Override
    public AppCache getAppCache() throws Exception {
        return client != null ? client.getAppCache() : null;
    }

    @Override
    public void startConnecting2db(final String aDatasourceId) {
        if (!isDbConnected(aDatasourceId) && connecting2Db == null) {
            connecting2Db = RP.create(new Runnable() {
                @Override
                public void run() {
                    connect2db(aDatasourceId);
                }
            });
            final ProgressHandle ph = ProgressHandleFactory.createHandle(NbBundle.getMessage(PlatypusProjectImpl.class, "LBL_Connecting_Progress"), connecting2Db); // NOI18N  
            connecting2Db.addTaskListener(new TaskListener() {
                @Override
                public void taskFinished(org.openide.util.Task task) {
                    ph.finish();
                }
            });
            ph.start();
            connecting2Db.schedule(0);
        }
    }

    public void disconnected(String aDatasourceId) {
        try {
            if (ModelInspector.isInstance()) {
                ModelInspector mi = ModelInspector.getInstance();
                mi.setNodesReflector(null);
                mi.setViewData(null);
            }
            GeneralResourceProvider.getInstance().disconnectDatasource(aDatasourceId);
            deployer = null;
            migrator = null;
            if (!isDbConnected(aDatasourceId)) {
                fireClientChange();
                StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(PlatypusProjectActions.class, "LBL_Connection_Lost")); // NOI18N
            }
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void disconnectFormDb(final String aDatasourceName) throws InterruptedException, ExecutionException {
        if (connecting2Db != null) {
            connecting2Db.waitFinished();
            DatabaseConnection conn = ConnectionManager.getDefault().getConnection(aDatasourceName);
            if (conn != null) {
                ConnectionManager.getDefault().disconnect(conn);
            }
        }
        if (ModelInspector.isInstance()) {
            ModelInspector mi = ModelInspector.getInstance();
            mi.setNodesReflector(null);
            mi.setViewData(null);
        }
        deployer = null;
        migrator = null;
        fireClientChange();
        StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(PlatypusProjectActions.class, "LBL_Connection_Lost")); // NOI18N                    
    }

    private synchronized void fireClientChange() {
        for (Runnable onChange : clientListeners) {
            SwingUtilities.invokeLater(onChange);
        }
    }

    @Override
    public Deployer getDeployer() {
        if (deployer == null) {
            deployer = new Deployer(FileUtil.toFile(projectDir), getClient());
        }
        return deployer;
    }

    @Override
    public DbMigrator getDbMigrator() {
        if (migrator == null) {
            migrator = new DbMigrator(FileUtil.toFile(projectDir), getClient());
        }
        return migrator;
    }

    @Override
    public InputOutput getOutputWindowIO() {
        return IOProvider.getDefault().getIO(getDisplayName(), false);
    }

    public void connectedToDb(String aDatasourceId) {
        connecting2Db = null;
        fireClientChange();
        String dbConnectingCompleteMsg = NbBundle.getMessage(PlatypusProjectImpl.class, "LBL_Connecting_Complete"); // NOI18N  
        StatusDisplayer.getDefault().setStatusText(dbConnectingCompleteMsg);
        getOutputWindowIO().getOut().println(dbConnectingCompleteMsg);
    }

    private void connect2db(String aDatasourceName) {
        try {
            DatabaseConnection conn = ConnectionManager.getDefault().getConnection(aDatasourceName);
            if (conn != null) {
                ConnectionManager.getDefault().connect(conn);
            }
        } catch (Exception ex) {
            connecting2Db = null;
            Logger.getLogger(PlatypusProjectImpl.class.getName()).log(Level.INFO, null, ex);
            String dbUnableToConnectMsg = NbBundle.getMessage(PlatypusProjectImpl.class, "LBL_UnableToConnect"); // NOI18N
            StatusDisplayer.getDefault().setStatusText(dbUnableToConnectMsg);
            getOutputWindowIO().getErr().println(dbUnableToConnectMsg);
        }
    }

    @Override
    public Component generateDbPlaceholder(final String aDatasourceId) throws Exception {
        byte[] htmlData = BinaryUtils.readStream(PlatypusProjectImpl.class.getResourceAsStream(NbBundle.getMessage(PlatypusProjectImpl.class, "dbPlaceholder")), -1);  // NOI18N 
        String html = new String(htmlData, PlatypusUtils.COMMON_ENCODING_NAME);
        URL urlBase = PlatypusProjectImpl.class.getResource("");
        html = html.replaceAll("noDatabaseBaseHref", urlBase.toString());
        JEditorPane htmlPage = new JEditorPane("text/html", html);
        htmlPage.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED && e.getDescription().equals("platypus://start_connecting")) { // NOI18N   
                    startConnecting2db(aDatasourceId);
                }
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
    public synchronized HandlerRegistration addClientChangeListener(final Runnable onChange) {
        clientListeners.add(onChange);
        return new HandlerRegistration() {
            @Override
            public void remove() {
                synchronized (PlatypusProjectImpl.this) {
                    clientListeners.remove(onChange);
                }
            }
        };
    }

    @Override
    public FileObject getLocalProjectFile() {
        return localProjectFile;
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
    public final FileObject getSrcRoot() throws Exception {
        return getDirectory(PlatypusUtils.PLATYPUS_PROJECT_SOURCES_ROOT);
    }

    @Override
    public FileObject getDbMigrationsRoot() throws Exception {
        return getDirectory(PlatypusUtils.PLATYPUS_PROJECT_DB_MIGRATIONS_DIR);
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

    private final class PlatypusOpenedHook extends ProjectOpenedHook {

        @Override
        protected void projectOpened() {
            try {
                Logger.getLogger(PlatypusProjectImpl.class.getName()).log(Level.INFO, "Project opened");
                sourceRoot = ClassPath.getClassPath(getSrcRoot(), PlatypusPathRecognizer.SOURCE_CP);
                GlobalPathRegistry.getDefault().register(PlatypusPathRecognizer.SOURCE_CP, new ClassPath[]{sourceRoot});
                startConnecting2db(getSettings().getAppSettings().getDefaultDatasource());
            } catch (Exception ex) {
                Logger.getLogger(PlatypusProjectImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        protected void projectClosed() {
            try {
                Logger.getLogger(PlatypusProjectImpl.class.getName()).log(Level.INFO, "Project closed");
                GlobalPathRegistry.getDefault().unregister(PlatypusPathRecognizer.SOURCE_CP, new ClassPath[]{sourceRoot});
            } catch (Exception ex) {
                Logger.getLogger(PlatypusProjectImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private final class PlatypusClassPathProvider implements ClassPathProvider {

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
                return ClassPathFactory.createClassPath(new PlatypusClassPathImplementation());
            }
            return null;
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
                final URL[] urls = {getSrcRoot().toURL()};
                if (resources == null) {
                    PathResourceImpl pri = new PathResourceImpl(urls);
                    resources = Collections.<PathResourceImplementation>singletonList(pri);
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

        public PathResourceImpl(URL[] roots) {
            this.roots = roots;
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
