/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.changes.ChangeValue;
import com.bearsoft.rowset.changes.Command;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.dataflow.TransactionListener;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.Application;
import com.eas.client.ClientConstants;
import com.eas.client.ModulesProxy;
import com.eas.client.RemoteModulesProxy;
import com.eas.client.ServerModulesProxy;
import com.eas.client.login.AppPlatypusPrincipal;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.queries.PlatypusQuery;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.queries.RemoteQueriesProxy;
import com.eas.client.threetier.requests.*;
import com.eas.util.BinaryUtils;
import com.eas.util.ListenerRegistration;
import com.eas.util.StringUtils;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.*;
import javax.security.auth.login.LoginException;
import javax.swing.JOptionPane;

/**
 *
 * @author kl, mg refactoring
 */
public class PlatypusClient implements Application<PlatypusQuery>{

    static {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier((String aHostName, SSLSession aSslSession) -> aHostName.equalsIgnoreCase(aSslSession.getPeerHost()));
            HttpsURLConnection.setDefaultSSLSocketFactory(createSSLContext().getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException | NoSuchProviderException | KeyStoreException | CertificateException | UnrecoverableKeyException | URISyntaxException | IOException ex) {
            Logger.getLogger(PlatypusClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // SSL defaults
    public static final String DEFAULT_KEYSTORE_PASSWORD = "keyword";
    public static final String DEFAULT_TRUSTSTORE_PASSWORD = "trustword";
    public static final String ACCEPTED_CERTSALIAS_PREFIX = "acceptedFromServers";
    // Random number generator algorithm
    public static final String DEFAULT_SECURE_RANDOM_ALGORITHM = "SHA1PRNG";
    // Certificated storage type
    public static final String DEFAULT_CETRS_STORE_TYPE = "JKS";
    // Ssl protocol
    public static final String DEFAULT_SSL_PROTOCOL = "TLS";
    //
    public static final String DEFAULT_TRUST_ALGORITHM = "PKIX";
    // configuration paths
    public static final String CLIENT_PREFS_PATH = "/com/eas/client";
    public static final String SSL_PREFS_PATH = "/com/eas/net/ssl";
    // local disk paths
    public static final String SECURITY_SUBDIRECTORY = "security";
    // error messages
    public static final String KEYSTORE_MISING_MSG = "Can't locate key store file. May be bad installation.";
    public static final String TRUSTSTORE_MISSING_MSG = "Can't locate trust store file. May be bad installation.";
    public static final String ENQUEUEING_UPDATES_THREE_TIER_MSG = "Enqueueing updates are not allowed in three tier mode.";
    public static final String EXECUTING_UPDATES_THREE_TIER_MSG = "Executing updates are not allowed in three tier mode.";
    public static final String SQL_TEXT_PROVIDERS_ARE_NOT_ALLOWED_MSG = "Sql query text based flow providers are not allowed in three tier mode.";
    //
    public static final ResourceBundle clientLocalizations = ResourceBundle.getBundle("com/eas/client/threetier/clientlocalizations");

    private static char[] getTrustStorePassword() {
        char[] password = DEFAULT_TRUSTSTORE_PASSWORD.toCharArray();
        return password;
    }
    protected URL url;
    protected PlatypusPrincipal principal;
    protected PlatypusConnection conn;
    protected QueriesProxy<PlatypusQuery> queries;
    protected ModulesProxy modules;
    protected ServerModulesProxy serverModulesProxy;
    protected List<Change> changeLog = new ArrayList<>();
    protected Set<TransactionListener> transactionListeners = new HashSet<>();

    public PlatypusClient(PlatypusConnection aConn) throws Exception {
        super();
        url = aConn.getUrl();
        conn = aConn;
        queries = new RemoteQueriesProxy(aConn, this);
        modules = new RemoteModulesProxy(aConn);
        serverModulesProxy = new ServerModulesProxy(aConn);
    }

    @Override
    public QueriesProxy<PlatypusQuery> getQueries() {
        return queries;
    }

    @Override
    public ModulesProxy getModules() {
        return modules;
    }

    @Override
    public ServerModulesProxy getServerModules() {
        return serverModulesProxy;
    }

    public URL getUrl() {
        return url;
    }

    public ListenerRegistration addTransactionListener(final TransactionListener aListener) {
        transactionListeners.add(aListener);
        return () -> {
            transactionListeners.remove(aListener);
        };
    }

    public List<Change> getChangeLog() {
        return changeLog;
    }

    public FlowProvider createFlowProvider(String aQueryId, Fields aExpectedFields) {
        return new PlatypusFlowProvider(this, conn, aQueryId, aExpectedFields);
    }

    public Object executeServerModuleMethod(String aModuleName, String aMethodName, Consumer<Object> onSuccess, Consumer<Exception> onFailure, Object... aArguments) throws Exception {
        final ExecuteServerModuleMethodRequest request = new ExecuteServerModuleMethodRequest(aModuleName, aMethodName, aArguments);
        if (onSuccess != null) {
            conn.<ExecuteServerModuleMethodRequest.Response>enqueueRequest(request, (ExecuteServerModuleMethodRequest.Response aResponse) -> {
                onSuccess.accept(aResponse.getResult());
            }, onFailure);
            return null;
        } else {
            ExecuteServerModuleMethodRequest.Response response = conn.executeRequest(request);
            return response.getResult();
        }
    }

    public PlatypusPrincipal getPrincipal() {
        return principal;
    }

    public int commit(Consumer<Integer> onSuccess, Consumer<Exception> onFailure) throws Exception {
        Runnable doWork = () -> {
            changeLog.clear();
            for (TransactionListener l : transactionListeners.toArray(new TransactionListener[]{})) {
                try {
                    l.commited();
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        CommitRequest request = new CommitRequest(changeLog);
        if (onSuccess != null) {
            conn.<CommitRequest.Response>enqueueRequest(request, (CommitRequest.Response aResponse) -> {
                doWork.run();
                onSuccess.accept(aResponse.getUpdated());
            }, (Exception aException) -> {
                rollback();
                if (onFailure != null) {
                    onFailure.accept(aException);
                }
            });
            return 0;
        } else {
            try {
                CommitRequest.Response response = conn.executeRequest(request);
                doWork.run();
                return response.getUpdated();
            } catch (Exception ex) {
                rollback();
                throw ex;
            }
        }
    }

    protected void rollback() {
        try {
            changeLog.clear();
            for (TransactionListener l : transactionListeners.toArray(new TransactionListener[]{})) {
                try {
                    l.rolledback();
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(PlatypusClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String login(String aUserName, char[] aPassword, Consumer<String> onSuccess, Consumer<Exception> onFailure) throws LoginException {
        LoginRequest rq = new LoginRequest(aUserName, aPassword != null ? new String(aPassword) : null);
        try {
            if (onSuccess != null) {
                conn.enqueueRequest(rq, (LoginRequest.Response aResponse) -> {
                    String sessionId = aResponse.getSessionId();
                    conn.setLoginCredentials(aUserName, aPassword != null ? new String(aPassword) : null, sessionId);
                    principal = new AppPlatypusPrincipal(aUserName, conn);
                    onSuccess.accept(sessionId);
                }, onFailure);
                return null;
            } else {
                LoginRequest.Response response = conn.executeRequest(rq);
                String sessionId = response.getSessionId();
                conn.setLoginCredentials(aUserName, aPassword != null ? new String(aPassword) : null, sessionId);
                principal = new AppPlatypusPrincipal(aUserName, conn);
                return sessionId;
            }
        } catch (Exception ex) {
            Logger.getLogger(PlatypusClient.class.getName()).log(Level.SEVERE, null, ex);
            throw new LoginException(ex.getMessage());
        }
    }

    public void logout(Consumer<Void> onSuccess, Consumer<Exception> onFailure) throws Exception {
        LogoutRequest request = new LogoutRequest();
        if (onSuccess != null) {
            conn.<LogoutRequest.Response>enqueueRequest(request, (LogoutRequest.Response aResponse) -> {
                onSuccess.accept(null);
            }, (Exception aException) -> {
                if (onFailure != null) {
                    onFailure.accept(aException);
                }
            });
        } else {
            conn.executeRequest(request);
        }
    }

    public void shutdown() {
        if (conn != null) {
            conn.shutdown();
            conn = null;
        }
    }

    protected static KeyManager[] createKeyManagers() throws NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException, FileNotFoundException, IOException, CertificateException, UnrecoverableKeyException, URISyntaxException {
        KeyStore ks = KeyStore.getInstance(DEFAULT_CETRS_STORE_TYPE);
        // get user password and file input stream
        char[] password = DEFAULT_KEYSTORE_PASSWORD.toCharArray();
        File keyStoreFile = new File(StringUtils.join(File.separator, System.getProperty(ClientConstants.USER_HOME_PROP_NAME), ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME, SECURITY_SUBDIRECTORY, "keystore"));
        if (!keyStoreFile.exists()) {
            File keyPath = new File(StringUtils.join(File.separator, System.getProperty(ClientConstants.USER_HOME_PROP_NAME), ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME, SECURITY_SUBDIRECTORY));
            keyPath.mkdirs();
            keyStoreFile.createNewFile();
            try (OutputStream keyOut = new FileOutputStream(keyStoreFile); InputStream keyIn = PlatypusClient.class.getResourceAsStream("emptyKeystore")) {
                byte[] resData = BinaryUtils.readStream(keyIn, -1);
                keyOut.write(resData);
            }
        }
        if (keyStoreFile.exists()) {
            try (InputStream is = new FileInputStream(keyStoreFile)) {
                ks.load(is, password);
            }
            final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(ks, password);
            return keyManagerFactory.getKeyManagers();
        } else {
            throw new FileNotFoundException(KEYSTORE_MISING_MSG);
        }
    }

    protected static TrustManager[] createTrustManagers() throws NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException, FileNotFoundException, IOException, CertificateException, URISyntaxException {
        KeyStore ks = KeyStore.getInstance(DEFAULT_CETRS_STORE_TYPE);
        char[] password = getTrustStorePassword();
        File trustStore = new File(StringUtils.join(File.separator, System.getProperty(ClientConstants.USER_HOME_PROP_NAME), ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME, SECURITY_SUBDIRECTORY, "truststore"));
        if (!trustStore.exists()) {
            File trustPath = new File(StringUtils.join(File.separator, System.getProperty(ClientConstants.USER_HOME_PROP_NAME), ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME, SECURITY_SUBDIRECTORY));
            trustPath.mkdirs();
            trustStore.createNewFile();
            try (OutputStream trustOut = new FileOutputStream(trustStore); InputStream trustIn = PlatypusClient.class.getResourceAsStream("emptyTruststore")) {
                byte[] resData = BinaryUtils.readStream(trustIn, -1);
                trustOut.write(resData);
            }
        }
        if (trustStore.exists()) {
            try (InputStream is = new FileInputStream(trustStore)) {
                ks.load(is, password);
            }
            final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(DEFAULT_TRUST_ALGORITHM);
            trustManagerFactory.init(ks);
            return wrapTrustManagers(ks, trustManagerFactory.getTrustManagers());
        } else {
            throw new FileNotFoundException(TRUSTSTORE_MISSING_MSG);
        }
    }

    public static SSLContext createSSLContext() throws NoSuchAlgorithmException, KeyManagementException, NoSuchProviderException, KeyStoreException, FileNotFoundException, IOException, CertificateException, UnrecoverableKeyException, URISyntaxException {
        SSLContext context = SSLContext.getInstance(DEFAULT_SSL_PROTOCOL);
        context.init(createKeyManagers(), createTrustManagers(), SecureRandom.getInstance(DEFAULT_SECURE_RANDOM_ALGORITHM));
        return context;
    }

    private static TrustManager[] wrapTrustManagers(KeyStore aKeyStore, TrustManager[] aTrustManagers) {
        return new TrustManager[]{new PlatypusTrustManager(aKeyStore, aTrustManagers)};
    }

    private static class PlatypusTrustManager implements X509TrustManager {

        protected KeyStore keyStore;
        protected TrustManager[] defaultTrustManagers;

        public PlatypusTrustManager(KeyStore aKeyStore, TrustManager[] aDefaultTrustManagers) {
            super();
            keyStore = aKeyStore;
            defaultTrustManagers = aDefaultTrustManagers;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] aCertsChain, String aAlgotithm) throws CertificateException {
            throw new UnsupportedOperationException("This client implementation can only check if server is trusted.");
        }

        @Override
        public void checkServerTrusted(X509Certificate[] aCertsChain, String aAlgotithm) throws CertificateException {
            try {
                for (TrustManager tm : defaultTrustManagers) {
                    if (tm instanceof X509TrustManager) {
                        ((X509TrustManager) tm).checkServerTrusted(aCertsChain, aAlgotithm);
                    }
                }
            } catch (Exception ex) {
                int userChoice = JOptionPane.showOptionDialog(
                        null,
                        clientLocalizations.getString("ssl.server.certificate.bad"),
                        clientLocalizations.getString("ssl.dialog.title"),
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        new Object[]{
                            clientLocalizations.getString("ssl.server.certificate.accept"),
                            clientLocalizations.getString("ssl.server.certificate.acceptsave"),
                            clientLocalizations.getString("ssl.server.certificate.reject")
                        },
                        clientLocalizations.getString("ssl.server.certificate.accept"));
                if (userChoice == JOptionPane.CANCEL_OPTION)// Reject
                {
                    throw ex;
                } else {
                    try {
                        for (X509Certificate aCertsChain1 : aCertsChain) {
                            String alias = generateUniqueAlias(keyStore);
                            keyStore.setCertificateEntry(alias, aCertsChain1);
                        }
                        if (userChoice == JOptionPane.NO_OPTION) {
                            // Save accepted
                            char[] password = getTrustStorePassword();
                            File trustStore = new File(StringUtils.join(File.separator, System.getProperty(ClientConstants.USER_HOME_PROP_NAME), ClientConstants.USER_HOME_PLATYPUS_DIRECTORY_NAME, SECURITY_SUBDIRECTORY, "truststore"));
                            if (trustStore.exists()) {
                                try (FileOutputStream out = new FileOutputStream(trustStore)) {
                                    keyStore.store(out, password);
                                }
                            }
                        }//else if(userChoice == JOptionPane.YES_OPTION)// Accept is no op. Nothing to save and nothing to throw
                    } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException ex1) {
                        throw new CertificateException(ex1);
                    }
                }
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            List<X509Certificate> accepted = new ArrayList<>();
            for (TrustManager tm : defaultTrustManagers) {
                if (tm instanceof X509TrustManager) {
                    X509Certificate[] laccepted = ((X509TrustManager) tm).getAcceptedIssuers();
                    accepted.addAll(Arrays.asList(laccepted));
                }
            }
            return accepted.toArray(new X509Certificate[0]);
        }

        private String generateUniqueAlias(KeyStore aKeyStore) throws KeyStoreException {
            int nameCounter = 0;
            String generated = ACCEPTED_CERTSALIAS_PREFIX;
            while (aKeyStore.containsAlias(generated)) {
                nameCounter++;
                generated = ACCEPTED_CERTSALIAS_PREFIX + nameCounter;
            }
            return generated;
        }
    }

    public void enqueueUpdate(String aQueryId, Parameters aParams) throws Exception {
        Command command = new Command(aQueryId);
        command.parameters = new ChangeValue[aParams.getParametersCount()];
        for (int i = 0; i < command.parameters.length; i++) {
            Parameter p = aParams.get(i + 1);
            command.parameters[i] = new ChangeValue(p.getName(), p.getValue(), p.getTypeInfo());
        }
        changeLog.add(command);
    }
}
