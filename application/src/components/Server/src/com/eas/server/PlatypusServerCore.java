/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.AppElementFiles;
import com.eas.client.Application;
import com.eas.client.DatabasesClient;
import com.eas.client.LocalModulesProxy;
import com.eas.client.ModulesProxy;
import com.eas.client.ScriptedDatabasesClient;
import com.eas.client.ServerModulesProxy;
import com.eas.client.SqlQuery;
import com.eas.client.cache.ApplicationSourceIndexer;
import com.eas.client.cache.ModelsDocuments;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.login.AnonymousPlatypusPrincipal;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.login.PrincipalHost;
import com.eas.client.queries.ContextHost;
import com.eas.client.queries.LocalQueriesProxy;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.scripts.ScriptDocument;
import com.eas.client.scripts.ScriptedResource;
import com.eas.client.settings.SettingsConstants;
import com.eas.script.JsDoc;
import com.eas.script.ScriptUtils;
import com.eas.util.FileUtils;
import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.JSObject;

/**
 * The core class for platypus server infrastructure (e.g. Standalone J2SE
 * server and J2EE servlets).
 *
 * @author mg
 */
public class PlatypusServerCore implements ContextHost, PrincipalHost, Application<SqlQuery> {

    protected static PlatypusServerCore instance;

    public static PlatypusServerCore getInstance(String aApplicationUrl, String aDefaultDatasourceName, String aStartAppElementId) throws Exception {
        ScriptUtils.init();
        if (instance == null) {
            final Set<String> tasks = new HashSet<>();
            ScriptedDatabasesClient serverCoreDbClient;
            if (aApplicationUrl.toLowerCase().startsWith("file")) {
                File f = new File(new URI(aApplicationUrl));
                if (f.exists() && f.isDirectory()) {
                    ApplicationSourceIndexer indexer = new ApplicationSourceIndexer(f.getPath(), new ServerTasksScanner(tasks));
                    indexer.watch();
                    serverCoreDbClient = new ScriptedDatabasesClient(aDefaultDatasourceName, indexer, true);
                    instance = new PlatypusServerCore(indexer, new LocalModulesProxy(indexer, new ModelsDocuments()), new LocalQueriesProxy(serverCoreDbClient, indexer), serverCoreDbClient, tasks, aStartAppElementId);
                    serverCoreDbClient.setContextHost(instance);
                    serverCoreDbClient.setPrincipalHost(instance);
                    ScriptedResource.init(instance);
                    instance.startServerTasks();
                } else {
                    throw new IllegalArgumentException("applicationUrl: " + aApplicationUrl + " doesn't point to existent directory.");
                }
            } else {
                throw new Exception("Unknown protocol in url: " + aApplicationUrl);
            }
        }
        return instance;
    }

    public static PlatypusServerCore getInstance() throws Exception {
        return instance;
    }

    protected String defaultAppElement;
    protected boolean anonymousEnabled;
    protected SessionManager sessionManager;
    protected ScriptedDatabasesClient databasesClient;
    protected ApplicationSourceIndexer indexer;
    protected ModulesProxy modules;
    protected QueriesProxy<SqlQuery> queries;
    protected final Set<String> tasks;
    protected final Set<String> extraAuthorizers = new HashSet<>();

    public PlatypusServerCore(ApplicationSourceIndexer aIndexer, ModulesProxy aModules, QueriesProxy<SqlQuery> aQueries, ScriptedDatabasesClient aDatabasesClient, Set<String> aTasks, String aDefaultAppElement) throws Exception {
        super();
        indexer = aIndexer;
        modules = aModules;
        queries = aQueries;
        databasesClient = aDatabasesClient;
        sessionManager = new SessionManager(this);
        defaultAppElement = aDefaultAppElement;
        tasks = aTasks;
    }

    public ApplicationSourceIndexer getIndexer() {
        return indexer;
    }

    @Override
    public ModulesProxy getModules() {
        return modules;
    }

    @Override
    public QueriesProxy<SqlQuery> getQueries() {
        return queries;
    }

    public boolean isAnonymousEnabled() {
        return anonymousEnabled;
    }

    @Override
    public ServerModulesProxy getServerModules() {
        return null;
    }

    public void setAnonymousEnabled(boolean aValue) {
        anonymousEnabled = aValue;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public DatabasesClient getDatabasesClient() {
        return databasesClient;
    }

    public String getDefaultAppElement() {
        return defaultAppElement;
    }

    public boolean isUserInApplicationRole(String aUser, String aRole) throws Exception {
        for (String moduleName : extraAuthorizers) {
            Object result = executeServerModuleMethod(moduleName, "isUserInRole", new Object[]{aUser, aRole});
            if (Boolean.TRUE.equals(result)) {
                return true;
            }
        }
        return false;
    }

    /**
     * WARNING!!! This method executes a method with system permissions! You
     * should think twice before calling it in your code.
     *
     * @param aModuleName
     * @param aMethodName
     * @param aArgs
     * @return
     * @throws Exception
     */
    public Object executeServerModuleMethod(String aModuleName, String aMethodName, Object[] aArgs) throws Exception {
        JSObject module = getSessionManager().getSystemSession().getModule(aModuleName);
        Session oldSession = getSessionManager().getCurrentSession();
        getSessionManager().setCurrentSession(getSessionManager().getSystemSession());
        try {
            if (module == null) {
                ScriptedResource.require(new String[]{aModuleName});
                module = ScriptUtils.createModule(aModuleName);
            }
            if (module != null) {
                Object oFunction = module.getMember(aMethodName);
                if (oFunction instanceof JSObject && ((JSObject) oFunction).isFunction()) {
                    return ScriptUtils.toJava(((JSObject) oFunction).call(module, ScriptUtils.toJs(aArgs)));
                } else {
                    throw new Exception(String.format("Module %s has no function %s", aModuleName, aMethodName));
                }
            } else {
                throw new Exception(String.format("Module %s is not found.", aModuleName));
            }
        } finally {
            getSessionManager().setCurrentSession(oldSession);
        }
    }

    public Set<String> getTasks() {
        return tasks;
    }

    public int startServerTasks() throws Exception {
        Session oldSession = getSessionManager().getCurrentSession();
        try {
            getSessionManager().setCurrentSession(getSessionManager().getSystemSession());
            int startedTasks = 0;
            for (String moduleId : tasks) {
                if (startServerTask(moduleId)) {
                    startedTasks++;
                }
            }
            return startedTasks;
        } finally {
            getSessionManager().setCurrentSession(oldSession);
        }
    }
    public static final String STARTING_RESIDENT_TASK_MSG = "Starting resident task \"%s\"";
    public static final String STARTED_RESIDENT_TASK_MSG = "Resident task \"%s\" started successfully";

    /**
     * Starts a server task, initializing it with supplied module annotations.
     *
     * @param aModuleName Module identifier, specifying a module for the task
     * @return Success status
     * @throws java.lang.Exception
     */
    public boolean startServerTask(String aModuleName) throws Exception {
        Logger.getLogger(PlatypusServerCore.class.getName()).info(String.format(STARTING_RESIDENT_TASK_MSG, aModuleName));
        ScriptedResource.require(new String[]{aModuleName});
        AppElementFiles files = modules.nameToFiles(aModuleName);
        if (files != null && files.isModule()) {
            File jsFile = files.findFileByExtension(PlatypusFiles.JAVASCRIPT_EXTENSION);
            ScriptDocument sDoc = ScriptDocument.parse(FileUtils.readString(jsFile, SettingsConstants.COMMON_ENCODING));
            boolean stateless = false;
            for (JsDoc.Tag tag : sDoc.getModuleAnnotations()) {
                switch (tag.getName()) {
                    case JsDoc.Tag.STATELESS_TAG:
                        stateless = true;
                        break;
                    case JsDoc.Tag.AUTHORIZER_TAG:
                        extraAuthorizers.add(aModuleName);
                        break;
                    case JsDoc.Tag.VALIDATOR_TAG:
                        stateless = true;
                        databasesClient.addValidator(aModuleName, tag.getParams());
                        break;
                }
            }
            if (!stateless) {
                try {
                    JSObject module = ScriptUtils.getCachedModule(aModuleName);
                    if (module != null) {
                        sessionManager.getSystemSession().registerModule(module);
                        Logger.getLogger(PlatypusServerCore.class.getName()).info(String.format(STARTED_RESIDENT_TASK_MSG, aModuleName));
                        return true;
                    } else {
                        Logger.getLogger(PlatypusServerCore.class.getName()).warning(String.format("Resident task \"%s\" is illegal (may be bad class name). Skipping it.", aModuleName));
                        return false;
                    }
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusServerCore.class.getName()).severe(String.format("Resident task \"%s\" caused an error: %s. Skipping it.", aModuleName, ex.getMessage()));
                    return false;
                }
            } else {
                Logger.getLogger(PlatypusServerCore.class.getName()).warning(String.format("Module \"%s\" is stateless, skipping it. Hope it will be used as an authorizer, validator or as an acceptor.", aModuleName));
                return false;
            }
        } else {
            Logger.getLogger(PlatypusServerCore.class.getName()).warning(String.format("Resident task \"%s\" is illegal (no module). Skipping it.", aModuleName));
            return false;
        }
    }

    @Override
    public String preparationContext() throws Exception {
        Session session = sessionManager.getCurrentSession();
        if (session != null) {
            if (session.getPrincipal() != null) {
                return session.getPrincipal().getContext();
            } else if (session.getContext() != null) {
                return session.getContext();
            }
        }
        return null;
    }

    @Override
    public String unpreparationContext() throws Exception {
        return databasesClient.getDbMetadataCache(null).getConnectionSchema();
    }

    @Override
    public PlatypusPrincipal getPrincipal() {
        if (sessionManager.getCurrentSession() != null) {
            return sessionManager.getCurrentSession().getPrincipal();
        } else {
            // Construct a dummy Principal for a debugger can discover inner Principal's structure
            return new AnonymousPlatypusPrincipal("No current session found");
        }
    }
}
