/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.changes.Command;
import com.bearsoft.rowset.dataflow.FlowProvider;
import com.bearsoft.rowset.dataflow.TransactionListener;
import com.bearsoft.rowset.metadata.Fields;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.AppCache;
import com.eas.client.AppClient;
import com.eas.client.ClientConstants;
import com.eas.client.cache.PlatypusAppCache;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.queries.PlatypusQuery;
import com.eas.client.threetier.requests.*;
import com.eas.util.BinaryUtils;
import com.eas.util.ListenerRegistration;
import com.eas.util.StringUtils;
import java.io.*;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.*;
import javax.swing.JOptionPane;

/**
 *
 * @author kl, mg refactoring
 */
public abstract class PlatypusClient implements AppClient {

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
    private final PlatypusAppCache appCache;
    protected String url;
    protected PlatypusPrincipal principal;
    protected List<Change> changeLog = new ArrayList<>();
    protected Set<TransactionListener> transactionListeners = new HashSet<>();

    public PlatypusClient(String aUrl) throws Exception{
        super();
        url = aUrl;
        appCache = new PlatypusAppCache(this);
    }
    
    @Override
    public String getUrl() {
        return url;
    }
    
    @Override
    public ListenerRegistration addTransactionListener(final TransactionListener aListener) {
        transactionListeners.add(aListener);
        return new ListenerRegistration() {
            @Override
            public void remove() {
                transactionListeners.remove(aListener);
            }
        };
    }

    @Override
    public List<Change> getChangeLog() {
        return changeLog;
    }

    @Override
    abstract public void executeRequest(Request aRequest) throws Exception;

    @Override
    public String getStartAppElement() throws Exception {
        StartAppElementRequest rq = new StartAppElementRequest(IDGenerator.genID());
        executeRequest(rq);
        return ((StartAppElementRequest.Response) rq.getResponse()).getAppElementId();
    }

    @Override
    public FlowProvider createFlowProvider(String aQueryId, Fields aExpectedFields) {
        FlowProvider flowProvider = new PlatypusThreeTierFlowProvider(this, aQueryId, aExpectedFields);
        return flowProvider;
    }

    @Override
    public String createServerModule(String aModuleName) throws Exception {
        Request req = new CreateServerModuleRequest(IDGenerator.genID(), aModuleName);
        executeRequest(req);
        Response resp = req.getResponse();
        assert resp instanceof CreateServerModuleResponse;
        return ((CreateServerModuleResponse) resp).getModuleName();
    }

    @Override
    public void disposeServerModule(String aModuleName) throws Exception {
        DisposeServerModuleRequest req = new DisposeServerModuleRequest(IDGenerator.genID(), aModuleName);
        executeRequest(req);
    }

    @Override
    public Object executeServerModuleMethod(String moduleId, String methodName, Object... arguments) throws Exception {
        final ExecuteServerModuleMethodRequest rq = new ExecuteServerModuleMethodRequest(IDGenerator.genID(), moduleId, methodName, arguments);
        executeRequest(rq);
        final Response rsp = rq.getResponse();
        assert rsp instanceof ExecuteServerModuleMethodRequest.Response;
        return ((ExecuteServerModuleMethodRequest.Response) rsp).getResult();
    }

    @Override
    public PlatypusPrincipal getPrincipal() {
        return principal;
    }

    @Override
    public int commit() throws Exception {
        CommitRequest request = new CommitRequest(IDGenerator.genID(), changeLog);
        try {
            executeRequest(request);
            changeLog.clear();
            for (TransactionListener l : transactionListeners.toArray(new TransactionListener[]{})) {
                try {
                    l.commited();
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusNativeClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return ((CommitRequest.Response) request.getResponse()).getUpdated();
        } catch (Exception ex) {
            rollback();
            throw ex;
        }
    }

    protected void rollback() {
        try {
            changeLog.clear();
            for (TransactionListener l : transactionListeners.toArray(new TransactionListener[]{})) {
                try {
                    l.rolledback();
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusNativeClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(PlatypusNativeClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public synchronized AppCache getAppCache() throws Exception {
        return appCache;
    }

    @Override
    public PlatypusQuery getAppQuery(String aQueryId) throws Exception {
        AppQueryRequest request = new AppQueryRequest(IDGenerator.genID(), aQueryId);
        executeRequest(request);
        PlatypusQuery query = (PlatypusQuery) ((AppQueryResponse) request.getResponse()).getAppQuery();
        query.setClient(this);
        assert aQueryId.equals(query.getEntityId());
        return query;
    }

    @Override
    abstract public void appEntityChanged(String aEntityId) throws Exception;

    @Override
    public void dbTableChanged(String aDbId, String aSchema, String aTable) throws Exception {
        DbTableChangedRequest request = new DbTableChangedRequest(IDGenerator.genID(), aDbId, aSchema, aTable);
        try {
            executeRequest(request);
        } catch (Exception ex) {
            Logger.getLogger(PlatypusNativeClient.class.getName()).log(Level.SEVERE, null, ex);
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

    protected static SSLContext createSSLContext() throws NoSuchAlgorithmException, KeyManagementException, NoSuchProviderException, KeyStoreException, FileNotFoundException, IOException, CertificateException, UnrecoverableKeyException, URISyntaxException {
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
                        // Accept
                        for (int i = 0; i < aCertsChain.length; i++) {
                            String alias = generateUniqueAlias(keyStore);
                            keyStore.setCertificateEntry(alias, aCertsChain[i]);
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

    @Override
    public void enqueueUpdate(String aQueryId, Parameters aParams) throws Exception {
        Command command = new Command(aQueryId);
        command.parameters = new Change.Value[aParams.getParametersCount()];
        for (int i = 0; i < command.parameters.length; i++) {
            Parameter p = aParams.get(i + 1);
            command.parameters[i] = new Change.Value(p.getName(), p.getValue(), p.getTypeInfo());
        }
        changeLog.add(command);
    }

}
