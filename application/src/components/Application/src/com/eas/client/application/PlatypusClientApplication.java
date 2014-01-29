/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.eas.client.*;
import com.eas.client.forms.FormRunner;
import com.eas.client.forms.FormRunnerPrototype;
import com.eas.client.login.*;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.queries.ContextHost;
import com.eas.client.reports.ReportRunner;
import com.eas.client.reports.ReportRunnerPrototype;
import com.eas.client.reports.ServerReportProxyPrototype;
import com.eas.client.resourcepool.DatasourcesArgsConsumer;
import com.eas.client.scripts.*;
import com.eas.client.threetier.PlatypusClient;
import com.eas.debugger.jmx.server.Breakpoints;
import com.eas.debugger.jmx.server.Debugger;
import com.eas.debugger.jmx.server.DebuggerMBean;
import com.eas.script.ScriptUtils;
import java.awt.EventQueue;
import java.beans.ExceptionListener;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Locale;
import java.util.logging.*;
import java.util.prefs.Preferences;
import javax.management.ObjectName;
import javax.security.auth.login.FailedLoginException;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author pk, mg refactoring
 */
public class PlatypusClientApplication implements ExceptionListener, PrincipalHost, ContextHost, CompiledScriptDocumentsHost {

    public static final String CMD_SWITCHS_PREFIX = "-";
    // command line switches
    public static final String MODULES_SCRIPT_NAME = "Modules";
    public static final String APPELEMENT_CMD_SWITCH = "appElement";
    // auto login switchs
    public static final String URL_CMD_SWITCH = "url";
    public static final String DEF_DATASOURCE_CONF_PARAM = "default-datasource";
    public static final String USER_CMD_SWITCH = "user";
    public static final String PASSWORD_CMD_SWITCH = "password";
    // local disk paths
    public static final String LOGS_PATH = "logs";
    // error messages
    public static final String BAD_DEF_DATASOURCE_MSG = "default-datasource value not specified";

    public static final String USER_HOME_ABSENTFILE_MSG = ClientConstants.USER_HOME_PROP_NAME + " property points to non-existent location";
    public static final String USER_HOME_MISSING_MSG = ClientConstants.USER_HOME_PROP_NAME + " property missing. Please specify it with -D" + ClientConstants.USER_HOME_PROP_NAME + "=... command line switch";
    public static final String USER_HOME_NOT_A_DIRECTORY_MSG = ClientConstants.USER_HOME_PROP_NAME + " property points to non-directory";
    public static final String BAD_DB_CREDENTIALS_MSG = "Bad database credentials.  May be bad db connection settings (url, dbuser, dbpassword).";
    public static final String BAD_APP_CREDENTIALS_MSG = "Bad application credentials.";
    public static final String APP_ELEMENT_MISSING_MSG = "Application element name missing. Nothing to do, so exit.";
    public static final String CLIENT_REQUIRED_AFTER_LOGIN_MSG = "After successfull login there must be a client.";
    public static final String MISSING_SUCH_APP_ELEMENT_MSG = "Application element with name specified (%s) is absent. Nothing to do, so exit.";
    public static final String NON_RUNNABLE_APP_ELEMENT_MSG = "Application element specified (%s) is of non-runnable type. Nothing to do, so exit.";
    public static final String STOP_BEFORE_RUN_CMD_SWITCH = "stopBeforeRun";
    public static final String BAD_APPLICATION_PATH_MSG = "Application path must follow applicationpath (ap) parameter";
    public static final String APPLICATION_PATH_NOT_EXISTS_MSG = "Application path does not exist.";
    public static final String APPLICATION_PATH_NOT_DIRECTORY_MSG = "Application path must point to a directory.";
    //
    protected static PlatypusClientApplication app;
    public static final String APPLICATION_ELEMENTS_LOCATION_MSG = "Application elements are located at: {0}";
    protected JFrame mainWindow;
    protected Client client;
    protected PlatypusPrincipal principal;
    protected AppCache appCache;
    protected CompiledScriptDocuments scriptDocuments;
    protected ScriptsCache scriptsCache;
    protected String appElementId;
    protected boolean needInitialBreak;
    //protected Level logLevel;// null is the default, and so, original J2SE configuration is aplied
    // auto login
    protected String url;
    private String defDatasource;
    protected String user;
    protected char[] password;

    /**
     * @param args the command line arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        try {
            app = new PlatypusClientApplication(args);
            app.run();
        } catch (Throwable t) {
            Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.SEVERE, null, t);
            System.exit(255);
        }
    }

    public static PlatypusClientApplication getInstance() {
        assert app != null;
        return app;
    }

    public JFrame getMainWindow() {
        return mainWindow;
    }

    protected PlatypusClientApplication(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, Exception {
        super();
        // turn off bold fonts
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        parseArgs(args);
        System.setProperty("java.awt.Window.locationByPlatform", "true");
    }

    public void exit(int exitCode) {
        try {
            if (client instanceof PlatypusClient) {
                ((PlatypusClient) client).logout();
            }
            client.shutdown();
        } catch (Exception ex) {
            Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            System.exit(exitCode);
        }
    }

    protected boolean login() throws Exception {
        if (url != null) {
            if (user != null && password != null) {
                return consoleLogin();
            } else {
                return guiLogin();
            }
        } else {
            return guiLogin();
            //throw new Exception("Platypus application needs at least a valid application directory path in url parameter or url of service (database, platypus server or j2ee server application) with an application.");
        }
    }

    private boolean consoleLogin() throws Exception {
        try {
            if (user == null || user.isEmpty() || password == null) {
                throw new Exception(BAD_APP_CREDENTIALS_MSG + " May be bad connection settings (user, password).");
            }
            Client lclient = ClientFactory.getInstance(url, defDatasource);
            try {
                return appLogin(lclient, user, password);
            } catch (Exception ex) {
                lclient.shutdown();
                throw ex;
            }
        } finally {
            user = null;
            password = null;
        }
    }

    private boolean guiLogin() throws Exception {
        LoginFrame frame = new LoginFrame(url, user, password, new LoginCallback() {
            @Override
            public boolean tryToLogin(String aUrl, String aAppUserName, char[] aAppPassword) throws Exception {
                Client lclient = ClientFactory.getInstance(aUrl, defDatasource);
                try {
                    url = aUrl;
                    return appLogin(lclient, aAppUserName, aAppPassword);
                } catch (Exception ex) {
                    lclient.shutdown();
                    throw ex;
                }
            }
        });
        Preferences settingsNode = Preferences.userRoot().node(ClientFactory.SETTINGS_NODE);
        frame.addExceptionListener(this);
        frame.selectDefaultSettings();
        frame.pack();
        frame.setVisible(true);
        int retValue = frame.getReturnStatus();
        frame.dispose();
        settingsNode.putInt(ClientFactory.DEFAULT_CONNECTION_INDEX_SETTING, frame.getSelectedConnectionIndex());
        return retValue == LoginFrame.RET_OK;
    }

    private boolean appLogin(Client aClient, String aUserName, char[] aPassword) throws Exception {
        if (aUserName != null && !aUserName.isEmpty() && aPassword != null) {
            if (aClient instanceof AppClient) {
                AppClient appClient = (AppClient) aClient;
                String sessionId = appClient.login(aUserName, aPassword);
                if (sessionId != null && !sessionId.isEmpty()) {
                    principal = appClient.getPrincipal();
                    client = appClient;
                    return true;
                } else {
                    assert false;
                }
            } else if (aClient instanceof DatabasesClient) {
                DatabasesClient dbClient = (DatabasesClient) aClient;
                String passwordMd5 = MD5Generator.generate(String.valueOf(aPassword));
                principal = DatabasesClient.credentialsToPrincipalWithBasicAuthentication(dbClient, aUserName, passwordMd5);
                if (principal == null) {
                    throw new FailedLoginException("Login incorrect");
                }
                dbClient.setPrincipalHost(this);
                dbClient.setContextHost(this);
                client = dbClient;
                return true;
            }
        }
        return false;
    }

    public Client getClient() {
        return client;
    }

    @Override
    public PlatypusPrincipal getPrincipal() {
        return principal;
    }

    private void checkUserHome() {
        String home = System.getProperty(ClientConstants.USER_HOME_PROP_NAME);
        if (home == null || home.isEmpty()) {
            throw new IllegalArgumentException(USER_HOME_MISSING_MSG);
        }
        if (!(new File(home)).exists()) {
            throw new IllegalArgumentException(USER_HOME_ABSENTFILE_MSG);
        }
        if (!(new File(home)).isDirectory()) {
            throw new IllegalArgumentException(USER_HOME_NOT_A_DIRECTORY_MSG);
        }
    }

    private void parseArgs(String[] args) throws Exception {
        DatasourcesArgsConsumer dsArgs = new DatasourcesArgsConsumer();
        int i = 0;
        while (i < args.length) {
            if ((CMD_SWITCHS_PREFIX + URL_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    url = args[i + 1];
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Url syntax: -url <value>");
                }
            } else if ((CMD_SWITCHS_PREFIX + DEF_DATASOURCE_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    defDatasource = args[i + 1];
                    i += 2;
                } else {
                    throw new IllegalArgumentException(BAD_DEF_DATASOURCE_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + USER_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    user = args[i + 1];
                    i += 2;
                } else {
                    throw new IllegalArgumentException("User syntax: -user <value>");
                }
            } else if ((CMD_SWITCHS_PREFIX + PASSWORD_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    password = args[i + 1].toCharArray();
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Password syntax: -password <value>");
                }
            } else if ((CMD_SWITCHS_PREFIX + APPELEMENT_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    appElementId = args[i + 1];
                    i += 2;
                } else {
                    throw new IllegalArgumentException("syntax: -appElement <application element id>");
                }
            } else if ((CMD_SWITCHS_PREFIX + STOP_BEFORE_RUN_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                needInitialBreak = true;
                i++;
            } else {
                int consumed = dsArgs.consume(args, i);
                if (consumed > 0) {
                    i += consumed;
                } else {
                    throw new IllegalArgumentException("unknown argument: " + args[i]);
                }
            }
        }
        dsArgs.registerDatasources();
    }

    protected void run() throws Exception {
        checkUserHome();
        if (System.getProperty(ScriptRunner.DEBUG_PROPERTY) != null) {
            Debugger debugger = Debugger.initialize(needInitialBreak);
            registerMBean(DebuggerMBean.DEBUGGER_MBEAN_NAME, debugger);
            registerMBean(Breakpoints.BREAKPOINTS_MBEAN_NAME, Breakpoints.getInstance());
        }
        // Start working
        if (login()) {
            assert client != null : CLIENT_REQUIRED_AFTER_LOGIN_MSG;
            appCache = client.getAppCache();
            Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.INFO, APPLICATION_ELEMENTS_LOCATION_MSG, appCache.getApplicationPath());
            scriptsCache = new ScriptsCache(this);
            scriptDocuments = new ClientCompiledScriptDocuments(client);
            if (appElementId == null) {
                appElementId = client.getStartAppElement();
            }
            ScriptRunnerPrototype.init(ScriptUtils.getScope(), true);
            ServerScriptProxyPrototype.init(ScriptUtils.getScope(), true);
            ServerReportProxyPrototype.init(ScriptUtils.getScope(), true);
            ReportRunnerPrototype.init(ScriptUtils.getScope(), true);
            FormRunnerPrototype.init(ScriptUtils.getScope(), true);
            PlatypusScriptedResource.init(client, getInstance(), getInstance());
            ScriptUtils.getScope().defineProperty(MODULES_SCRIPT_NAME, scriptsCache, ScriptableObject.READONLY);

            runFirstAction();
        }
    }

    protected void registerMBean(String aName, Object aBean) throws Exception {
        // Get the platform MBeanServer
        // Uniquely identify the MBeans and register them with the platform MBeanServer
        ManagementFactory.getPlatformMBeanServer().registerMBean(aBean, new ObjectName(aName));
    }

    protected void runFirstAction() throws Exception {
        if (appElementId != null) {
            ApplicationElement appElement = client.getAppCache().get(appElementId);
            if (appElement != null) {
                if (appElement.getType() == ClientConstants.ET_FORM) {
                    final FormRunner form = new FormRunner(appElementId, client, FormRunner.initializePlatypusStandardLibScope(), this, this, new Object[]{});
                    EventQueue.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                form.displayAsFrame();
                            } catch (Exception ex) {
                                Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    });
                    // When all windows are disposed, java VM exit automatically.
                } else if (appElement.getType() == ClientConstants.ET_REPORT) {
                    ReportRunner report = new ReportRunner(appElementId, client, ScriptRunner.initializePlatypusStandardLibScope(), this, this, new Object[]{});
                    report.show();
                    exit(0);
                } else if (appElement.getType() == ClientConstants.ET_COMPONENT) {
                    ScriptRunner script = new ScriptRunner(appElementId, client, ScriptRunner.initializePlatypusStandardLibScope(), this, this, new Object[]{});
                    script.execute();
                    exit(0);
                } else if (appElement.getType() == ClientConstants.ET_RESOURCE) {
                    ScriptRunner.executeResource(appElement.getId());
                } else {
                    Logger.getLogger(PlatypusClientApplication.class.getName()).severe(String.format(NON_RUNNABLE_APP_ELEMENT_MSG, appElementId));
                    // no actions, so just exit.
                    exit(0);
                }
            } else {
                Logger.getLogger(PlatypusClientApplication.class.getName()).severe(String.format(MISSING_SUCH_APP_ELEMENT_MSG, appElementId));
                // no actions, so just exit.
                exit(0);
            }
        } else {
            Logger.getLogger(PlatypusClientApplication.class.getName()).severe(APP_ELEMENT_MISSING_MSG);
            // no actions, so just exit.
            exit(0);
        }
    }

    @Override
    public void exceptionThrown(Exception e) {
        Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.SEVERE, null, e);
    }

    @Override
    public String preparationContext() throws Exception {
        if (principal instanceof DbPlatypusPrincipal) {
            assert client instanceof DbClient : "DbPlatypusPrincipal should only be used with two-tier client of DbClient class.";
            return ((DbPlatypusPrincipal) principal).getContext();
        }
        return null;
    }

    @Override
    public String unpreparationContext() throws Exception {
        if (client instanceof DbClient) {
            DbClient dbClient = (DbClient) client;
            return dbClient.getDbMetadataCache(null).getConnectionSchema();
        }
        return null;
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
                        protected Scriptable createObject(Context cntxt, Scriptable scope, Object[] args) {
                            try {
                                ScriptRunner runner = new ScriptRunner(client, scope, PlatypusClientApplication.this, PlatypusClientApplication.this);
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
                case ClientConstants.ET_FORM: {
                    Function f = new ScriptRunner.PlatypusModuleConstructorWrapper(aClassName, sDoc.getFunction()) {
                        @Override
                        protected Scriptable createObject(Context cntxt, Scriptable scope, Object[] args) {
                            try {
                                return new FormRunner(aClassName, client, scope, PlatypusClientApplication.this, PlatypusClientApplication.this, args);
                            } catch (Exception ex) {
                                throw new IllegalStateException(ex);
                            }
                        }
                    };
                    ScriptUtils.extend(f, (Function) ScriptUtils.getScope().get("Form", ScriptUtils.getScope()));
                    ScriptUtils.getScope().defineProperty(aClassName, f, ScriptableObject.READONLY);
                }
                break;
                case ClientConstants.ET_REPORT: {
                    Function f = new ScriptRunner.PlatypusModuleConstructorWrapper(aClassName, sDoc.getFunction()) {
                        @Override
                        protected Scriptable createObject(Context cntxt, Scriptable scope, Object[] args) {
                            try {
                                return new ReportRunner(aClassName, client, scope, PlatypusClientApplication.this, PlatypusClientApplication.this, args);
                            } catch (Exception ex) {
                                throw new IllegalStateException(ex);
                            }
                        }
                    };
                    ScriptUtils.extend(f, (Function) ScriptUtils.getScope().get("Report", ScriptUtils.getScope()));
                    ScriptUtils.getScope().defineProperty(aClassName, f, ScriptableObject.READONLY);
                }
                break;
            }
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
