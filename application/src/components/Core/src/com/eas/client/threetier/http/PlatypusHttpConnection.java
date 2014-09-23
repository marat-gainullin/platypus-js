/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import com.eas.client.threetier.requests.ErrorResponse;
import com.eas.client.threetier.PlatypusConnection;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kl, mg refactoring
 */
public class PlatypusHttpConnection extends PlatypusConnection implements HttpRequestSender.Authenticator {

    private final URL url;
    private boolean authenticated;
    protected Map<String, Cookie> cookies = new ConcurrentHashMap<>();
    private final ExecutorService requestsSender = Executors.newCachedThreadPool();

    public PlatypusHttpConnection(URL aUrl) throws Exception {
        super();
        url = aUrl;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public <R extends Response> void enqueueRequest(Request rq, Consumer<R> onSuccess, Consumer<Exception> onFailure) {
        enqueue(new RequestCallback(rq, (Response aResponse) -> {
            if (aResponse instanceof ErrorResponse) {
                if (onFailure != null) {
                    onFailure.accept(handleErrorResponse((ErrorResponse) aResponse));
                }
            } else {
                if (onSuccess != null) {
                    onSuccess.accept((R) aResponse);
                }
            }
        }), onFailure);
    }

    private void enqueue(RequestCallback rqc, Consumer<Exception> onFailure) {
        requestsSender.submit(() -> {
            try {
                HttpRequestSender httpSender = new HttpRequestSender(url, cookies, login, password, this, 1);
                rqc.request.accept(httpSender);// wait complition analog
                rqc.request.setDone(true);
                if (rqc.onComplete != null) {
                    rqc.onComplete.accept(httpSender.getResponse());
                } else {
                    rqc.response = httpSender.getResponse();
                    rqc.notifyAll();
                }
            } catch (Exception ex) {
                Logger.getLogger(PlatypusHttpConnection.class.getName()).log(Level.SEVERE, null, ex);
                if (onFailure != null) {
                    onFailure.accept(ex);
                }
            }
        });
    }

    @Override
    public <R extends Response> R executeRequest(Request rq) throws Exception {
        RequestCallback rqc = new RequestCallback(rq, null);
        enqueue(rqc, null);
        rqc.waitCompletion();
        if (rqc.response instanceof ErrorResponse) {
            throw handleErrorResponse((ErrorResponse) rqc.response);
        } else {
            return (R) rqc.response;
        }
    }

    @Override
    public void shutdown() {
        requestsSender.shutdown();
    }

    @Override
    public synchronized void authenticate(Callable<Void> onAuthenticate, Callable<Void> onAuthenticated) throws Exception {
        if (!authenticated) {
            onAuthenticate.call();
            authenticated = true;
        } else {
            onAuthenticated.call();
        }
    }

}
