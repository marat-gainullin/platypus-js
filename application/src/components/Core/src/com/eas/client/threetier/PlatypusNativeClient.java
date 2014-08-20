/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier;

import com.eas.client.threetier.requests.AppElementChangedRequest;
import com.eas.client.threetier.requests.LogoutRequest;
import java.net.URI;
import java.util.function.Consumer;

/**
 *
 * @author mg
 */
public class PlatypusNativeClient extends PlatypusClient {

    public PlatypusNativeClient(String aUrl) throws Exception {
        super(aUrl, new PlatypusNativeConnection(createSSLContext(), new URI(aUrl).getHost(), new URI(aUrl).getPort()));
    }

    /*
     @Override
     public String login(String aUserName, char[] aPassword) throws LoginException {
     LoginRequest rq = new LoginRequest(IDGenerator.genID(), aUserName, aPassword != null ? new String(aPassword) : null);
     try {
     conn.executeRequest(rq);
     String sessionId = ((LoginRequest.Response) rq.getResponse()).getSessionId();
     conn.setLoginCredentials(aUserName, aPassword != null ? new String(aPassword) : null, sessionId);
     principal = new AppPlatypusPrincipal(aUserName, this);
     return sessionId;
     } catch (Exception ex) {
     Logger.getLogger(PlatypusNativeClient.class.getName()).log(Level.SEVERE, null, ex);
     throw new LoginException(ex.getMessage());
     }
     }
     */
    @Override
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

    @Override
    public void shutdown() {
        if (conn != null) {
            conn.shutdown();
            conn = null;
        }
    }

    @Override
    public void appEntityChanged(String aEntityId, Consumer<Void> onSuccess, Consumer<Exception> onFailure) throws Exception {
        AppElementChangedRequest request = new AppElementChangedRequest(null, aEntityId);
        if (onSuccess != null) {
            conn.<AppElementChangedRequest.Response>enqueueRequest(request, (AppElementChangedRequest.Response aResponse) -> {
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
}
