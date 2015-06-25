/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.AppElementFiles;
import com.eas.client.Application;
import com.eas.client.DatabasesClient;
import com.eas.client.ModulesProxy;
import com.eas.client.ScriptedDatabasesClient;
import com.eas.client.ServerModulesProxy;
import com.eas.client.SqlQuery;
import com.eas.client.cache.ApplicationSourceIndexer;
import com.eas.client.cache.FormsDocuments;
import com.eas.client.cache.ModelsDocuments;
import com.eas.client.cache.ReportsConfigs;
import com.eas.client.cache.ScriptsConfigs;
import com.eas.client.cache.ScriptDocument;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.queries.ContextHost;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.scripts.ScriptedResource;
import com.eas.script.JsDoc;
import com.eas.script.Scripts;
import com.eas.server.handlers.ServerModuleStructureRequestHandler;
import com.eas.server.handlers.RPCRequestHandler;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
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

    protected String defaultAppElement;
    protected SessionManager sessionManager;
    protected ScriptedDatabasesClient basesProxy;
    protected ApplicationSourceIndexer indexer;
    protected ModulesProxy modules;
    protected QueriesProxy<SqlQuery> queries;
    protected ScriptsConfigs scriptsConfigs;
    protected FormsDocuments forms = new FormsDocuments();
    protected ReportsConfigs reports = new ReportsConfigs();
    protected ModelsDocuments models = new ModelsDocuments();
    protected Scripts.Space queueSpace;
    protected Queue<Scripts.Space> spacesPool = new ConcurrentLinkedQueue<>();

    public PlatypusServerCore(ApplicationSourceIndexer aIndexer, ModulesProxy aModules, QueriesProxy<SqlQuery> aQueries, ScriptedDatabasesClient aDatabasesClient, ScriptsConfigs aSecurityConfigs, String aDefaultAppElement) throws Exception {
        this(aIndexer, aModules, aQueries, aDatabasesClient, aSecurityConfigs, aDefaultAppElement, new SessionManager());
    }

    public PlatypusServerCore(ApplicationSourceIndexer aIndexer, ModulesProxy aModules, QueriesProxy<SqlQuery> aQueries, ScriptedDatabasesClient aDatabasesClient, ScriptsConfigs aSecurityConfigs, String aDefaultAppElement, SessionManager aSessionManager) throws Exception {
        super();
        indexer = aIndexer;
        modules = aModules;
        queries = aQueries;
        basesProxy = aDatabasesClient;
        sessionManager = aSessionManager;
        defaultAppElement = aDefaultAppElement;
        scriptsConfigs = aSecurityConfigs;
        queueSpace = Scripts.createQueue();
    }

    public Scripts.Space getQueueSpace() {
        return queueSpace;
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
    public ScriptsConfigs getScriptsConfigs() {
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
     * @param aNetworkRPC
     * @param aOnSuccess
     * @param aOnFailure
     */
    public void executeMethod(String aModuleName, String aMethodName, Object[] aArguments, boolean aNetworkRPC, Session aSession, Consumer<Object> aOnSuccess, Consumer<Exception> aOnFailure) {
        Scripts.LocalContext callingContext = Scripts.getContext();
        Consumer<Object> onSuccess = (Object res) -> {
            Scripts.LocalContext oldContext = Scripts.getContext();
            Scripts.setContext(callingContext);
            try {
                Scripts.getSpace().process(() -> {
                    aOnSuccess.accept(res);
                });
            } finally {
                Scripts.setContext(oldContext);
            }
        };
        Consumer<Exception> onFailure = (Exception ex) -> {
            Scripts.LocalContext oldContext = Scripts.getContext();
            Scripts.setContext(callingContext);
            try {
                Scripts.getSpace().process(() -> {
                    aOnFailure.accept(ex);
                });
            } finally {
                Scripts.setContext(oldContext);
            }
        };
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
                            ServerModuleStructureRequestHandler.checkPrincipalPermission(config.getModuleAllowedRoles(), aModuleName);
                            Scripts.Space targetSpace;
                            Session targetSession;
                            if (config.hasModuleAnnotation(JsDoc.Tag.RESIDENT_TAG)) {
                                targetSession = sessionManager.getSystemSession();
                                targetSpace = sessionManager.getSystemSession().getSpace();
                            } else if (config.hasModuleAnnotation(JsDoc.Tag.STATELESS_TAG)) {
                                Scripts.Space pooledSpace = spacesPool.poll();
                                targetSpace = pooledSpace == null ? Scripts.createSpace() : pooledSpace;
                                targetSession = null;
                            } else {
                                targetSpace = Scripts.getSpace();
                                targetSession = aSession;
                            }
                            Scripts.LocalContext targetContext = Scripts.createContext(targetSpace);
                            targetContext.setAsync(callingContext.getAsync());
                            targetContext.setPrincipal(callingContext.getPrincipal());
                            targetContext.setRequest(callingContext.getRequest());
                            targetContext.setResponse(callingContext.getResponse());
                            Scripts.setContext(targetContext);
                            try {
                                targetSpace.process(() -> {
                                    try {
                                        ScriptedResource._require(new String[]{aModuleName}, null, Scripts.getSpace(), (Void v) -> {
                                            try {
                                                JSObject moduleInstance;
                                                if (targetSession == null || !targetSession.containsModule(aModuleName)) {
                                                    JSObject constr = Scripts.getSpace().lookupInGlobal(aModuleName);
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
                                                                    onSuccess.accept(returned);// WARNING! Don't insert .toJava() because of RPC handler
                                                                    if (targetSession == null) {// stateless module called
                                                                        spacesPool.offer(targetSpace);
                                                                    }
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
                                                                    Object reason = largs.length > 0 ? Scripts.getSpace().toJava(largs[0]) : null;
                                                                    if (reason instanceof Exception) {
                                                                        onFailure.accept((Exception) reason);
                                                                    } else {
                                                                        onFailure.accept(new Exception(String.valueOf(reason)));
                                                                    }
                                                                    if (targetSession == null) {// stateless module called
                                                                        spacesPool.offer(targetSpace);
                                                                    }
                                                                } else {
                                                                    Logger.getLogger(RPCRequestHandler.class.getName()).log(Level.WARNING, RPCRequestHandler.BOTH_IO_MODELS_MSG, new Object[]{aMethodName, aModuleName});
                                                                }
                                                                return null;
                                                            }

                                                        });
                                                        Scripts.getContext().initAsyncs(0);
                                                        try {
                                                            ServerModuleStructureRequestHandler.checkPrincipalPermission(config.getPropertyAllowedRoles().get(aMethodName), aModuleName + "." + aMethodName);
                                                            Object result = ((JSObject) oFun).call(moduleInstance, args.toArray());
                                                            int asyncs = Scripts.getContext().getAsyncsCount();
                                                            if (!(result instanceof Undefined) || asyncs == 0) {
                                                                if (!args.isEmpty()) {
                                                                    args.clear();
                                                                    onSuccess.accept(result);// WARNING! Don't insert .toJava() because of RPC handler
                                                                    if (targetSession == null) {// stateless module called
                                                                        spacesPool.offer(targetSpace);
                                                                    }
                                                                } else {
                                                                    Logger.getLogger(RPCRequestHandler.class.getName()).log(Level.WARNING, RPCRequestHandler.BOTH_IO_MODELS_MSG, new Object[]{aMethodName, aModuleName});
                                                                }
                                                            }
                                                        } finally {
                                                            Scripts.getContext().initAsyncs(null);
                                                        }
                                                    } else {
                                                        throw new Exception(String.format(RPCRequestHandler.METHOD_MISSING_MSG, aMethodName, aModuleName));
                                                    }
                                                } else {
                                                    throw new Exception(String.format(RPCRequestHandler.MODULE_MISSING_MSG, aModuleName));
                                                }
                                            } catch (Exception ex) {
                                                onFailure.accept(ex);
                                                if (targetSession == null) {// stateless module called
                                                    spacesPool.offer(targetSpace);
                                                }
                                            }
                                        }, (Exception ex) -> {
                                            onFailure.accept(ex);
                                            if (targetSession == null) {// stateless module called
                                                spacesPool.offer(targetSpace);
                                            }
                                        });
                                    } catch (Exception ex) {
                                        onFailure.accept(ex);
                                        if (targetSession == null) {// stateless module called
                                            spacesPool.offer(targetSpace);
                                        }
                                    }
                                });
                            } finally {
                                Scripts.setContext(callingContext);
                            }
                        } else {
                            throw new AccessControlException(String.format("Public access to module %s is denied.", aModuleName));//NOI18N
                        }
                    } else {
                        throw new IllegalArgumentException(String.format(RPCRequestHandler.MODULE_MISSING_OR_NOT_A_MODULE, aModuleName));
                    }
                } catch (Exception ex) {
                    onFailure.accept(ex);
                }
            }
        }
    }

    public void startResidents(Set<String> aRezidents) throws Exception {
        Scripts.Space space = sessionManager.getSystemSession().getSpace();
        Scripts.LocalContext context = Scripts.createContext(space);
        context.setAsync(null);
        context.setPrincipal(sessionManager.getSystemSession().getPrincipal());
        context.setRequest(null);
        context.setResponse(null);
        Scripts.setContext(context);
        try {
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
        } finally {
            Scripts.setContext(null);
        }
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
        Scripts.LocalContext context = Scripts.getContext();
        if (context != null && context.getPrincipal() != null) {
            return ((PlatypusPrincipal) context.getPrincipal()).getContext();
        } else {
            return null;
        }
    }

    @Override
    public String unpreparationContext() throws Exception {
        return basesProxy.getDbMetadataCache(null).getConnectionSchema();
    }
}
