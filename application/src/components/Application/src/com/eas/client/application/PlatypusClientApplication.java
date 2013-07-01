/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.application;

import com.eas.client.*;
import com.eas.client.cache.FilesAppCache;
import com.eas.client.forms.FormRunner;
import com.eas.client.forms.FormRunnerPrototype;
import com.eas.client.login.*;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.queries.ContextHost;
import com.eas.client.reports.ReportRunner;
import com.eas.client.reports.ReportRunnerPrototype;
import com.eas.client.reports.ServerReportProxyPrototype;
import com.eas.client.scripts.*;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.settings.EasSettings;
import com.eas.client.settings.SettingsConstants;
import com.eas.client.threetier.PlatypusClient;
import com.eas.debugger.jmx.server.Breakpoints;
import com.eas.debugger.jmx.server.Debugger;
import com.eas.debugger.jmx.server.DebuggerMBean;
import com.eas.debugger.jmx.server.Settings;
import com.eas.script.ScriptUtils;
import com.eas.util.StringUtils;
import com.eas.util.logging.PlatypusFormatter;
import java.awt.EventQueue;
import java.beans.ExceptionListener;
import java.io.File;
import java.io.FileNotFoundException;
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
import org.mozilla.javascript.IdFunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

/**
 *
 * @author pk, mg refactoring
 */
public class PlatypusClientApplication implements ExceptionListener, PrincipalHost, ContextHost, CompiledScriptDocumentsHost {

    public static final String CMD_SWITCHS_PREFIX = "-";
    // command line switches
    public static final String LAF_CMD_SWITCH = "laf";
    public static final String LOGLEVEL_CMD_SWITCH = "loglevel";
    public static final String LOG_CMD_SWITCH = "log";
    public static final String MODULES_SCRIPT_NAME = "Modules";
    public static final String RUSSIAN_LOCALE_CMD_SWITCH = "russian";
    public static final String APPELEMENT_CMD_SWITCH = "appElement";
    public static final String APP_PATH_CMD_SWITCH = "applicationpath";
    public static final String APP_PATH_CMD_SWITCH1 = "ap";
    // auto login switchs
    public static final String URL_CMD_SWITCH = "url";
    public static final String DBUSER_CMD_SWITCH = "dbuser";
    public static final String DBSCHEMA_CMD_SWITCH = "dbschema";
    public static final String DBPASSWORD_CMD_SWITCH = "dbpassword";
    public static final String USER_CMD_SWITCH = "user";
    public static final String PASSWORD_CMD_SWITCH = "password";
    // local disk paths
    public static final String LOGS_PATH = "logs";
    // error messages
    public static final String USER_HOME_ABSENTFILE_MSG = ClientConstants.USER_HOME_PROP_NAME + " property points to non-existent location";
    public static final String USER_HOME_MISSING_MSG = ClientConstants.USER_HOME_PROP_NAME + " property missing. Please specify it with -Dplatypus.home=... java comannd line switch";
    public static final String USER_HOME_NOT_A_DIRECTORY_MSG = ClientConstants.USER_HOME_PROP_NAME + " property points to non-directory";
    public static final String BAD_DB_CREDENTIALS_MSG = "Bad database credentials.  May be bad db connection settings (url, dbuser, dbpassword).";
    public static final String BAD_APP_CREDENTIALS_MSG = "Bad application credentials.";
    public static final String APP_ELEMENT_MISSING_MSG = "Application element identifier missing. Nothing to do, so exit.";
    public static final String CLIENT_REQUIRED_AFTER_LOGIN_MSG = "After successfull login there must be a client.";
    public static final String MISSING_SUCH_APP_ELEMENT_MSG = "Application element with identifier specified is absent. Nothing to do, so exit.";
    public static final String NON_RUNNABLE_APP_ELEMENT_MSG = "Application element specified is of non-runnable type. Nothing to do, so exit.";
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
    protected String appPath;
    protected String appElementId;
    protected boolean needInitialBreak;
    // auto login
    protected String url;
    protected String dbSchema;
    protected String dbUser;
    protected char[] dbPassword;
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

    public static String expandLogFileName(String logFileName) throws FileNotFoundException {
        if (logFileName != null) {
            String path = calcLogsDirectory();
            if (path != null) {
                return StringUtils.join(File.separator, path, logFileName);
            }
        }
        return null;
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
        checkLogsDirectory();
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
        if (url != null && user != null && password != null) {
            return consoleLogin();
        } else {
            return guiLogin();
        }
    }

    private boolean consoleLogin() throws Exception {
        try {
            if (user == null || user.isEmpty() || password == null) {
                throw new Exception(BAD_APP_CREDENTIALS_MSG + " May be bad connection settings (user, password).");
            }
            EasSettings settings = EasSettings.createInstance(url);
            if (settings instanceof DbConnectionSettings) {
                if (dbUser == null || dbUser.isEmpty() || dbPassword == null || dbPassword.length == 0 || dbSchema == null || dbSchema.isEmpty()) {
                    throw new Exception(BAD_DB_CREDENTIALS_MSG + " May be bad db connection settings (url, dbuser, dbpassword, dbschema).");
                }
                settings.getInfo().put(ClientConstants.DB_CONNECTION_USER_PROP_NAME, dbUser);
                settings.getInfo().put(ClientConstants.DB_CONNECTION_PASSWORD_PROP_NAME, new String(dbPassword));
                if (dbSchema != null && !dbSchema.isEmpty()) {
                    settings.getInfo().put(ClientConstants.DB_CONNECTION_SCHEMA_PROP_NAME, dbSchema);
                }
                if (appPath != null) {
                    ((DbConnectionSettings) settings).setApplicationPath(appPath);
                }
            }
            Client lclient = ClientFactory.getInstance(settings);
            try {
                return appLogin(lclient, user, password);
            } catch (Exception ex) {
                lclient.shutdown();
                throw ex;
            }
        } finally {
            dbUser = null;
            dbSchema = null;
            dbPassword = null;
            user = null;
            password = null;
        }
    }

    private boolean guiLogin() throws Exception {
        LoginFrame frame = new LoginFrame(url, dbUser, dbPassword, user, password, new LoginCallback() {
            @Override
            public boolean tryToLogin(EasSettings aSettings, String aDbUser, char[] aDbPassword, String aUserName, char[] aAppPassword) throws Exception {
                EasSettings lsettings = aSettings;
                if (aSettings instanceof DbConnectionSettings) {
                    if (aDbUser == null || aDbUser.isEmpty() || aDbPassword == null || aDbPassword.length == 0) {
                        throw new Exception(BAD_DB_CREDENTIALS_MSG);
                    }
                    DbConnectionSettings dbSettings = new DbConnectionSettings();
                    dbSettings.setName(((DbConnectionSettings) aSettings).getName());
                    dbSettings.setDrivers(((DbConnectionSettings) aSettings).getDrivers());
                    dbSettings.setUrl(((DbConnectionSettings) aSettings).getUrl());
                    dbSettings.getInfo().putAll(((DbConnectionSettings) aSettings).getInfo());
                    dbSettings.getInfo().put(ClientConstants.DB_CONNECTION_USER_PROP_NAME, aDbUser);
                    dbSettings.getInfo().put(ClientConstants.DB_CONNECTION_PASSWORD_PROP_NAME, new String(aDbPassword));
                    if (appPath != null) {
                        dbSettings.setApplicationPath(appPath);
                    }
                    lsettings = dbSettings;
                }
                Client lclient = ClientFactory.getInstance(lsettings);
                try {
                    return appLogin(lclient, aUserName, aAppPassword);
                } catch (Exception ex) {
                    lclient.shutdown();
                    throw ex;
                }
            }
        });
        Preferences settingsNode = Preferences.userRoot().node(ClientFactory.SETTINGS_NODE);
        frame.addExceptionListener(this);
        frame.selectDefaultSettings();
        frame.setDbPassword(settingsNode.get(ClientFactory.DEFAULT_CONNECTION_DB_PASSWORD, ""));
        frame.setUserPassword(settingsNode.get(ClientFactory.DEFAULT_CONNECTION_USER_PASSWORD, ""));
        frame.pack();
        frame.setVisible(true);
        int retValue = frame.getReturnStatus();
        frame.dispose();
        settingsNode.putInt(ClientFactory.DEFAULT_CONNECTION_INDEX_SETTING, frame.getSelectedConnectionIndex());

        if (retValue == LoginFrame.RET_OK) {
            if (frame.getDbPassword() != null) {
                settingsNode.put(ClientFactory.DEFAULT_CONNECTION_DB_PASSWORD, frame.getDbPassword());
            } else {
                settingsNode.remove(ClientFactory.DEFAULT_CONNECTION_DB_PASSWORD);
            }
            if (frame.getUserPassword() != null) {
                settingsNode.put(ClientFactory.DEFAULT_CONNECTION_USER_PASSWORD, frame.getUserPassword());
            } else {
                settingsNode.remove(ClientFactory.DEFAULT_CONNECTION_USER_PASSWORD);
            }
        }
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

    private static String calcLogsDirectory() {
        return StringUtils.join(File.separator, System.getProperty(ClientConstants.USER_HOME_PROP_NAME), ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME, LOGS_PATH);
    }

    private void checkLogsDirectory() {
        String path = calcLogsDirectory();
        if (path != null) {
            File logsDir = new File(path);
            if (!logsDir.exists()) {
                logsDir.mkdirs();
            }
        }
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
            printHelp(USER_HOME_MISSING_MSG);
            System.exit(1);
        }
        if (!(new File(home)).exists()) {
            printHelp(USER_HOME_ABSENTFILE_MSG);
            System.exit(1);
        }
        if (!(new File(home)).isDirectory()) {
            printHelp(USER_HOME_NOT_A_DIRECTORY_MSG);
            System.exit(1);
        }
    }

    private void parseArgs(String[] args) throws Exception {
        String logFileName = null;
        String logLevel = Preferences.userRoot().node(ClientFactory.SETTINGS_NODE).get("logLevel", "WARNING");
        int i = 0;
        while (i < args.length) {
            if ((CMD_SWITCHS_PREFIX + URL_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    url = args[i + 1];
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Url syntax: -url <value>");
                }
            } else if ((CMD_SWITCHS_PREFIX + DBUSER_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    dbUser = args[i + 1];
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Db user syntax: -dbuser <value>");
                }
            } else if ((CMD_SWITCHS_PREFIX + DBSCHEMA_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    dbSchema = args[i + 1];
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Db schema syntax: -dbschema <value>");
                }
            } else if ((CMD_SWITCHS_PREFIX + DBPASSWORD_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    dbPassword = args[i + 1].toCharArray();
                    i += 2;
                } else {
                    throw new IllegalArgumentException("Db password syntax: -dbpassword <value>");
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
            } else if ((CMD_SWITCHS_PREFIX + CMD_SWITCHS_PREFIX + RUSSIAN_LOCALE_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                Locale.setDefault(new Locale("ru", "RU"));
                i++;
            } else if ((CMD_SWITCHS_PREFIX + LAF_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    UIManager.setLookAndFeel(args[i + 1]);
                    i += 2;
                } else {
                    throw new IllegalArgumentException("syntax: -laf <LaF class name>");
                }
            } else if ((CMD_SWITCHS_PREFIX + LOGLEVEL_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    logLevel = args[i + 1];
                    i += 2;
                } else {
                    throw new IllegalArgumentException("syntax: -loglevel <log level>");
                }
            } else if ((CMD_SWITCHS_PREFIX + LOG_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    logFileName = args[i + 1];
                    i += 2;
                } else {
                    throw new IllegalArgumentException("syntax: -log <log file base name>");
                }
            } else if ((CMD_SWITCHS_PREFIX + APPELEMENT_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    try {
                        appElementId = args[i + 1];
                    } catch (NumberFormatException ex) {
                        throw new IllegalArgumentException("syntax: -appElement <application element id comprised of numbers>");
                    }
                    i += 2;
                } else {
                    throw new IllegalArgumentException("syntax: -appElement <application element id>");
                }
            } else if ((CMD_SWITCHS_PREFIX + APP_PATH_CMD_SWITCH).equalsIgnoreCase(args[i]) || (CMD_SWITCHS_PREFIX + APP_PATH_CMD_SWITCH1).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    File check = new File(args[i + 1]);
                    if (check.exists()) {
                        if (check.isDirectory()) {
                            appPath = args[i + 1];
                        } else {
                            printHelp(APPLICATION_PATH_NOT_DIRECTORY_MSG);
                        }
                    } else {
                        printHelp(APPLICATION_PATH_NOT_EXISTS_MSG);
                    }
                    i += 2;
                } else {
                    printHelp(BAD_APPLICATION_PATH_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + STOP_BEFORE_RUN_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                needInitialBreak = true;
                i++;
            } else {
                throw new IllegalArgumentException("unknown argument: " + args[i]);
            }
        }
        setupLoggers(Level.parse(logLevel), expandLogFileName(logFileName));
    }
    private static Logger[] loggers = {
        Logger.getLogger("com.eas"),
        Logger.getLogger("sun.reflect"),
        Logger.getLogger("com.bearsoft"),
        Logger.getLogger("org.mozilla.javascript"),
        Logger.getLogger(Client.APPLICATION_LOGGER_NAME)
    };

    public static Logger[] getLoggers() {
        return loggers;
    }

    public static void setLoggersLevel(Level aLevel) throws Exception {
        for (Logger logger : loggers) {
            logger.setLevel(aLevel);
        }
    }

    private static void setupLoggers(Level aLevel, String aLogFileName) throws Exception {
        for (Logger logger : loggers) {
            logger.setLevel(aLevel);
            if (aLogFileName != null && !aLogFileName.isEmpty()) {
                Handler fHandler = new FileHandler(aLogFileName, 1024 * 1024 * 2, 1, true);
                fHandler.setEncoding(SettingsConstants.COMMON_ENCODING);
                if (Client.APPLICATION_LOGGER_NAME.equals(logger.getName())) {
                    fHandler.setFormatter(new PlatypusFormatter());
                } else {
                    fHandler.setFormatter(new SimpleFormatter());
                }
                logger.addHandler(fHandler);
            }
            Handler consoleHandler = new ConsoleHandler();
            if (Client.APPLICATION_LOGGER_NAME.equals(logger.getName())) {
                consoleHandler.setFormatter(new PlatypusFormatter());
            }
            logger.addHandler(consoleHandler);
            logger.setUseParentHandlers(false);

            Handler[] handlers = logger.getHandlers();
            for (Handler handler : handlers) {
                handler.setLevel(aLevel);
            }
        }
    }

    protected void run() throws Exception {
        checkUserHome();
        checkLogsDirectory();
        if (System.getProperty(ScriptRunner.DEBUG_PROPERTY) != null) {
            Debugger debugger = Debugger.initialize(needInitialBreak);
            registerMBean(DebuggerMBean.DEBUGGER_MBEAN_NAME, debugger);
            registerMBean(Breakpoints.BREAKPOINTS_MBEAN_NAME, Breakpoints.getInstance());
        }
        // Start working
        if (login()) {
            assert client != null : CLIENT_REQUIRED_AFTER_LOGIN_MSG;
            // Apply debugging facility
            if (System.getProperty(ScriptRunner.DEBUG_PROPERTY) != null) {
                registerMBean(Settings.SETTINGS_MBEAN_NAME, new Settings(client));
            }
            appCache = client.getAppCache();
            Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.INFO, APPLICATION_ELEMENTS_LOCATION_MSG, appCache instanceof FilesAppCache ? ((FilesAppCache) appCache).getSrcPathName() : client.getSettings().getUrl());
            scriptsCache = new ScriptsCache(this);
            scriptDocuments = new ClientCompiledScriptDocuments(client);
            if (appElementId == null) {
                appElementId = client.getStartAppElement();
            }
            ScriptRunner.PlatypusScriptedResource.init(appCache);
            ScriptRunnerPrototype.init(ScriptUtils.getScope(), true);
            ServerScriptProxyPrototype.init(ScriptUtils.getScope(), true);
            ServerReportProxyPrototype.init(ScriptUtils.getScope(), true);
            ReportRunnerPrototype.init(ScriptUtils.getScope(), true);
            FormRunnerPrototype.init(ScriptUtils.getScope(), true);
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
                    final FormRunner form = new FormRunner(appElementId, client, ScriptUtils.getScope(), this, this);
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
                    ReportRunner report = new ReportRunner(appElementId, client, ScriptUtils.getScope(), this, this);
                    report.show();
                    exit(0);
                } else if (appElement.getType() == ClientConstants.ET_COMPONENT) {
                    ScriptRunner script = new ScriptRunner(appElementId, client, ScriptUtils.getScope(), this, this);
                    script.execute();
                    exit(0);
                } else if (appElement.getType() == ClientConstants.ET_RESOURCE) {
                    ScriptRunner.executeResource(appElement.getId());
                } else {
                    Logger.getLogger(PlatypusClientApplication.class.getName()).severe(NON_RUNNABLE_APP_ELEMENT_MSG);
                    // no actions, so just exit.
                    exit(0);
                }
            } else {
                Logger.getLogger(PlatypusClientApplication.class.getName()).severe(MISSING_SUCH_APP_ELEMENT_MSG);
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
    /*
     public Document getDocumentContent(String aDocId) {
     try {
     if (client != null && appCache != null) {
     ApplicationElement appElement = appCache.get(aDocId);
     if (appElement != null) {
     return appElement.getContent();
     }
     }
     } catch (Exception ex) {
     Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.SEVERE, null, ex);
     }
     return null;
     }

     public String getDocumentTitle(String aDocId) {
     try {
     if (client != null && appCache != null) {
     ApplicationElement appElement = appCache.get(aDocId);
     if (appElement != null) {
     return appElement.getName();
     }
     }
     } catch (Exception ex) {
     Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.SEVERE, null, ex);
     }
     return null;
     }

     public void setDocumentContent(String aDocId, String aContent) {
     if (client != null && client instanceof DbClient) {
     DbClient dbClient = (DbClient) client;
     try {
     try {
     String contentParamName = "content";
     String sizeParamName = "contentSize";
     String crcParamName = "contentCRC";
     String entityIdParamName = "fieldValue";
     Long size = null;
     Long crcValue = null;
     if (aContent != null) {
     CRC32 crc = new CRC32();
     byte[] bytes = aContent.getBytes(SettingsConstants.COMMON_ENCODING);
     crc.update(bytes);
     crcValue = crc.getValue();
     size = (long) aContent.length();
     }
     CompactClob clobContent = new CompactClob(aContent);
     SqlQuery sql = new SqlQuery(dbClient, String.format(SQLUtils.SQL_UPDATE3_COMMON_WHERE_BY_FIELD, ClientConstants.T_MTD_ENTITIES, ClientConstants.F_MDENT_CONTENT_TXT, ":" + contentParamName, ClientConstants.F_MDENT_CONTENT_TXT_SIZE, ":" + sizeParamName, ClientConstants.F_MDENT_CONTENT_TXT_CRC32, ":" + crcParamName, ClientConstants.T_MTD_ENTITIES, ClientConstants.F_MDENT_ID));
     sql.putParameter(contentParamName, DataTypeInfo.CLOB, clobContent);
     sql.putParameter(sizeParamName, DataTypeInfo.NUMERIC, size);
     sql.putParameter(crcParamName, DataTypeInfo.NUMERIC, crcValue);
     sql.putParameter(entityIdParamName, DataTypeInfo.NUMERIC, aDocId);
     dbClient.enqueueUpdate(sql.compile());
     dbClient.commit(null);
     dbClient.appEntityChanged(aDocId);
     //postChangesToApplicationServer(aDocId); // Refactoring PlatypusPrincipal TODO: check this method
     } catch (Exception ex) {
     dbClient.rollback(null);
     Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.SEVERE, null, ex);
     }
     } catch (Exception ex) {
     Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.SEVERE, null, ex);
     }
     }
     }
     */

    private void printHelp(String aMsgToPrint) {
        System.err.println(aMsgToPrint);
        System.err.println(String.format("Refer to %s sources for help.", PlatypusClientApplication.class.getName()));
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
            case ClientConstants.ET_FORM:
                FormRunnerPrototype.init(ScriptUtils.getScope(), true, new FormRunnerPrototype() {
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
}
