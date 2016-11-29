/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import com.eas.client.login.AnonymousPlatypusPrincipal;
import com.eas.client.login.Credentials;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.report.Report;
import com.eas.client.settings.SettingsConstants;
import com.eas.client.threetier.PlatypusClient;
import com.eas.client.threetier.requests.ExceptionResponse;
import com.eas.client.threetier.PlatypusConnection;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.client.threetier.requests.RPCRequest;
import com.eas.concurrent.PlatypusThreadFactory;
import com.eas.script.Scripts;
import com.eas.util.BinaryUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
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

    protected Map<String, Cookie> cookies = new HashMap<>();
    private final ThreadPoolExecutor bioExecutor;
    protected boolean basicSchemeMet = false;

    public PlatypusHttpConnection(URL aUrl, String aSourcePath, Callable<Credentials> aOnCredentials, int aMaximumAuthenticateAttempts, int aMaximumBIOThreads) throws Exception {
        super(aUrl, aSourcePath, aOnCredentials, aMaximumAuthenticateAttempts);
        bioExecutor = new ThreadPoolExecutor(aMaximumBIOThreads, aMaximumBIOThreads,
                1L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                new PlatypusThreadFactory("http-client-", false));
        bioExecutor.allowCoreThreadTimeOut(true);
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

    @Override
    public <R extends Response> void enqueueRequest(Request aRequest, Scripts.Space aSpace, Consumer<R> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (Scripts.getContext() != null) {
            Scripts.getContext().incAsyncsCount();
        }
        Consumer<PlatypusHttpRequestWriter> responseHandler = (PlatypusHttpRequestWriter aHttpSender) -> {
            if (aHttpSender.response instanceof ExceptionResponse) {
                if (onFailure != null) {
                    try {
                        PlatypusHttpResponseReader reader = new PlatypusHttpResponseReader(aRequest, aHttpSender.conn, aHttpSender.responseBody);
                        aHttpSender.response.accept(reader);
                        Exception cause = handleErrorResponse((ExceptionResponse) aHttpSender.response, aSpace);
                        onFailure.accept(cause);
                    } catch (Exception ex) {
                        Logger.getLogger(PlatypusHttpConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                if (aRequest instanceof LogoutRequest) {
                    cookies.clear();
                    credentials = null;
                }
                if (onSuccess != null) {
                    try {
                        // Response reading in a working thread because of BIO nature of http client :(
                        PlatypusHttpResponseReader reader = new PlatypusHttpResponseReader(aRequest, aHttpSender.conn, aHttpSender.responseBody);
                        aHttpSender.response.accept(reader);
                        if (aHttpSender.response instanceof RPCRequest.Response && ((RPCRequest.Response) aHttpSender.response).getResult() instanceof URL) {
                            RPCRequest.Response rpcResponse = (RPCRequest.Response) aHttpSender.response;
                            fetchReport(rpcResponse, aSpace, onSuccess, onFailure);
                        } else {
                            onSuccess.accept((R) aHttpSender.response);
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(PlatypusHttpConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        };
        Consumer<Consumer<PlatypusHttpRequestWriter>> requestPusher = (Consumer<PlatypusHttpRequestWriter> aOnComplete) -> {
            Map<String, Cookie> localCookies = new HashMap<>();
            localCookies.putAll(cookies);
            Scripts.LocalContext context = Scripts.getContext();
            bioExecutor.submit(() -> {
                Scripts.setContext(context);
                try {
                    PlatypusHttpRequestWriter httpSender = new PlatypusHttpRequestWriter(url, sourcePath, localCookies, basicSchemeMet ? new Credentials(credentials.userName, credentials.password) : null);
                    aRequest.accept(httpSender);// bio in a background thread
                    aSpace.process(() -> {
                        try {
                            acceptCookies(httpSender.conn);
                            aOnComplete.accept(httpSender);
                        } catch (ParseException | NumberFormatException ex) {
                            Logger.getLogger(PlatypusHttpConnection.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusHttpConnection.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    Scripts.setContext(null);
                }
            });
        };
        Attempts attempts = new Attempts();
        retryRequest(requestPusher, aSpace, responseHandler, attempts);
    }

    private <R extends Response> void fetchReport(RPCRequest.Response rpcResponse, Scripts.Space aSpace, Consumer<R> onSuccess, Consumer<Exception> onFailure) {
        URL reportUrl = (URL) rpcResponse.getResult();
        String reportLocation = reportUrl.getFile();
        Scripts.LocalContext context = Scripts.getContext();
        bioExecutor.submit(() -> {// bio background thread
            Scripts.setContext(context);
            try {
                HttpURLConnection reportConn = (HttpURLConnection) reportUrl.openConnection();
                reportConn.setDoInput(true);
                PlatypusHttpRequestWriter.addCookies(cookies, reportConn);
                if (basicSchemeMet) {
                    PlatypusHttpRequestWriter.addBasicAuthentication(reportConn, credentials.userName, credentials.password);
                }
                try (InputStream in = reportConn.getInputStream()) {
                    byte[] content = BinaryUtils.readStream(in, -1);
                    int slashIdx = reportLocation.lastIndexOf("/");
                    String fileName = reportLocation.substring(slashIdx + 1);
                    if (fileName.contains(".")) {
                        String[] nameFormat = fileName.split("\\.");
                        Report report = new Report(content, nameFormat[nameFormat.length - 1], nameFormat[0]);
                        rpcResponse.setResult(report);
                    } else {
                        Report report = new Report(content, "unknown", fileName);
                        rpcResponse.setResult(report);
                    }
                }
                aSpace.process(() -> {// worker thread
                    try {
                        acceptCookies(reportConn);
                        onSuccess.accept((R) rpcResponse);
                    } catch (ParseException | NumberFormatException ex) {
                        Logger.getLogger(PlatypusHttpConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
            } catch (IOException ex) {
                if (onFailure != null) {
                    aSpace.process(() -> {// worker thread
                        onFailure.accept(ex);
                    });
                } else {
                    Logger.getLogger(PlatypusHttpConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
            } finally {
                Scripts.setContext(null);
            }
        });
    }

    private void retryRequest(Consumer<Consumer<PlatypusHttpRequestWriter>> requestPusher, Scripts.Space aSpace, Consumer<PlatypusHttpRequestWriter> responseHandler, Attempts attempts) {
        Credentials sentCreds = credentials;
        requestPusher.accept((PlatypusHttpRequestWriter resender) -> {
            PlatypusHttpRequestWriter.HttpResult nextResendedRes = resender.getHttpResult();
            if (nextResendedRes.isUnauthorized()) {
                if (attempts.count++ < maximumAuthenticateAttempts) {
                    try {
                        if (credentials != null && !credentials.equals(sentCreds)) {
                            // retry                    
                            retryRequest(requestPusher, aSpace, responseHandler, attempts);
                        } else {
                            Credentials creds = onCredentials.call();
                            if (creds != null) {
                                credentials = creds;
                                if (nextResendedRes.authScheme.toLowerCase().contains(PlatypusHttpConstants.BASIC_AUTH_NAME.toLowerCase())) {
                                    basicSchemeMet = true;
                                    // retry
                                    retryRequest(requestPusher, aSpace, responseHandler, attempts);
                                } else if (PlatypusHttpConstants.FORM_AUTH_NAME.equalsIgnoreCase(nextResendedRes.authScheme)) {
                                    String redirectLocation = nextResendedRes.redirectLocation;
                                    Map<String, Cookie> localCookies = new HashMap<>();
                                    localCookies.putAll(cookies);
                                    Scripts.LocalContext context = Scripts.getContext();
                                    bioExecutor.submit(() -> {// bio background thread
                                        Scripts.setContext(context);
                                        try {
                                            URL securityFormUrl = new URL(url + (url.toString().endsWith("/") ? "" : "/") + redirectLocation);
                                            HttpURLConnection securityFormConn = (HttpURLConnection) securityFormUrl.openConnection();
                                            securityFormConn.setInstanceFollowRedirects(false);
                                            securityFormConn.setDoOutput(true);
                                            securityFormConn.setRequestMethod(PlatypusHttpConstants.HTTP_METHOD_POST);
                                            securityFormConn.setRequestProperty(PlatypusHttpConstants.HEADER_CONTENTTYPE, PlatypusHttpConstants.FORM_CONTENT_TYPE);
                                            PlatypusHttpRequestWriter.addCookies(localCookies, securityFormConn);
                                            String formData = PlatypusHttpConstants.SECURITY_CHECK_USER + "=" + URLEncoder.encode(credentials.userName, SettingsConstants.COMMON_ENCODING) + "&" + PlatypusHttpConstants.SECURITY_CHECK_PASSWORD + "=" + URLEncoder.encode(credentials.password, SettingsConstants.COMMON_ENCODING);
                                            byte[] formDataConent = formData.getBytes(SettingsConstants.COMMON_ENCODING);
                                            securityFormConn.setRequestProperty(PlatypusHttpConstants.HEADER_CONTENTLENGTH, "" + formDataConent.length);
                                            try (OutputStream out = securityFormConn.getOutputStream()) {
                                                out.write(formDataConent);
                                            }
                                            int responseCode = securityFormConn.getResponseCode();
                                            aSpace.process(() -> {
                                                try {
                                                    acceptCookies(securityFormConn);
                                                    // retry
                                                    retryRequest(requestPusher, aSpace, responseHandler, attempts);
                                                } catch (ParseException | NumberFormatException ex) {
                                                    Logger.getLogger(PlatypusHttpConnection.class.getName()).log(Level.SEVERE, null, ex);
                                                }
                                            });
                                        } catch (Exception ex) {
                                            Logger.getLogger(PlatypusHttpConnection.class.getName()).log(Level.SEVERE, null, ex);
                                        } finally {
                                            Scripts.setContext(null);
                                        }
                                    });
                                } else {
                                    Logger.getLogger(PlatypusHttpRequestWriter.class.getName()).log(Level.SEVERE, "Unsupported authorization scheme: {0}", nextResendedRes.authScheme);
                                }
                            } else {// Credentials are inaccessible, so leave things as is...
                                responseHandler.accept(resender);
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(PlatypusHttpConnection.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {// Maximum authentication attempts per request exceeded, so leave things as is...
                    responseHandler.accept(resender);
                }
            } else {
                PlatypusPrincipal.setClientSpacePrincipal(credentials != null ? new PlatypusPrincipal(credentials.userName, null, null, this) : new AnonymousPlatypusPrincipal());
                responseHandler.accept(resender);
            }
        });
    }

    @Override
    public <R extends Response> R executeRequest(Request aRequest) throws Exception {
        PlatypusHttpRequestWriter httpSender = new PlatypusHttpRequestWriter(url, sourcePath, cookies, basicSchemeMet ? credentials : null);
        aRequest.accept(httpSender);// bio
        acceptCookies(httpSender.conn);
        int authenticationAttempts = 0;
        PlatypusHttpRequestWriter.HttpResult res = httpSender.httpResult;
        while (res.isUnauthorized() && onCredentials != null && authenticationAttempts++ < maximumAuthenticateAttempts) {
            credentials = onCredentials.call();
            if (res.authScheme.toLowerCase().contains(PlatypusHttpConstants.BASIC_AUTH_NAME.toLowerCase())) {
                basicSchemeMet = true;
                httpSender = new PlatypusHttpRequestWriter(url, sourcePath, cookies, credentials);
                aRequest.accept(httpSender);// bio
                acceptCookies(httpSender.conn);
            } else if (PlatypusHttpConstants.FORM_AUTH_NAME.equalsIgnoreCase(res.authScheme)) {
                String redirectLocation = res.redirectLocation;
                URL securityFormUrl = new URL(url + (url.toString().endsWith("/") ? "" : "/") + redirectLocation);
                HttpURLConnection securityFormConn = (HttpURLConnection) securityFormUrl.openConnection();
                securityFormConn.setInstanceFollowRedirects(false);
                securityFormConn.setDoOutput(true);
                securityFormConn.setRequestMethod(PlatypusHttpConstants.HTTP_METHOD_POST);
                securityFormConn.setRequestProperty(PlatypusHttpConstants.HEADER_CONTENTTYPE, PlatypusHttpConstants.FORM_CONTENT_TYPE);
                PlatypusHttpRequestWriter.addCookies(cookies, securityFormConn);
                String formData = PlatypusHttpConstants.SECURITY_CHECK_USER + "=" + URLEncoder.encode(credentials.userName, SettingsConstants.COMMON_ENCODING) + "&" + PlatypusHttpConstants.SECURITY_CHECK_PASSWORD + "=" + URLEncoder.encode(credentials.password, SettingsConstants.COMMON_ENCODING);
                byte[] formDataConent = formData.getBytes(SettingsConstants.COMMON_ENCODING);
                securityFormConn.setRequestProperty(PlatypusHttpConstants.HEADER_CONTENTLENGTH, "" + formDataConent.length);
                try (OutputStream out = securityFormConn.getOutputStream()) {
                    out.write(formDataConent);
                }
                int responseCode = securityFormConn.getResponseCode();
                acceptCookies(securityFormConn);
                httpSender = new PlatypusHttpRequestWriter(url, sourcePath, cookies, null);
                aRequest.accept(httpSender);// bio
                acceptCookies(httpSender.conn);
            } else {
                Logger.getLogger(PlatypusHttpRequestWriter.class.getName()).log(Level.SEVERE, "Unsupported authorization scheme: {0}", res.authScheme);
            }
        }
        if (httpSender.response instanceof ExceptionResponse) {
            throw handleErrorResponse((ExceptionResponse) httpSender.response, Scripts.getSpace());
        } else {
            PlatypusHttpResponseReader reader = new PlatypusHttpResponseReader(aRequest, httpSender.conn, httpSender.responseBody);
            httpSender.response.accept(reader);
            if (aRequest instanceof LogoutRequest) {
                cookies.clear();
                credentials = null;
            } else if (httpSender.response instanceof RPCRequest.Response) {
                RPCRequest.Response rpcResponse = (RPCRequest.Response) httpSender.response;
                if (rpcResponse.getResult() instanceof URL) {
                    URL reportUrl = (URL) rpcResponse.getResult();
                    String reportLocation = reportUrl.getFile();
                    HttpURLConnection reportConn = (HttpURLConnection) reportUrl.openConnection();
                    reportConn.setDoInput(true);
                    PlatypusHttpRequestWriter.addCookies(cookies, reportConn);
                    if (basicSchemeMet) {
                        PlatypusHttpRequestWriter.addBasicAuthentication(reportConn, credentials.userName, credentials.password);
                    }
                    try (InputStream in = reportConn.getInputStream()) {
                        byte[] content = BinaryUtils.readStream(in, -1);
                        int slashIdx = reportLocation.lastIndexOf("/");
                        String fileName = reportLocation.substring(slashIdx + 1);
                        if (fileName.contains(".")) {
                            String[] nameFormat = fileName.split("\\.");
                            Report report = new Report(content, nameFormat[nameFormat.length - 1], nameFormat[0]);
                            rpcResponse.setResult(report);
                        } else {
                            Report report = new Report(content, "unknown", fileName);
                            rpcResponse.setResult(report);
                        }
                    }
                    acceptCookies(reportConn);
                }
            }
            return (R) httpSender.response;
        }
    }

    @Override
    public void shutdown() {
        bioExecutor.shutdown();
    }
}
