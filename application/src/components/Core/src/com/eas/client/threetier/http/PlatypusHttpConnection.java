/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import com.eas.client.login.Credentials;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.threetier.PlatypusClient;
import com.eas.client.threetier.requests.ErrorResponse;
import com.eas.client.threetier.PlatypusConnection;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.platypus.RequestEnvelope;
import com.eas.concurrent.DeamonThreadFactory;
import com.eas.script.ScriptUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 *
 * @author kl, mg refactoring
 */
public class PlatypusHttpConnection extends PlatypusConnection {

    static {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier((String aHostName, SSLSession aSslSession) -> aHostName.equalsIgnoreCase(aSslSession.getPeerHost()));
            HttpsURLConnection.setDefaultSSLSocketFactory(createSSLContext().getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException | NoSuchProviderException | KeyStoreException | CertificateException | UnrecoverableKeyException | URISyntaxException | IOException ex) {
            Logger.getLogger(PlatypusClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected Map<String, Cookie> cookies = new ConcurrentHashMap<>();
    private final ThreadPoolExecutor requestsSender;

    public PlatypusHttpConnection(URL aUrl, Callable<Credentials> aOnCredentials, int aMaximumAuthenticateAttempts, int aMaximumThreads) throws Exception {
        super(aUrl, aOnCredentials, aMaximumAuthenticateAttempts);
        requestsSender = new ThreadPoolExecutor(0, aMaximumThreads,
                10L, TimeUnit.SECONDS,
                new SynchronousQueue<>(),
                new DeamonThreadFactory("http-client-", false));
        requestsSender.allowCoreThreadTimeOut(true);
    }

    @Override
    public <R extends Response> void enqueueRequest(Request rq, Consumer<R> onSuccess, Consumer<Exception> onFailure) {
        enqueue(new RequestCallback(new RequestEnvelope(rq, null, null, null), (Response aResponse) -> {
            final Object lock = ScriptUtils.getLock() != null ? ScriptUtils.getLock() : this;
            synchronized (lock) {
                if (aResponse instanceof ErrorResponse) {
                    if (onFailure != null) {
                        onFailure.accept(handleErrorResponse((ErrorResponse) aResponse));
                    }
                } else {
                    if (onSuccess != null) {
                        onSuccess.accept((R) aResponse);
                    }
                }
            }
        }), onFailure);
    }

    private void startRequestTask(Runnable aTask) {
        Object closureLock = ScriptUtils.getLock();
        Object closureRequest = ScriptUtils.getRequest();
        Object closureResponse = ScriptUtils.getResponse();
        Object closureSession = ScriptUtils.getSession();
        PlatypusPrincipal closurePrincipal = PlatypusPrincipal.getInstance();
        requestsSender.submit(() -> {
            ScriptUtils.setLock(closureLock);
            ScriptUtils.setRequest(closureRequest);
            ScriptUtils.setResponse(closureResponse);
            ScriptUtils.setSession(closureSession);
            PlatypusPrincipal.setInstance(closurePrincipal);
            try {
                aTask.run();
            } finally {
                ScriptUtils.setLock(null);
                ScriptUtils.setRequest(null);
                ScriptUtils.setResponse(null);
                ScriptUtils.setSession(null);
                PlatypusPrincipal.setInstance(null);
            }
        });
    }

    private void enqueue(RequestCallback rqc, Consumer<Exception> onFailure) {
        startRequestTask(() -> {
            try {
                HttpRequestSender httpSender = new HttpRequestSender(url, cookies, onCredentials, sequence, maximumAuthenticateAttempts);
                rqc.requestEnv.request.accept(httpSender);// wait completion analog
                rqc.requestEnv.request.setDone(true);
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
        RequestCallback rqc = new RequestCallback(new RequestEnvelope(rq, null, null, null), null);
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
}
