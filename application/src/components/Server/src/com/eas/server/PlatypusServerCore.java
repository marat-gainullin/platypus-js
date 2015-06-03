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
import com.eas.client.cache.ScriptConfigs;
import com.eas.client.cache.ScriptDocument;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.queries.ContextHost;
import com.eas.client.queries.LocalQueriesProxy;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.scripts.ScriptedResource;
import com.eas.script.JsDoc;
import com.eas.script.Scripts;
import com.eas.server.handlers.ServerModuleStructureRequestHandler;
import com.eas.server.handlers.RPCRequestHandler;
import java.io.File;
import java.net.URI;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.Undefined;

/**
 * The core class for platypus server infrastructure (e.g. Standalone J2SE
 * server and J2EE servlets).
 *
 * @author mg
 */
public class PlatypusServerCore implements ContextHost, Application<SqlQuery> {

    protected static PlatypusServerCore instance;

    public static PlatypusServerCore getInstance(String aApplicationUrl, String aDefaultDatasourceName, String aStartAppElementName, int aMaximumJdbcThreads, int aMaximumServicesThreads) throws Exception {
        if (instance == null) {
            ScriptedDatabasesClient basesProxy;
            if (aApplicationUrl.toLowerCase().startsWith("file")) {
                File f = new File(new URI(aApplicationUrl));
                if (f.exists() && f.isDirectory()) {
                    ScriptConfigs lsecurityConfigs = new ScriptConfigs();
                    ServerTasksScanner tasksScanner = new ServerTasksScanner(lsecurityConfigs);
                    ApplicationSourceIndexer indexer = new ApplicationSourceIndexer(f.getPath(), tasksScanner);
                    indexer.watch();
                    Scripts.initBIO(aMaximumServicesThreads);
                    basesProxy = new ScriptedDatabasesClient(aDefaultDatasourceName, indexer, true, tasksScanner.getValidators(), aMaximumJdbcThreads);
                    QueriesProxy<SqlQuery> queries = new LocalQueriesProxy(basesProxy, indexer);
                    basesProxy.setQueries(queries);
                    instance = new PlatypusServerCore(indexer, new LocalModulesProxy(indexer, new ModelsDocuments(), aStartAppElementName), queries, basesProxy, lsecurityConfigs, aStartAppElementName);
                    basesProxy.setContextHost(instance);
                    ScriptedResource.init(instance);
                    instance.startResidents(tasksScanner.getResidents());
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
    protected ScriptConfigs scriptsConfigs;
    protected FormsDocuments forms = new FormsDocuments();
    protected ReportsConfigs reports = new ReportsConfigs();
    protected ModelsDocuments models = new ModelsDocuments();

    public PlatypusServerCore(ApplicationSourceIndexer aIndexer, ModulesProxy aModules, QueriesProxy<SqlQuery> aQueries, ScriptedDatabasesClient aDatabasesClient, ScriptConfigs aSecurityConfigs, String aDefaultAppElement) throws Exception {
        super();
        indexer = aIndexer;
        modules = aModules;
        queries = aQueries;
        basesProxy = aDatabasesClient;
        sessionManager = new SessionManager();
        defaultAppElement = aDefaultAppElement;
        scriptsConfigs = aSecurityConfigs;
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
    public ScriptConfigs getScriptsConfigs() {
        return scriptsConfigs;
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

    /**
     * Executes a script module according to all rules defimed within
     * Platypus.js Such as @wait, @stateless and @rezident annotations, async-io
     * convensions etc.
     *
     * @param aModuleName
     * @param aMethodName
     * @param aArguments
     * @param aSession
     * @param onSuccess
     * @param onFailure
     */
    public void executeMethod(String aModuleName, String aMethodName, Object[] aArguments, Session aSession, boolean aNetworkRPC, Consumer<Object> onSuccess, Consumer<Exception> onFailure) {
        if (aModuleName == null || aModuleName.isEmpty()) {
            onFailure.accept(new Exception("Module name is missing. Unnamed server modules are not allowed."));
        } else {
            if (aMethodName == null || aMethodName.isEmpty()) {
                onFailure.accept(new Exception("Module's method name is missing."));
            } else {
                try {
                    AppElementFiles files = indexer.nameToFiles(aModuleName);
                    if (files != null && files.isModule()) {
                        ScriptDocument config = scriptsConfigs.get(aModuleName, files);
                        if (!aNetworkRPC || config.hasModuleAnnotation(JsDoc.Tag.PUBLIC_TAG)) {
                            // Let's perform security checks
                            ServerModuleStructureRequestHandler.checkPrincipalPermission(aSession, config.getModuleAllowedRoles(), aModuleName);
                            Scripts.Space targetSpace;
                            Session targetSession;
                            if (config.hasModuleAnnotation(JsDoc.Tag.RESIDENT_TAG)) {
                                targetSession = sessionManager.getSystemSession();
                                targetSpace = sessionManager.getSystemSession().getSpace();
                            } else if (config.hasModuleAnnotation(JsDoc.Tag.STATELESS_TAG)) {
                                targetSpace = Scripts.createSpace();
                                targetSpace.setSession(aSession.getSpace().getSession());
                                targetSpace.setPrincipal(aSession.getSpace().getPrincipal());
                                targetSession = null;
                            } else {
                                targetSpace = aSession.getSpace();
                                targetSession = aSession;
                            }
                            targetSpace.process(() -> {
                                try {
                                    ScriptedResource._require(new String[]{aModuleName}, null, targetSpace, (Void v) -> {
                                        try {
                                            JSObject moduleInstance;
                                            if (targetSession == null || !targetSession.containsModule(aModuleName)) {
                                                JSObject constr = targetSpace.lookupInGlobal(aModuleName);
                                                if (constr != null) {
                                                    moduleInstance = (JSObject) constr.newObject(new Object[]{});
                                                    if (targetSession != null) {
                                                        targetSession.registerModule(aModuleName, moduleInstance);
                                                    }
                                                } else {
                                                    throw new IllegalArgumentException(String.format(RPCRequestHandler.MODULE_MISSING_OR_NOT_A_MODULE, aModuleName));
                                                }
                                            } else {
                                                moduleInstance = targetSession.getModule(aModuleName);
                                            }
                                            if (moduleInstance != null) {
                                                Logger.getLogger(PlatypusServerCore.class.getName()).log(Level.FINE, RPCRequestHandler.EXECUTING_METHOD_TRACE_MSG, new Object[]{aMethodName, aModuleName});
                                                Object oFun = moduleInstance.getMember(aMethodName);
                                                if (oFun instanceof JSObject && ((JSObject) oFun).isFunction()) {
                                                    List<Object> args = new ArrayList<>(Arrays.asList(aArguments));
                                                    args.add(new AbstractJSObject() {
                                                        @Override
                                                        public Object call(final Object thiz, final Object... largs) {
                                                            if (!args.isEmpty()) {
                                                                args.clear();
                                                                Object returned = largs.length > 0 ? largs[0] : null;
                                                                aSession.getSpace().process(() -> {
                                                                    onSuccess.accept(targetSpace.toJava(returned));
                                                                });
                                                            } else {
                                                                Logger.getLogger(RPCRequestHandler.class.getName()).log(Level.WARNING, RPCRequestHandler.BOTH_IO_MODELS_MSG, new Object[]{aMethodName, aModuleName});
                                                            }
                                                            return null;
                                                        }

                                                    });
                                                    args.add(new AbstractJSObject() {
                                                        @Override
                                                        public Object call(final Object thiz, final Object... largs) {
                                                            if (!args.isEmpty()) {
                                                                args.clear();
                                                                Object reason = largs.length > 0 ? targetSpace.toJava(largs[0]) : null;
                                                                aSession.getSpace().process(() -> {
                                                                    if (reason instanceof Exception) {
                                                                        onFailure.accept((Exception) reason);
                                                                    } else {
                                                                        onFailure.accept(new Exception(String.valueOf(reason)));
                                                                    }
                                                                });
                                                            } else {
                                                                Logger.getLogger(RPCRequestHandler.class.getName()).log(Level.WARNING, RPCRequestHandler.BOTH_IO_MODELS_MSG, new Object[]{aMethodName, aModuleName});
                                                            }
                                                            return null;
                                                        }

                                                    });
                                                    targetSpace.initAsyncs(0);
                                                    try {
                                                        ServerModuleStructureRequestHandler.checkPrincipalPermission(aSession, config.getPropertyAllowedRoles().get(aMethodName), aMethodName);
                                                        Object result = ((JSObject) oFun).call(moduleInstance, args.toArray());
                                                        int asyncs = targetSpace.getAsyncsCount();
                                                        if (!(result instanceof Undefined) || asyncs == 0) {
                                                            if (!args.isEmpty()) {
                                                                args.clear();
                                                                onSuccess.accept(targetSpace.toJava(result));
                                                            } else {
                                                                Logger.getLogger(RPCRequestHandler.class.getName()).log(Level.WARNING, RPCRequestHandler.BOTH_IO_MODELS_MSG, new Object[]{aMethodName, aModuleName});
                                                            }
                                                        }
                                                    } finally {
                                                        targetSpace.initAsyncs(null);
                                                    }
                                                } else {
                                                    throw new Exception(String.format(RPCRequestHandler.METHOD_MISSING_MSG, aMethodName, aModuleName));
                                                }
                                            } else {
                                                throw new Exception(String.format(RPCRequestHandler.MODULE_MISSING_MSG, aModuleName));
                                            }
                                        } catch (Exception ex) {
                                            aSession.getSpace().process(() -> {
                                                onFailure.accept(ex);
                                            });
                                        }
                                    }, onFailure);
                                } catch (Exception ex) {
                                    aSession.getSpace().process(() -> {
                                        onFailure.accept(ex);
                                    });
                                }
                            });
                        } else {
                            throw new AccessControlException(String.format("Public access to module %s is denied.", aModuleName));//NOI18N
                        }
                    } else {
                        throw new IllegalArgumentException(String.format(RPCRequestHandler.MODULE_MISSING_OR_NOT_A_MODULE, aModuleName));
                    }
                } catch (Exception ex) {
                    aSession.getSpace().process(() -> {
                        onFailure.accept(ex);
                    });
                }
            }
        }
    }

    public void startResidents(Set<String> aRezidents) throws Exception {
        Scripts.Space space = sessionManager.getSystemSession().getSpace();
        space.process(() -> {
            aRezidents.stream().forEach((moduleName) -> {
                try {
                    AppElementFiles files = modules.nameToFiles(moduleName);
                    if (files != null && files.isModule()) {
                        startResidentModule(moduleName, space);
                    } else {
                        Logger.getLogger(PlatypusServerCore.class.getName()).log(Level.WARNING, "Rezident task \"{0}\" is illegal (no module). Skipping it.", moduleName);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusServerCore.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        });
    }

    /**
     * Starts a server task, initializing it with supplied module annotations.
     *
     * @param aModuleName Module identifier, specifying a module for the task
     * @param aSpace
     * @return Success status
     * @throws java.lang.Exception
     */
    protected boolean startResidentModule(String aModuleName, Scripts.Space aSpace) throws Exception {
        ScriptedResource.require(new String[]{aModuleName}, null);
        Logger.getLogger(PlatypusServerCore.class.getName()).log(Level.INFO, "Starting resident module \"{0}\"", aModuleName);
        try {
            JSObject jsConstr = aSpace.lookupInGlobal(aModuleName);
            Object oModule = jsConstr != null ? jsConstr.newObject(new Object[]{}) : null;
            JSObject module = oModule instanceof JSObject ? (JSObject) oModule : null;
            if (module != null) {
                sessionManager.getSystemSession().registerModule(module);
                Logger.getLogger(PlatypusServerCore.class.getName()).log(Level.INFO, "Resident module \"{0}\" has been started successfully", aModuleName);
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
        PlatypusPrincipal principal = (PlatypusPrincipal) Scripts.getSpace().getPrincipal();
        return principal != null ? principal.getContext() : null;
    }

    @Override
    public String unpreparationContext() throws Exception {
        return basesProxy.getDbMetadataCache(null).getConnectionSchema();
    }
}
