package com.eas.client.application;

import com.eas.client.*;
import com.eas.client.cache.ApplicationSourceIndexer;
import com.eas.client.cache.FormsDocuments;
import com.eas.client.cache.ModelsDocuments;
import com.eas.client.cache.ReportsConfigs;
import com.eas.client.cache.ScriptsConfigs;
import com.eas.client.login.AnonymousPlatypusPrincipal;
import com.eas.client.login.ConnectionsSelector;
import com.eas.client.login.Credentials;
import com.eas.client.login.CredentialsSelector;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.queries.LocalQueriesProxy;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.resourcepool.DatasourcesArgsConsumer;
import com.eas.client.resourcepool.GeneralResourceProvider;
import com.eas.client.scripts.ScriptedResource;
import com.eas.client.settings.ConnectionSettings;
import com.eas.client.threetier.PlatypusClient;
import com.eas.client.threetier.http.PlatypusHttpConnection;
import com.eas.client.threetier.http.PlatypusHttpConstants;
import com.eas.client.threetier.platypus.PlatypusPlatypusConnection;
import com.eas.script.Scripts;
import com.eas.util.args.ThreadsArgsConsumer;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.concurrent.Callable;
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
    public static final String SOURCE_PATH_CONF_PARAM = "source-path";
    // login switchs
    public static final String USER_CMD_SWITCH = "user";
    public static final String PASSWORD_CMD_SWITCH = "password";
    public static final String MAX_LOGIN_ATTEMPTS_CMD_SWITCH = "max-login-attempts";

    // error messages
    public static final String BAD_DEF_DATASOURCE_MSG = "default-datasource value not specified";
    public static final String BAD_SOURCE_PATH_MSG = "source-path value not specified";
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
    public static final String APPLICATION_ELEMENTS_LOCATION_MSG = "Application is located at: {0}";
    //

    public static class Config {

        private String startScriptPath;
        private String userName;
        private char[] password;
        private int maximumAuthenticateAttempts = Integer.MAX_VALUE;
        private URL url;
        private String defDatasource;
        private String sourcePath;
        private final DatasourcesArgsConsumer datasourcesArgs = new DatasourcesArgsConsumer();
        private final ThreadsArgsConsumer threadsArgs = new ThreadsArgsConsumer();

        public String getStartScriptPath() {
            return startScriptPath;
        }

        public String getUserName() {
            return userName;
        }

        public URL getUrl() {
            return url;
        }

        public String getSourcePath() {
            return sourcePath;
        }

        public ThreadsArgsConsumer getThreadsArgs() {
            return threadsArgs;
        }

        public DatasourcesArgsConsumer getDatasourcesArgs() {
            return datasourcesArgs;
        }

        public String getDefDatasource() {
            return defDatasource;
        }

        public int getMaximumAuthenticateAttempts() {
            return maximumAuthenticateAttempts;
        }

        public static Config parse(String[] args) throws Exception {
            Config commonArgs = new Config();
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
                } else if ((CMD_SWITCHS_PREFIX + SOURCE_PATH_CONF_PARAM).equalsIgnoreCase(args[i])) {
                    if (i < args.length - 1) {
                        commonArgs.sourcePath = args[i + 1];
                        i += 2;
                    } else {
                        throw new IllegalArgumentException(BAD_SOURCE_PATH_MSG);
                    }
                } else if ((CMD_SWITCHS_PREFIX + USER_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                    if (i < args.length - 1) {
                        commonArgs.userName = args[i + 1];
                        i += 2;
                    } else {
                        throw new IllegalArgumentException("syntax: -user <value>");
                    }
                } else if ((CMD_SWITCHS_PREFIX + PASSWORD_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                    if (i < args.length - 1) {
                        commonArgs.password = args[i + 1].toCharArray();
                        i += 2;
                    } else {
                        throw new IllegalArgumentException("syntax: -password <value>");
                    }
                } else if ((CMD_SWITCHS_PREFIX + MAX_LOGIN_ATTEMPTS_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                    if (i < args.length - 1) {
                        commonArgs.maximumAuthenticateAttempts = Integer.valueOf(args[i + 1]);
                        i += 2;
                    } else {
                        throw new IllegalArgumentException("syntax: -max-login-attempts <value>");
                    }
                } else if ((CMD_SWITCHS_PREFIX + APPELEMENT_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                    if (i < args.length - 1) {
                        commonArgs.startScriptPath = args[i + 1];
                        i += 2;
                    } else {
                        throw new IllegalArgumentException("syntax: -appElement <application element id>");
                    }
                } else {
                    int consumed = commonArgs.datasourcesArgs.consume(args, i);
                    if (consumed > 0) {
                        i += consumed;
                    } else {
                        consumed = commonArgs.threadsArgs.consume(args, i);
                        if (consumed > 0) {
                            i += consumed;
                        } else {
                            throw new IllegalArgumentException("unknown argument: " + args[i]);
                        }
                    }
                }
            }
            return commonArgs;
        }
    }

    public static class UIOnCredentials implements Callable<Credentials> {

        protected Config config;

        public UIOnCredentials(Config aConfig) {
            super();
            config = aConfig;
        }

        @Override
        public Credentials call() throws Exception {
            if (config.userName != null) {
                return new Credentials(config.userName, new String(config.password));
            } else {
                Callable<Credentials> selector = () -> {
                    CredentialsSelector credentialsSelector = new CredentialsSelector();
                    credentialsSelector.setVisible(true);
                    if (credentialsSelector.getReturnStatus() == CredentialsSelector.RET_OK) {
                        return new Credentials(credentialsSelector.getUserName(), credentialsSelector.getPassword());
                    } else {
                        return null;
                    }
                };
                if (EventQueue.isDispatchThread()) {
                    Scripts.LocalContext context = Scripts.getContext();
                    try {
                        return selector.call();
                    } finally {
                        Scripts.setContext(context);
                    }
                } else {
                    Credentials res = new Credentials(null, null);
                    EventQueue.invokeAndWait(() -> {
                        try {
                            Credentials cr = selector.call();
                            if (cr != null) {
                                res.userName = cr.userName;
                                res.password = cr.password;
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                    return res.userName != null ? res : null;
                }
            }
        }
    }

    /**
     * @param args the command line arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        try {
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            System.setProperty("java.awt.Window.locationByPlatform", "true");
            Config config = Config.parse(args);
            checkUrl(config);
            init(config);
            run("");
        } catch (Throwable t) {
            Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.SEVERE, null, t);
            System.exit(0xff);
        }
    }

    public static void run(String aModuleName) {
        Scripts.getSpace().process(() -> {
            try {
                ScriptedResource._require(new String[]{aModuleName}, null, Scripts.getSpace(), new HashSet<>(), (Void v) -> {
                    Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.INFO, "Platypus application started.");
                }, (Exception ex) -> {
                    Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.SEVERE, null, ex);
                });
            } catch (Exception ex) {
                Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public static void init(Config config) throws Exception {
        if (config.url != null) {
            checkUserHome();
            GeneralResourceProvider.registerDrivers();
            config.datasourcesArgs.registerDatasources();
            Scripts.initBIO(config.threadsArgs.getMaxServicesTreads());
            Scripts.initTasks((Runnable aTask) -> {
                EventQueue.invokeLater(aTask);
            });
            Application app;
            PlatypusPrincipal.setClientSpacePrincipal(new AnonymousPlatypusPrincipal());
            Path apiFolder = ScriptedResource.lookupPlatypusJs();
            Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.INFO, "Application is located at: {0}", config.url);
            if (config.url.getProtocol().equalsIgnoreCase(PlatypusHttpConstants.PROTOCOL_HTTP)) {
                app = new PlatypusClient(new PlatypusHttpConnection(config.url, config.sourcePath, new UIOnCredentials(config), config.maximumAuthenticateAttempts, config.threadsArgs.getMaxHttpTreads()));
            } else if (config.url.getProtocol().equalsIgnoreCase(PlatypusHttpConstants.PROTOCOL_HTTPS)) {
                app = new PlatypusClient(new PlatypusHttpConnection(config.url, config.sourcePath, new UIOnCredentials(config), config.maximumAuthenticateAttempts, config.threadsArgs.getMaxHttpTreads()));
            } else if (config.url.getProtocol().equalsIgnoreCase("platypus")) {
                app = new PlatypusClient(new PlatypusPlatypusConnection(config.url, new UIOnCredentials(config), config.maximumAuthenticateAttempts, (Runnable aTask) -> {
                    EventQueue.invokeLater(aTask);
                }, config.threadsArgs.getMaxPlatypusConnections(), true));
            } else if (config.url.getProtocol().equalsIgnoreCase("file")) {
                File f = new File(config.url.toURI());
                if (f.exists() && f.isDirectory()) {
                    ModelsDocuments models = new ModelsDocuments();
                    ScriptsConfigs scriptsConfigs = new ScriptsConfigs();
                    ValidatorsScanner validatorsScanner = new ValidatorsScanner();
                    Path projectRoot = Paths.get(f.toURI());
                    Path appFolder = config.sourcePath != null ? projectRoot.resolve(config.sourcePath) : projectRoot;
                    ApplicationSourceIndexer indexer = new ApplicationSourceIndexer(appFolder, apiFolder, scriptsConfigs, validatorsScanner);
                    // TODO: add command line argument "watch" after watcher refactoring
                    //indexer.watch();
                    ScriptedDatabasesClient twoTierCore = new ScriptedDatabasesClient(config.defDatasource, indexer, true, validatorsScanner.getValidators(), config.threadsArgs.getMaxJdbcTreads());
                    QueriesProxy qp = new LocalQueriesProxy(twoTierCore, indexer);
                    ModulesProxy mp = new LocalModulesProxy(indexer, models, config.startScriptPath);
                    twoTierCore.setQueries(qp);
                    app = new Application() {

                        protected FormsDocuments forms = new FormsDocuments();
                        protected ReportsConfigs reports = new ReportsConfigs();

                        @Override
                        public Application.Type getType() {
                            return Type.CLIENT;
                        }

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
                            throw new UnsupportedOperationException("Application.getServerModules() is not supported in two-tier architecture.");
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
                        public ScriptsConfigs getScriptsConfigs() {
                            return scriptsConfigs;
                        }

                    };
                } else {
                    throw new IllegalArgumentException("applicationUrl: " + config.url + " doesn't point to existent directory or JNDI resource.");
                }
            } else {
                throw new Exception("Unknown protocol in url: " + config.url);
            }
            ScriptedResource.init(app, apiFolder, false);
            Scripts.setOnlySpace(Scripts.createSpace());
        } else {
            throw new IllegalArgumentException("Application url is missing. url is a required parameter.");
        }
    }

    protected static void checkUrl(Config config) throws InvocationTargetException, InterruptedException {
        if (config.url == null) {
            Callable<URL> urlSelector = () -> {
                ConnectionsSelector connectionsSelector = new ConnectionsSelector(null);
                connectionsSelector.setVisible(true);
                if (connectionsSelector.getReturnStatus() == ConnectionsSelector.RET_OK) {
                    ConnectionSettings settings = ConnectionsSelector.getDefaultSettings();
                    return new URL(null, settings.getUrl(), new URLStreamHandler() {

                        @Override
                        protected URLConnection openConnection(URL u) throws IOException {
                            throw new UnsupportedOperationException("Not supported yet.");
                        }

                    });
                }
                return null;
            };
            EventQueue.invokeAndWait(() -> {
                try {
                    config.url = urlSelector.call();
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusClientApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        }
    }

    protected PlatypusClientApplication() {
        super();
    }

    public static void checkUserHome() {
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
}
