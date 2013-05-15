/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.ClientConstants;
import com.eas.client.DatabasesClient;
import com.eas.client.login.DbPlatypusPrincipal;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.login.PrincipalHost;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.queries.ContextHost;
import com.eas.client.reports.ReportRunnerPrototype;
import com.eas.client.scripts.CompiledScriptDocuments;
import com.eas.client.scripts.CompiledScriptDocumentsHost;
import com.eas.client.scripts.ScriptResolver;
import com.eas.client.scripts.ScriptResolverHost;
import com.eas.client.scripts.ScriptRunner;
import com.eas.client.scripts.ScriptRunnerPrototype;
import com.eas.client.scripts.ServerScriptProxyPrototype;
import com.eas.script.ScriptUtils;
import com.eas.server.filter.AppElementsFilter;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author mg
 */
public class PlatypusServerCore implements ContextHost, PrincipalHost, CompiledScriptDocumentsHost, ScriptResolverHost {

    static {
        ServerScriptRunnerPrototype.init(ScriptUtils.getScope(), true);
        ReportRunnerPrototype.init(ScriptUtils.getScope(), true);
        ServerScriptProxyPrototype.init(ScriptUtils.getScope(), true);
    }
    protected String defaultAppElement = null;
    protected SessionManager sessionManager;
    protected DatabasesClient databasesClient;
    protected ServerScriptsCache scriptsCache;
    protected ServerCompiledScriptDocuments scriptDocuments;
    private ServerScriptResolver scriptResolver;
    protected AppElementsFilter browsersFilter;
    protected final Set<ModuleConfig> moduleConfigs;

    public PlatypusServerCore(DatabasesClient aDatabasesClient, Set<ModuleConfig> aModuleConfigs, String aDefaultAppElement) throws Exception {
        databasesClient = aDatabasesClient;
        moduleConfigs = aModuleConfigs;
        sessionManager = new SessionManager(this);
        scriptsCache = new ServerScriptsCache(this);
        scriptDocuments = new ServerCompiledScriptDocuments(databasesClient);
        scriptResolver = new ServerScriptResolver(this);
        browsersFilter = new AppElementsFilter(this);
        defaultAppElement = aDefaultAppElement;
    }

    public Set<ModuleConfig> getModuleConfigs() {
        return moduleConfigs;
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

    public ServerScriptsCache getScriptsCache() {
        return scriptsCache;
    }

    /**
     * Starts a background task, initializing it with supplied configuratin
     * instance.
     *
     * @param config ModuleConfig instance, specifying all needed information to
     * start the task
     * @param executor ExecutorService instance to used as actual runner of
     * startup process. May be null. If it's null, than task will be started in
     * calling thread.
     * @return Success status
     * @see ModuleConfig
     */
    public boolean startBackgroundTask(ModuleConfig config, ExecutorService executor) {
        if (config.isLoadOnStartup()) {
            final Session systemSession = getSessionManager().getSystemSession();
            try {
                ServerScriptRunner module = new ServerScriptRunner(this, systemSession, config, ScriptUtils.getScope(), this, this, this);
                if (module != null) {
                    BackgroundTask task = new BackgroundTask(systemSession, module);
                    if (executor != null) {
                        executor.execute(task);
                    } else {
                        task.run();
                    }
                    return true;
                } else {
                    Logger.getLogger(PlatypusServerCore.class.getName()).warning(String.format("Background task \"%s\" is illegal (may bad class name). Skipping it.", config.getModuleId()));
                }
            } catch (Exception ex) {
                Logger.getLogger(PlatypusServerCore.class.getName()).severe(String.format("Background task \"%s\" caused an error: %s. Skipping it.", config.getModuleId(), ex.getMessage()));
            }
        }
        return false;
    }

    @Override
    public String preparationContext() throws Exception {
        Session session = sessionManager.getCurrentSession();
        if (session != null && session.getPrincipal() != null && session.getPrincipal() instanceof DbPlatypusPrincipal) {
            return ((DbPlatypusPrincipal) session.getPrincipal()).getContext();
        }
        return null;
    }

    @Override
    public String unpreparationContext() throws Exception {
        return databasesClient.getDbMetadataCache(null).getConnectionSchema();
    }

    @Override
    public PlatypusPrincipal getPrincipal() {
        return sessionManager.getCurrentSession().getPrincipal();
    }

    @Override
    public CompiledScriptDocuments getDocuments() {
        return scriptDocuments;
    }

    @Override
    public void defineJsClass(final String aClassName, ApplicationElement aAppElement) {
        switch (aAppElement.getType()) {
            case ClientConstants.ET_COMPONENT:
                ScriptRunnerPrototype.init(ScriptUtils.getScope(), true, new ScriptRunnerPrototype() {
                    @Override
                    public String getClassName() {
                        return aClassName;
                    }

                    @Override
                    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                        if (f.methodId() == Id_constructor && thisObj == null) {
                            return super.execIdCall(f, cx, scope, thisObj, new Object[]{aClassName});
                        } else {
                            return super.execIdCall(f, cx, scope, thisObj, args);
                        }
                    }
                });
                break;
            case ClientConstants.ET_REPORT:
                ReportRunnerPrototype.init(ScriptUtils.getScope(), true, new ReportRunnerPrototype() {
                    @Override
                    public String getClassName() {
                        return aClassName;
                    }

                    @Override
                    public Object execIdCall(IdFunctionObject f, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                        if (f.methodId() == Id_constructor && thisObj == null) {
                            return super.execIdCall(f, cx, scope, thisObj, new Object[]{aClassName});
                        } else {
                            return super.execIdCall(f, cx, scope, thisObj, args);
                        }
                    }
                });
                break;
        }
    }

    public AppElementsFilter getBrowsersFilter() {
        return browsersFilter;
    }

    @Override
    public ScriptResolver getResolver() {
        return scriptResolver;
    }

    protected class BackgroundTask implements Runnable {

        public static final String STARTING_BACKGROUND_TASK_MSG = "Starting background task \"%s\" with class name \"%s\"";
        public static final String STARTED_BACKGROUND_TASK_MSG = "Background task \"%s\" with class name \"%s\" started successfully";
        private final Session session;
        private ServerScriptRunner module;

        public BackgroundTask(Session aSession, ServerScriptRunner aModule) {
            super();
            module = aModule;
            session = aSession;
        }

        @Override
        public void run() {
            try {
                Logger.getLogger(BackgroundTask.class.getName()).info(String.format(STARTING_BACKGROUND_TASK_MSG, module.getModuleConfig().getModuleId(), module.getClass().getName()));
                module.execute();
                session.registerModule(module);
                Logger.getLogger(BackgroundTask.class.getName()).info(String.format(STARTED_BACKGROUND_TASK_MSG, module.getModuleConfig().getModuleId(), module.getClass().getName()));
            } catch (Exception ex) {
                Logger.getLogger(PlatypusServerCore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
