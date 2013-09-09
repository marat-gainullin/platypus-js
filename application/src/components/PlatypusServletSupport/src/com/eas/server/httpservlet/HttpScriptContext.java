/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.eas.util.BinaryUtils;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.UniqueTag;

/**
 *
 * @author vv
 */
public class HttpScriptContext extends ScriptableObject {

    private final static String REQUEST_PROP_NAME = "request";//NOI18N
    private final static String RESPONSE_PROP_NAME = "response";//NOI18N
    private final static String HTTP_JS_CLASS_NAME = "Http";//NOI18N
    private Request request;
    private Response response;

    public static HttpScriptContext getInstance(Scriptable scope, HttpServletRequest aHttpRequest, HttpServletResponse aHttpResponse) {
        HttpScriptContext sc = new HttpScriptContext();
        ScriptRuntime.setBuiltinProtoAndParent(sc, scope, TopLevel.Builtins.Object);
        sc.request = Request.getInstance(sc, aHttpRequest);
        sc.response = Response.getInstance(sc, aHttpResponse);
        sc.defineProperty(REQUEST_PROP_NAME, HttpScriptContext.class, READONLY);
        sc.defineProperty(RESPONSE_PROP_NAME, HttpScriptContext.class, READONLY);
        return sc;
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

    @Override
    public String getClassName() {
        return HTTP_JS_CLASS_NAME;
    }

    public static class Request extends ScriptableObject {

        private final static String AUTH_TYPE_PROP_NAME = "authType";//NOI18N
        private final static String CHARACTER_ENCODING_PROP_NAME = "characterEncoding";//NOI18N
        private final static String CONTENT_LENGTH_PROP_NAME = "contentLength";//NOI18N
        private final static String CONTENT_TYPE_PROP_NAME = "contentType";//NOI18N
        private final static String BODY_PROP_NAME = "body";//NOI18N
        private final static String BODY_BUFFER_PROP_NAME = "bodyBuffer";//NOI18N
        private final static String CONTEXT_PATH_PROP_NAME = "contextPath";//NOI18N
        private final static String COOKIES_PROP_NAME = "cookies";//NOI18N
        private final static String HEADERS_PROP_NAME = "headers";//NOI18N
        private final static String LOCAL_ADDR_PROP_NAME = "localAddr";//NOI18N
        private final static String LOCAL_NAME_PROP_NAME = "localName";//NOI18N
        private final static String LOCAL_PORT_PROP_NAME = "localPort";//NOI18N
        private final static String METHOD_PROP_NAME = "method";//NOI18N
        private final static String PARAMS_PROP_NAME = "params";//NOI18N
        private final static String PATH_INFO_PROP_NAME = "pathInfo";//NOI18N
        private final static String PATH_TRANSLATED_PROP_NAME = "pathTranslated";//NOI18N
        private final static String PROTOCOL_PROP_NAME = "protocol";//NOI18N
        private final static String QUERY_STRING_PROP_NAME = "queryString";//NOI18N
        private final static String REMOTE_ADDR_PROP_NAME = "remoteAddr";//NOI18N
        private final static String REMOTE_HOST_PROP_NAME = "remoteHost";//NOI18N
        private final static String REMOTE_PORT_PROP_NAME = "remotePort";//NOI18N
        private final static String REQUEST_URI_PROP_NAME = "requestURI";//NOI18N
        private final static String REQUEST_URL_PROP_NAME = "requestURL";//NOI18N
        private final static String SCHEME_PROP_NAME = "scheme";//NOI18N
        private final static String SERVER_NAME_PROP_NAME = "serverName";//NOI18N
        private final static String SERVER_PORT_PROP_NAME = "serverPort";//NOI18N
        private final static String SECURE_PROP_NAME = "secure";//NOI18N
        private final static String REQUEST_JS_CLASS_NAME = "Request";//NOI18N
        private HttpServletRequest httpRequest;
        private Cookies cookies;
        private RequestHeaders headers;
        private Params params;

        public static Request getInstance(Scriptable scope, HttpServletRequest aHttpRequest) {
            Request r = new Request();
            r.httpRequest = aHttpRequest;
            r.defineProperty(AUTH_TYPE_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(CHARACTER_ENCODING_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(CONTENT_LENGTH_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(CONTENT_TYPE_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(BODY_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(BODY_BUFFER_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(CONTEXT_PATH_PROP_NAME, HttpScriptContext.Request.class, READONLY);

            r.cookies = Cookies.getInstance(scope, r.httpRequest);
            r.defineProperty(COOKIES_PROP_NAME, HttpScriptContext.Request.class, READONLY);

            r.headers = RequestHeaders.getInstance(scope, aHttpRequest);
            r.defineProperty(HEADERS_PROP_NAME, HttpScriptContext.Request.class, READONLY);

            r.defineProperty(LOCAL_ADDR_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(LOCAL_NAME_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(LOCAL_PORT_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(METHOD_PROP_NAME, HttpScriptContext.Request.class, READONLY);

            r.params = Params.getInstance(scope, r.httpRequest);
            r.defineProperty(PARAMS_PROP_NAME, HttpScriptContext.Request.class, READONLY);

            r.defineProperty(PATH_INFO_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(PATH_TRANSLATED_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(PROTOCOL_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(QUERY_STRING_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(REMOTE_ADDR_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(REMOTE_HOST_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(REMOTE_PORT_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(REQUEST_URI_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(REQUEST_URL_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(SCHEME_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(SERVER_NAME_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(SERVER_PORT_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(SECURE_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            ScriptRuntime.setBuiltinProtoAndParent(r, scope, TopLevel.Builtins.Object);
            return r;
        }

        public String getAuthType() {
            return httpRequest.getAuthType();
        }

        public String getCharacterEncoding() {
            return httpRequest.getCharacterEncoding();
        }

        public int getContentLength() {
            return httpRequest.getContentLength();
        }

        public String getContentType() {
            return httpRequest.getContentType();
        }

        public String getBody() throws IOException {
            String encoding = httpRequest.getCharacterEncoding();
            if (encoding == null || encoding.isEmpty()) {
                Logger.getLogger(HttpScriptContext.class.getName()).log(Level.WARNING, "Missing character encoding. Falling back to utf-8.");
                encoding = "utf-8";
            }
            if (Charset.isSupported(encoding)) {
                try (InputStream is = httpRequest.getInputStream()) {
                    byte[] data = BinaryUtils.readStream(is, -1);
                    return new String(data, httpRequest.getCharacterEncoding());
                }
            } else {
                throw new IOException(String.format("Character encoding %s is not supported.", encoding));
            }
        }

        public byte[] getBodyBuffer() throws IOException {
            try (InputStream is = httpRequest.getInputStream()) {
                return BinaryUtils.readStream(is, -1);
            }
        }

        public String getContextPath() {
            return httpRequest.getContextPath();
        }

        public Cookies getCookies() {
            return cookies;
        }

        public RequestHeaders getHeaders() {
            return headers;
        }

        public String getLocalAddr() {
            return httpRequest.getLocalAddr();
        }

        public String getLocalName() {
            return httpRequest.getLocalName();
        }

        public int getLocalPort() {
            return httpRequest.getLocalPort();
        }

        public String getMethod() {
            return httpRequest.getMethod();
        }

        public Params getParams() {
            return params;
        }

        public String getPathInfo() {
            return httpRequest.getPathInfo();
        }

        public String getPathTranslated() {
            return httpRequest.getPathTranslated();
        }

        public String getProtocol() {
            return httpRequest.getProtocol();
        }

        public String getQueryString() {
            return httpRequest.getQueryString();
        }

        public String getRemoteAddr() {
            return httpRequest.getRemoteAddr();
        }

        public String getRemoteHost() {
            return httpRequest.getRemoteHost();
        }

        public int getRemotePort() {
            return httpRequest.getRemotePort();
        }

        public String getRequestURI() {
            return httpRequest.getRequestURI();
        }

        public String getRequestURL() {
            return httpRequest.getRequestURL().toString();
        }

        public String getScheme() {
            return httpRequest.getScheme();
        }

        public String getServerName() {
            return httpRequest.getServerName();
        }

        public int getServerPort() {
            return httpRequest.getServerPort();
        }

        public boolean getSecure() {
            return httpRequest.isSecure();
        }

        @Override
        public String getClassName() {
            return REQUEST_JS_CLASS_NAME;
        }
    }

    public static class Response extends ScriptableObject {

        private final static String HEADERS_PROP_NAME = "headers";//NOI18N
        private final static String STATUS_PROP_NAME = "status";//NOI18N
        private final static String CONTENT_TYPE_PROP_NAME = "contentType";//NOI18N
        private final static String ADD_HEADER_METHOD_NAME = "addHeader";//NOI18N
        private final static String SET_HEADER_METHOD_NAME = "setHeader";//NOI18N
        private final static String ADD_COOKIE_METHOD_NAME = "addCookie";//NOI18N
        private final static String RESPONSE_JS_CLASS_NAME = "Response";//NOI18N
        private HttpServletResponse httpResponse;
        private ResponseHeaders headers;

        public static Response getInstance(Scriptable scope, HttpServletResponse aHttpResponse) {
            Response r = new Response();
            r.httpResponse = aHttpResponse;

            r.headers = ResponseHeaders.getInstance(scope, aHttpResponse);
            r.defineProperty(HEADERS_PROP_NAME, HttpScriptContext.Response.class, READONLY);

            r.defineProperty(STATUS_PROP_NAME, HttpScriptContext.Response.class, EMPTY);
            r.defineProperty(CONTENT_TYPE_PROP_NAME, HttpScriptContext.Response.class, EMPTY);
            r.defineFunctionProperties(new String[]{
                ADD_HEADER_METHOD_NAME,
                SET_HEADER_METHOD_NAME,
                ADD_COOKIE_METHOD_NAME}, Response.class, EMPTY);
            ScriptRuntime.setBuiltinProtoAndParent(r, scope, TopLevel.Builtins.Object);
            return r;
        }

        @Override
        public String getClassName() {
            return RESPONSE_JS_CLASS_NAME;
        }

        public int getStatus() {
            return httpResponse.getStatus();
        }

        public void setStatus(int sc) {
            httpResponse.setStatus(sc);
        }

        public String getContentType() {
            return httpResponse.getContentType();
        }

        public void setContentType(String type) {
            httpResponse.setContentType(type);
        }

        public ResponseHeaders getHeaders() {
            return headers;
        }

        public void addHeader(String name, String value) {
            httpResponse.addHeader(name, value);
        }

        public void setHeader(String name, String value) {
            httpResponse.setHeader(name, value);
        }

        public void addCookie(Object obj) {
            if (obj instanceof Scriptable) {
                Scriptable cookieObj = (Scriptable) obj;
                Object name = cookieObj.get(Cookie.NAME_PROP_NAME, cookieObj);
                Object value = cookieObj.get(Cookie.VALUE_PROP_NAME, cookieObj);
                if (name != null && !UniqueTag.NOT_FOUND.equals(name) && value != null && !UniqueTag.NOT_FOUND.equals(value)) {
                    javax.servlet.http.Cookie httpCookie = new javax.servlet.http.Cookie(name.toString(), value.toString());
                    Object comment = cookieObj.get(Cookie.COMMENT_PROP_NAME, cookieObj);
                    if (comment != null && !UniqueTag.NOT_FOUND.equals(comment)) {
                        httpCookie.setComment(comment.toString());
                    }
                    Object domain = cookieObj.get(Cookie.DOMAIN_PROP_NAME, cookieObj);
                    if (domain != null && !UniqueTag.NOT_FOUND.equals(domain)) {
                        httpCookie.setComment(domain.toString());
                    }
                    Object maxAge = cookieObj.get(Cookie.MAX_AGE_PROP_NAME, cookieObj);
                    if (maxAge != null && !UniqueTag.NOT_FOUND.equals(maxAge)) {
                        Integer maxAgeInt = parseInt(maxAge.toString());
                        if (maxAgeInt != null) {
                            httpCookie.setMaxAge(maxAgeInt);
                        }
                    }
                    Object path = cookieObj.get(Cookie.PATH_PROP_NAME, cookieObj);
                    if (path != null && !UniqueTag.NOT_FOUND.equals(path)) {
                        httpCookie.setPath(path.toString());
                    }
                    Object secure = cookieObj.get(Cookie.SECURE_PROP_NAME, cookieObj);
                    if (secure != null && !UniqueTag.NOT_FOUND.equals(secure)) {
                        httpCookie.setSecure(Boolean.valueOf(secure.toString()));
                    }
                    Object version = cookieObj.get(Cookie.VERSION_PROP_NAME, cookieObj);
                    if (version != null && !UniqueTag.NOT_FOUND.equals(version)) {
                        Integer versionInt = parseInt(version.toString());
                        if (versionInt != null) {
                            httpCookie.setVersion(versionInt);
                        }
                    }
                    httpResponse.addCookie(httpCookie);
                }

            }
        }
    }

    private static Integer parseInt(String str) {
        try {
            return Math.round(Float.parseFloat(str));
        } catch (Exception ex) {
            return null;
        }
    }

    public static class Cookies extends ScriptableObject {

        private final static String COOKIES_JS_CLASS_NAME = "Cookies";//NOI18N

        public static Cookies getInstance(Scriptable scope, HttpServletRequest httpRequest) {
            Cookies cookies = new Cookies();
            javax.servlet.http.Cookie[] httpCookies = httpRequest.getCookies();
            if (httpCookies != null) {
                for (int i = 0; i < httpCookies.length; i++) {
                    Cookie cookie = new Cookie(httpCookies[i]);
                    ScriptRuntime.setBuiltinProtoAndParent(cookie, scope, TopLevel.Builtins.Object);
                    cookies.defineProperty(httpCookies[i].getName(), cookie, READONLY);

                }
            }
            ScriptRuntime.setBuiltinProtoAndParent(cookies, scope, TopLevel.Builtins.Object);
            return cookies;
        }

        @Override
        public String getClassName() {
            return COOKIES_JS_CLASS_NAME;
        }
    }

    public static class RequestHeaders extends ScriptableObject {

        private final static String REQUEST_HEADERS_JS_CLASS_NAME = "RequestHeaders";//NOI18N

        public static RequestHeaders getInstance(Scriptable scope, HttpServletRequest httpRequest) {
            RequestHeaders headers = new RequestHeaders();
            Enumeration<String> headerNames = httpRequest.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                headers.defineProperty(headerName, httpRequest.getHeader(headerName), READONLY);

            }
            ScriptRuntime.setBuiltinProtoAndParent(headers, scope, TopLevel.Builtins.Object);
            return headers;
        }

        @Override
        public String getClassName() {
            return REQUEST_HEADERS_JS_CLASS_NAME;
        }
    }

    public static class ResponseHeaders extends ScriptableObject {

        private final static String RESPONSE_HEADERS_JS_CLASS_NAME = "ResponseHeaders";//NOI18N
        private HttpServletResponse httpResponse;
        private boolean externalUpdateEnabled;

        public static ResponseHeaders getInstance(Scriptable aScope, HttpServletResponse aHttpResponse) {
            ResponseHeaders headers = new ResponseHeaders();
            headers.httpResponse = aHttpResponse;
            Collection<String> headerNames = headers.httpResponse.getHeaderNames();
            for (String headerName : headerNames) {
                headers.defineProperty(headerName, headers.httpResponse.getHeader(headerName), READONLY);
            }
            ScriptRuntime.setBuiltinProtoAndParent(headers, aScope, TopLevel.Builtins.Object);
            headers.externalUpdateEnabled = true;
            return headers;
        }

        @Override
        public void put(String name, Scriptable start, Object value) {
            super.put(name, start, value);
            if (externalUpdateEnabled) {
                if (httpResponse.getHeader(name) != null) {
                    httpResponse.setHeader(name, value.toString());
                } else {
                    httpResponse.addHeader(name, value.toString());
                }
            }
        }

        @Override
        public String getClassName() {
            return RESPONSE_HEADERS_JS_CLASS_NAME;
        }
    }

    public static class Params extends ScriptableObject {

        private final static String PARAMS_JS_CLASS_NAME = "Params";//NOI18N

        private static Params getInstance(Scriptable scope, HttpServletRequest httpRequest) {
            Params params = new Params();
            for (String paramName : httpRequest.getParameterMap().keySet()) {
                String[] paramValues = httpRequest.getParameterValues(paramName);
                if (paramValues.length == 1) {
                    params.defineProperty(paramName, httpRequest.getParameter(paramName), READONLY);
                } else {
                    params.defineProperty(paramName, javaToJS(paramValues, scope), READONLY);
                }
            }
            ScriptRuntime.setBuiltinProtoAndParent(params, scope, TopLevel.Builtins.Object);
            return params;
        }

        private static Object javaToJS(Object obj, Scriptable scope) {
            Context cx = Context.enter();
            try {
                return Context.javaToJS(obj, scope);
            } finally {
                Context.exit();
            }
        }

        @Override
        public String getClassName() {
            return PARAMS_JS_CLASS_NAME;
        }
    }

    public static class Cookie extends ScriptableObject {

        private final static String NAME_PROP_NAME = "name";//NOI18N
        private final static String VALUE_PROP_NAME = "value";//NOI18N
        private final static String COMMENT_PROP_NAME = "comment";//NOI18N
        private final static String DOMAIN_PROP_NAME = "domain";//NOI18N
        private final static String MAX_AGE_PROP_NAME = "maxAge";//NOI18N
        private final static String PATH_PROP_NAME = "path";//NOI18N
        private final static String SECURE_PROP_NAME = "secure";//NOI18N
        private final static String VERSION_PROP_NAME = "version";//NOI18N
        private final static String COOKIE_JS_CLASS_NAME = "Cookie";//NOI18N
        private javax.servlet.http.Cookie cookie;

        public Cookie(javax.servlet.http.Cookie aCookie) {
            cookie = aCookie;
            defineProperty(COMMENT_PROP_NAME, HttpScriptContext.Cookie.class, EMPTY);
            defineProperty(DOMAIN_PROP_NAME, HttpScriptContext.Cookie.class, EMPTY);
            defineProperty(MAX_AGE_PROP_NAME, HttpScriptContext.Cookie.class, EMPTY);
            defineProperty(NAME_PROP_NAME, HttpScriptContext.Cookie.class, READONLY);
            defineProperty(PATH_PROP_NAME, HttpScriptContext.Cookie.class, EMPTY);
            defineProperty(SECURE_PROP_NAME, HttpScriptContext.Cookie.class, EMPTY);
            defineProperty(VALUE_PROP_NAME, HttpScriptContext.Cookie.class, EMPTY);
            defineProperty(VERSION_PROP_NAME, HttpScriptContext.Cookie.class, EMPTY);
        }

        @Override
        public String getClassName() {
            return COOKIE_JS_CLASS_NAME;
        }

        public String getComment() {
            return cookie.getComment();
        }

        public void setComment(String purpose) {
            cookie.setComment(purpose);
        }

        public String getDomain() {
            return cookie.getDomain();
        }

        public void setDomain(String domain) {
            cookie.setDomain(domain);
        }

        public int getMaxAge() {
            return cookie.getMaxAge();
        }

        public void setMaxAge(int maxAge) {
            cookie.setMaxAge(maxAge);
        }

        public String getName() {
            return cookie.getName();
        }

        public String getPath() {
            return cookie.getPath();
        }

        public void setPath(String path) {
            cookie.setPath(path);
        }

        public boolean getSecure() {
            return cookie.getSecure();
        }

        public void setSecure(boolean secure) {
            cookie.setSecure(secure);
        }

        public String getValue() {
            return cookie.getValue();
        }

        public void setValue(String value) {
            cookie.setValue(value);
        }

        public int getVersion() {
            return cookie.getVersion();
        }

        public void setVersion(int ver) {
            cookie.setVersion(ver);
        }
    }
}