/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.login.AppPlatypusPrincipal;
import com.eas.client.threetier.requests.AppElementChangedRequest;
import com.eas.client.threetier.requests.LoginRequest;
import com.eas.client.threetier.requests.LogoutRequest;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;

/**
 *
 * @author mg
 */
public class PlatypusNativeClient extends PlatypusClient {

    private PlatypusNativeConnection conn;

    public PlatypusNativeClient(String aUrl) throws Exception {
        super(aUrl);
        URI uri = new URI(url);
        conn = new PlatypusNativeConnection(createSSLContext().getSocketFactory(), uri.getHost(), uri.getPort());
        conn.connect();
        conn.createExchangeThreads();
    }

    @Override
    public void executeRequest(Request aRequest) throws Exception {
        conn.executeRequest(aRequest);
    }

    public PlatypusNativeConnection getConnection() {
        return conn;
    }

    @Override
    public String login(String aUserName, char[] aPassword) throws LoginException {
        LoginRequest rq = new LoginRequest(IDGenerator.genID(), aUserName, new String(aPassword));
        try {
            conn.executeRequest(rq);
            String sessionId = ((LoginRequest.Response) rq.getResponse()).getSessionId();
            conn.setLoginCredentials(aUserName, new String(aPassword), sessionId);
            principal = new AppPlatypusPrincipal(aUserName, this);
            return sessionId;
        } catch (Exception ex) {
            Logger.getLogger(PlatypusNativeClient.class.getName()).log(Level.SEVERE, null, ex);
            throw new LoginException(ex.getMessage());
        }
    }

    @Override
    public void logout() throws Exception {
        LogoutRequest rq = new LogoutRequest(IDGenerator.genID());
        conn.enqueueRequest(rq);
        conn.setLoginCredentials(null, null, null);
        principal = null;
    }

    @Override
    public void shutdown() {
        if (conn != null) {
            conn.shutdown();
            conn = null;
        }
    }

    @Override
    public void appEntityChanged(String aEntityId) {
        AppElementChangedRequest request = new AppElementChangedRequest(IDGenerator.genID(), null, aEntityId);
        try {
            conn.enqueueRequest(request);
            if (aEntityId != null) {
                getAppCache().remove(aEntityId);
            } else {
                getAppCache().clear();
            }
        } catch (Exception ex) {
            Logger.getLogger(PlatypusNativeClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
