/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.ClientConstants;
import com.eas.client.DatabasesClient;
import com.eas.client.ScriptedDatabasesClient;
import com.eas.client.login.DbPlatypusPrincipal;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.login.PrincipalHost;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.queries.ContextHost;
import com.eas.client.scripts.CompiledScriptDocuments;
import com.eas.client.scripts.CompiledScriptDocumentsHost;
import com.eas.client.scripts.ScriptDocument;
import com.eas.client.scripts.ScriptRunner;
import com.eas.client.scripts.ServerScriptProxyPrototype;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.debugger.jmx.server.Breakpoints;
import com.eas.debugger.jmx.server.Debugger;
import com.eas.debugger.jmx.server.DebuggerMBean;
import com.eas.debugger.jmx.server.Settings;
import com.eas.script.JsDoc;
import com.eas.script.ScriptUtils;
import com.eas.server.filter.AppElementsFilter;
import java.lang.management.ManagementFactory;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author mg
 */
public class PlatypusServerCore implements ContextHost, PrincipalHost, CompiledScriptDocumentsHost {

    static {
        ServerScriptRunnerPrototype.init(ScriptUtils.getScope(), true);
        ServerReportRunnerPrototype.init(ScriptUtils.getScope(), true);
        ServerScriptProxyPrototype.init(ScriptUtils.getScope(), true);
    }
    protected static PlatypusServerCore instance;

    public static PlatypusServerCore getInstance(DbConnectionSettings aDbSettings, Set<String> aTasks, String aStartAppElementId) throws Exception {
        if (instance == null) {
            final Set<String> tasks = new HashSet<>();
            if (aTasks != null) {
                tasks.addAll(aTasks);
            }
            ScriptedDatabasesClient serverCoreDbClient = new ScriptedDatabasesClient(aDbSettings, true, new ServerTasksScanner(tasks));
            instance = new PlatypusServerCore(serverCoreDbClient, tasks, aStartAppElementId);
            serverCoreDbClient.setContextHost(instance);
            serverCoreDbClient.setPrincipalHost(instance);
            ScriptRunner.PlatypusScriptedResource.init(serverCoreDbClient, instance, instance);
            ScriptUtils.getScope().defineProperty(ServerScriptRunner.MODULES_SCRIPT_NAME, instance.getScriptsCache(), ScriptableObject.READONLY);

            if (System.getProperty(ScriptRunner.DEBUG_PROPERTY) != null) {
                Debugger debugger = Debugger.initialize(false);
                unRegisterMBean(DebuggerMBean.DEBUGGER_MBEAN_NAME);
                registerMBean(DebuggerMBean.DEBUGGER_MBEAN_NAME, debugger);
                unRegisterMBean(Breakpoints.BREAKPOINTS_MBEAN_NAME);
                registerMBean(Breakpoints.BREAKPOINTS_MBEAN_NAME, Breakpoints.getInstance());
                unRegisterMBean(Settings.SETTINGS_MBEAN_NAME);
                registerMBean(Settings.SETTINGS_MBEAN_NAME, new Settings(serverCoreDbClient));
            }
            instance.startServerTasks();
        }
        return instance;
    }

    public static PlatypusServerCore getInstance() throws Exception {
        return instance;
    }

    public static void registerMBean(String aName, Object aBean) throws Exception {
        // Get the platform MBeanServer
        // Uniquely identify the MBeans and register them with the platform MBeanServer
        ManagementFactory.getPlatformMBeanServer().registerMBean(aBean, new ObjectName(aName));
    }

    public static void unRegisterMBean(String aName) throws MBeanRegistrationException, MalformedObjectNameException {
        try {
            ManagementFactory.getPlatformMBeanServer().unregisterMBean(new ObjectName(aName));
        } catch (InstanceNotFoundException ex) {
            //no-op
        }
    }
    protected String defaultAppElement;
    protected SessionManager sessionManager;
    protected ScriptedDatabasesClient databasesClient;
    protected ServerScriptsCache scriptsCache;
    protected ServerCompiledScriptDocuments scriptDocuments;
    protected AppElementsFilter browsersFilter;
    protected final Set<String> tasks;
    protected final Set<String> extraAuthorizers = new HashSet<>();

    public PlatypusServerCore(ScriptedDatabasesClient aDatabasesClient, Set<String> aTasks, String aDefaultAppElement) throws Exception {
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
     * should think twice befoce calling it i your code.
     *
     * @param aModuleName
     * @param aMethodName
     * @param aArgs
     * @return
     * @throws Exception
     */
    public Object executeServerModuleMethod(String aModuleName, String aMethodName, Object[] aArgs) throws Exception {
        ServerScriptRunner module = getSessionManager().getSystemSession().getModule(aModuleName);
        if (module == null) {
            module = new ServerScriptRunner(this, getSessionManager().getSystemSession(), aModuleName, ScriptRunner.initializePlatypusStandardLibScope(), this, this, new Object[]{});
        }
        module.execute();
        Session oldSession = getSessionManager().getCurrentSession();
        try {
            getSessionManager().setCurrentSession(getSessionManager().getSystemSession());
            return module.executeMethod(aMethodName, aArgs);
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
     * @param aModuleId Module identifier, specifying a module for the task
     * @return Success status
     */
    public boolean startServerTask(String aModuleId) throws Exception {
        Logger.getLogger(PlatypusServerCore.class.getName()).info(String.format(STARTING_RESIDENT_TASK_MSG, aModuleId));
        ScriptDocument sDoc = scriptDocuments.compileScriptDocument(aModuleId);
        if (sDoc != null) {
            boolean stateless = false;
            for (JsDoc.Tag tag : sDoc.getModuleAnnotations()) {
                switch (tag.getName()) {
                    case JsDoc.Tag.STATELESS_TAG:
                        stateless = true;
                        break;
                    case JsDoc.Tag.AUTHORIZER_TAG:
                        extraAuthorizers.add(aModuleId);
                        break;
                    case JsDoc.Tag.VALIDATOR_TAG:
                        stateless = true;
                        databasesClient.addValidator(aModuleId, tag.getParams());
                        break;
                }
            }
            if (!stateless) {
                try {
                    ServerScriptRunner module = scriptsCache.get(aModuleId);
                    if (module != null) {
                        module.execute();
                        Logger.getLogger(PlatypusServerCore.class.getName()).info(String.format(STARTED_RESIDENT_TASK_MSG, aModuleId));
                        return true;
                    } else {
                        Logger.getLogger(PlatypusServerCore.class.getName()).warning(String.format("Resident task \"%s\" is illegal (may be bad class name). Skipping it.", aModuleId));
                        return false;
                    }
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusServerCore.class.getName()).severe(String.format("Resident task \"%s\" caused an error: %s. Skipping it.", aModuleId, ex.getMessage()));
                    return false;
                }
            } else {
                Logger.getLogger(PlatypusServerCore.class.getName()).warning(String.format("Module \"%s\" is stateless, skipping it. Hope it will be used as an authorizer, validator or as an acceptor.", aModuleId));
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
        if (sessionManager.getCurrentSession() != null) {
            return sessionManager.getCurrentSession().getPrincipal();
        } else {
            // Construct a dummy Principal for a debugger can discover inner Principal's structure
            return new PlatypusPrincipal("No current session found") {
                @Override
                public boolean hasRole(String string) throws Exception {
                    return false;
                }
            };
        }
    }

    @Override
    public CompiledScriptDocuments getDocuments() {
        return scriptDocuments;
    }

    @Override
    public void defineJsClass(final String aClassName, ApplicationElement aAppElement) {
        try {
            ScriptDocument sDoc = scriptDocuments.compileScriptDocument(aClassName);
            switch (aAppElement.getType()) {
                case ClientConstants.ET_COMPONENT: {
                    Function f = new ScriptRunner.PlatypusModuleConstructorWrapper(aClassName, sDoc.getFunction()) {
                        @Override
                        protected Scriptable createObject(Context cx, Scriptable scope, Object[] args) {
                            try {
                                ServerScriptRunner runner = new ServerScriptRunner(PlatypusServerCore.this, PlatypusServerCore.this.getSessionManager().getCurrentSession(), aClassName, scope, PlatypusServerCore.this, PlatypusServerCore.this, args);
                                runner.loadApplicationElement(aClassName, args);
                                return runner;
                            } catch (Exception ex) {
                                throw new IllegalStateException(ex);
                            }
                        }
                    };
                    ScriptUtils.extend(f, (Function) ScriptUtils.getScope().get("Module", ScriptUtils.getScope()));
                    ScriptUtils.getScope().defineProperty(aClassName, f, ScriptableObject.READONLY);
                }
                break;
                case ClientConstants.ET_REPORT:
                    Function f = new ScriptRunner.PlatypusModuleConstructorWrapper(aClassName, sDoc.getFunction()) {
                        @Override
                        protected Scriptable createObject(Context cntxt, Scriptable scope, Object[] args) {
                            try {
                                ServerReportRunner runner = new ServerReportRunner(PlatypusServerCore.this, PlatypusServerCore.this.getSessionManager().getCurrentSession(), aClassName, scope, PlatypusServerCore.this, PlatypusServerCore.this, args);
                                runner.loadApplicationElement(aClassName, args);
                                return runner;
                            } catch (Exception ex) {
                                throw new IllegalStateException(ex);
                            }
                        }
                    };
                    ScriptUtils.extend(f, (Function) ScriptUtils.getScope().get("Report", ScriptUtils.getScope()));
                    ScriptUtils.getScope().defineProperty(aClassName, f, ScriptableObject.READONLY);
                    break;
            }
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    public AppElementsFilter getBrowsersFilter() {
        return browsersFilter;
    }
}
