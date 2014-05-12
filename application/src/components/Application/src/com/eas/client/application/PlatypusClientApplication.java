package com.eas.client.application;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.*;
import com.eas.client.login.*;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.queries.ContextHost;
import com.eas.client.resourcepool.DatasourcesArgsConsumer;
import com.eas.client.scripts.*;
import com.eas.client.threetier.PlatypusClient;
import com.eas.script.ScriptUtils;
import java.awt.EventQueue;
import java.beans.ExceptionListener;
import java.io.File;
import java.util.logging.*;
import java.util.prefs.Preferences;
import javax.security.auth.login.FailedLoginException;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author pk, mg refactoring
 */
public class PlatypusClientApplication implements ExceptionListener, PrincipalHost, ContextHost {

    public static final String CMD_SWITCHS_PREFIX = "-";
    // command line switches
    public static final String APPELEMENT_CMD_SWITCH = "appelement";
    public static final String URL_CMD_SWITCH = "url";
    // container switches
    public static final String DEF_DATASOURCE_CONF_PARAM = "default-datasource";
    // login switchs
    public static final String USER_CMD_SWITCH = "user";
    public static final String PASSWORD_CMD_SWITCH = "password";
    // security switches
    public static final String ANONYMOUS_ON_CMD_SWITCH = "enable-anonymous";

    // error messages
    public static final String BAD_DEF_DATASOURCE_MSG = "default-datasource value not specified";
    public static final String USER_HOME_ABSENTFILE_MSG = ClientConstants.USER_HOME_PROP_NAME + " property points to non-existent location";
    public static final String USER_HOME_MISSING_MSG = ClientConstants.USER_HOME_PROP_NAME + " property missing. Please specify it with -D" + ClientConstants.USER_HOME_PROP_NAME + "=... command line switch";
    public static final String USER_HOME_NOT_A_DIRECTORY_MSG = ClientConstants.USER_HOME_PROP_NAME + " property points to non-directory";
    public static final String BAD_DB_CREDENTIALS_MSG = "Bad database credentials.  May be bad db connection settings (url, dbuser, dbpassword).";
    public static final String BAD_APP_CREDENTIALS_MSG = "Bad application credentials.";
    public static final String APP_ELEMENT_MISSING_MSG = "Script to be executed is missing. Nothing to do, so exit.";
    public static final String CLIENT_REQUIRED_AFTER_LOGIN_MSG = "After successfull login there must be a client.";
    public static final String MISSING_SUCH_APP_ELEMENT_MSG = "Application element with name specified (%s) is absent. Nothing to do, so exit.";
    public static final String NON_RUNNABLE_APP_ELEMENT_MSG = "Application element specified (%s) is of non-runnable type. Nothing to do, so exit.";
    public static final String STOP_BEFORE_RUN_CMD_SWITCH = "stopBeforeRun";
    public static final String BAD_APPLICATION_PATH_MSG = "Application path must follow applicationpath (ap) parameter";
    public static final String APPLICATION_PATH_NOT_EXISTS_MSG = "Application path does not exist.";
    public static final String APPLICATION_PATH_NOT_DIRECTORY_MSG = "Application path must point to a directory.";
    //
    protected static PlatypusClientApplication app;
    public static final String APPLICATION_ELEMENTS_LOCATION_MSG = "Application is located at: {0}";
    protected JFrame mainWindow;
    protected Client client;
    protected PlatypusPrincipal principal;
    protected AppCache appCache;
    protected String startScriptPath;
    protected boolean needInitialBreak;
    // auto login
    protected String url;
    private String defDatasource;
    protected String user;
    protected char[] password;
    protected boolean anonymousEnabled;

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
        if (url != null && !url.isEmpty()) {
            try {
                return consoleLogin();
            } catch (FailedLoginException ex) {
                Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.WARNING, ex.getMessage());
                return guiLogin();
            }
        } else {
            return guiLogin();
        }
    }

    private void checkUrl() throws Exception {
        if (url == null || url.isEmpty()) {
            throw new Exception(BAD_APP_CREDENTIALS_MSG + " Url parameter is required.");
        }
    }

    private boolean tryToAppLogin() throws Exception {
        Client lclient = ClientFactory.getInstance(url, defDatasource);
        try {
            return appLogin(lclient, user, password);
        } catch (Exception ex) {
            lclient.shutdown();
            throw ex;
        }
    }

    private boolean consoleLogin() throws Exception {
        checkUrl();
        return tryToAppLogin();
    }

    private boolean guiLogin() throws Exception {
        LoginFrame frame = new LoginFrame(url, user, password, new LoginCallback() {
            @Override
            public boolean tryToLogin(String aUrl, String aAppUserName, char[] aAppPassword) throws Exception {
                url = aUrl;
                user = aAppUserName;
                password = aAppPassword;
                return consoleLogin();
            }
        });
        Preferences settingsNode = Preferences.userRoot().node(ClientFactory.SETTINGS_NODE);
        frame.addExceptionListener(this);
        frame.applyDefaults();
        frame.pack();
        frame.setVisible(true);
        int retValue = frame.getReturnStatus();
        frame.dispose();
        settingsNode.putInt(ClientFactory.DEFAULT_CONNECTION_INDEX_SETTING, frame.getSelectedConnectionIndex());
        return retValue == LoginFrame.RET_OK;
    }

    private boolean appLogin(Client aClient, String aUserName, char[] aPassword) throws Exception {
        if (aClient instanceof AppClient) {
            AppClient appClient = (AppClient) aClient;
            String sessionId = appClient.login(aUserName, aPassword);
            if (sessionId != null && !sessionId.isEmpty()) {
                principal = appClient.getPrincipal();
                client = appClient;
                return true;
            } else {
                throw new FailedLoginException("A session is expected from server");
            }
        } else if (aClient instanceof DatabasesClient) {
            DatabasesClient dbClient = (DatabasesClient) aClient;
            if (aUserName != null && !aUserName.isEmpty() && aPassword != null) {
                String passwordMd5 = MD5Generator.generate(String.valueOf(aPassword));
                try {
                    principal = DatabasesClient.credentialsToPrincipalWithBasicAuthentication(dbClient, aUserName, passwordMd5);
                } catch (Exception ex) {
                    throw new FailedLoginException(ex.getMessage());
                }
                if (principal == null) {
                    throw new FailedLoginException("User name or password is incorrect");
                }
            } else if (anonymousEnabled) {
                principal = new AnonymousPlatypusPrincipal("anonymous-" + IDGenerator.genID());
            } else {
                throw new FailedLoginException("User name and password are required while anonymous access is disabled.");
            }
            dbClient.setPrincipalHost(this);
            dbClient.setContextHost(this);
            client = dbClient;
            return true;
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
            } else if ((CMD_SWITCHS_PREFIX + ANONYMOUS_ON_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length) {
                    anonymousEnabled = true;
                    i += 1;
                }
            } else if ((CMD_SWITCHS_PREFIX + APPELEMENT_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    startScriptPath = args[i + 1];
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
        ScriptUtils.init();
        // Start working
        if (login()) {
            assert client != null : CLIENT_REQUIRED_AFTER_LOGIN_MSG;
            appCache = client.getAppCache();
            Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.INFO, APPLICATION_ELEMENTS_LOCATION_MSG, appCache.getApplicationPath());
            PlatypusScriptedResource.init(client, getInstance());
            runStartScript();
        }
    }

    protected void runStartScript() throws Exception {
        if (startScriptPath != null) {
            ApplicationElement appElement = client.getAppCache().get(startScriptPath);
            if (appElement != null) {
                if (appElement.getType() == ClientConstants.ET_FORM) {
                    EventQueue.invokeLater(() -> {
                        try {
                            PlatypusScriptedResource.executeScriptResource(appElement.getId());
                        } catch (Exception ex) {
                            Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                    // When all windows are disposed, java VM exit automatically.
                } else if (appElement.getType() == ClientConstants.ET_REPORT
                        || appElement.getType() == ClientConstants.ET_COMPONENT
                        || appElement.getType() == ClientConstants.ET_RESOURCE) {
                    PlatypusScriptedResource.executeScriptResource(appElement.getId());
                } else {
                    Logger.getLogger(PlatypusClientApplication.class.getName()).severe(String.format(NON_RUNNABLE_APP_ELEMENT_MSG, startScriptPath));
                    // no actions, so just exit.
                    exit(0);
                }
            } else {
                Logger.getLogger(PlatypusClientApplication.class.getName()).severe(String.format(MISSING_SUCH_APP_ELEMENT_MSG, startScriptPath));
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
}
