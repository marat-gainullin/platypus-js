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
import com.eas.client.scripts.ScriptDocument;
import com.eas.client.scripts.ScriptRunnerPrototype;
import com.eas.client.scripts.ServerScriptProxyPrototype;
import com.eas.script.JsDoc;
import com.eas.script.ScriptUtils;
import com.eas.server.filter.AppElementsFilter;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Scriptable;

/**
 *
 * @author mg
 */
public class PlatypusServerCore implements ContextHost, PrincipalHost, CompiledScriptDocumentsHost {

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
    protected AppElementsFilter browsersFilter;
    protected final Set<String> tasks;

    public PlatypusServerCore(DatabasesClient aDatabasesClient, Set<String> aTasks, String aDefaultAppElement) throws Exception {
        databasesClient = aDatabasesClient;
        sessionManager = new SessionManager(this);
        scriptsCache = new ServerScriptsCache(this);
        scriptDocuments = new ServerCompiledScriptDocuments(databasesClient);
        browsersFilter = new AppElementsFilter(this);
        defaultAppElement = aDefaultAppElement;
        tasks = aTasks;
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

    public Set<String> getTasks() {
        return tasks;
    }

    public int startBackgroundTasks() throws Exception {
        int startedTasks = 0;
        for (String moduleId : tasks) {
            if (startBackgroundTask(moduleId)) {
                startedTasks++;
            }
        }
        return startedTasks;
    }

    /**
     * Starts a background task, initializing it with supplied module annotations.
     *
     * @param aModuleId Module identifier, specifying a module for the task
     * @return Success status
     */
    public boolean startBackgroundTask(String aModuleId) throws Exception {
        ScriptDocument sDoc = scriptDocuments.compileScriptDocument(aModuleId);
        if (sDoc != null) {
            boolean stateless = false;
            for (JsDoc.Tag tag : sDoc.getModuleAnnotations()) {
                if (JsDoc.Tag.STATELESS_TAG.equals(tag.getName())) {
                    stateless = true;
                }
            }
            if (!stateless) {
                final Session systemSession = getSessionManager().getSystemSession();
                try {
                    ServerScriptRunner module = scriptsCache.get(aModuleId);
                    if (module != null) {
                        BackgroundTask task = new BackgroundTask(systemSession, module);
                        task.run();
                        return true;
                    } else {
                        Logger.getLogger(PlatypusServerCore.class.getName()).warning(String.format("Background task \"%s\" is illegal (may be bad class name). Skipping it.", aModuleId));
                        return false;
                    }
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusServerCore.class.getName()).severe(String.format("Background task \"%s\" caused an error: %s. Skipping it.", aModuleId, ex.getMessage()));
                    return false;
                }
            } else {
                Logger.getLogger(PlatypusServerCore.class.getName()).warning(String.format("Module \"%s\" is stateless, skipping it. Hope it will be used as an acceptor for specific protocol.", aModuleId));
                return false;
            }
        } else {
            Logger.getLogger(PlatypusServerCore.class.getName()).warning(String.format("Background task \"%s\" is illegal (no module). Skipping it.", aModuleId));
            return false;
        }
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
                            return super.execIdCall(f, cx, scope, thisObj, new Object[]{aClassName, args});
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
                            return super.execIdCall(f, cx, scope, thisObj, new Object[]{aClassName, args});
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
                Logger.getLogger(BackgroundTask.class.getName()).info(String.format(STARTING_BACKGROUND_TASK_MSG, module.getApplicationElementId(), module.getClass().getName()));
                module.execute();
                session.registerModule(module);
                Logger.getLogger(BackgroundTask.class.getName()).info(String.format(STARTED_BACKGROUND_TASK_MSG, module.getApplicationElementId(), module.getClass().getName()));
            } catch (Exception ex) {
                Logger.getLogger(PlatypusServerCore.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
