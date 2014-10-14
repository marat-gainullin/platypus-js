/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.ClientConstants;
import com.eas.client.LocalModulesProxy;
import com.eas.client.ScriptedDatabasesClient;
import com.eas.client.cache.ApplicationSourceIndexer;
import com.eas.client.cache.ModelsDocuments;
import com.eas.client.cache.ScriptSecurityConfigs;
import com.eas.client.queries.LocalQueriesProxy;
import com.eas.client.resourcepool.DatasourcesArgsConsumer;
import com.eas.client.scripts.ScriptedResource;
import com.eas.client.threetier.PlatypusConnection;
import com.eas.script.ScriptUtils;
import com.eas.sensors.api.RetranslateFactory;
import com.eas.sensors.api.SensorsFactory;
import com.eas.util.args.ThreadsArgsConsumer;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.*;

/**
 *
 * @author pk, mg
 */
public class ServerMain {

    public static final String CMD_SWITCHS_PREFIX = "-";
    // configuration parameters
    public static final String APP_URL_CONF_PARAM = "url";
    public static final String DEF_DATASOURCE_CONF_PARAM = "default-datasource";

    public static final String IFACE_CONF_PARAM = "iface";
    public static final String PROTOCOLS_CONF_PARAM = "protocols";
    public static final String NUM_WORKER_THREADS_CONF_PARAM = "numworkerthreads";
    public static final String SESSION_IDLE_TIMEOUT_CONF_PARAM = "sessionidletimeout";
    public static final String SESSION_IDLE_CHECK_INTERVAL_CONF_PARAM = "sessionidlecheckinterval";
    public static final String APP_ELEMENT_CONF_PARAM = "appelement";
    // local disk paths
    public static final String LOGS_PATH = "logs";
    public static final String SECURITY_SUBDIRECTORY = "security";
    // error messages    
    public static final String INTERFACES_WITHOUT_VALUE_MSG = "Interfaces not specified.";
    public static final String BACKGROUND_TASK_WITHOUT_VALUE_MSG = "Background task not specified";
    public static final String BAD_APP_URL_MSG = "url not specified";
    public static final String BAD_DEF_DATASOURCE_MSG = "default-datasource value not specified";

    public static final String BAD_TASK_MSG = "Background task is specified with '-backgroundTask <moduleName>:<moduleId>'";
    public static final String LOG_FILE_WITHOUT_VALUE_MSG = "Log file is not specified.";
    public static final String LOG_LEVEL_WITHOUT_VALUE_MSG = "Log level is not specified.";
    public static final String NO_DB_URL_SPECIFIED_MSG = "No Database URL specified.";
    public static final String USER_HOME_ABSENTFILE_MSG = ClientConstants.USER_HOME_PROP_NAME + " property points to non-existent location";
    public static final String USER_HOME_MISSING_MSG = ClientConstants.USER_HOME_PROP_NAME + " property missing. Please specify it with -Duser.home=... java comannd line switch";
    public static final String USER_HOME_NOT_A_DIRECTORY_MSG = ClientConstants.USER_HOME_PROP_NAME + " property points to non-directory";
    public static final String PROTOCOLS_WITHOUT_VALUE_MSG = "Protocols not specified.";
    public static final String KEYSTORE_MISING_MSG = "Can't locate key store file. May be user.home platypus security directory missing.";
    public static final String TRUSTSTORE_MISSING_MSG = "Can't locate trust store file. May be user.home platypus security directory missing.";
    public static final String BAD_DEFAULT_APPLICATION_ELEMENT_MSG = "Default application element must be simple integer";
    public static final String BAD_APPLICATION_PATH_MSG = "Application path must follow applicationpath (ap) parameter";

    private static String url;
    private static String defDatasource;
    private static String iface;
    private static String protocols;
    private static String numWorkerThreads;
    private static String sessionIdleTimeout;
    private static String sessionIdleCheckInterval;
    private static ThreadsArgsConsumer threadsConfig;
    private static String appElement;

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

    private static void parseArgs(String[] args) throws Exception {
        DatasourcesArgsConsumer dsArgs = new DatasourcesArgsConsumer();
        ThreadsArgsConsumer threadsArgs = new ThreadsArgsConsumer();
        int i = 0;
        while (i < args.length) {
            if ((CMD_SWITCHS_PREFIX + APP_URL_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    url = args[i + 1];
                    i += 2;
                } else {
                    printHelp(BAD_APP_URL_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + DEF_DATASOURCE_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    defDatasource = args[i + 1];
                    i += 2;
                } else {
                    printHelp(BAD_DEF_DATASOURCE_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + IFACE_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    iface = args[i + 1];
                    i += 2;
                } else {
                    printHelp(INTERFACES_WITHOUT_VALUE_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + PROTOCOLS_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    protocols = args[i + 1];
                    i += 2;
                } else {
                    printHelp(PROTOCOLS_WITHOUT_VALUE_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + NUM_WORKER_THREADS_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    numWorkerThreads = args[i + 1];
                    i += 2;
                } else {
                    printHelp(PROTOCOLS_WITHOUT_VALUE_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + SESSION_IDLE_TIMEOUT_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    sessionIdleTimeout = args[i + 1];
                    i += 2;
                } else {
                    printHelp(PROTOCOLS_WITHOUT_VALUE_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + SESSION_IDLE_CHECK_INTERVAL_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    sessionIdleCheckInterval = args[i + 1];
                    i += 2;
                } else {
                    printHelp(PROTOCOLS_WITHOUT_VALUE_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + APP_ELEMENT_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    appElement = args[i + 1];
                    i += 2;
                } else {
                    printHelp(BAD_DEFAULT_APPLICATION_ELEMENT_MSG);
                }
            } else {
                int consumed = dsArgs.consume(args, i);
                if (consumed > 0) {
                    i += consumed;
                } else {
                    consumed = threadsArgs.consume(args, i);
                    if (consumed > 0) {
                        i += consumed;
                    } else {
                        throw new IllegalArgumentException("unknown argument: " + args[i]);
                    }
                }
            }
        }
        dsArgs.registerDatasources();
        threadsConfig = threadsArgs;
    }

    /**
     * @param args the command line arguments
     * @throws IOException
     * @throws Exception
     */
    public static void main(String[] args) throws IOException, Exception {
        checkUserHome();
        ScriptUtils.init();
        parseArgs(args);
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("Application url (-url parameter) is required.");
        }
        SSLContext sslContext = PlatypusConnection.createSSLContext();

        ScriptedDatabasesClient serverCoreDbClient;
        if (url.toLowerCase().startsWith("file")) {
            File f = new File(new URI(url));
            if (f.exists() && f.isDirectory()) {
                Logger.getLogger(ServerMain.class.getName()).log(Level.INFO, "Application is located at: {0}", f.getPath());
                ScriptSecurityConfigs securityConfigs = new ScriptSecurityConfigs();
                ServerTasksScanner tasksScanner = new ServerTasksScanner(securityConfigs);
                ApplicationSourceIndexer indexer = new ApplicationSourceIndexer(f.getPath(), tasksScanner);
                indexer.watch();
                serverCoreDbClient = new ScriptedDatabasesClient(defDatasource, indexer, true, tasksScanner.getValidators(), threadsConfig.getMaxJdbcTreads());
                PlatypusServer server = new PlatypusServer(indexer, new LocalModulesProxy(indexer, new ModelsDocuments(), appElement), new LocalQueriesProxy(serverCoreDbClient, indexer), serverCoreDbClient, sslContext, parseListenAddresses(), parsePortsProtocols(), parsePortsSessionIdleTimeouts(), parsePortsSessionIdleCheckIntervals(), parsePortsNumWorkerThreads(), securityConfigs, appElement, tasksScanner.getAuthorizers());
                serverCoreDbClient.setContextHost(server);
                ScriptedResource.init(server);
                SensorsFactory.init(server.getAcceptorsFactory());
                RetranslateFactory.init(server.getRetranslateFactory());
                //
                server.start(tasksScanner.getResidents(), tasksScanner.getAcceptors());
            } else {
                throw new IllegalArgumentException("applicationUrl: " + url + " doesn't point to existent directory.");
            }
        } else {
            throw new Exception("Unknown protocol in url: " + url);
        }
    }

    private static void printHelp(String string) {
        System.err.println(string);
    }

    private static InetSocketAddress[] parseListenAddresses() {
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

    private static Map<Integer, String> parsePortsProtocols() {
        Map<Integer, String> protocolsMap = new HashMap<>();
        if (protocols != null && !protocols.isEmpty()) {
            String[] splitted = protocols.replace(" ", "").split(",");
            for (int i = 0; i < splitted.length; i++) {
                String[] protParts = splitted[i].split(":");
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

    private static Map<Integer, Integer> parsePortsNumWorkerThreads() {
        Map<Integer, Integer> numWorkerThreadsMap = new HashMap<>();
        if (numWorkerThreads != null && !numWorkerThreads.isEmpty()) {
            String[] splitted = numWorkerThreads.replace(" ", "").split(",");
            for (int i = 0; i < splitted.length; i++) {
                String[] protParts = splitted[i].split(":");
                if (protParts.length == 2) {
                    numWorkerThreadsMap.put(Integer.valueOf(protParts[0]), Integer.valueOf(protParts[1]));
                }
            }
        }
        return numWorkerThreadsMap;
    }

    private static Map<Integer, Integer> parsePortsSessionIdleTimeouts() {
        Map<Integer, Integer> sessionIdleTimeoutMap = new HashMap<>();
        if (sessionIdleTimeout != null && !sessionIdleTimeout.isEmpty()) {
            String[] splitted = sessionIdleTimeout.replace(" ", "").split(",");
            for (int i = 0; i < splitted.length; i++) {
                String[] protParts = splitted[i].split(":");
                if (protParts.length == 2) {
                    sessionIdleTimeoutMap.put(Integer.valueOf(protParts[0]), Integer.valueOf(protParts[1]));
                }
            }
        }
        return sessionIdleTimeoutMap;
    }

    private static Map<Integer, Integer> parsePortsSessionIdleCheckIntervals() {
        Map<Integer, Integer> sessionIdleCheckIntervalsMap = new HashMap<>();
        if (sessionIdleCheckInterval != null && !sessionIdleCheckInterval.isEmpty()) {
            String[] splitted = sessionIdleCheckInterval.replace(" ", "").split(",");
            for (int i = 0; i < splitted.length; i++) {
                String[] protParts = splitted[i].split(":");
                if (protParts.length == 2) {
                    sessionIdleCheckIntervalsMap.put(Integer.valueOf(protParts[0]), Integer.valueOf(protParts[1]));
                }
            }
        }
        return sessionIdleCheckIntervalsMap;
    }
    /*
     private static KeyManager[] createKeyManagers() throws NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException, FileNotFoundException, IOException, CertificateException, UnrecoverableKeyException, URISyntaxException {
     KeyStore ks = KeyStore.getInstance("JKS");
     // get user password and file input stream
     char[] sslPassword = "keyword".toCharArray();
     File keyStoreFile = new File(StringUtils.join(File.separator, System.getProperty(ClientConstants.USER_HOME_PROP_NAME), ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME, SECURITY_SUBDIRECTORY, "keystore"));
     if (!keyStoreFile.exists()) {
     File keyPath = new File(StringUtils.join(File.separator, System.getProperty(ClientConstants.USER_HOME_PROP_NAME), ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME, SECURITY_SUBDIRECTORY));
     keyPath.mkdirs();
     keyStoreFile.createNewFile();
     try (OutputStream keyOut = new FileOutputStream(keyStoreFile); InputStream keyIn = PlatypusConnection.class.getResourceAsStream("emptyKeystore")) {
     byte[] resData = BinaryUtils.readStream(keyIn, -1);
     keyOut.write(resData);
     }
     }
     if (keyStoreFile.exists()) {
     try (InputStream is = new FileInputStream(keyStoreFile)) {
     ks.load(is, sslPassword);
     }
     final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
     keyManagerFactory.init(ks, sslPassword);
     return keyManagerFactory.getKeyManagers();
     } else {
     throw new FileNotFoundException(KEYSTORE_MISING_MSG);
     }
     }

     private static TrustManager[] createTrustManagers() throws NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException, FileNotFoundException, IOException, CertificateException, URISyntaxException {
     KeyStore ks = KeyStore.getInstance("JKS");
     char[] ksPassword = "trustword".toCharArray();
     File trustStore = new File(StringUtils.join(File.separator, System.getProperty(ClientConstants.USER_HOME_PROP_NAME), ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME, SECURITY_SUBDIRECTORY, "truststore"));
     if (!trustStore.exists()) {
     File trustPath = new File(StringUtils.join(File.separator, System.getProperty(ClientConstants.USER_HOME_PROP_NAME), ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME, SECURITY_SUBDIRECTORY));
     trustPath.mkdirs();
     trustStore.createNewFile();
     try (OutputStream trustOut = new FileOutputStream(trustStore); InputStream trustIn = PlatypusConnection.class.getResourceAsStream("emptyTruststore")) {
     byte[] resData = BinaryUtils.readStream(trustIn, -1);
     trustOut.write(resData);
     }
     }
     if (trustStore.exists()) {
     try (InputStream is = new FileInputStream(trustStore)) {
     ks.load(is, ksPassword);
     }
     final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("PKIX");
     trustManagerFactory.init(ks);
     return trustManagerFactory.getTrustManagers();
     } else {
     throw new FileNotFoundException(TRUSTSTORE_MISSING_MSG);
     }
     }

     public static SSLContext createSSLContext() throws NoSuchAlgorithmException, KeyManagementException, NoSuchProviderException, KeyStoreException, FileNotFoundException, IOException, CertificateException, UnrecoverableKeyException, URISyntaxException {
     SSLContext context = SSLContext.getInstance("TLS");
     context.init(createKeyManagers(), createTrustManagers(), SecureRandom.getInstance("SHA1PRNG"));
     return context;
     }
     */
}
