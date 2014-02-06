/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.threetier.ErrorResponse;
import com.eas.client.threetier.HelloRequest;
import com.eas.client.threetier.PlatypusConnection;
import com.eas.client.threetier.Request;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.FailedLoginException;
import sun.misc.BASE64Encoder;

/**
 *
 * @author kl, mg refactoring
 */
public class PlatypusHttpConnection extends PlatypusConnection {

    private String url;
    private Map<String, Object> params;
    private HttpURLConnection conn;
    private String urlPrefix;
    private String method;
    protected Map<String, Cookie> cookies = new HashMap<>();

    public PlatypusHttpConnection(String aUrl) throws Exception {
        super();
        url = aUrl;
        params = new HashMap<>();
    }

    public void setUrl(String aUrl) {
        url = aUrl;
    }

    public void setHttpConnection(HttpURLConnection aConnection) {
        conn = aConnection;
    }

    public HttpURLConnection getHttpConnection() {
        return conn;
    }

    @Override
    public void executeRequest(Request aRequest) throws Exception {
        HttpRequestSender sender = new HttpRequestSender(this);
        aRequest.accept(sender);
        params.clear();
        urlPrefix = null;
        if (aRequest.getResponse() instanceof ErrorResponse) {
            handleErrorResponse((ErrorResponse) aRequest.getResponse());
        }
    }

    @Override
    public void disconnect() {
        if (conn != null) {
            conn.disconnect();
            conn = null;
        }
        params.clear();
        urlPrefix = null;
    }

    public void killSession() {
        sessionId = null;
        password = null;
        login = null;
        if (cookies != null) {
            cookies.clear();
        }
    }

    private void addBasicAuthentication(String aLogin, String aPassword) throws UnsupportedEncodingException {
        if (aLogin != null && !aLogin.isEmpty() && aPassword != null) {
            BASE64Encoder encoder = new BASE64Encoder();
            String requestAuthSting = PlatypusHttpConstants.BASIC_AUTH_NAME + " " + encoder.encode(aLogin.concat(":").concat(aPassword).getBytes(PlatypusHttpConstants.HEADERS_ENCODING_NAME));
            conn.setRequestProperty(PlatypusHttpConstants.HEADER_AUTHORIZATION, requestAuthSting);
        }
    }

    @Override
    public void connect() throws Exception {
        if (conn == null) {
            URL rqUrl = new URL(assembleUrl());
            conn = (HttpURLConnection) rqUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty(PlatypusHttpConstants.HEADER_CONTENTTYPE, PlatypusHttpConstants.CONTENT_TYPE);
            conn.setRequestProperty(PlatypusHttpConstants.HEADER_USER_AGENT, PlatypusHttpConstants.AGENT_NAME);
        }
        addCookies();
        if (login != null && password != null) {
            addBasicAuthentication(login, password);
        }
    }

    private String assembleUrl() {
        StringBuilder buf = new StringBuilder();
        buf.append(url);
        if (urlPrefix != null) {
            buf.append(urlPrefix);
        }
        if (params.size() > 0) {
            buf.append("?");
            int paramsCount = 0;
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getValue() != null) {
                    buf.append(entry.getKey()).append("=").append(entry.getValue());
                    if (paramsCount < params.size() - 1) {
                        buf.append("&");
                    }
                }
                paramsCount++;
            }

        }
        return buf.toString();
    }

    public void setUrlPrefix(String aValue) {
        urlPrefix = aValue;
    }

    public void putParam(String pName, Object pValue) {
        params.put(pName, pValue);
    }

    public Object getParam(String pName) {
        return params.get(pName);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String aMethod) {
        method = aMethod;
    }

    public void authenticate(String aLogin, String aPassword) throws Exception {
        int responseCode = -1;
        String responseMessage;
        String authType = null;
        HelloRequest rq = new HelloRequest(IDGenerator.genID());
        HttpRequestSender sender = new HttpRequestSender(this);
        sender.enqueueRequest(rq);// connection
        try {
            responseCode = conn.getResponseCode();// sending a request
            responseMessage = conn.getResponseMessage();
            authType = conn.getHeaderField(PlatypusHttpConstants.HEADER_WWW_AUTH);
        } finally {
            disconnect();
        }
        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED
                && authType != null) {
            if (authType.toLowerCase().startsWith(PlatypusHttpConstants.BASIC_AUTH_NAME.toLowerCase())) {
                sender.enqueueRequest(rq);// connection
                try {
                    addBasicAuthentication(aLogin, aPassword);
                    responseCode = conn.getResponseCode();// sending a request
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        acceptCookies();
                    } else {
                        if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            throw new FailedLoginException(conn.getResponseMessage() + " " + responseCode);
                        } else {
                            throw new Exception(String.format("Http request error. Response is %d %s", responseCode, conn.getResponseMessage()));
                        }
                    }
                } finally {
                    disconnect();
                }
            } else {
                throw new Exception(String.format("Unsupported authorization schema '%s', only basic schema is supported.", authType));
            }
        } else {
            if (responseCode != HttpURLConnection.HTTP_OK)// Unauthorized access is untypical for platypus application, but it is acceptable
            {
                //throw new Exception(String.format("No Platypus application found at %s", url));
                throw new Exception(String.format("Error %d . %s", responseCode, responseMessage));
            }
        }
    }

    public void acceptCookies() throws ParseException, NumberFormatException {
        Map<String, List<String>> headers = conn.getHeaderFields();
        List<String> cookieHeaders = headers.get(PlatypusHttpConstants.HEADER_SETCOOKIE);
        if (cookieHeaders != null) {
            for (String setCookieHeaderValue : cookieHeaders) {
                try {
                    Cookie cookie = Cookie.parse(setCookieHeaderValue);
                    cookies.put(cookie.getName(), cookie);
                } catch (ParseException | NumberFormatException ex) {
                    Logger.getLogger(PlatypusHttpConnection.class.getName()).log(Level.SEVERE, ex.getMessage());
                    continue;
                }
            }
        }
    }

    private void addCookies() {
        String[] cookiesNames = cookies.keySet().toArray(new String[]{});
        for (String cookieName : cookiesNames) {
            Cookie cookie = cookies.get(cookieName);
            if (cookie.isActual()) {
                String cookieHeaderValue = cookieName + "=" + cookie.getValue();
                conn.addRequestProperty(PlatypusHttpConstants.HEADER_COOKIE, cookieHeaderValue);
            } else {
                cookies.remove(cookieName);
            }
        }
    }
}
