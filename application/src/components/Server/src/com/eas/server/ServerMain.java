/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.AppCache;
import com.eas.client.ClientConstants;
import com.eas.client.ClientFactory;
import com.eas.client.ScriptedDatabasesClient;
import com.eas.client.resourcepool.DatasourcesArgsConsumer;
import com.eas.client.scripts.PlatypusScriptedResource;
import com.eas.util.StringUtils;
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
import javax.management.ObjectName;
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
    // security switches
    public static final String ANONYMOUS_ON_CMD_SWITCH = "enable-anonymous";

    public static final String BACKGROUNDTASK_CONF_PARAM = "tasks";
    public static final String IFACE_CONF_PARAM = "iface";
    public static final String PROTOCOLS_CONF_PARAM = "protocols";
    public static final String NUM_WORKER_THREADS_CONF_PARAM = "numworkerthreads";
    public static final String SESSION_IDLE_TIMEOUT_CONF_PARAM = "sessionidletimeout";
    public static final String SESSION_IDLE_CHECK_INTERVAL_CONF_PARAM = "sessionidlecheckinterval";
    public static final String APP_ELEMENT_CONF_PARAM = "appelement";
    // configuration paths
    public static final String SERVER_PREFS_PATH = "/com/eas/server";
    public static final String SSL_PREFS_PATH = "/com/eas/net/ssl";
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
    private static String appElement;
    private static boolean anonymousEnabled;

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

    private static void parseArgs(String[] args, Set<String> aTasksModules) throws Exception {
        DatasourcesArgsConsumer dsArgs = new DatasourcesArgsConsumer();
        int i = 0;
        while (i < args.length) {
            if ((CMD_SWITCHS_PREFIX + APP_URL_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    url = args[i + 1];
                    i += 2;
                } else {
                    printHelp(BAD_APP_URL_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + ANONYMOUS_ON_CMD_SWITCH).equalsIgnoreCase(args[i])) {
                if (i < args.length - 1) {
                    anonymousEnabled = true;
                    i += 1;
                }
            } else if ((CMD_SWITCHS_PREFIX + DEF_DATASOURCE_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    defDatasource = args[i + 1];
                    i += 2;
                } else {
                    printHelp(BAD_DEF_DATASOURCE_MSG);
                }
            } else if ((CMD_SWITCHS_PREFIX + BACKGROUNDTASK_CONF_PARAM).equalsIgnoreCase(args[i])) {
                if (i + 1 < args.length) {
                    String modulesNames = args[i + 1];
                    aTasksModules.addAll(StringUtils.split(modulesNames, ","));
                    i += 2;
                } else {
                    printHelp(BACKGROUND_TASK_WITHOUT_VALUE_MSG);
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
                    i += 2;
                    appElement = args[i + 1];
                } else {
                    printHelp(BAD_DEFAULT_APPLICATION_ELEMENT_MSG);
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
        dsArgs.registerDatasources();
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
        // tasks from command-line
        Set<String> tasks = new HashSet<>();
        parseArgs(args, tasks);
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("Application url ( -url parameter) is required.");
        }
        SSLContext ctx = createSSLContext();
        AppCache appCache = ClientFactory.obtainTwoTierAppCache(url, new ServerTasksScanner(tasks));
        ScriptedDatabasesClient appDbClient = new ScriptedDatabasesClient(appCache, defDatasource, true, null);
        PlatypusServer server = new PlatypusServer(appDbClient, ctx, getListenAddresses(), getPortsProtocols(), getPortsSessionIdleTimeouts(), getPortsSessionIdleCheckIntervals(), getPortsNumWorkerThreads(), tasks, appElement);
        server.setAnonymousEnabled(anonymousEnabled);
        appDbClient.setContextHost(server);
        appDbClient.setPrincipalHost(server);
        appDbClient.setDocumentsHost(server);
        PlatypusScriptedResource.init(appDbClient, server, server);
        //
        server.start();
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

    private static Map<Integer, Integer> getPortsNumWorkerThreads() {
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

    private static Map<Integer, Integer> getPortsSessionIdleTimeouts() {
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

    private static Map<Integer, Integer> getPortsSessionIdleCheckIntervals() {
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

    private static KeyManager[] createKeyManagers() throws NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException, FileNotFoundException, IOException, CertificateException, UnrecoverableKeyException, URISyntaxException {
        KeyStore ks = KeyStore.getInstance("JKS");
        // get user password and file input stream
        char[] sslPassword = "keyword".toCharArray();
        File keyStore = new File(StringUtils.join(File.separator, System.getProperty(ClientConstants.USER_HOME_PROP_NAME), ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME, SECURITY_SUBDIRECTORY, "keystore"));
        if (keyStore.exists()) {
            try (InputStream is = new FileInputStream(keyStore)) {
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
}
