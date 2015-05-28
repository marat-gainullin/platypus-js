/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import com.eas.client.login.Credentials;
import com.eas.client.threetier.PlatypusClient;
import com.eas.client.threetier.requests.ErrorResponse;
import com.eas.client.threetier.PlatypusConnection;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.Sequence;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.client.threetier.requests.PlatypusResponsesFactory;
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
    // Avoid parallel login 
    protected Sequence sequence = (Callable<Void> aCallable) -> {
        synchronized (this) {
            aCallable.call();
        }
    };
    //
    private final ThreadPoolExecutor requestsSender;

    public PlatypusHttpConnection(URL aUrl, Callable<Credentials> aOnCredentials, int aMaximumAuthenticateAttempts, int aMaximumBIOThreads) throws Exception {
        super(aUrl, aOnCredentials, aMaximumAuthenticateAttempts);
        requestsSender = new ThreadPoolExecutor(0, aMaximumBIOThreads,
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
    public <R extends Response> void enqueueRequest(Request aRequest, Scripts.Space aSpace, Consumer<R> onSuccess, Consumer<Exception> onFailure) throws Exception {
        aSpace.incAsyncsCount();
        requestsSender.submit(() -> {
            PlatypusHttpRequestWriter httpSender = new PlatypusHttpRequestWriter(url, cookies, onCredentials, sequence, maximumAuthenticateAttempts, PlatypusHttpConnection.this);
            try {
                aRequest.accept(httpSender);// bio in a background thread
                aSpace.process(() -> {
                    try {
                        if (httpSender.response instanceof ErrorResponse) {
                            if (onFailure != null) {
                                Exception cause = handleErrorResponse((ErrorResponse) httpSender.response);
                                onFailure.accept(cause);
                            }
                        } else {
                            if (aRequest instanceof LogoutRequest) {
                                cookies.clear();
                                basicCredentials = null;
                                if (onLogout != null) {
                                    onLogout.run();
                                }
                            }
                            if (onSuccess != null) {
                                // Actual response reading in a working thread
                                PlatypusHttpResponseReader reader = new PlatypusHttpResponseReader(aRequest, httpSender.conn, httpSender.responseBody, aSpace);
                                httpSender.response.accept(reader);
                                onSuccess.accept((R) httpSender.response);
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(PlatypusHttpConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            } catch (Exception ex) {
                Logger.getLogger(PlatypusHttpConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    @Override
    public <R extends Response> R executeRequest(Request aRequest) throws Exception {
        PlatypusHttpRequestWriter httpSender = new PlatypusHttpRequestWriter(url, cookies, onCredentials, sequence, maximumAuthenticateAttempts, PlatypusHttpConnection.this);
        aRequest.accept(httpSender);// bio
        PlatypusResponsesFactory responseFactory = new PlatypusResponsesFactory();
        aRequest.accept(responseFactory);
        Response response = responseFactory.getResponse();
        PlatypusHttpResponseReader reader = new PlatypusHttpResponseReader(aRequest, httpSender.conn, httpSender.responseBody, Scripts.getSpace());
        response.accept(reader);
        if (response instanceof ErrorResponse) {
            throw handleErrorResponse((ErrorResponse) response);
        } else {
            if (aRequest instanceof LogoutRequest) {
                cookies.clear();
                basicCredentials = null;
                if (onLogout != null) {
                    onLogout.run();
                }
            }
            return (R) response;
        }
    }

    @Override
    public void shutdown() {
        requestsSender.shutdown();
    }
}
