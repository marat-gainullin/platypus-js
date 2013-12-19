/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.eas.script.ScriptFunction;
import com.eas.util.BinaryUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public final static String REQUEST_PROP_NAME = "request";//NOI18N
    public final static String RESPONSE_PROP_NAME = "response";//NOI18N
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

    private static final String REQUEST_JS_DOC = "/**\n"
            + "* HTTP request, when invoked by HTTP protocol.\n"
            + "*/";

    @ScriptFunction(jsDoc = REQUEST_JS_DOC)
    public Request getRequest() {
        return request;
    }

    private static final String RESPONSE_JS_DOC = "/**\n"
            + "* HTTP response, when invoked by HTTP protocol.\n"
            + "*/";
    @ScriptFunction(jsDoc = RESPONSE_JS_DOC)
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

        private static final String AUTH_TYPE_JS_DOC = "/**\n"
                + "* The name of the protection authentication scheme.\n"
                + "*/";

        @ScriptFunction(jsDoc = AUTH_TYPE_JS_DOC)
        public String getAuthType() {
            return httpRequest.getAuthType();
        }

        private static final String CHARACTER_ENCODING_JS_DOC = "/**\n"
                + "* The name of the character encoding used in the body of this request.\n"
                + "* <code>null</code> if the request does not specify a character encoding.\n"
                + "*/";

        @ScriptFunction(jsDoc = CHARACTER_ENCODING_JS_DOC)
        public String getCharacterEncoding() {
            return httpRequest.getCharacterEncoding();
        }

        private static final String CONTENT_LENGTH_JS_DOC = "/**\n"
                + "* The length, in bytes, of the request body and made available by the input stream, or -1 if the length is not known.\n"
                + "*/";

        @ScriptFunction(jsDoc = CONTENT_LENGTH_JS_DOC)
        public int getContentLength() {
            return httpRequest.getContentLength();
        }

        private static final String CONTENT_TYPE_JS_DOC = "/**\n"
                + "* The MIME type of the body of the request, or <code>null</code> if the type is not known.\n"
                + "*/";
        @ScriptFunction(jsDoc = CONTENT_TYPE_JS_DOC)
        public String getContentType() {
            return httpRequest.getContentType();
        }

        private static final String BODY_JS_DOC = "/**\n"
                + "* The request body.\n"
                + "*/";
        @ScriptFunction(jsDoc = BODY_JS_DOC)
        public String getBody() throws IOException {
            String encoding = httpRequest.getCharacterEncoding();
            if (encoding == null || encoding.isEmpty()) {
                Logger.getLogger(HttpScriptContext.class.getName()).log(Level.WARNING, "Missing character encoding. Falling back to utf-8.");
                encoding = "utf-8";
            }
            if (Charset.isSupported(encoding)) {
                try (InputStream is = httpRequest.getInputStream()) {
                    byte[] data = BinaryUtils.readStream(is, -1);
                    return new String(data, encoding);
                }
            } else {
                throw new IOException(String.format("Character encoding %s is not supported.", encoding));
            }
        }

        private static final String BODY_BUFFER_JS_DOC = "/**\n"
                + "* The request body as a binary array.\n"
                + "*/";
        @ScriptFunction(jsDoc = BODY_BUFFER_JS_DOC)
        public byte[] getBodyBuffer() throws IOException {
            try (InputStream is = httpRequest.getInputStream()) {
                return BinaryUtils.readStream(is, -1);
            }
        }

        
        private static final String CONTEXT_PATH_JS_DOC = "/**\n"
                + "* The portion of the request URI that indicates the context of the request.\n"
                + "* The context path always comes first in a request URI. The path starts with a \"/\" character but does not end with a \"/\" character.\n"
                + "* For the default (root) context, this method returns \"\".\n"
                + "*/";
        @ScriptFunction(jsDoc = CONTEXT_PATH_JS_DOC)
        public String getContextPath() {
            return httpRequest.getContextPath();
        }

        
        private static final String COOKIES_JS_DOC = "/**\n"
                + "* The cookies for the request.\n"
                + "*/";
        @ScriptFunction(jsDoc = COOKIES_JS_DOC)
        public Cookies getCookies() {
            return cookies;
        }

        private static final String HEADERS_JS_DOC = "/**\n"
                + "* The headers object for the request (read only).\n"
                + "* A header data is avaliable as a JavaScript property of this object.\n"
                + "*/";
        @ScriptFunction(jsDoc = HEADERS_JS_DOC)
        public RequestHeaders getHeaders() {
            return headers;
        }

        private static final String LOCAL_ADDR_JS_DOC = "/**\n"
                + "* The Internet Protocol (IP) address of the interface on which the request was received.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = LOCAL_ADDR_JS_DOC)
        public String getLocalAddr() {
            return httpRequest.getLocalAddr();
        }

        private static final String LOCAL_NAME_JS_DOC = "/**\n"
                + "* The host name of the Internet Protocol (IP) interface on which the request was received.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = LOCAL_NAME_JS_DOC)
        public String getLocalName() {
            return httpRequest.getLocalName();
        }

        private static final String LOCAL_PORT_JS_DOC = "/**\n"
                + "* The Internet Protocol (IP) port number of the interface on which the request was received.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = LOCAL_PORT_JS_DOC)
        public int getLocalPort() {
            return httpRequest.getLocalPort();
        }

        private static final String METHOD_JS_DOC = "/**\n"
                + "* The name of the HTTP method with which this request was made, for example, GET, POST, or PUT.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = METHOD_JS_DOC)
        public String getMethod() {
            return httpRequest.getMethod();
        }

        private static final String PARAMS_JS_DOC = "/**\n"
                + "* The request parameters.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = PARAMS_JS_DOC)
        public Params getParams() {
            return params;
        }

        private static final String PATH_INFO_JS_DOC = "/**\n"
                + "* Any extra path information associated with the URL the client sent when it made this request.\n"
                + "* The extra path information follows the servlet path but precedes the query string and will start with a \"/\" character.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = PATH_INFO_JS_DOC)
        public String getPathInfo() {
            return httpRequest.getPathInfo();
        }

        private static final String PATH_TRANSLATED_JS_DOC = "/**\n"
                + "* Any extra path information after the servlet name but before the query string, and translates it to a real path.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = PATH_TRANSLATED_JS_DOC)
        public String getPathTranslated() {
            return httpRequest.getPathTranslated();
        }

        private static final String PROTOCOL_JS_DOC = "/**\n"
                + "* The name and version of the protocol the request uses in the form protocol/majorVersion.minorVersion, for example, HTTP/1.1.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = PROTOCOL_JS_DOC)
        public String getProtocol() {
            return httpRequest.getProtocol();
        }

        private static final String QUERY_STRING_JS_DOC = "/**\n"
                + "* The query string that is contained in the request URL after the path.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = QUERY_STRING_JS_DOC)
        public String getQueryString() {
            return httpRequest.getQueryString();
        }

        private static final String REMOTE_ADDR_JS_DOC = "/**\n"
                + "* The Internet Protocol (IP) address of the client or last proxy that sent the request.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = REMOTE_ADDR_JS_DOC)
        public String getRemoteAddr() {
            return httpRequest.getRemoteAddr();
        }

        private static final String REMOTE_HOST_JS_DOC = "/**\n"
                + "* The fully qualified name of the client or the last proxy that sent the request.\n"
                + "* If the engine cannot or chooses not to resolve the hostname (to improve performance), this method returns the dotted-string form of the IP address.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = REMOTE_HOST_JS_DOC)
        public String getRemoteHost() {
            return httpRequest.getRemoteHost();
        }

        private static final String REMOTE_PORT_JS_DOC = "/**\n"
                + "* The Internet Protocol (IP) source port of the client or last proxy that sent the request.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = REMOTE_PORT_JS_DOC)
        public int getRemotePort() {
            return httpRequest.getRemotePort();
        }
        
        private static final String REQUEST_URI_JS_DOC = "/**\n"
                + "* The part of this request's URL from the protocol name up to the query string in the first line of the HTTP request.\n"
                + "* The web container does not decode this String.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = REQUEST_URI_JS_DOC)
        public String getRequestURI() {
            return httpRequest.getRequestURI();
        }

        private static final String REQUEST_URL_JS_DOC = "/**\n"
                + "* Reconstructs the URL the client used to make the request.\n"
                + "* The returned URL contains a protocol, server name, port number, and server path, but it does not include query string parameters.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = REQUEST_URL_JS_DOC)
        public String getRequestURL() {
            return httpRequest.getRequestURL().toString();
        }

        private static final String SCHEME_JS_DOC = "/**\n"
                + "* The name of the scheme used to make this request, for example, http, https, or ftp.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = SCHEME_JS_DOC)
        public String getScheme() {
            return httpRequest.getScheme();
        }

        private static final String SERVER_NAME_JS_DOC = "/**\n"
                + "* The host name of the server to which the request was sent. It is the value of the part before \":\".\n"
                + "*/";
        
        @ScriptFunction(jsDoc = SERVER_NAME_JS_DOC)
        public String getServerName() {
            return httpRequest.getServerName();
        }

        private static final String SERVER_PORT_JS_DOC = "/**\n"
                + "* The port number to which the request was sent. It is the value of the part after \":\".\n"
                + "*/";
        
        @ScriptFunction(jsDoc = SERVER_PORT_JS_DOC)
        public int getServerPort() {
            return httpRequest.getServerPort();
        }

        private static final String SECURE_JS_DOC = "/**\n"
                + "* A boolean indicating whether this request was made using a secure channel, such as HTTPS.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = SECURE_JS_DOC)
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
        private final static String RESET_METHOD_NAME = "reset";//NOI18N
        private final static String ADD_HEADER_METHOD_NAME = "addHeader";//NOI18N
        private final static String SET_HEADER_METHOD_NAME = "setHeader";//NOI18N
        private final static String ADD_COOKIE_METHOD_NAME = "addCookie";//NOI18N
        private final static String BODY_PROP_NAME = "body";//NOI18N
        private final static String BODY_BUFFER_PROP_NAME = "bodyBuffer";//NOI18N
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
            r.defineProperty(BODY_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineProperty(BODY_BUFFER_PROP_NAME, HttpScriptContext.Request.class, READONLY);
            r.defineFunctionProperties(new String[]{
                RESET_METHOD_NAME,
                ADD_HEADER_METHOD_NAME,
                SET_HEADER_METHOD_NAME,
                ADD_COOKIE_METHOD_NAME}, Response.class, EMPTY);
            ScriptRuntime.setBuiltinProtoAndParent(r, scope, TopLevel.Builtins.Object);
            return r;
        }

        private static final String STATUS_JS_DOC = "/**\n"
                + "* The current status code of this response.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = STATUS_JS_DOC)
        public int getStatus() {
            return httpResponse.getStatus();
        }

        public void setStatus(int sc) {
            httpResponse.setStatus(sc);
        }

        private static final String CONTENT_TYPE_JS_DOC = "/**\n"
                + "* The content type used for the MIME body sent in this response.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = CONTENT_TYPE_JS_DOC)
        public String getContentType() {
            return httpResponse.getContentType();
        }

        public void setContentType(String type) {
            httpResponse.setContentType(type);
        }

        public void reset(){
            httpResponse.reset();
        }
        
        protected String body;

        public String getBody() throws IOException {
            return body;
        }

        public void setBody(String aValue) throws IOException {
            body = aValue;
            String encoding = httpResponse.getCharacterEncoding();
            if (encoding == null || encoding.isEmpty()) {
                Logger.getLogger(HttpScriptContext.class.getName()).log(Level.WARNING, "Missing character encoding. Falling back to utf-8.");
                encoding = "utf-8";
            }
            if (Charset.isSupported(encoding)) {
                setBodyBuffer(aValue.getBytes(encoding));
            } else {
                throw new IOException(String.format("Character encoding %s is not supported.", encoding));
            }
        }

        protected byte[] bodyBuffer;

        public byte[] getBodyBuffer() throws IOException {
            return bodyBuffer;
        }

        public void setBodyBuffer(byte[] aValue) throws IOException {
            bodyBuffer = aValue;
            httpResponse.resetBuffer();
            try (OutputStream os = httpResponse.getOutputStream()) {
                os.write(aValue);
            }
        }

        private static final String HEADERS_JS_DOC = "/**\n"
                + "* The response headers object.\n"
                + "* A header data is avaliable as a JavaScript property of this object.\n"
                + "* "
                + "*/";
        
        @ScriptFunction(jsDoc = HEADERS_JS_DOC)
        public ResponseHeaders getHeaders() {
            return headers;
        }

        private static final String ADD_HEADER_JS_DOC = "/**\n"
                + "* Adds the new header to the response.\n"
                + "* @param name the header name\n"
                + "* @param value the header value\n"
                + "*/";
        
        @ScriptFunction(jsDoc = ADD_HEADER_JS_DOC,  params = {"name", "value"})
        public void addHeader(String name, String value) {
            httpResponse.addHeader(name, value);
        }

        private static final String SET_HEADER_JS_DOC = "/**\n"
                + "* Sets the header's value.\n"
                + "* @param name the header name\n"
                + "* @param value the header value\n"
                + "*/";
        
        @ScriptFunction(jsDoc = SET_HEADER_JS_DOC,  params = {"name", "value"})
        public void setHeader(String name, String value) {
            httpResponse.setHeader(name, value);
        }

        private static final String ADD_COOKIE_JS_DOC = "/**\n"
                + "* Adds a new cookie to the response.\n"
                + "* Use a key-value object with the following properties:\n"
                + "* <code>name</code>, <code>value</code>, <code>comment</code>, <code>domain</code>, <code>maxAg</code>e, <code>path</code>, <code>secure</code>, <code>version</code>.\n"
                + "* @param cookie the cookie object, for example <code>{name: 'platypus', value: 'test', maxAge: 60*60}</code>\n"
                + "*/";
        
        @ScriptFunction(jsDoc = ADD_COOKIE_JS_DOC,  params = {"cookie"})
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
        
        @Override
        public String getClassName() {
            return RESPONSE_JS_CLASS_NAME;
        }
    }

    private static Integer parseInt(String str) {
        try {
            return Math.round(Float.parseFloat(str));
        } catch (NumberFormatException ex) {
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

        private static final String COMMENT_JS_DOC = "/**\n"
                + "* The comment describing the purpose of this cookie, or <code>null</code> if the cookie has no comment.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = COMMENT_JS_DOC)
        public String getComment() {
            return cookie.getComment();
        }

        public void setComment(String purpose) {
            cookie.setComment(purpose);
        }

        private static final String DOMAIN_JS_DOC = "/**\n"
                + "* The domain name of this Cookie.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = DOMAIN_JS_DOC)
        public String getDomain() {
            return cookie.getDomain();
        }

        public void setDomain(String domain) {
            cookie.setDomain(domain);
        }

        private static final String MAX_AGE_JS_DOC = "/**\n"
                + "* The maximum age in seconds for this Cookie.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = MAX_AGE_JS_DOC)
        public int getMaxAge() {
            return cookie.getMaxAge();
        }

        public void setMaxAge(int maxAge) {
            cookie.setMaxAge(maxAge);
        }

        private static final String NAME_JS_DOC = "/**\n"
                + "* The name of the cookie.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = NAME_JS_DOC)
        public String getName() {
            return cookie.getName();
        }

        private static final String PATH_JS_DOC = "/**\n"
                + "* The path on the server to which the browser returns this cookie. The cookie is visible to all subpaths on the server.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = PATH_JS_DOC)
        public String getPath() {
            return cookie.getPath();
        }

        public void setPath(String path) {
            cookie.setPath(path);
        }

        private static final String SECURE_JS_DOC = "/**\n"
                + "* Indicates to the browser whether the cookie should only be sent using a secure protocol, such as HTTPS or SSL.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = SECURE_JS_DOC)
        public boolean getSecure() {
            return cookie.getSecure();
        }

        public void setSecure(boolean secure) {
            cookie.setSecure(secure);
        }

        private static final String VALUE_JS_DOC = "/**\n"
                + "* The current value of this Cookie.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = VALUE_JS_DOC)
        public String getValue() {
            return cookie.getValue();
        }

        public void setValue(String value) {
            cookie.setValue(value);
        }

        private static final String VERSION_JS_DOC = "/**\n"
                + "* The version of the protocol this cookie complies with.\n"
                + "*/";
        
        @ScriptFunction(jsDoc = VERSION_JS_DOC)
        public int getVersion() {
            return cookie.getVersion();
        }

        public void setVersion(int ver) {
            cookie.setVersion(ver);
        }
    }
}
