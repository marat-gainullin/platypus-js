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
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.concurrent.DeamonThreadFactory;
import com.eas.script.Scripts;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import sun.misc.BASE64Encoder;

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
    protected Credentials basicCredentials;
    //
    private final ThreadPoolExecutor requestsSender;

    public PlatypusHttpConnection(URL aUrl, Callable<Credentials> aOnCredentials, int aMaximumAuthenticateAttempts, int aMaximumThreads) throws Exception {
        super(aUrl, aOnCredentials, aMaximumAuthenticateAttempts);
        requestsSender = new ThreadPoolExecutor(aMaximumThreads, aMaximumThreads,
                1L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new DeamonThreadFactory("http-client-", false));
        requestsSender.allowCoreThreadTimeOut(true);
    }

    public void checkedAddBasicAuthentication(HttpURLConnection aConn) throws UnsupportedEncodingException {
        Credentials creds = basicCredentials; // To avoid multithreading issues
        if (creds != null) {
            addBasicAuthentication(aConn, creds.userName, creds.password);
        }
    }

    public void setBasicCredentials(Credentials aValue) {
        basicCredentials = aValue;
    }

    void reloggedIn() {
        if (onLogin != null) {
            onLogin.run();
        }
    }

    private static void addBasicAuthentication(HttpURLConnection aConnection, String aLogin, String aPassword) throws UnsupportedEncodingException {
        if (aLogin != null && !aLogin.isEmpty() && aPassword != null) {
            BASE64Encoder encoder = new BASE64Encoder();
            String requestAuthSting = PlatypusHttpConstants.BASIC_AUTH_NAME + " " + encoder.encode(aLogin.concat(":").concat(aPassword).getBytes(PlatypusHttpConstants.HEADERS_ENCODING_NAME));
            aConnection.setRequestProperty(PlatypusHttpConstants.HEADER_AUTHORIZATION, requestAuthSting);
        }
    }

    public void acceptCookies(HttpURLConnection aConnection) throws ParseException, NumberFormatException {
        Map<String, List<String>> headers = aConnection.getHeaderFields();
        List<String> cookieHeaders = headers.get(PlatypusHttpConstants.HEADER_SETCOOKIE);
        if (cookieHeaders != null) {
            cookieHeaders.stream().forEach((setCookieHeaderValue) -> {
                try {
                    Cookie cookie = Cookie.parse(setCookieHeaderValue);
                    cookies.put(cookie.getName(), cookie);
                } catch (ParseException | NumberFormatException ex) {
                    Logger.getLogger(PlatypusHttpConnection.class.getName()).log(Level.SEVERE, ex.getMessage());
                }
            });
        }
    }

    public void addCookies(HttpURLConnection aConnection) {
        String[] cookiesNames = cookies.keySet().toArray(new String[]{});
        for (String cookieName : cookiesNames) {
            Cookie cookie = cookies.get(cookieName);
            if (cookie != null && cookie.isActual()) {
                String cookieHeaderValue = cookieName + "=" + cookie.getValue();
                aConnection.addRequestProperty(PlatypusHttpConstants.HEADER_COOKIE, cookieHeaderValue);
            } else {
                cookies.remove(cookieName);
            }
        }
    }

    @Override
    public <R extends Response> void enqueueRequest(Request aRequest, Consumer<R> onSuccess, Consumer<Exception> onFailure) {
        Scripts.incAsyncsCount();
        enqueue(new RequestCallback(new RequestEnvelope(aRequest, null, null, null), (Response aResponse) -> {
            if (aResponse instanceof ErrorResponse) {
                if (onFailure != null) {
                    Exception cause = handleErrorResponse((ErrorResponse) aResponse);
                    if (Scripts.getGlobalQueue() != null) {
                        Scripts.getGlobalQueue().accept(() -> {
                            onFailure.accept(cause);
                        });
                    } else {
                        final Object lock = Scripts.getLock() != null ? Scripts.getLock() : this;
                        synchronized (lock) {
                            onFailure.accept(cause);
                        }
                    }
                }
            } else {
                if (onSuccess != null) {
                    if (Scripts.getGlobalQueue() != null) {
                        Scripts.getGlobalQueue().accept(() -> {
                            if (aRequest instanceof LogoutRequest) {
                                cookies.clear();
                                basicCredentials = null;
                                if (onLogout != null) {
                                    onLogout.run();
                                }
                            }
                            onSuccess.accept((R) aResponse);
                        });
                    } else {
                        final Object lock = Scripts.getLock() != null ? Scripts.getLock() : this;
                        synchronized (lock) {
                            if (aRequest instanceof LogoutRequest) {
                                cookies.clear();
                                basicCredentials = null;
                                if (onLogout != null) {
                                    onLogout.run();
                                }
                            }
                            onSuccess.accept((R) aResponse);
                        }
                    }
                }
            }
        }), onFailure);
    }

    private void startRequestTask(Runnable aTask) {
        Object closureLock = Scripts.getLock();
        Object closureRequest = Scripts.getRequest();
        Object closureResponse = Scripts.getResponse();
        Object closureSession = Scripts.getSession();
        PlatypusPrincipal closurePrincipal = PlatypusPrincipal.getInstance();
        requestsSender.submit(() -> {
            Scripts.setLock(closureLock);
            Scripts.setRequest(closureRequest);
            Scripts.setResponse(closureResponse);
            Scripts.setSession(closureSession);
            PlatypusPrincipal.setInstance(closurePrincipal);
            try {
                aTask.run();
            } finally {
                Scripts.setLock(null);
                Scripts.setRequest(null);
                Scripts.setResponse(null);
                Scripts.setSession(null);
                PlatypusPrincipal.setInstance(null);
            }
        });
    }

    private void enqueue(final RequestCallback rqc, Consumer<Exception> onFailure) {
        Runnable doer = () -> {
            try {
                PlatypusHttpRequestWriter httpSender = new PlatypusHttpRequestWriter(url, cookies, onCredentials, sequence, maximumAuthenticateAttempts, PlatypusHttpConnection.this);
                rqc.requestEnv.request.accept(httpSender);// wait completion analog
                if (rqc.onComplete != null) {
                    rqc.requestEnv.request.setDone(true);
                    rqc.completed = true;
                    rqc.onComplete.accept(httpSender.getResponse());
                } else {
                    synchronized (rqc) {
                        rqc.requestEnv.request.setDone(true);
                        rqc.response = httpSender.getResponse();
                        rqc.completed = true;
                        rqc.notifyAll();
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(PlatypusHttpConnection.class.getName()).log(Level.SEVERE, null, ex);
                if (onFailure != null) {
                    onFailure.accept(ex);
                }
            }
        };
        if (onFailure != null) {
            startRequestTask(doer);
        } else {
            doer.run();
        }
    }

    @Override
    public <R extends Response> R executeRequest(Request rq) throws Exception {
        RequestCallback rqc = new RequestCallback(new RequestEnvelope(rq, null, null, null), null);
        enqueue(rqc, null);
        if (rqc.response instanceof ErrorResponse) {
            throw handleErrorResponse((ErrorResponse) rqc.response);
        } else {
            if (rq instanceof LogoutRequest) {
                cookies.clear();
                basicCredentials = null;
                if (onLogout != null) {
                    onLogout.run();
                }
            }
            return (R) rqc.response;
        }
    }

    @Override
    public void shutdown() {
        requestsSender.shutdown();
    }
}
