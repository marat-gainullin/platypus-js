/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.Client;
import com.eas.client.ClientConstants;
import com.eas.client.DatabasesClient;
import com.eas.client.scripts.ScriptRunner;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.settings.SettingsConstants;
import com.eas.debugger.jmx.server.Breakpoints;
import com.eas.debugger.jmx.server.Debugger;
import com.eas.debugger.jmx.server.DebuggerMBean;
import com.eas.debugger.jmx.server.Settings;
import com.eas.util.StringUtils;
import com.eas.util.logging.PlatypusFormatter;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.management.ObjectName;
import javax.net.ssl.*;

/**
 *
 * @author pk
 */
public class ServerMain {

    public static final String CMD_SWITCHS_PREFIX = "-";
    // configuration parameters
    public static final String ACCEPTORTASK_CONF_PARAM = "acceptorTask";
    public static final String APP_DB_PASSWORD_CONF_PARAM = "dbpassword";
    public static final String APP_DB_SCHEMA_CONF_PARAM = "dbschema";
    public static final String APP_DB_URL_CONF_PARAM = "url";
    public static final String APP_DB_USERNAME_CONF_PARAM = "dbuser";
    public static final String BACKGROUNDTASK_CONF_PARAM = "backgroundTask";
    public static final String IFACE_CONF_PARAM = "iface";
    public static final String PROTOCOLS_CONF_PARAM = "protocols";
    public static final String LOGLEVEL_CONF_PARAM = "loglevel";
    public static final String LOG_CONF_PARAM = "log";
    public static final String APP_ELEMENT_CONF_PARAM = "appelement";
    public static final String APP_PATH_PARAM = "applicationpath";
    public static final String APP_PATH_PARAM1 = "ap";
    // configuration paths
    public static final String SERVER_PREFS_PATH = "/com/eas/server";
    public static final String SSL_PREFS_PATH = "/com/eas/net/ssl";
    // local disk paths
    public static final String LOGS_PATH = "logs";
    public static final String SECURITY_SUBDIRECTORY = "security";
    // error messages    
    public static final String INTERFACES_WITHOUT_VALUE_MSG = "Interfaces not specified.";
    public static final String BACKGROUND_TASK_WITHOUT_VALUE_MSG = "Background task not specified";
    public static final String BAD_APP_DB_PASSWORD_MSG = "Password not specified";
    public static final String BAD_APP_DB_URL_MSG = "URL not specified.";
    public static final String BAD_APP_DB_USERNAME_MSG = "User name not specified";
    public static final String BAD_APP_SCHEMA_MSG = "Schema not specified";
    public static final String BAD_TASK_MSG = "Background task is specified with '-backgroundTask <moduleName>:<moduleId>'";
    public static final String LOG_FILE_WITHOUT_VALUE_MSG = "Log file is not specified.";
    public static final String LOG_LEVEL_WITHOUT_VALUE_MSG = "Log level is not specified.";
    public static final String NO_SCHEMA_SPECIFIED_MSG = "No schema specified.";
    public static final String NO_URL_SPECIFIED_MSG = "No URL specified.";
    public static final String USER_HOME_ABSENTFILE_MSG = ClientConstants.USER_HOME_PROP_NAME + " property points to non-existent location";
    public static final String USER_HOME_MISSING_MSG = ClientConstants.USER_HOME_PROP_NAME + " property missing. Please specify it with -Duser.home=... java comannd line switch";
    public static final String USER_HOME_NOT_A_DIRECTORY_MSG = ClientConstants.USER_HOME_PROP_NAME + " property points to non-directory";
    public static final String PROTOCOLS_WITHOUT_VALUE_MSG = "Protocols not specified.";
    public static final String KEYSTORE_MISING_MSG = "Can't locate key store file. May be user.home platypus security directory missing.";
    public static final String TRUSTSTORE_MISSING_MSG = "Can't locate trust store file. May be user.home platypus security directory missing.";
    public static final String BAD_DEFAULT_APPLICATION_ELEMENT_MSG = "Default application element must be simple integer";
    public static final String BAD_APPLICATION_PATH_MSG = "Application path must follow applicationpath (ap) parameter";
    public static final String APPLICATION_PATH_NOT_EXISTS_MSG = "Application path does not exist.";
    public static final String APPLICATION_PATH_NOT_DIRECTORY_MSG = "Application path must point to a directory.";
    //
    private static Logger[] loggers = {
        Logger.getLogger("com.eas"),
        Logger.getLogger("sun.reflect"),
        Logger.getLogger("com.bearsoft"),
        Logger.getLogger("org.mozilla.javascript"),
        Logger.getLogger(Client.APPLICATION_LOGGER_NAME)
    };
    private static String logFileNamePattern = null;
    private static Level logsLevel = Level.OFF;
    private static String url;
    private static String schema;
    private static String username;
    private static String password;
    private static String iface;
    private static String protocols;
    private static String appElement;
    private static String appPath;

    private static void checkUserHome() {
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

    private static void setupLoggers(Level aLevel, String aLogFileName) throws Exception {
        for (Logger logger : loggers) {
            logger.setLevel(aLevel);
            if (aLogFileName != null && !aLogFileName.isEmpty()) {
                Handler fHandler = new FileHandler(aLogFileName, 1024 * 1024 * 2, 1, true);
                fHandler.setEncoding(SettingsConstants.COMMON_ENCODING);
                fHandler.setFormatter(new SimpleFormatter());
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

    private static void parseArgs(String[] args, Set<ModuleConfig> aTasks) throws Exception {
        for (int i = 0; i < args.length; i++) {
            if ((CMD_SWITCHS_PREFIX + APP_DB_URL_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    url = args[i + 1];
                } else {
                    printHelp(BAD_APP_DB_URL_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + APP_DB_SCHEMA_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    schema = args[i + 1];
                } else {
                    printHelp(BAD_APP_SCHEMA_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + APP_DB_USERNAME_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    username = args[i + 1];
                } else {
                    printHelp(BAD_APP_DB_USERNAME_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + APP_DB_PASSWORD_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    password = args[i + 1];
                } else {
                    printHelp(BAD_APP_DB_PASSWORD_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + BACKGROUNDTASK_CONF_PARAM).equalsIgnoreCase(args[i]) || (CMD_SWITCHS_PREFIX + ACCEPTORTASK_CONF_PARAM).equalsIgnoreCase(args[i])) {
                boolean acceptor = (CMD_SWITCHS_PREFIX + ACCEPTORTASK_CONF_PARAM).equalsIgnoreCase(args[i]);
                if (i + 1 < args.length) {
                    String moduleName = args[i + 1];
                    try {
                        ModuleConfig config = new ModuleConfig(true, false, acceptor, null, moduleName);
                        aTasks.add(config);
                    } catch (NumberFormatException ex) {
                        printHelp(BAD_TASK_MSG);
                    }
                } else {
                    printHelp(BACKGROUND_TASK_WITHOUT_VALUE_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + IFACE_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    iface = args[i + 1];
                } else {
                    printHelp(INTERFACES_WITHOUT_VALUE_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + PROTOCOLS_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    protocols = args[i + 1];
                } else {
                    printHelp(PROTOCOLS_WITHOUT_VALUE_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + LOG_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    logFileNamePattern = args[i + 1];
                } else {
                    printHelp(LOG_FILE_WITHOUT_VALUE_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + APP_ELEMENT_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    appElement = args[i + 1];
                } else {
                    printHelp(BAD_DEFAULT_APPLICATION_ELEMENT_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + APP_PATH_PARAM).equalsIgnoreCase(args[i]) || (CMD_SWITCHS_PREFIX + APP_PATH_PARAM1).equalsIgnoreCase(args[i])) {
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
                } else {
                    printHelp(BAD_APPLICATION_PATH_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + LOGLEVEL_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    logsLevel = Level.parse(args[i + 1]);
                } else {
                    printHelp(LOG_LEVEL_WITHOUT_VALUE_MSG);
                }
            }
        }
    }

    private static void checkLogsDirectory() {
        String path = System.getProperty(ClientConstants.USER_HOME_PROP_NAME);
        if (path != null) {
            File logsDir = new File(StringUtils.join(File.separator, path, ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME, LOGS_PATH));
            if (!logsDir.exists()) {
                logsDir.mkdirs();
            }
        }
    }

    protected static void registerMBean(String aName, Object aBean) throws Exception {
        // Get the platform MBeanServer
        // Uniquely identify the MBeans and register them with the platform MBeanServer
        ManagementFactory.getPlatformMBeanServer().registerMBean(aBean, new ObjectName(aName));
    }

    /**
     * @param args the command line arguments
     * @throws IOException
     * @throws Exception
     */
    public static void main(String[] args) throws IOException, Exception {
        checkUserHome();
        checkLogsDirectory();
        // tasks from configuration
        Set<ModuleConfig> tasks = new HashSet<>();
        readModuleConfigs(tasks);
        // tasks from command-line
        parseArgs(args, tasks);
        if (url == null) {
            printHelp(NO_URL_SPECIFIED_MSG);
            System.exit(1);
        }
        if (schema == null) {
            printHelp(NO_SCHEMA_SPECIFIED_MSG);
            System.exit(1);
        }
        if (!url.startsWith("jdbc:")) {
            throw new Exception(String.format("Unsupported URL in connection settings: %s. Only jdbc urls are supported", url));
        }        
        DbConnectionSettings settings = new DbConnectionSettings();
        settings.setUrl(url);
        settings.getInfo().setProperty(ClientConstants.DB_CONNECTION_SCHEMA_PROP_NAME, schema);
        if (username != null) {
            settings.getInfo().setProperty(ClientConstants.DB_CONNECTION_USER_PROP_NAME, username);
        }
        if (password != null) {
            settings.getInfo().setProperty(ClientConstants.DB_CONNECTION_PASSWORD_PROP_NAME, password);
        }
        setupLoggers(logsLevel, expandLogFileName(logFileNamePattern));
        SSLContext ctx = createSSLContext();
        if (appPath != null) {
            settings.setApplicationPath(appPath);
        }
        DatabasesClient appDbClient = new DatabasesClient(settings);
        Debugger debugger = null;
        if (System.getProperty(ScriptRunner.DEBUG_PROPERTY) != null) {
            debugger = Debugger.initialize(false);
            registerMBean(DebuggerMBean.DEBUGGER_MBEAN_NAME, debugger);
            registerMBean(Breakpoints.BREAKPOINTS_MBEAN_NAME, Breakpoints.getInstance());
            // Apply debugging facility
            registerMBean(Settings.SETTINGS_MBEAN_NAME, new Settings(appDbClient));
        }
        PlatypusServer server = new PlatypusServer(appDbClient, ctx, getListenAddresses(), getPortsProtocols(), tasks, appElement);
        appDbClient.setContextHost(server);
        appDbClient.setPrincipalHost(server);
        ScriptRunner.PlatypusScriptedResource.init(appDbClient.getAppCache());
        Thread sgc = new Thread(new GarbageSessionsCollector(server));
        sgc.setDaemon(true);
        sgc.start();
        server.start();
    }

    private static String expandLogFileName(String logFileName) throws FileNotFoundException {
        if (logFileName != null) {
            String path = System.getProperty(ClientConstants.USER_HOME_PROP_NAME);
            if (path != null) {
                return StringUtils.join(File.separator, path, ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME, LOGS_PATH, logFileName);
            }
        }
        return null;
    }

    private static void printHelp(String string) {
        System.err.println(string);
    }

    private static InetSocketAddress[] getListenAddresses() {
        if (iface == null || iface.isEmpty()) {
            return new InetSocketAddress[]{
                        new InetSocketAddress(PlatypusServer.DEFAULT_PORT)
                    };
        } else {
            String[] splittedAddresses = iface.replace(" ", "").split(",");
            InetSocketAddress[] result = new InetSocketAddress[splittedAddresses.length];
            for (int i = 0; i < splittedAddresses.length; i++) {
                String[] addressParts = splittedAddresses[i].split(":");
                assert addressParts.length > 0;
                if (addressParts.length == 1) {
                    result[i] = new InetSocketAddress(addressParts[0], PlatypusServer.DEFAULT_PORT);
                } else if ("*".equals(addressParts[0])) {
                    result[i] = new InetSocketAddress(Integer.parseInt(addressParts[1]));
                } else {
                    result[i] = new InetSocketAddress(addressParts[0], Integer.parseInt(addressParts[1]));
                }
            }
            return result;
        }
    }

    private static Map<Integer, String> getPortsProtocols() {
        Map<Integer, String> protocolsMap = new HashMap<>();
        if (protocols != null && !protocols.isEmpty()) {
            String[] splittedPortsProtocols = protocols.replace(" ", "").split(",");
            for (int i = 0; i < splittedPortsProtocols.length; i++) {
                String[] protParts = splittedPortsProtocols[i].split(":");
                if (protParts.length == 2) {
                    protocolsMap.put(Integer.valueOf(protParts[0]), protParts[1]);
                }
            }
        }
        if (protocolsMap.isEmpty()) {
            protocolsMap.put(PlatypusServer.DEFAULT_PORT, PlatypusServer.DEFAULT_PROTOCOL);
        }
        return protocolsMap;
    }

    private static void readModuleConfigs(Set<ModuleConfig> contexts) throws Exception {
        try {
            Preferences modules = Preferences.systemRoot().node(SERVER_PREFS_PATH + "/modules");
            if (modules != null) {
                String[] modulesNames = modules.childrenNames();
                if (modulesNames != null) {
                    for (String mName : modulesNames) {
                        Preferences moduleNode = modules.node(mName);
                        if (moduleNode != null) {
                            contexts.add(new ModuleConfig(moduleNode));
                        }
                    }
                }
            }
        } catch (BackingStoreException ex) {
            Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    private static KeyManager[] createKeyManagers() throws NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException, FileNotFoundException, IOException, CertificateException, UnrecoverableKeyException, URISyntaxException {
        KeyStore ks = KeyStore.getInstance(Preferences.systemRoot().node(SSL_PREFS_PATH).get("keyStoreType", "JKS"));
        // get user password and file input stream
        char[] sslPassword = Preferences.systemRoot().node(SSL_PREFS_PATH).get("keyStorePassword", "keyword").toCharArray();
        File keyStore = new File(StringUtils.join(File.separator, System.getProperty(ClientConstants.USER_HOME_PROP_NAME), ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME, SECURITY_SUBDIRECTORY, "keystore"));
        if (keyStore.exists()) {
            try (InputStream is = new FileInputStream(keyStore)) {
                ks.load(is, sslPassword);
            }
            final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                    Preferences.systemRoot().node(SSL_PREFS_PATH).get("keyManagerAlgorithm", "SunX509"));
            keyManagerFactory.init(ks, sslPassword);
            return keyManagerFactory.getKeyManagers();
        } else {
            throw new FileNotFoundException(KEYSTORE_MISING_MSG);
        }
    }

    private static TrustManager[] createTrustManagers() throws NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException, FileNotFoundException, IOException, CertificateException, URISyntaxException {
        KeyStore ks = KeyStore.getInstance(Preferences.userRoot().node(SSL_PREFS_PATH).get("trustStoreType", "JKS"));
        char[] ksPassword = Preferences.userRoot().node(SSL_PREFS_PATH).get("trustStorePassword", "trustword").toCharArray();
        File trustStore = new File(StringUtils.join(File.separator, System.getProperty(ClientConstants.USER_HOME_PROP_NAME), ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME, SECURITY_SUBDIRECTORY, "truststore"));
        if (trustStore.exists()) {
            try (InputStream is = new FileInputStream(trustStore)) {
                ks.load(is, ksPassword);
            }
            final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    Preferences.userRoot().node(SSL_PREFS_PATH).get("trustManagerAlgorithm", "PKIX"));
            trustManagerFactory.init(ks);
            return trustManagerFactory.getTrustManagers();
        } else {
            throw new FileNotFoundException(TRUSTSTORE_MISSING_MSG);
        }
    }

    public static SSLContext createSSLContext() throws NoSuchAlgorithmException, KeyManagementException, NoSuchProviderException, KeyStoreException, FileNotFoundException, IOException, CertificateException, UnrecoverableKeyException, URISyntaxException {
        SSLContext context = SSLContext.getInstance(Preferences.systemRoot().node(SSL_PREFS_PATH).get("protocol", "TLS"));
        context.init(createKeyManagers(), createTrustManagers(), SecureRandom.getInstance(Preferences.systemRoot().node(SSL_PREFS_PATH).get("secureRandomAlgorithm", "SHA1PRNG")));
        return context;
    }
}
