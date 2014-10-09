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
import com.eas.client.cache.FormsDocuments;
import com.eas.client.cache.ModelsDocuments;
import com.eas.client.cache.ReportsConfigs;
import com.eas.client.cache.ScriptDocument;
import com.eas.client.cache.ScriptSecurityConfigs;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.login.SystemPlatypusPrincipal;
import com.eas.client.queries.ContextHost;
import com.eas.client.queries.LocalQueriesProxy;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.scripts.ScriptedResource;
import com.eas.script.JsDoc;
import com.eas.script.ScriptUtils;
import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;

/**
 * The core class for platypus server infrastructure (e.g. Standalone J2SE
 * server and J2EE servlets).
 *
 * @author mg
 */
public class PlatypusServerCore implements ContextHost, Application<SqlQuery> {

    protected static PlatypusServerCore instance;

    public static PlatypusServerCore getInstance(String aApplicationUrl, String aDefaultDatasourceName, String aStartAppElementName) throws Exception {
        ScriptUtils.init();
        if (instance == null) {
            final Set<String> tasks = new HashSet<>();
            ScriptedDatabasesClient serverCoreDbClient;
            if (aApplicationUrl.toLowerCase().startsWith("file")) {
                File f = new File(new URI(aApplicationUrl));
                if (f.exists() && f.isDirectory()) {
                    ScriptSecurityConfigs securityConfigs = new ScriptSecurityConfigs();
                    ApplicationSourceIndexer indexer = new ApplicationSourceIndexer(f.getPath(), new ServerTasksScanner(tasks, securityConfigs));
                    indexer.watch();
                    serverCoreDbClient = new ScriptedDatabasesClient(aDefaultDatasourceName, indexer, true);
                    instance = new PlatypusServerCore(indexer, new LocalModulesProxy(indexer, new ModelsDocuments(), aStartAppElementName), new LocalQueriesProxy(serverCoreDbClient, indexer), serverCoreDbClient, tasks, securityConfigs, aStartAppElementName);
                    serverCoreDbClient.setContextHost(instance);
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
    protected SessionManager sessionManager;
    protected ScriptedDatabasesClient basesProxy;
    protected ApplicationSourceIndexer indexer;
    protected ModulesProxy modules;
    protected QueriesProxy<SqlQuery> queries;
    protected final Set<String> tasks;
    protected final Set<String> extraAuthorizers = new HashSet<>();
    protected ScriptSecurityConfigs securityConfigs;
    protected FormsDocuments forms = new FormsDocuments();
    protected ReportsConfigs reports = new ReportsConfigs();
    protected ModelsDocuments models = new ModelsDocuments();

    public PlatypusServerCore(ApplicationSourceIndexer aIndexer, ModulesProxy aModules, QueriesProxy<SqlQuery> aQueries, ScriptedDatabasesClient aDatabasesClient, Set<String> aTasks, ScriptSecurityConfigs aSecurityConfigs, String aDefaultAppElement) throws Exception {
        super();
        indexer = aIndexer;
        modules = aModules;
        queries = aQueries;
        basesProxy = aDatabasesClient;
        sessionManager = new SessionManager(this);
        defaultAppElement = aDefaultAppElement;
        tasks = aTasks;
        securityConfigs = aSecurityConfigs;
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

    @Override
    public ScriptSecurityConfigs getSecurityConfigs() {
        return securityConfigs;
    }

    @Override
    public ModelsDocuments getModels() {
        return models;
    }

    @Override
    public ReportsConfigs getReports() {
        return reports;
    }

    @Override
    public FormsDocuments getForms() {
        return forms;
    }

    @Override
    public ServerModulesProxy getServerModules() {
        return null;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public DatabasesClient getDatabasesClient() {
        return basesProxy;
    }

    public String getDefaultAppElement() {
        return defaultAppElement;
    }

    public boolean isUserInApplicationRole(String aUser, String aRole) throws Exception {
        for (String moduleName : extraAuthorizers) {
            Object result = executeServerModuleMethod(moduleName, "isUserInRole", new Object[]{aUser, aRole}, null, null);
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
     * @param onSuccess
     * @param onFailure
     * @return
     * @throws Exception
     */
    public Object executeServerModuleMethod(String aModuleName, String aMethodName, Object[] aArgs, Consumer<Object> onSuccess, Consumer<Exception> onFailure) throws Exception {
        JSObject module = getSessionManager().getSystemSession().getModule(aModuleName);
        PlatypusPrincipal oldPrincipal = PlatypusPrincipal.getInstance();
        PlatypusPrincipal.setInstance(new SystemPlatypusPrincipal());
        try {
            if (module == null) {
                ScriptedResource.require(new String[]{aModuleName});
                module = ScriptUtils.createModule(aModuleName);
            }
            if (module != null) {
                Object oFunction = module.getMember(aMethodName);
                if (oFunction instanceof JSObject && ((JSObject) oFunction).isFunction()) {
                    if (onSuccess != null) {
                        Object[] args = ScriptUtils.toJs(aArgs);
                        List<Object> listArgs = Arrays.asList(args);
                        listArgs.add(new AbstractJSObject() {

                            @Override
                            public Object call(final Object thiz, final Object... args) {
                                Object result = null;
                                if (args.length > 0) {
                                    result = ScriptUtils.toJava(args[0]);
                                }
                                onSuccess.accept(result);
                                return null;
                            }

                        });
                        listArgs.add(new AbstractJSObject() {

                            @Override
                            public Object call(final Object thiz, final Object... args) {
                                if (onFailure != null) {
                                    if (args.length > 0) {
                                        if (args[0] instanceof Exception) {
                                            onFailure.accept((Exception) args[0]);
                                        } else {
                                            onFailure.accept(new Exception(String.valueOf(ScriptUtils.toJava(args[0]))));
                                        }
                                    } else {
                                        onFailure.accept(new Exception("No error information from " + aMethodName + " method"));
                                    }
                                }
                                return null;
                            }
                        });
                        ((JSObject) oFunction).call(module, listArgs.toArray());
                        return null;
                    } else {
                        return ScriptUtils.toJava(((JSObject) oFunction).call(module, ScriptUtils.toJs(aArgs)));
                    }
                } else {
                    throw new Exception(String.format("Module %s has no function %s", aModuleName, aMethodName));
                }
            } else {
                throw new Exception(String.format("Module %s is not found.", aModuleName));
            }
        } finally {
            PlatypusPrincipal.setInstance(oldPrincipal);
        }
    }

    public Set<String> getTasks() {
        return tasks;
    }

    public int startServerTasks() throws Exception {
        PlatypusPrincipal oldPrincipal = PlatypusPrincipal.getInstance();
        PlatypusPrincipal.setInstance(new SystemPlatypusPrincipal());
        try {
            int startedTasks = 0;
            for (String moduleName : tasks) {
                AppElementFiles files = modules.nameToFiles(moduleName);
                if (files != null && files.isModule()) {
                    ScriptDocument sDoc = securityConfigs.get(moduleName, files);
                    for (JsDoc.Tag tag : sDoc.getModuleAnnotations()) {
                        switch (tag.getName()) {
                            case JsDoc.Tag.AUTHORIZER_TAG:
                                extraAuthorizers.add(moduleName);
                                Logger.getLogger(PlatypusServerCore.class.getName()).log(Level.INFO, "Authorizer \"{0}\" registered", moduleName);
                                break;
                            case JsDoc.Tag.VALIDATOR_TAG:
                                basesProxy.addValidator(moduleName, tag.getParams());
                                Logger.getLogger(PlatypusServerCore.class.getName()).log(Level.INFO, "Validator \"{0}\" registered", moduleName);
                                break;
                            case JsDoc.Tag.RESIDENT_TAG:
                                if (startResidentModule(moduleName)) {
                                    startedTasks++;
                                }
                                break;
                        }
                    }
                } else {
                    Logger.getLogger(PlatypusServerCore.class.getName()).log(Level.WARNING, "Server task \"{0}\" is illegal (no module). Skipping it.", moduleName);
                }
            }
            return startedTasks;
        } finally {
            PlatypusPrincipal.setInstance(oldPrincipal);
        }
    }

    /**
     * Starts a server task, initializing it with supplied module annotations.
     *
     * @param aModuleName Module identifier, specifying a module for the task
     * @return Success status
     * @throws java.lang.Exception
     */
    public boolean startResidentModule(String aModuleName) throws Exception {
        ScriptedResource.require(new String[]{aModuleName});
        Logger.getLogger(PlatypusServerCore.class.getName()).log(Level.INFO, "Starting resident module \"{0}\"", aModuleName);
        try {
            JSObject module = ScriptUtils.getCachedModule(aModuleName);
            if (module != null) {
                sessionManager.getSystemSession().registerModule(module);
                Logger.getLogger(PlatypusServerCore.class.getName()).log(Level.INFO, "Resident module \"{0}\" started successfully", aModuleName);
                return true;
            } else {
                Logger.getLogger(PlatypusServerCore.class.getName()).log(Level.WARNING, "Resident module \"{0}\" is illegal (may be bad name). Skipping it.", aModuleName);
                return false;
            }
        } catch (Exception ex) {
            Logger.getLogger(PlatypusServerCore.class.getName()).log(Level.SEVERE, "Resident module \"{0}\" caused an error: {1}. Skipping it.", new Object[]{aModuleName, ex.getMessage()});
            return false;
        }
    }

    @Override
    public String preparationContext() throws Exception {
        PlatypusPrincipal principal = PlatypusPrincipal.getInstance();
        return principal != null ? principal.getContext() : null;
    }

    @Override
    public String unpreparationContext() throws Exception {
        return basesProxy.getDbMetadataCache(null).getConnectionSchema();
    }
}
