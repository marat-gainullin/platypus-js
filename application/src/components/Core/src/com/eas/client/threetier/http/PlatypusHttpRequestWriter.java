/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import com.bearsoft.rowset.RowsetConverter;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.login.Credentials;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.settings.SettingsConstants;
import com.eas.client.threetier.Sequence;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.*;
import com.eas.client.threetier.requests.ErrorResponse;
import com.eas.concurrent.CallableConsumer;
import com.eas.script.ScriptUtils;
import com.eas.util.JSONUtils;
import com.eas.util.StringUtils;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jdk.nashorn.internal.runtime.Undefined;

/**
 *
 * @author mg
 */
public class PlatypusHttpRequestWriter implements PlatypusRequestVisitor {

    public static final String J_SECURITY_CHECK_ACTION_NAME = "j_security_check";
    public static final String RESPONSE_MISSING_MSG = "%s must have a response.";
    //
    protected URL url;
    protected Map<String, Cookie> cookies;
    protected Sequence sequence;
    protected int maximumAuthenticateAttempts = 1;
    //
    protected String method = PlatypusHttpConstants.HTTP_METHOD_GET;
    protected String uriPrefix = "application";
    protected List<Map.Entry<String, String>> params = new ArrayList<>();
    protected HttpURLConnection conn;
    protected Response response;
    protected Callable<Credentials> onCredentials;
    protected PlatypusHttpConnection pConn;

    public PlatypusHttpRequestWriter(URL aUrl, Map<String, Cookie> aCookies, Callable<Credentials> aOnCredentials, Sequence aSequence, int aMaximumAuthenticateAttempts, PlatypusHttpConnection aPConn) {
        super();
        url = aUrl;
        cookies = aCookies;
        sequence = aSequence;
        maximumAuthenticateAttempts = aMaximumAuthenticateAttempts;
        onCredentials = aOnCredentials;
        pConn = aPConn;
    }

    private String valueToString(Object aValue) {
        if (aValue != null && !(aValue instanceof Undefined)) {
            if (aValue instanceof Date) {
                SimpleDateFormat sdf = new SimpleDateFormat(PlatypusHttpConstants.HTTP_DATE_FORMAT, Locale.US);
                return sdf.format((Date) aValue);
            } else if (aValue instanceof Number) {
                return StringUtils.formatDouble(((Number) aValue).doubleValue());
            } else {
                return aValue.toString();
            }
        } else {
            return "null";
        }
    }

    public Response getResponse() {
        return response;
    }

    protected static class HttpResult {

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

        private boolean isUnauthorized() {
            return (redirectLocation != null && redirectLocation.toLowerCase().contains(J_SECURITY_CHECK_ACTION_NAME))
                    || (authScheme != null && !authScheme.isEmpty())
                    || responseCode == HttpURLConnection.HTTP_UNAUTHORIZED;
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
            pConn.checkedAddBasicAuthentication(conn);
            conn.setRequestProperty(PlatypusHttpConstants.HEADER_USER_AGENT, PlatypusHttpConstants.AGENT_NAME);
            pConn.addCookies(conn);
            if (lonHeaders != null) {
                lonHeaders.accept(conn);
            }
            // wait a result
            HttpResult res = completeRequest(request);
            conn.disconnect();
            return res;
        };
        HttpResult res = performer.call(onHeaders);
        if (res.isUnauthorized() && res.authScheme != null && !res.authScheme.isEmpty()) {
            sequence.in(() -> {
                // Probably new cookies from another thread...
                res.assign(performer.call(onHeaders));
                if (res.isUnauthorized()) {
                    // nice try :-(
                    int authenticateAttempts = 0;
                    while (res.isUnauthorized() && authenticateAttempts++ < maximumAuthenticateAttempts) {
                        Credentials credentials = onCredentials.call();
                        if (credentials != null) {
                            if (res.authScheme.toLowerCase().contains(PlatypusHttpConstants.BASIC_AUTH_NAME.toLowerCase())) {
                                pConn.setBasicCredentials(credentials);
                                res.assign(performer.call(onHeaders));
                                if (!res.isUnauthorized()) {
                                    PlatypusPrincipal.setClientSpacePrincipal(new PlatypusPrincipal(credentials.userName, null, null, pConn));
                                    pConn.reloggedIn();
                                }
                                return null;
                                //} else if (authScheme.toLowerCase().contains(PlatypusHttpConstants.DIGEST_AUTH_NAME.toLowerCase())) {
                            } else if (PlatypusHttpConstants.FORM_AUTH_NAME.equalsIgnoreCase(res.authScheme)) {
                                String redirectLocation = res.redirectLocation;
                                URL securityFormUrl = new URL(url + (url.toString().endsWith("/") ? "" : "/") + redirectLocation);
                                HttpURLConnection securityFormConn = (HttpURLConnection) securityFormUrl.openConnection();
                                securityFormConn.setInstanceFollowRedirects(false);
                                securityFormConn.setDoOutput(true);
                                securityFormConn.setRequestMethod(PlatypusHttpConstants.HTTP_METHOD_POST);
                                securityFormConn.setRequestProperty(PlatypusHttpConstants.HEADER_CONTENTTYPE, PlatypusHttpConstants.FORM_CONTENT_TYPE);
                                pConn.addCookies(securityFormConn);
                                String formData = PlatypusHttpConstants.SECURITY_CHECK_USER + "=" + URLEncoder.encode(credentials.userName, SettingsConstants.COMMON_ENCODING) + "&" + PlatypusHttpConstants.SECURITY_CHECK_PASSWORD + "=" + URLEncoder.encode(credentials.password, SettingsConstants.COMMON_ENCODING);
                                byte[] formDataConent = formData.getBytes(SettingsConstants.COMMON_ENCODING);
                                securityFormConn.setRequestProperty(PlatypusHttpConstants.HEADER_CONTENTLENGTH, "" + formDataConent.length);
                                try (OutputStream out = securityFormConn.getOutputStream()) {
                                    out.write(formDataConent);
                                }
                                int responseCode = securityFormConn.getResponseCode();
                                pConn.acceptCookies(securityFormConn);
                                res.assign(performer.call(onHeaders));
                                if (!res.isUnauthorized()) {
                                    PlatypusPrincipal.setClientSpacePrincipal(new PlatypusPrincipal(credentials.userName, null, null, pConn));
                                    pConn.reloggedIn();
                                }
                                return null;
                            } else {
                                Logger.getLogger(PlatypusHttpRequestWriter.class.getName()).log(Level.SEVERE, "Unsupported authorization scheme: {0}", res.authScheme);
                                return null;
                            }
                        } else {// Credentials are inaccessible, so leave things as is...
                            authenticateAttempts = Integer.MAX_VALUE;
                        }
                    }
                }
                return null;
            });
        }
    }

    private HttpResult completeRequest(Request aRequest) throws Exception {
        int responseCode = conn.getResponseCode();
        String authScheme = null;
        String redirectLocation = null;
        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
            authScheme = conn.getHeaderField(PlatypusHttpConstants.HEADER_WWW_AUTH);
            response = new ErrorResponse(conn.getResponseCode() + " " + conn.getResponseMessage());
            ((ErrorResponse) response).setAccessControl(true);
            ((ErrorResponse) response).setNotLoggedIn(true);
        } else if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                && conn.getHeaderField(PlatypusHttpConstants.HEADER_LOCATION) != null
                && conn.getHeaderField(PlatypusHttpConstants.HEADER_LOCATION).toLowerCase().contains(PlatypusHttpConstants.SECURITY_REDIRECT_LOCATION.toLowerCase())) {
            redirectLocation = PlatypusHttpConstants.SECURITY_REDIRECT_LOCATION;
            authScheme = PlatypusHttpConstants.FORM_AUTH_NAME;
            response = new ErrorResponse(conn.getResponseCode() + " " + conn.getResponseMessage());
            ((ErrorResponse) response).setAccessControl(true);
            ((ErrorResponse) response).setNotLoggedIn(true);
        } else {
            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
                PlatypusHttpResponseReader reader = new PlatypusHttpResponseReader(aRequest, conn, new RowsetConverter(), pConn);
                if (reader.checkIfSecirutyForm()) {
                    redirectLocation = PlatypusHttpConstants.SECURITY_REDIRECT_LOCATION;
                    authScheme = PlatypusHttpConstants.FORM_AUTH_NAME;
                    response = new ErrorResponse(HttpURLConnection.HTTP_UNAUTHORIZED + "");
                    ((ErrorResponse) response).setAccessControl(true);
                    ((ErrorResponse) response).setNotLoggedIn(true);
                } else {
                    PlatypusResponsesFactory responseFactory = new PlatypusResponsesFactory();
                    aRequest.accept(responseFactory);
                    response = responseFactory.getResponse();
                    response.accept(reader);
                }
            } else {
                Logger.getLogger(PlatypusHttpRequestWriter.class.getName()).log(Level.SEVERE, String.format("Server error %d. %s", conn.getResponseCode(), conn.getResponseMessage()));
                response = new ErrorResponse(conn.getResponseCode() + " " + conn.getResponseMessage());
            }
        }
        pConn.acceptCookies(conn);
        return new HttpResult(responseCode, authScheme, redirectLocation);
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
        params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.MODULE_NAME, rq.getModuleOrResourceName()));
        pushRequest(rq, null);
    }

    @Override
    public void visit(ResourceRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        String encodedResourceName = (new URI(null, null, rq.getResourceName(), null)).toASCIIString();
        String oldUriPrefix = uriPrefix;
        uriPrefix = "app/" + encodedResourceName;
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
        List<String> changes = new ArrayList<>();
        for (Change change : rq.getChanges()) {
            ChangeJSONWriter changeWriter = new ChangeJSONWriter();
            change.accept(changeWriter);
            changes.add(changeWriter.written);
        }
        String bodyText = JSONUtils.a(changes.toArray(new String[]{})).toString();
        pushRequest(rq, (HttpURLConnection aConn) -> {
            aConn.setRequestProperty(PlatypusHttpConstants.HEADER_CONTENTTYPE, PlatypusHttpConstants.JSON_CONTENT_TYPE + ";charset=" + SettingsConstants.COMMON_ENCODING);
            try (OutputStream out = aConn.getOutputStream()) {
                byte[] bodyContent = bodyText.getBytes(SettingsConstants.COMMON_ENCODING);
                out.write(bodyContent);
            } catch (IOException ex) {
                Logger.getLogger(PlatypusHttpRequestWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    @Override
    public void visit(CreateServerModuleRequest rq) throws Exception {
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
        Parameters qParams = rq.getParams();
        for (int i = 0; i < qParams.getParametersCount(); i++) {
            Parameter p = qParams.get(i + 1);
            params.add(new AbstractMap.SimpleEntry<>(p.getName(), valueToString(p.getValue())));
        }
        pushRequest(rq, null);
    }

    @Override
    public void visit(ExecuteServerModuleMethodRequest rq) throws Exception {
        method = PlatypusHttpConstants.HTTP_METHOD_GET;
        params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.TYPE, "" + rq.getType()));
        params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.MODULE_NAME, rq.getModuleName()));
        params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.METHOD_NAME, rq.getMethodName()));
        for (Object oArg : rq.getArguments()) {
            params.add(new AbstractMap.SimpleEntry<>(PlatypusHttpRequestParams.PARAMS_ARRAY, ScriptUtils.toJson(oArg)));
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
