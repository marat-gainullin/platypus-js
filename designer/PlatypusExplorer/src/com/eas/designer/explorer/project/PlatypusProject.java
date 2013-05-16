/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project;

import com.eas.client.AppCache;
import com.eas.client.ClientConstants;
import com.eas.client.ClientFactory;
import com.eas.client.DbClient;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.deploy.BaseDeployer;
import com.eas.deploy.DbMigrator;
import com.eas.deploy.Deployer;
import com.eas.designer.application.HandlerRegistration;
import com.eas.designer.application.PlatypusUtils;
import com.eas.designer.application.indexer.PlatypusPathRecognizer;
import com.eas.designer.explorer.j2ee.PlatypusWebModule;
import com.eas.designer.explorer.j2ee.PlatypusWebModuleManager;
import com.eas.designer.explorer.model.windows.ModelInspector;
import com.eas.designer.explorer.project.ui.PlatypusProjectCustomizerProvider;
import com.eas.util.BinaryUtils;
import java.awt.Component;
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
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.java.classpath.GlobalPathRegistry;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.api.project.Project;
import org.netbeans.spi.java.classpath.ClassPathFactory;
import org.netbeans.spi.java.classpath.ClassPathImplementation;
import org.netbeans.spi.java.classpath.ClassPathProvider;
import org.netbeans.spi.java.classpath.PathResourceImplementation;
import org.netbeans.spi.java.classpath.support.PathResourceBase;
import org.netbeans.spi.project.ProjectState;
import org.netbeans.spi.project.ui.ProjectOpenedHook;
import org.netbeans.spi.queries.FileEncodingQueryImplementation;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.StatusDisplayer;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataNode;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.TaskListener;
import org.openide.util.lookup.Lookups;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author Gala
 */
public class PlatypusProject implements Project {

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
    protected DbClient client;
    protected RequestProcessor.Task connecting2Db;
    protected PlatypusProjectInformation info;
    protected PlatypusProjectSettings settings;
    private boolean autoDeployEnabled;
    private Deployer deployer;
    private DbMigrator migrator;
    private ClassPath sourceRoot;
    private Set<Runnable> clientListeners = new HashSet<>();
    private static final RequestProcessor RP = new RequestProcessor(PlatypusProject.class);

    public PlatypusProject(FileObject aProjectDir, ProjectState aState) throws Exception {
        super();
        DataNode.setShowFileExtensions(false);
        state = aState;
        projectDir = aProjectDir;
        localProjectFile = aProjectDir.getFileObject(BaseDeployer.PLATYPUS_SETTINGS_FILE);
        settings = new PlatypusProjectSettings(aProjectDir);
        settings.getAppSettings().getDbSettings().setInitSchema(true);
        settings.getChangeSupport().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                state.markModified();
            }
        });
        info = new PlatypusProjectInformation(this);
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
                new PlatypusWebModuleManager(this));
    }

    public boolean isDbConnected() {
        return client != null;
    }

    public DbClient getClient() {
        return client;
    }

    public AppCache getAppCache() throws Exception {
        return client != null ? client.getAppCache() : null;
    }

    public void startConnecting2db() {
        if (client == null && connecting2Db == null) {
            connecting2Db = RP.create(new Runnable() {
                @Override
                public void run() {
                    connect2db();
                }
            });
            final ProgressHandle ph = ProgressHandleFactory.createHandle(NbBundle.getMessage(PlatypusProject.class, "LBL_Connecting_Progress"), connecting2Db); // NOI18N  
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

    public void disconnectFormDb() throws InterruptedException, ExecutionException {
        if (connecting2Db != null) {
            connecting2Db.waitFinished();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (client != null) {
                        if (ModelInspector.isInstance()) {
                            ModelInspector mi = ModelInspector.getInstance();
                            mi.setNodesReflector(null);
                            mi.setViewData(null);
                        }
                        final DbClient oldValue = client;
                        client.shutdown();
                        client = null;
                        deployer = null;
                        migrator = null;
                        if (oldValue != client) {
                            fireClientChange();
                            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(PlatypusProjectActions.class, "LBL_Connection_Lost")); // NOI18N                    
                        }
                    }
                }
            });
        }
        if (client != null) {
            if (ModelInspector.isInstance()) {
                ModelInspector mi = ModelInspector.getInstance();
                mi.setNodesReflector(null);
                mi.setViewData(null);
            }
            client.shutdown();
            client = null;
            deployer = null;
            migrator = null;
            fireClientChange();
            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(PlatypusProjectActions.class, "LBL_Connection_Lost")); // NOI18N                    
        }
    }

    private void fireClientChange() {
        for (Runnable onChange : clientListeners) {
            SwingUtilities.invokeLater(onChange);
        }
    }

    public Deployer getDeployer() {
        if (deployer == null) {
            deployer = new Deployer(FileUtil.toFile(projectDir), getClient());
        }
        return deployer;
    }

    public DbMigrator getDbMigrator() {
        if (migrator == null) {
            migrator = new DbMigrator(FileUtil.toFile(projectDir), getClient());
        }
        return migrator;
    }

    public InputOutput getOutputWindowIO() {
        return IOProvider.getDefault().getIO(getDisplayName(), false);
    }

    private void connect2db() {
        try {
            DbConnectionSettings dbSettings = settings.getAppSettings().getDbSettings();
            dbSettings.setApplicationPath(getProjectDirectory().getPath());

            String dbSchema = dbSettings.getInfo().getProperty(ClientConstants.DB_CONNECTION_SCHEMA_PROP_NAME);
            if (dbSchema != null && !dbSchema.isEmpty()) {
                String url = dbSettings.getUrl();
                String dbUserName = dbSettings.getInfo().getProperty(ClientConstants.DB_CONNECTION_USER_PROP_NAME);
                String dbPassword = dbSettings.getInfo().getProperty(ClientConstants.DB_CONNECTION_PASSWORD_PROP_NAME);
                List<String> schemas = PlatypusUtils.achieveSchemas(url, dbUserName, dbPassword);
                List<String> upperCaseSchemas = new ArrayList<>();
                for (String s : schemas) {
                    upperCaseSchemas.add(s.toUpperCase());
                }
                if (!upperCaseSchemas.contains(dbSchema.toUpperCase())) {
                    NotifyDescriptor d = new NotifyDescriptor(
                            String.format(NbBundle.getMessage(PlatypusProject.class, "MSG_Create_New_Schema_Dialog"), dbSchema, dbSchema), //NOI18N
                            NbBundle.getMessage(PlatypusProject.class, "LBL_Create_New_Schema_Dialog_Title"), //NOI18N
                            NotifyDescriptor.OK_CANCEL_OPTION,
                            NotifyDescriptor.QUESTION_MESSAGE,
                            null,
                            null);
                    if (DialogDisplayer.getDefault().notify(d).equals(NotifyDescriptor.OK_OPTION)) {
                        PlatypusUtils.createSchema(url, dbUserName, dbPassword, dbSchema);
                    }
                }
            }
            final DbClient lclient = (DbClient) ClientFactory.getInstance(dbSettings);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    final DbClient oldValue = client;
                    client = lclient;
                    connecting2Db = null;
                    if (oldValue != client) {
                        fireClientChange();
                    }
                    String dbConnectingCompleteMsg = NbBundle.getMessage(PlatypusProject.class, "LBL_Connecting_Complete"); // NOI18N  
                    StatusDisplayer.getDefault().setStatusText(dbConnectingCompleteMsg);
                    getOutputWindowIO().getOut().println(dbConnectingCompleteMsg);
                }
            });
        } catch (Exception ex) {
            connecting2Db = null;
            Logger.getLogger(PlatypusProject.class.getName()).log(Level.INFO, null, ex);
            String dbUnableToConnectMsg = NbBundle.getMessage(PlatypusProject.class, "LBL_UnableToConnect"); // NOI18N
            StatusDisplayer.getDefault().setStatusText(dbUnableToConnectMsg);                 
            getOutputWindowIO().getErr().println(dbUnableToConnectMsg);
        }
    }

    public Component generateDbPlaceholder() throws Exception {
        byte[] htmlData = BinaryUtils.readStream(PlatypusProject.class.getResourceAsStream(NbBundle.getMessage(PlatypusProject.class, "dbPlaceholder")), -1);  // NOI18N 
        String html = new String(htmlData, PlatypusUtils.COMMON_ENCODING_NAME);
        URL urlBase = PlatypusProject.class.getResource("");
        html = html.replaceAll("noDatabaseBaseHref", urlBase.toString());
        JEditorPane htmlPage = new JEditorPane("text/html", html);
        htmlPage.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED && e.getDescription().equals("platypus://start_connecting")) { // NOI18N   
                    startConnecting2db();
                }
            }
        });
        htmlPage.setEditable(false);
        return new JScrollPane(htmlPage);
    }

    public HandlerRegistration addClientChangeListener(final Runnable onChange) {
        clientListeners.add(onChange);
        return new HandlerRegistration() {
            @Override
            public void remove() {
                clientListeners.remove(onChange);
            }
        };
    }

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

    public PlatypusProjectSettings getSettings() {
        return settings;
    }

    public void save() throws Exception {
        getSettings().save();
    }

    public boolean isAutoDeployEnabled() {
        return autoDeployEnabled;
    }

    public synchronized void setAutoDeployEnabled(boolean isEnabled) {
        autoDeployEnabled = isEnabled;
    }

    public String getDisplayName() {
        return settings.getDisplayName();
    }

    public ProjectState getState() {
        return state;
    }

    public final FileObject getSrcRoot() throws Exception {
        return getDirectory(PlatypusUtils.PLATYPUS_PROJECT_SOURCES_ROOT);
    }

    public FileObject getDbMigrationsRoot() throws Exception {
        return getDirectory(PlatypusUtils.PLATYPUS_PROJECT_DB_MIGRATIONS_DIR);
    }

    private FileObject getDirectory(String name) {
        FileObject directory = projectDir.getFileObject(name);
        if (directory == null) {
            try {
                directory = projectDir.createFolder(name);
            } catch (IOException ex) {
                Logger.getLogger(PlatypusProject.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return directory;
    }

    public PlatypusProjectInformation getProjectInfo() {
        return info;
    }

    private final class PlatypusOpenedHook extends ProjectOpenedHook {

        @Override
        protected void projectOpened() {
            try {
                Logger.getLogger(PlatypusProject.class.getName()).log(Level.INFO, "Project opened");
                sourceRoot = ClassPath.getClassPath(getSrcRoot(), PlatypusPathRecognizer.SOURCE_CP);
                GlobalPathRegistry.getDefault().register(PlatypusPathRecognizer.SOURCE_CP, new ClassPath[]{sourceRoot});
                startConnecting2db();
            } catch (Exception ex) {
                Logger.getLogger(PlatypusProject.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        protected void projectClosed() {
            try {
                Logger.getLogger(PlatypusProject.class.getName()).log(Level.INFO, "Project closed");
                GlobalPathRegistry.getDefault().unregister(PlatypusPathRecognizer.SOURCE_CP, new ClassPath[]{sourceRoot});
                disconnectFormDb();
            } catch (Exception ex) {
                Logger.getLogger(PlatypusProject.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private final class PlatypusClassPathProvider implements ClassPathProvider {

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
                Logger.getLogger(PlatypusProject.class.getName()).log(Level.SEVERE, null, ex);
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
