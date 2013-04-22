/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import com.eas.client.settings.PlatypusConnectionSettings;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 *
 * @author mg
 */
public class PlatypusHttpsClient extends PlatypusHttpClient {

    static {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String aHostName, SSLSession aSslSession) {
                    return aHostName.equalsIgnoreCase(aSslSession.getPeerHost());
                }
            });
            HttpsURLConnection.setDefaultSSLSocketFactory(createSSLContext().getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException | NoSuchProviderException | KeyStoreException | CertificateException | UnrecoverableKeyException | URISyntaxException | IOException ex) {
            Logger.getLogger(PlatypusHttpClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PlatypusHttpsClient(PlatypusConnectionSettings aConnectionSettings) throws Exception {
        super(aConnectionSettings);
    }
}
