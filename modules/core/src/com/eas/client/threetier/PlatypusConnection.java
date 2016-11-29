/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.eas.client.threetier.requests.ExceptionResponse;
import com.eas.client.threetier.platypus.PlatypusPlatypusConnection;
import com.eas.client.AppConnection;
import com.eas.client.ClientConstants;
import com.eas.client.login.Credentials;
import com.eas.client.threetier.requests.AccessControlExceptionResponse;
import com.eas.client.threetier.requests.JsonExceptionResponse;
import com.eas.client.threetier.requests.SqlExceptionResponse;
import com.eas.script.JsObjectException;
import com.eas.script.Scripts;
import com.eas.util.BinaryUtils;
import com.eas.util.StringUtils;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.AccessControlException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.AuthPermission;
import javax.swing.JOptionPane;

/**
 *
 * @author kl, mg refactoring
 */
public abstract class PlatypusConnection implements AppConnection {

    public static final ResourceBundle clientLocalizations = ResourceBundle.getBundle("com/eas/client/threetier/clientlocalizations");
    // local disk paths
    public static final String SECURITY_SUBDIRECTORY = "security";
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
    // error messages
    public static final String ACCESSCONTROL_EXCEPTION_LOG_MSG = "AccessControlException from server. {0}";
    public static final String SQL_EXCEPTION_LOG_MSG = "SQLException from server. Message: {0}. SqlState: {1}. SqlError code: {2}.";
    public static final String KEYSTORE_MISING_MSG = "Can't locate key store file. May be bad installation.";
    public static final String TRUSTSTORE_MISSING_MSG = "Can't locate trust store file. May be bad installation.";
    // misc
    public static final int DEFAULT_MAX_THREADS = 25;

    protected final URL url;
    protected final String sourcePath;
    protected Credentials credentials;
    protected Callable<Credentials> onCredentials;
    protected int maximumAuthenticateAttempts = 1;

    protected static class Attempts {

        public Attempts() {
            super();
        }
        public int count;
    }

    public PlatypusConnection(URL aUrl, String aSourcePath, Callable<Credentials> aOnCredentials, int aMaximumAuthenticateAttempts) {
        super();
        url = aUrl;
        sourcePath = aSourcePath;
        onCredentials = aOnCredentials;
        maximumAuthenticateAttempts = Math.max(1, aMaximumAuthenticateAttempts);
    }

    public URL getUrl() {
        return url;
    }

    public Exception handleErrorResponse(ExceptionResponse aResponse, Scripts.Space aSpace) {
        if (aResponse instanceof SqlExceptionResponse) {
            SqlExceptionResponse errorResponse = (SqlExceptionResponse) aResponse;
            Logger.getLogger(PlatypusPlatypusConnection.class.getName()).log(Level.WARNING, SQL_EXCEPTION_LOG_MSG, new Object[]{aResponse.getErrorMessage(), errorResponse.getSqlState(), errorResponse.getSqlErrorCode()});
            return new SQLException(aResponse.getErrorMessage(), errorResponse.getSqlState(), errorResponse.getSqlErrorCode());
        } else if (aResponse instanceof AccessControlExceptionResponse) {
            AccessControlExceptionResponse errorResponse = (AccessControlExceptionResponse) aResponse;
            Logger.getLogger(PlatypusPlatypusConnection.class.getName()).log(Level.WARNING, ACCESSCONTROL_EXCEPTION_LOG_MSG, new Object[]{errorResponse.getErrorMessage()});
            return new AccessControlException(errorResponse.getErrorMessage(), errorResponse.isNotLoggedIn() ? new AuthPermission("*") : null);
        } else if (aResponse instanceof JsonExceptionResponse) {
            JsonExceptionResponse errorResponse = (JsonExceptionResponse) aResponse;
            return new JsObjectException(aSpace.parseJsonWithDates(errorResponse.getJsonContent()));
        } else {
            String msg = "Exception from server. " + aResponse.getErrorMessage();
            Logger.getLogger(PlatypusConnection.class.getName()).log(Level.WARNING, msg);
            return new Exception(msg);
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
            try (OutputStream keyOut = new FileOutputStream(keyStoreFile); InputStream keyIn = PlatypusConnection.class.getResourceAsStream("defaultKeystore")) {
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
            try (OutputStream trustOut = new FileOutputStream(trustStore); InputStream trustIn = PlatypusConnection.class.getResourceAsStream("emptyTruststore")) {
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
        //return SSLContext.getDefault();
        SSLContext context = SSLContext.getInstance(DEFAULT_SSL_PROTOCOL);
        context.init(createKeyManagers(), createTrustManagers(), SecureRandom.getInstance(DEFAULT_SECURE_RANDOM_ALGORITHM));
        return context;
    }

    private static TrustManager[] wrapTrustManagers(KeyStore aKeyStore, TrustManager[] aTrustManagers) {
        return new TrustManager[]{new PlatypusTrustManager(aKeyStore, aTrustManagers)};
    }

    private static char[] getTrustStorePassword() {
        char[] password = DEFAULT_TRUSTSTORE_PASSWORD.toCharArray();
        return password;
    }

    private static class PlatypusTrustManager implements X509TrustManager {

        private static class Choice {

            public int choice;
        }

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
        public synchronized void checkServerTrusted(X509Certificate[] aCertsChain, String aAlgotithm) throws CertificateException {
            try {
                for (TrustManager tm : defaultTrustManagers) {
                    if (tm instanceof X509TrustManager) {
                        ((X509TrustManager) tm).checkServerTrusted(aCertsChain, aAlgotithm);
                    }
                }
            } catch (Exception ex) {
                Callable<Integer> uiChoicer = () -> {
                    return JOptionPane.showOptionDialog(
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
                };
                int userChoice = JOptionPane.CANCEL_OPTION;
                try {
                    if (EventQueue.isDispatchThread()) {
                        userChoice = uiChoicer.call();
                    } else {
                        Choice ch = new Choice();
                        ch.choice = userChoice;
                        EventQueue.invokeAndWait(() -> {
                            try {
                                ch.choice = uiChoicer.call();
                            } catch (Exception _ex) {
                                // no op
                            }
                        });
                        userChoice = ch.choice;
                    }
                } catch (Exception _ex) {
                    // no op
                }
                if (userChoice == JOptionPane.CANCEL_OPTION) {// Reject
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
                        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(DEFAULT_TRUST_ALGORITHM);
                        trustManagerFactory.init(keyStore);
                        defaultTrustManagers = trustManagerFactory.getTrustManagers();
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
}
