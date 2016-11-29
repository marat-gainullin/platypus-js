/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import com.eas.client.login.Credentials;
import com.eas.client.settings.SettingsConstants;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.*;
import com.eas.client.threetier.requests.ExceptionResponse;
import com.eas.concurrent.CallableConsumer;
import com.eas.util.BinaryUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class PlatypusHttpRequestWriter implements PlatypusRequestVisitor {

    public static final String J_SECURITY_CHECK_ACTION_NAME = "j_security_check";
    public static final String RESPONSE_MISSING_MSG = "%s must have a response.";
    //
    protected URL url;
    protected String sourcePath;
    protected Map<String, Cookie> cookies;
    //
    protected String method = PlatypusHttpConstants.HTTP_METHOD_GET;
    protected String uriPrefix = "application";
    protected List<Map.Entry<String, String>> params = new ArrayList<>();
    protected HttpURLConnection conn;
    protected byte[] responseBody;
    protected Response response;
    protected Credentials basicCredentials;
    protected HttpResult httpResult;

    public PlatypusHttpRequestWriter(URL aUrl, String aSourcePath, Map<String, Cookie> aCookies, Credentials aBasicCredentials) {
        super();
        url = aUrl;
        sourcePath = aSourcePath;
        cookies = aCookies;
        basicCredentials = aBasicCredentials;
    }

    public HttpResult getHttpResult() {
        return httpResult;
    }

    public static class HttpResult {

        public int responseCode;
        public String authScheme;
        public String redirectLocation;

        public HttpResult(int aResponseCode, String aAuthScheme, String aRedirectLocation) {
            super();
            responseCode = aResponseCode;
            authScheme = aAuthScheme;
            redirectLocation = aRedirectLocation;
        }

        public void assign(HttpResult aSource) {
            responseCode = aSource.responseCode;
            authScheme = aSource.authScheme;
            redirectLocation = aSource.redirectLocation;
        }

        public boolean isOk() {
            return responseCode == HttpURLConnection.HTTP_OK;
        }

        public boolean isUnauthorized() {
            return (redirectLocation != null && redirectLocation.toLowerCase().contains(J_SECURITY_CHECK_ACTION_NAME))
                    || (authScheme != null && !authScheme.isEmpty())
                    || responseCode == HttpURLConnection.HTTP_UNAUTHORIZED;
        }
    }

    private void addCookies(HttpURLConnection aConnection) {
        addCookies(cookies, aConnection);
    }

    public static void addCookies(Map<String, Cookie> aCookies, HttpURLConnection aConnection) {
        String[] cookiesNames = aCookies.keySet().toArray(new String[]{});
        for (String cookieName : cookiesNames) {
            Cookie cookie = aCookies.get(cookieName);
            if (cookie != null && cookie.isActual()) {
                String cookieHeaderValue = cookieName + "=" + cookie.getValue();
                aConnection.addRequestProperty(PlatypusHttpConstants.HEADER_COOKIE, cookieHeaderValue);
            } else {
                aCookies.remove(cookieName);
            }
        }
    }

    public static void addBasicAuthentication(HttpURLConnection aConnection, String aLogin, String aPassword) throws UnsupportedEncodingException {
        if (aLogin != null && !aLogin.isEmpty() && aPassword != null) {
            Base64.Encoder encoder = Base64.getEncoder();
            String requestAuthSting = PlatypusHttpConstants.BASIC_AUTH_NAME + " " + encoder.encodeToString(aLogin.concat(":").concat(aPassword).getBytes(PlatypusHttpConstants.HEADERS_ENCODING_NAME));
            aConnection.setRequestProperty(PlatypusHttpConstants.HEADER_AUTHORIZATION, requestAuthSting);
        }
    }

    private void checkedAddBasicAuthentication(HttpURLConnection aConn) throws UnsupportedEncodingException {
        if (basicCredentials != null) {
            addBasicAuthentication(aConn, basicCredentials.userName, basicCredentials.password);
        }
    }

    private void pushRequest(Request request, Consumer<HttpURLConnection> onHeaders) throws Exception {
        CallableConsumer<HttpResult, Consumer<HttpURLConnection>> performer = (Consumer<HttpURLConnection> lonHeaders) -> {
            URL rqUrl = new URL(assembleUrl());
            // create a connection
            conn = (HttpURLConnection) rqUrl.openConnection();
            // setup the connection
            conn.setDefaultUseCaches(false);
            conn.setInstanceFollowRedirects(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // add headers
            checkedAddBasicAuthentication(conn);
            conn.setRequestProperty(PlatypusHttpConstants.HEADER_USER_AGENT, PlatypusHttpConstants.AGENT_NAME);
            addCookies(conn);
            if (lonHeaders != null) {
                lonHeaders.accept(conn);
            }
            // wait a result
            HttpResult res = completeRequest(request);
            conn.disconnect();
            return res;
        };
        httpResult = performer.call(onHeaders);
    }

    private HttpResult completeRequest(Request aRequest) throws Exception {
        int responseCode = conn.getResponseCode();
        String authScheme = null;
        String redirectLocation = null;
        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            authScheme = conn.getHeaderField(PlatypusHttpConstants.HEADER_WWW_AUTH);
            response = new AccessControlExceptionResponse();
            ((AccessControlExceptionResponse) response).setErrorMessage(conn.getResponseCode() + " " + conn.getResponseMessage());
            ((AccessControlExceptionResponse) response).setNotLoggedIn(true);
        } else if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                && conn.getHeaderField(PlatypusHttpConstants.HEADER_LOCATION) != null
                && conn.getHeaderField(PlatypusHttpConstants.HEADER_LOCATION).toLowerCase().contains(PlatypusHttpConstants.SECURITY_REDIRECT_LOCATION.toLowerCase())) {
            redirectLocation = PlatypusHttpConstants.SECURITY_REDIRECT_LOCATION;
            authScheme = PlatypusHttpConstants.FORM_AUTH_NAME;
            response = new AccessControlExceptionResponse();
            ((AccessControlExceptionResponse) response).setErrorMessage(conn.getResponseCode() + " " + conn.getResponseMessage());
            ((AccessControlExceptionResponse) response).setNotLoggedIn(true);
        } else if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
            try (InputStream in = conn.getInputStream()) {
                responseBody = BinaryUtils.readStream(in, -1);
                if (checkIfSecirutyForm(responseBody)) {
                    redirectLocation = PlatypusHttpConstants.SECURITY_REDIRECT_LOCATION;
                    authScheme = PlatypusHttpConstants.FORM_AUTH_NAME;
                    response = new AccessControlExceptionResponse();
                    ((AccessControlExceptionResponse) response).setErrorMessage(HttpURLConnection.HTTP_UNAUTHORIZED + "");
                    ((AccessControlExceptionResponse) response).setNotLoggedIn(true);
                } else {
                    // background bio waiting thread will enqueue responseBody reading to a working thread
                    PlatypusResponsesFactory responseFactory = new PlatypusResponsesFactory();
                    aRequest.accept(responseFactory);
                    response = responseFactory.getResponse();
                }
            }
        } else if (responseCode == HttpURLConnection.HTTP_CONFLICT && conn.getContentType().toLowerCase().startsWith("application/json")) {
            response = new JsonExceptionResponse();
            try (InputStream in = conn.getErrorStream()) {
                responseBody = BinaryUtils.readStream(in, -1);
            }
        } else {
            Logger.getLogger(PlatypusHttpRequestWriter.class.getName()).log(Level.SEVERE, String.format("Server error %d. %s", conn.getResponseCode(), conn.getResponseMessage()));
            response = new ExceptionResponse();
            ((ExceptionResponse) response).setErrorMessage(conn.getResponseCode() + " " + conn.getResponseMessage());
        }
        return new HttpResult(responseCode, authScheme, redirectLocation);
    }

    public boolean checkIfSecirutyForm(byte[] aContent) throws IOException {
        String contentType = conn.getContentType();
        if ("text/html".equalsIgnoreCase(contentType)) {
            String formContent = extractText(aContent);
            return formContent.toLowerCase().contains(PlatypusHttpRequestWriter.J_SECURITY_CHECK_ACTION_NAME);
        } else {
            return false;
        }
    }

    protected String extractText(byte[] aContent) throws IOException {
        String contentType = conn.getContentType();
        String[] contentTypeCharset = contentType.split(";");
        if (contentTypeCharset == null || contentTypeCharset.length == 0) {
            throw new IOException("Response must contain ContentType header with charset");
        }
        if (!contentTypeCharset[0].toLowerCase().startsWith("text/") && !contentTypeCharset[0].toLowerCase().startsWith("application/json")) {
            throw new IOException("Response header 'ContentType' must be text/... or application/json");
        }
        if (contentTypeCharset.length > 1) {
            String[] charsetNameValue = contentTypeCharset[1].split("=");
            if (charsetNameValue == null || charsetNameValue.length != 2) {
                throw new IOException("Response must contain ContentType header with charset=... clause");
            }
            if (!charsetNameValue[0].equalsIgnoreCase("charset")) {
                throw new IOException("Response ContentType must be formatted as following: text/...;charset=...");
            }
            return new String(aContent, charsetNameValue[1].trim());
        } else {
            return new String(aContent, SettingsConstants.COMMON_ENCODING);
        }
    }

    private String assembleUrl() throws UnsupportedEncodingException {
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(url);
        if (!url.toString().endsWith("/")) {
            urlBuilder.append("/");
        }
        if (uriPrefix != null) {
            urlBuilder.append(uriPrefix);
        }
        if (params.size() > 0) {
            urlBuilder.append("?");
            int paramsCount = 0;
            for (Map.Entry<String, String> entry : params) {
                if (entry.getValue() != null) {
                    urlBuilder.append(entry.getKey()).append("=").append(URLEncoder.encode(String.valueOf(entry.getValue()), SettingsConstants.COMMON_ENCODING));
                    if (paramsCount < params.size() - 1) {
                        urlBuilder.append("&");
                    }
                }
                paramsCount++;
            }

        }
        return urlBuilder.toString();
    }

    @Override
    public void visit(AppQueryRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.TYPE, "" + rq.getType()));
        params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.QUERY_ID, rq.getQueryName()));
        pushRequest(rq, rq.getTimeStamp() != null ? (HttpURLConnection aConn) -> {
            aConn.setIfModifiedSince(rq.getTimeStamp().getTime());
        } : null);
    }

    @Override
    public void visit(ModuleStructureRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.TYPE, "" + rq.getType()));
        params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.MODULE_NAME, rq.getModuleName()));
        pushRequest(rq, null);
    }

    @Override
    public void visit(ResourceRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        String encodedResourceName = (new URI(null, null, sourcePath != null && !sourcePath.isEmpty() ? sourcePath + "/" + rq.getResourceName() : rq.getResourceName(), null)).toASCIIString();
        String oldUriPrefix = uriPrefix;
        uriPrefix = encodedResourceName;
        try {
            pushRequest(rq, rq.getTimeStamp() != null ? (HttpURLConnection aConn) -> {
                aConn.setIfModifiedSince(rq.getTimeStamp().getTime());
            } : null);
        } finally {
            uriPrefix = oldUriPrefix;
        }
    }

    @Override
    public void visit(CommitRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_POST;
        params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.TYPE, "" + rq.getType()));
        pushRequest(rq, (HttpURLConnection aConn) -> {
            aConn.setRequestProperty(PlatypusHttpConstants.HEADER_CONTENTTYPE, PlatypusHttpConstants.JSON_CONTENT_TYPE + ";charset=" + SettingsConstants.COMMON_ENCODING);
            try (OutputStream out = aConn.getOutputStream()) {
                byte[] bodyContent = rq.getChangesJson().getBytes(SettingsConstants.COMMON_ENCODING);
                out.write(bodyContent);
            } catch (IOException ex) {
                Logger.getLogger(PlatypusHttpRequestWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    @Override
    public void visit(ServerModuleStructureRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.TYPE, "" + rq.getType()));
        params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.MODULE_NAME, rq.getModuleName()));
        pushRequest(rq, rq.getTimeStamp() != null ? (HttpURLConnection aConn) -> {
            aConn.setIfModifiedSince(rq.getTimeStamp().getTime());
        } : null);
    }

    @Override
    public void visit(DisposeServerModuleRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.TYPE, "" + rq.getType()));
        params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.MODULE_NAME, rq.getModuleName()));
        pushRequest(rq, null);
    }

    @Override
    public void visit(ExecuteQueryRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.TYPE, "" + rq.getType()));
        params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.QUERY_ID, rq.getQueryName()));
        params.addAll(rq.getParamsJsons().entrySet());
        pushRequest(rq, null);
    }

    @Override
    public void visit(RPCRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.TYPE, "" + rq.getType()));
        params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.MODULE_NAME, rq.getModuleName()));
        params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.METHOD_NAME, rq.getMethodName()));
        for (String argJson : rq.getArgumentsJsons()) {
            params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.PARAMS_ARRAY, argJson));
        }
        pushRequest(rq, null);
    }

    @Override
    public void visit(LogoutRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.TYPE, "" + rq.getType()));
        pushRequest(rq, null);
    }

    @Override
    public void visit(CredentialRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.TYPE, "" + rq.getType()));
        pushRequest(rq, null);
    }
}
