package com.eas.client.application;

import com.eas.client.*;
import com.eas.client.cache.ApplicationSourceIndexer;
import com.eas.client.cache.FormsDocuments;
import com.eas.client.cache.ModelsDocuments;
import com.eas.client.cache.ReportsConfigs;
import com.eas.client.cache.ScriptSecurityConfigs;
import com.eas.client.queries.LocalQueriesProxy;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.resourcepool.DatasourcesArgsConsumer;
import com.eas.client.scripts.ScriptedResource;
import com.eas.client.threetier.PlatypusClient;
import com.eas.client.threetier.http.PlatypusHttpConnection;
import com.eas.client.threetier.http.PlatypusHttpConstants;
import com.eas.client.threetier.platypus.PlatypusNativeConnection;
import com.eas.script.ScriptUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.logging.*;
import javax.swing.UIManager;

/**
 *
 * @author pk, mg refactoring
 */
public class PlatypusClientApplication {

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
    public static final String SHOW_FUNC_MISSING_MSG = "'show' function missing in %s instance";
    public static final String EXECUTE_FUNC_MISSING_MSG = "'execute' function missing in %s instance";
    public static final String BAD_APPLICATION_PATH_MSG = "Application path must follow applicationpath (ap) parameter";
    public static final String APPLICATION_PATH_NOT_EXISTS_MSG = "Application path does not exist.";
    public static final String APPLICATION_PATH_NOT_DIRECTORY_MSG = "Application path must point to a directory.";
    public static final String APPLICATION_ELEMENTS_LOCATION_MSG = "Application is located at: {0}";
    //

    protected static class Config {

        protected String startScriptPath;
        protected String userName;
        protected char[] password;
        protected URL url;
        protected String defDatasource;
        protected DatasourcesArgsConsumer datasourcesArgs;

        private static Config parseArgs(String[] args) throws Exception {
            Config commonArgs = new Config();
            DatasourcesArgsConsumer dsArgs = new DatasourcesArgsConsumer();
            int i = 0;
            while (i < args.length) {
                if ((CMD_SWITCHS_PREFIX + URL_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                    if (i < args.length - 1) {
                        commonArgs.url = new URL(null, args[i + 1], new URLStreamHandler() {

                            @Override
                            protected URLConnection openConnection(URL u) throws IOException {
                                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                            }
                        });
                        i += 2;
                    } else {
                        throw new IllegalArgumentException("Url syntax: -url <value>");
                    }
                } else if ((CMD_SWITCHS_PREFIX + DEF_DATASOURCE_CONF_PARAM).equalsIgnoreCase(args[i])) {
                    if (i < args.length - 1) {
                        commonArgs.defDatasource = args[i + 1];
                        i += 2;
                    } else {
                        throw new IllegalArgumentException(BAD_DEF_DATASOURCE_MSG);
                    }
                } else if ((CMD_SWITCHS_PREFIX + USER_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                    if (i < args.length - 1) {
                        commonArgs.userName = args[i + 1];
                        i += 2;
                    } else {
                        throw new IllegalArgumentException("User syntax: -user <value>");
                    }
                } else if ((CMD_SWITCHS_PREFIX + PASSWORD_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                    if (i < args.length - 1) {
                        commonArgs.password = args[i + 1].toCharArray();
                        i += 2;
                    } else {
                        throw new IllegalArgumentException("Password syntax: -password <value>");
                    }
                } else if ((CMD_SWITCHS_PREFIX + APPELEMENT_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                    if (i < args.length - 1) {
                        commonArgs.startScriptPath = args[i + 1];
                        i += 2;
                    } else {
                        throw new IllegalArgumentException("syntax: -appElement <application element id>");
                    }
                } else {
                    int consumed = dsArgs.consume(args, i);
                    if (consumed > 0) {
                        i += consumed;
                    } else {
                        throw new IllegalArgumentException("unknown argument: " + args[i]);
                    }
                }
            }
            commonArgs.datasourcesArgs = dsArgs;
            return commonArgs;
        }
    }

    /**
     * @param args the command line arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        try {
            checkUserHome();
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            System.setProperty("java.awt.Window.locationByPlatform", "true");
            Config config = Config.parseArgs(args);
            config.datasourcesArgs.registerDatasources();
            ScriptUtils.init();
            if (config.url != null) {
                Application app;
                if (config.url.getProtocol().equalsIgnoreCase(PlatypusHttpConstants.PROTOCOL_HTTP)) {
                    app = new PlatypusClient(new PlatypusHttpConnection(config.url));
                } else if (config.url.getProtocol().equalsIgnoreCase(PlatypusHttpConstants.PROTOCOL_HTTPS)) {
                    app = new PlatypusClient(new PlatypusHttpConnection(config.url));
                } else if (config.url.getProtocol().equalsIgnoreCase("platypus")) {
                    app = new PlatypusClient(new PlatypusNativeConnection(config.url));
                } else if (config.url.getProtocol().equalsIgnoreCase("file")) {
                    File f = new File(config.url.toURI());
                    if (f.exists() && f.isDirectory()) {
                        ModelsDocuments models = new ModelsDocuments();
                        ApplicationSourceIndexer indexer = new ApplicationSourceIndexer(f.getPath());
                        ScriptedDatabasesClient twoTierCore = new ScriptedDatabasesClient(config.defDatasource, indexer, true);
                        QueriesProxy qp = new LocalQueriesProxy(twoTierCore, indexer);
                        ModulesProxy mp = new LocalModulesProxy(indexer, models);
                        twoTierCore.setQueries(qp);
                        app = new Application() {

                            protected ScriptSecurityConfigs securityConfigs = new ScriptSecurityConfigs();
                            protected FormsDocuments forms = new FormsDocuments();
                            protected ReportsConfigs reports = new ReportsConfigs();

                            @Override
                            public QueriesProxy getQueries() {
                                return qp;
                            }

                            @Override
                            public ModulesProxy getModules() {
                                return mp;
                            }

                            @Override
                            public ServerModulesProxy getServerModules() {
                                throw new UnsupportedOperationException("Not supported in two-tier architecture.");
                            }

                            @Override
                            public ModelsDocuments getModels() {
                                return models;
                            }

                            @Override
                            public FormsDocuments getForms() {
                                return forms;
                            }

                            @Override
                            public ReportsConfigs getReports() {
                                return reports;
                            }

                            @Override
                            public ScriptSecurityConfigs getSecurityConfigs() {
                                return securityConfigs;
                            }

                        };
                    } else {
                        throw new IllegalArgumentException("applicationUrl: " + config.url + " doesn't point to existent directory or JNDI resource.");
                    }
                } else {
                    throw new Exception("Unknown protocol in url: " + config.url);
                }
                ScriptedResource.init(app);
                ScriptedResource.require(new String[]{""}, null, null);
            } else {
                throw new IllegalArgumentException("Application url is missing. url is a required parameter.");
            }
        } catch (Throwable t) {
            Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.SEVERE, null, t);
            System.exit(0xff);
        }
    }

    protected PlatypusClientApplication() {
        super();
    }

    /*
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
     try {
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
     } else {
     throw new FailedLoginException("Missing user name and/or password");
     }
     } catch (FailedLoginException ex) {
     if (anonymousEnabled) {
     principal = new AnonymousPlatypusPrincipal("anonymous-" + IDGenerator.genID());
     } else {
     throw ex;
     }
     }
     dbClient.setPrincipalHost(this);
     dbClient.setContextHost(this);
     client = dbClient;
     return true;
     }
     return false;
     }
     @Override
     public PlatypusPrincipal getPrincipal() {
     return null;
     }
     */
    private static void checkUserHome() {
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

    /*
     protected void runStartScript() throws Exception {
     if (startScriptPath != null) {
     ApplicationElement appElement = client.getAppCache().get(startScriptPath);
     if (appElement != null) {
     PlatypusScriptedResource.executeScriptResource(appElement.getId());
     if (appElement.getType() == ClientConstants.ET_FORM
     || appElement.getType() == ClientConstants.ET_REPORT
     || appElement.getType() == ClientConstants.ET_COMPONENT) {
     final JSObject instance = ScriptUtils.createModule(appElement.getId());
     if (appElement.getType() == ClientConstants.ET_FORM) {
     EventQueue.invokeLater(() -> {
     Object oShow = instance.getMember("show");
     if (oShow instanceof JSObject && ((JSObject) oShow).isFunction()) {
     ((JSObject) oShow).call(instance, new Object[]{});
     } else {
     Logger.getLogger(PlatypusClientApplication.class.getName()).severe(String.format(SHOW_FUNC_MISSING_MSG, appElement.getId()));
     System.exit(0);
     }
     });
     } else {
     Object oShow = instance.getMember("execute");
     if (oShow instanceof JSObject && ((JSObject) oShow).isFunction()) {
     ((JSObject) oShow).call(instance, new Object[]{});
     } else {
     Logger.getLogger(PlatypusClientApplication.class.getName()).severe(String.format(EXECUTE_FUNC_MISSING_MSG, appElement.getId()));
     System.exit(0);
     }
     }
     }
     } else {
     Logger.getLogger(PlatypusClientApplication.class.getName()).severe(String.format(MISSING_SUCH_APP_ELEMENT_MSG, startScriptPath));
     System.exit(0);
     }
     } else {
     Logger.getLogger(PlatypusClientApplication.class.getName()).severe(APP_ELEMENT_MISSING_MSG);
     System.exit(0);
     }
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
     */
}
