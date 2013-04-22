/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.AppClient;
import com.eas.client.cache.DatabaseMdCache;
import com.eas.client.cache.PlatypusAppCache;
import com.eas.client.login.AppPlatypusPrincipal;
import com.eas.client.settings.PlatypusConnectionSettings;
import com.eas.client.threetier.PlatypusClient;
import com.eas.client.threetier.PlatypusNativeClient;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.requests.AppElementChangedRequest;
import com.eas.client.threetier.requests.LoginRequest;
import com.eas.client.threetier.requests.LogoutRequest;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;

/**
 *
 * @author kl, mg refactoring
 */
public class PlatypusHttpClient extends PlatypusClient implements AppClient {

    private PlatypusHttpConnection conn;

    public PlatypusHttpClient(PlatypusConnectionSettings aConnectionSettings) throws Exception {
        super();
        setSettings(aConnectionSettings);
        setAppCache(new PlatypusAppCache(this));
        conn = new PlatypusHttpConnection(aConnectionSettings.getUrl());
    }

    @Override
    public void executeRequest(Request aRequest) throws Exception {
        getConnection().executeRequest(aRequest);
    }

    @Override
    public String login(String aUserName, char[] aPassword) throws LoginException {
        LoginRequest rq = new LoginRequest(IDGenerator.genID(), aUserName, new String(aPassword));
        try {
            executeRequest(rq);
            String sessionId = String.valueOf(IDGenerator.genID());
            conn.setLoginCredentials(aUserName, String.valueOf(aPassword), sessionId);
            principal = new AppPlatypusPrincipal(aUserName, this);
            return sessionId;
        } catch (Exception ex) {
            Logger.getLogger(PlatypusNativeClient.class.getName()).log(Level.SEVERE, ex.getMessage());
            throw new LoginException(ex.getMessage());
        }
    }

    @Override
    public void logout() throws Exception {
        if (principal != null) {
            LogoutRequest rq = new LogoutRequest(IDGenerator.genID());
            executeRequest(rq);
            principal = null;
            conn.killSession();
        }
    }

    @Override
    public void shutdown() {
        try {
            logout();
        } catch (Exception ex) {
            Logger.getLogger(PlatypusHttpClient.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        if (conn != null) {
            conn.disconnect();
            conn = null;
        }
    }

    @Override
    public void appEntityChanged(String aEntityId) throws Exception {
        AppElementChangedRequest request = new AppElementChangedRequest(IDGenerator.genID(), null, aEntityId);
        try {
            executeRequest(request);
            if (aEntityId != null) {
                getAppCache().remove(aEntityId);
            } else {
                getAppCache().clear();
                for (DatabaseMdCache cache : mdCaches.values()) {
                    cache.clear();
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(PlatypusNativeClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PlatypusHttpConnection getConnection() {
        return conn;
    }
}
