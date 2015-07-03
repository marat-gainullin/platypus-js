package com.eas.client.scripts;

import com.eas.client.AppElementFiles;
import com.eas.client.Application;
import com.eas.client.AsyncProcess;
import com.eas.client.ModuleStructure;
import com.eas.client.ServerModuleInfo;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.queries.Query;
import com.eas.client.settings.SettingsConstants;
import com.eas.client.threetier.http.Cookie;
import com.eas.client.threetier.http.PlatypusHttpConstants;
import com.eas.script.Scripts;
import com.eas.util.BinaryUtils;
import com.eas.util.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.IDN;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author vv
 */
public class ScriptedResource {

    private static final Pattern httpPattern = Pattern.compile("https?://.*");
    protected static Application<?> app;

    /**
     * Initializes a static fields.
     *
     * @param aApp
     * @throws Exception If something goes wrong
     */
    public static void init(Application<?> aApp) throws Exception {
        assert app == null : "Platypus application resource may be initialized only once.";
        app = aApp;
    }

    public static Application<?> getApp() {
        return app;
    }

    /**
     * Do not use. Only for tests.
     *
     * @param aClient
     * @param aPrincipalHost
     * @throws Exception
     *
     * public static void initForTests(Client aClient, PrincipalHost
     * aPrincipalHost) throws Exception { client = aClient; principalHost =
     * aPrincipalHost; }
     */
    /**
     * Gets an principal provider.
     *
     * @return Principal host instance
     *
     * public static PrincipalHost getPrincipalHost() { return principalHost; }
     */
    /**
     * Gets an absolute path to the application's directory.
     *
     * @return Application's directory full path or null if not path is not
     * avaliable
     * @throws java.lang.Exception
     */
    public static String getApplicationPath() throws Exception {
        return app.getModules().getLocalPath();
    }

    public static Object load(final String aResourceName) throws Exception {
        return load(aResourceName, (JSObject) null, (JSObject) null);
    }

    public static Object load(final String aResourceName, JSObject onSuccess) throws Exception {
        return load(aResourceName, onSuccess, (JSObject) null);
    }

    public static Object load(final String aResourceName, JSObject onSuccess, JSObject onFailure) throws Exception {
        Scripts.Space space = Scripts.getSpace();
        return load(aResourceName, space, onSuccess != null ? (Object aLoaded) -> {
            onSuccess.call(null, new Object[]{space.toJs(aLoaded)});
        } : null, onSuccess != null ? (Exception ex) -> {
            onFailure.call(null, new Object[]{space.toJs(ex.getMessage())});
        } : null);
    }

    public static Object load(final String aResourceName, Scripts.Space aSpace, Consumer<Object> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (onSuccess != null) {
            Matcher htppMatcher = httpPattern.matcher(aResourceName);
            if (htppMatcher.matches()) {
                Scripts.startBIO(() -> {
                    try {
                        SEHttpResponse httpResponse = requestHttpResource(aResourceName, null, null, null);
                        try {
                            aSpace.process(() -> {
                                onSuccess.accept(httpResponse.getBody() != null ? httpResponse.getBody() : httpResponse.getBodyBuffer());
                            });
                        } catch (Exception ex) {
                            Logger.getLogger(ScriptedResource.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } catch (Exception ex) {
                        if (onFailure != null) {
                            aSpace.process(() -> {
                                onFailure.accept(ex);
                            });
                        } else {
                            Logger.getLogger(ScriptedResource.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                });
            } else {
                app.getModules().getModule(aResourceName, aSpace, (ModuleStructure s) -> {
                    try {
                        String resourceName = normalizeResourcePath(aResourceName);
                        String sourcesPath = app.getModules().getLocalPath();
                        File resourceFile = new File(sourcesPath + File.separator + resourceName);
                        if (resourceFile.exists() && !resourceFile.isDirectory()) {
                            byte[] data = FileUtils.readBytes(resourceFile);
                            String fileExt = FileUtils.getFileExtension(resourceFile);
                            String encoding;
                            if (PlatypusFiles.isPlatypusProjectFileExt(fileExt) && !PlatypusFiles.REPORT_LAYOUT_EXTENSION.equalsIgnoreCase(fileExt) && !PlatypusFiles.REPORT_LAYOUT_EXTENSION_X.equalsIgnoreCase(fileExt)) {
                                encoding = SettingsConstants.COMMON_ENCODING;
                            } else {
                                String contentType = Files.probeContentType(resourceFile.toPath());
                                if (contentType != null && contentType.toLowerCase().startsWith("text/")) {
                                    encoding = SettingsConstants.COMMON_ENCODING;
                                } else {
                                    encoding = null;// assume binary content
                                }
                            }
                            onSuccess.accept(encoding != null ? new String(data, encoding) : data);
                        } else {
                            Exception ex = new IllegalArgumentException(String.format("Resource %s not found", aResourceName));
                            if (onFailure != null) {
                                onFailure.accept(ex);
                            } else {
                                throw ex;
                            }
                        }
                    } catch (Exception ex) {
                        Logger.getLogger(ScriptedResource.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }, (Exception ex) -> {
                    if (onFailure != null) {
                        onFailure.accept(ex);
                    }
                });
            }
            return null;
        } else {
            return loadSync(aResourceName, aSpace);
        }
    }

    /**
     * Loads a resource as text for UTF-8 encoding.
     *
     * @param aResourceName An relative path to the resource
     * @param aSpace
     * @return Resource's text
     * @throws Exception If some error occurs when reading the resource
     */
    protected static Object loadSync(String aResourceName, Scripts.Space aSpace) throws Exception {
        byte[] data = null;
        String encoding;
        Matcher htppMatcher = httpPattern.matcher(aResourceName);
        if (htppMatcher.matches()) {
            SEHttpResponse httpResponse = requestHttpResource(aResourceName, null, null, null);
            return httpResponse.getBody() != null ? httpResponse.getBody() : httpResponse.getBodyBuffer();
        } else {
            app.getModules().getModule(aResourceName, aSpace, null, null);
            String resourceName = normalizeResourcePath(aResourceName);
            String sourcesPath = app.getModules().getLocalPath();
            File resourceFile = new File(sourcesPath + File.separator + resourceName);
            if (resourceFile.exists() && !resourceFile.isDirectory()) {
                data = FileUtils.readBytes(resourceFile);
                String fileExt = FileUtils.getFileExtension(resourceFile);
                if (PlatypusFiles.isPlatypusProjectFileExt(fileExt) && !PlatypusFiles.REPORT_LAYOUT_EXTENSION.equalsIgnoreCase(fileExt) && !PlatypusFiles.REPORT_LAYOUT_EXTENSION_X.equalsIgnoreCase(fileExt)) {
                    encoding = SettingsConstants.COMMON_ENCODING;
                } else {
                    String contentType = Files.probeContentType(resourceFile.toPath());
                    if (contentType != null && contentType.toLowerCase().startsWith("text/")) {
                        encoding = SettingsConstants.COMMON_ENCODING;
                    } else {
                        encoding = null;// assume binary content
                    }
                }
            } else {
                throw new IllegalArgumentException(String.format("Resource %s not found", aResourceName));
            }
            return encoding != null ? new String(data, encoding) : data;
        }
    }

    public static JSObject jsRequestHttpResource(String aUrl, String aMethod, String aRequestBody, JSObject aHeaders, JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        Scripts.Space space = Scripts.getSpace();
        Map<String, Object> headers = new HashMap<>();
        if (aHeaders != null) {
            aHeaders.keySet().stream().forEach((String aKey) -> {
                Object oValue = space.toJava(aHeaders.getMember(aKey));
                if (oValue != null) {
                    headers.put(aKey.toLowerCase(), oValue);
                }
            });
        }
        if (aOnSuccess != null) {
            Scripts.startBIO(() -> {
                try {
                    SEHttpResponse httResponse = requestHttpResource(aUrl, aMethod, aRequestBody, headers);
                    space.process(() -> {
                        JSObject jsResponse = httResponse.toJs(space);
                        aOnSuccess.call(null, new Object[]{jsResponse});
                    });
                } catch (Exception ex) {
                    Logger.getLogger(ScriptedResource.class.getName()).log(Level.SEVERE, null, ex);
                    space.process(() -> {
                        aOnFailure.call(null, new Object[]{space.toJs(ex.getMessage())});
                    });
                }
            });
            return null;
        } else {
            SEHttpResponse httResponse = requestHttpResource(aUrl, aMethod, aRequestBody, headers);
            return httResponse.toJs(space);
        }
    }

    private static String[] absoluteAppPaths(Path apiPath, String[] aScriptsNames, URI aCalledFromFile) throws URISyntaxException {
        if (aScriptsNames.length > 0 && aCalledFromFile != null) {
            String[] absolute = new String[aScriptsNames.length];
            Path appPath = Paths.get(new File(app.getModules().getLocalPath()).toURI());
            Path calledFromFile = Paths.get(aCalledFromFile);
            Path calledFromDir = calledFromFile.getParent();
            for (int i = 0; i < aScriptsNames.length; i++) {
                String relative = aScriptsNames[i];
                if (relative.startsWith("./") || relative.startsWith("../")) {
                    if (calledFromFile.startsWith(apiPath)) {// api relative
                        Path apiFile = calledFromDir.resolve(relative).normalize();
                        absolute[i] = apiFile.toString().substring((apiPath.toString().length() + 1)).replace(File.separator, "/");
                    } else if (calledFromFile.startsWith(appPath)) {// application relative
                        Path appFile = calledFromDir.resolve(relative).normalize();
                        absolute[i] = appFile.toString().substring((appPath.toString().length() + 1)).replace(File.separator, "/");
                    } else {
                        absolute[i] = relative;
                    }
                } else {
                    absolute[i] = relative;
                }
            }
            return absolute;
        } else {
            return aScriptsNames;
        }
    }

    private static final Path absoluteApiPath = lookupPlatypusJs();

    private static Path lookupPlatypusJs(){
        try {
            URL platypusURL = Thread.currentThread().getContextClassLoader().getResource("platypus.js");
            Path apiPath = Paths.get(platypusURL.toURI());
            apiPath = apiPath.getParent();
            return apiPath;
        } catch (URISyntaxException ex) {
            Logger.getLogger(ScriptedResource.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    protected static class SEHttpResponse {

        protected int status;
        protected String statusText;
        protected String characterEncoding;
        protected List<Cookie> cookies = new ArrayList<>();
        protected Map<String, Object> headers = new HashMap<>();
        protected byte[] bodyContent;
        protected String body;

        public SEHttpResponse() {
            super();
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getStatusText() {
            return statusText;
        }

        public void setStatusText(String statusText) {
            this.statusText = statusText;
        }

        public String getCharacterEncoding() {
            return characterEncoding;
        }

        public void setCharacterEncoding(String characterEncoding) {
            this.characterEncoding = characterEncoding;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public List<Cookie> getCookies() {
            return cookies;
        }

        public void setCookies(List<Cookie> cookies) {
            this.cookies = cookies;
        }

        public Map<String, Object> getHeaders() {
            return headers;
        }

        public void setHeaders(Map<String, Object> headers) {
            this.headers = headers;
        }

        public byte[] getBodyBuffer() {
            return bodyContent;
        }

        public void setBodyContent(byte[] bodyContent) {
            this.bodyContent = bodyContent;
        }

        public JSObject toJs(Scripts.Space aSpace) {
            JSObject jsResp = aSpace.makeObj();
            // general
            jsResp.setMember("status", getStatus());
            jsResp.setMember("statusText", getStatusText());
            jsResp.setMember("contentType", getHeaders().get(PlatypusHttpConstants.HEADER_CONTENTTYPE));
            jsResp.setMember("body", getBody());
            jsResp.setMember("bodyBuffer", getBodyBuffer());
            jsResp.setMember("characterEncoding", getCharacterEncoding());
            // headers
            JSObject jsHeaders = aSpace.makeObj();
            getHeaders().entrySet().stream().forEach((Map.Entry<String, Object> aEntry) -> {
                jsHeaders.setMember(aEntry.getKey(), aSpace.toJs(aEntry.getValue()));
            });
            jsResp.setMember("headers", jsHeaders);
            // cookies
            JSObject jsCookies = aSpace.makeObj();
            getCookies().forEach((Cookie aCookie) -> {
                JSObject jsCookie = aSpace.makeObj();
                jsCookie.setMember("name", aCookie.getName());
                jsCookie.setMember("domain", aCookie.getDomain());
                jsCookie.setMember("expires", aSpace.toJs(aCookie.getExpires()));
                jsCookie.setMember("maxAge", (double) aCookie.getMaxAge());
                jsCookie.setMember("path", aCookie.getPath());
                jsCookie.setMember("value", aCookie.getValue());
                jsCookies.setMember(aCookie.getName(), jsCookie);
            });
            jsResp.setMember("cookies", jsCookies);
            return jsResp;
        }
    }

    public static SEHttpResponse requestHttpResource(String aUrl, String aMethod, String aRequestBody, Map<String, Object> aHeaders) throws Exception {
        byte[] data;
        String encoding;
        Callable<HttpURLConnection> connFactory = () -> {
            try {
                return (HttpURLConnection) new URL(aUrl).openConnection();
            } catch (IOException ex) {
                URL encodedUrl = encodeUrl(new URL(aUrl));
                return (HttpURLConnection) encodedUrl.openConnection();
            }
        };
        final HttpURLConnection conn = connFactory.call();
        if (aMethod != null && !aMethod.isEmpty()) {
            conn.setRequestMethod(aMethod);
        }
        conn.setDoInput(true);
        conn.setRequestProperty("accept-encoding", "deflate");
        if (aHeaders != null) {
            aHeaders.entrySet().stream().forEach((Map.Entry<String, Object> aHeader) -> {
                Object oValue = aHeader.getValue();
                if (oValue instanceof Number) {
                    conn.setRequestProperty(aHeader.getKey(), "" + ((Number) oValue).intValue());
                } else if (oValue instanceof Date) {
                    DateFormat df = new SimpleDateFormat(PlatypusHttpConstants.HTTP_DATE_FORMAT);
                    conn.setRequestProperty(aHeader.getKey(), df.format((Date) oValue));
                } else if (oValue != null) {
                    conn.setRequestProperty(aHeader.getKey(), "" + oValue);
                }
            });
        }
        if (aRequestBody != null && !aRequestBody.isEmpty()) {
            conn.setRequestProperty(PlatypusHttpConstants.HEADER_CONTENTTYPE, aHeaders != null && aHeaders.containsKey(PlatypusHttpConstants.HEADER_CONTENTTYPE.toLowerCase()) ? "" + aHeaders.get(PlatypusHttpConstants.HEADER_CONTENTTYPE.toLowerCase()) : "text/plain;charset=" + SettingsConstants.COMMON_ENCODING);
            byte[] body = aRequestBody.getBytes(SettingsConstants.COMMON_ENCODING);
            conn.setRequestProperty(PlatypusHttpConstants.HEADER_CONTENTLENGTH, "" + body.length);
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body);
            }
        }
        SEHttpResponse resp = new SEHttpResponse();
        resp.setStatus(conn.getResponseCode());
        resp.setStatusText(conn.getResponseMessage());
        InputStream is = conn.getInputStream();
        String contentEncoding = conn.getContentEncoding();
        if (contentEncoding != null) {
            if (contentEncoding.contains("gzip") || contentEncoding.contains("zip")) {
                is = new GZIPInputStream(is);
            } else if (contentEncoding.contains("deflate")) {
                is = new InflaterInputStream(is);
            }
        }
        try (InputStream _is = is) {
            data = BinaryUtils.readStream(_is, -1);
            String contentType = conn.getContentType();
            if (contentType != null) {
                contentType = contentType.replaceAll("\\s+", "").toLowerCase();
                if (contentType.startsWith("text/") || contentType.contains("charset") || contentType.startsWith("application/json")) {
                    if (contentType.contains(";charset=")) {
                        String[] typeCharset = contentType.split(";charset=");
                        if (typeCharset.length == 2 && typeCharset[1] != null) {
                            encoding = typeCharset[1];
                        } else {
                            Logger.getLogger(ScriptedResource.class.getName()).log(Level.WARNING, CHARSET_MISSING_MSG);
                            encoding = SettingsConstants.COMMON_ENCODING;
                        }
                    } else {
                        Logger.getLogger(ScriptedResource.class.getName()).log(Level.WARNING, CHARSET_MISSING_MSG);
                        encoding = SettingsConstants.COMMON_ENCODING;
                    }
                } else {
                    encoding = null;// assume binary response
                }
            } else {
                Logger.getLogger(ScriptedResource.class.getName()).log(Level.WARNING, CHARSET_MISSING_MSG);
                encoding = SettingsConstants.COMMON_ENCODING;
            }
        }
        if (encoding != null) {
            resp.setCharacterEncoding(encoding);
            resp.setBody(new String(data, encoding));
        } else {
            resp.setBodyContent(data);
        }
        Map<String, List<String>> headers = conn.getHeaderFields();
        headers.entrySet().stream().forEach((Map.Entry<String, List<String>> aEntry) -> {
            /*  Crazy J2SE Http client!*/
            if (aEntry.getKey() != null && !aEntry.getValue().isEmpty()) {
                resp.getHeaders().put(aEntry.getKey().toLowerCase(), aEntry.getValue().get(0));
            }
        });
        List<String> cookieHeaders = headers.get(PlatypusHttpConstants.HEADER_SETCOOKIE);
        if (cookieHeaders != null) {
            cookieHeaders.stream().forEach((setCookieHeaderValue) -> {
                try {
                    Cookie cookie = Cookie.parse(setCookieHeaderValue.toLowerCase());
                    resp.getCookies().add(cookie);
                } catch (ParseException | NumberFormatException ex) {
                    Logger.getLogger(ScriptedResource.class.getName()).log(Level.SEVERE, ex.getMessage());
                }
            });
        }
        long contentLength = conn.getContentLengthLong();
        if (contentLength != -1) {
            resp.getHeaders().put(PlatypusHttpConstants.HEADER_CONTENTLENGTH, contentLength);
        }
        long date = conn.getDate();
        if (date != 0) {
            resp.getHeaders().put(PlatypusHttpConstants.HEADER_DATE, new Date(date));
        }
        long expires = conn.getExpiration();
        if (expires != 0) {
            resp.getHeaders().put(PlatypusHttpConstants.HEADER_EXPIRES, new Date(expires));
        }
        long lastModified = conn.getLastModified();
        if (lastModified != 0) {
            resp.getHeaders().put(PlatypusHttpConstants.HEADER_LAST_MODIFIED, new Date(lastModified));
        }
        return resp;
    }
    public static final String CHARSET_MISSING_MSG = "Charset missing in http response. Falling back to " + SettingsConstants.COMMON_ENCODING;

    protected static String normalizeResourcePath(String aPath) throws Exception {
        if (aPath.startsWith("/")) {
            throw new IllegalStateException("Platypus resource path can't begin with /. Platypus resource paths must point somewhere in application, but not in filesystem.");
        }
        if (aPath.startsWith("..") || aPath.startsWith(".")) {
            throw new IllegalStateException("Platypus resource paths must be application-absolute. \"" + aPath + "\" is not application-absolute");
        }
        URI uri = new URI(null, null, aPath, null);
        return uri.normalize().getPath();
    }

    private static URL encodeUrl(URL url) throws URISyntaxException, MalformedURLException {
        String file = "";
        if (url.getPath() != null && !url.getPath().isEmpty()) {
            file += (new URI(null, null, url.getPath(), null)).toASCIIString();
        }
        if (url.getQuery() != null && !url.getQuery().isEmpty()) {
            file += "?" + url.getQuery();
        }
        if (url.getRef() != null && !url.getRef().isEmpty()) {
            file += "#" + url.getRef();
        }
        url = new URL(url.getProtocol(), IDN.toASCII(url.getHost()), url.getPort(), file);
        return url;
    }

    protected static class RequireProcess extends AsyncProcess<String, Void> {

        public RequireProcess(int aExpected, Consumer<Void> aOnSuccess, Consumer<Exception> aOnFailure) {
            super(aExpected, aOnSuccess, aOnFailure);
        }

        @Override
        public void complete(String aValue, Exception aFailureCause) {
            if (aFailureCause != null) {
                exceptions.add(aFailureCause);
            }
            if (++completed == expected) {
                doComplete(null);
            }
        }

    }

    private static void loadModule(Path apiPath, String scriptOrModuleName, URI aCalledFromFile, Scripts.Space aSpace, Set<String> aCyclic, Consumer<URL> onSuccess, Consumer<Exception> onFailure) {
        Path apiLocalPath = apiPath.resolve(scriptOrModuleName);
        if (apiLocalPath != null && apiLocalPath.toFile().exists() && !apiLocalPath.toFile().isDirectory()) {
            // network activity simulation
            aSpace.process(() -> {
                try {
                    URL toLoad = apiLocalPath.toUri().toURL();
                    onSuccess.accept(toLoad);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(ScriptedResource.class.getName()).log(Level.SEVERE, "{0} - Failed {1}", new Object[]{checkedScriptOrModuleName(scriptOrModuleName), ex.toString()});
                }
            });
        } else {
            try {
                app.getModules().getModule(scriptOrModuleName, aSpace, (ModuleStructure structure) -> {
                    if (structure != null) {
                        try {
                            AppElementFiles files = structure.getParts();
                            File sourceFile = files.findFileByExtension(PlatypusFiles.JAVASCRIPT_EXTENSION);

                            RequireProcess moduleProcess = new RequireProcess(3, (Void v) -> {
                                try {
                                    URL toLoad = sourceFile.toURI().toURL();
                                    onSuccess.accept(toLoad);
                                } catch (MalformedURLException ex) {
                                    Logger.getLogger(ScriptedResource.class.getName()).log(Level.SEVERE, "{0} - Failed {1}", new Object[]{checkedScriptOrModuleName(scriptOrModuleName), ex.toString()});
                                }
                            }, (Exception ex) -> {
                                onFailure.accept(ex);
                            });
                            if (files.isModule()) {
                                try {
                                    // 1
                                    qRequire(structure.getQueryDependencies().toArray(new String[]{}), aSpace, (Void v) -> {
                                        moduleProcess.complete(scriptOrModuleName + ".q", null);
                                    }, (Exception ex) -> {
                                        moduleProcess.complete(scriptOrModuleName + ".q", ex);
                                    });
                                    // 2
                                    sRequire(structure.getServerDependencies().toArray(new String[]{}), aSpace, (Void v) -> {
                                        moduleProcess.complete(scriptOrModuleName + ".s", null);
                                    }, (Exception ex) -> {
                                        moduleProcess.complete(scriptOrModuleName + ".s", ex);
                                    });
                                } catch (Exception ex) {
                                    Logger.getLogger(ScriptedResource.class.getName()).log(Level.INFO, "{0} - Failed {1}", new Object[]{checkedScriptOrModuleName(scriptOrModuleName), ex.toString()});
                                }
                            } else {
                                // 1
                                moduleProcess.complete(scriptOrModuleName + ".q", null);// instead of qRequire
                                // 2
                                moduleProcess.complete(scriptOrModuleName + ".s", null);// instead of sRequire
                            }
                            // 3
                            _require(structure.getClientDependencies().toArray(new String[]{}), aCalledFromFile, aSpace, aCyclic, (Void v) -> {
                                moduleProcess.complete(null, null);
                            }, (Exception ex) -> {
                                moduleProcess.complete(null, ex);
                            });
                        } catch (Exception ex) {
                            Logger.getLogger(ScriptedResource.class.getName()).log(Level.INFO, "{0} - Failed {1}", new Object[]{checkedScriptOrModuleName(scriptOrModuleName), ex.toString()});
                        }
                    } else {
                        Exception ex = new FileNotFoundException(scriptOrModuleName);
                        onFailure.accept(ex);
                    }
                }, (Exception ex) -> {
                    onFailure.accept(ex);
                });
            } catch (Exception ex) {
                Logger.getLogger(ScriptedResource.class.getName()).log(Level.INFO, "{0} - Failed {1}", new Object[]{checkedScriptOrModuleName(scriptOrModuleName), ex.toString()});
            }
        }
    }

    private static Object checkedScriptOrModuleName(String scriptOrModuleName) {
        return scriptOrModuleName != null && !scriptOrModuleName.isEmpty() ? scriptOrModuleName : "[start]";
    }

    public static void require(String[] aScriptsNames, String aCalledFromFile, JSObject onSuccess, JSObject onFailure) throws Exception {
        Scripts.Space space = Scripts.getSpace();
        _require(aScriptsNames, aCalledFromFile != null ? new URL(aCalledFromFile).toURI() : null, space, new HashSet<>(), (Void v) -> {
            if (onSuccess != null) {
                onSuccess.call(null, new Object[]{});
            }
        }, (Exception ex) -> {
            if (onFailure != null) {
                onFailure.call(null, new Object[]{ex.getMessage()});
            }
        });
    }

    public static void _require(String[] aScriptsNames, URI aCalledFromFile, Scripts.Space aSpace, Set<String> aCyclic, Consumer<Void> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (aScriptsNames != null && aScriptsNames.length > 0) {
            Path apiPath = absoluteApiPath;
            aScriptsNames = absoluteAppPaths(apiPath, aScriptsNames, aCalledFromFile);
            RequireProcess process = new RequireProcess(aScriptsNames.length, (Void v) -> {
                aSpace.process(() -> {
                    onSuccess.accept(v);
                });
            }, (Exception ex) -> {
                aSpace.process(() -> {
                    onFailure.accept(ex);
                });
            });
            for (String scriptOrModuleName : aScriptsNames) {
                if (aSpace.getExecuted().contains(scriptOrModuleName) || aCyclic.contains(scriptOrModuleName)) {
                    process.complete(scriptOrModuleName, null);
                } else {
                    aCyclic.add(scriptOrModuleName);
                    // add callbacks to pendings
                    if (!aSpace.getPending().containsKey(scriptOrModuleName)) {
                        aSpace.getPending().put(scriptOrModuleName, new ArrayList<>());
                    }
                    List<Scripts.Pending> pending = aSpace.getPending().get(scriptOrModuleName);
                    pending.add(new Scripts.Pending((Void v) -> {
                        process.complete(scriptOrModuleName, null);
                    }, (Exception ex) -> {
                        process.complete(scriptOrModuleName, ex);
                    }));
                    if (!aSpace.getRequired().contains(scriptOrModuleName)) {
                        Logger.getLogger(ScriptedResource.class.getName()).log(Level.INFO, "Loading {0} ...", checkedScriptOrModuleName(scriptOrModuleName));
                        aSpace.getRequired().add(scriptOrModuleName);
                        loadModule(apiPath, scriptOrModuleName, aCalledFromFile, aSpace, aCyclic, (URL aLocalURL) -> {
                            try {
                                // sync require may occur while pending
                                if (!aSpace.getExecuted().contains(scriptOrModuleName)) {
                                    aSpace.getExecuted().add(scriptOrModuleName);
                                    Logger.getLogger(ScriptedResource.class.getName()).log(Level.INFO, "{0} - Loaded", checkedScriptOrModuleName(scriptOrModuleName));
                                    aSpace.exec(aLocalURL);
                                }
                                Scripts.Pending[] pend = pending.toArray(new Scripts.Pending[]{});
                                pending.clear();
                                for (Scripts.Pending p : pend) {
                                    p.loaded();
                                }
                            } catch (ScriptException | URISyntaxException ex) {
                                Logger.getLogger(ScriptedResource.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }, (Exception ex) -> {
                            Logger.getLogger(ScriptedResource.class.getName()).log(Level.INFO, "{0} - Failed {1}", new Object[]{checkedScriptOrModuleName(scriptOrModuleName), ex.toString()});
                            Scripts.Pending[] pend = pending.toArray(new Scripts.Pending[]{});
                            pending.clear();
                            for (Scripts.Pending p : pend) {
                                p.failed(ex);
                            }
                        });
                    }
                }
            }
        } else {
            aSpace.process(() -> {
                onSuccess.accept(null);
            });
        }
    }

    public static void require(String[] aScriptsNames, String aCalledFromFile) throws Exception {
        Scripts.Space space = Scripts.getSpace();
        _require(aScriptsNames, aCalledFromFile != null ? new URL(aCalledFromFile).toURI() : null, space);
    }

    public static void _require(String[] aScriptsNames, URI aCalledFromFile, Scripts.Space aSpace) throws Exception {
        Path apiPath = absoluteApiPath;
        aScriptsNames = absoluteAppPaths(apiPath, aScriptsNames, aCalledFromFile);
        for (String scriptOrModuleName : aScriptsNames) {
            if (!aSpace.getExecuted().contains(scriptOrModuleName)) {
                aSpace.getExecuted().add(scriptOrModuleName);
                Path apiLocalPath = apiPath.resolve(scriptOrModuleName);
                if (apiLocalPath != null && apiLocalPath.toFile().exists() && !apiLocalPath.toFile().isDirectory()) {
                    URL toLoad = apiLocalPath.toUri().toURL();
                    aSpace.exec(toLoad);
                } else {
                    ModuleStructure structure = app.getModules().getModule(scriptOrModuleName, null, null, null);
                    if (structure != null) {
                        AppElementFiles files = structure.getParts();
                        File sourceFile = files.findFileByExtension(PlatypusFiles.JAVASCRIPT_EXTENSION);
                        URL toLoad = sourceFile.toURI().toURL();
                        if (files.isModule()) {
                            qRequire(structure.getQueryDependencies().toArray(new String[]{}), null, null, null);
                            sRequire(structure.getServerDependencies().toArray(new String[]{}), null, null, null);
                        }
                        _require(structure.getClientDependencies().toArray(new String[]{}), null, aSpace);
                        aSpace.exec(toLoad);
                    } else {
                        throw new FileNotFoundException(scriptOrModuleName);
                    }
                }
            }
        }
    }

    protected static void qRequire(String[] aQueriesNames, Scripts.Space aSpace, Consumer<Void> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (onSuccess != null) {
            if (aQueriesNames != null && aQueriesNames.length > 0) {
                RequireProcess process = new RequireProcess(aQueriesNames.length, onSuccess, onFailure);
                for (String queryName : aQueriesNames) {
                    ((QueriesProxy<Query>) app.getQueries()).getQuery(queryName, aSpace, (Query query) -> {
                        process.complete(queryName, null);
                    }, (Exception ex) -> {
                        process.complete(queryName, ex);
                    });
                }
            } else {
                aSpace.process(()->{
                    onSuccess.accept(null);
                });
            }
        } else {
            for (String queryName : aQueriesNames) {
                app.getQueries().getQuery(queryName, null, null, null);
            }
        }
    }

    protected static void sRequire(String[] aModulesNames, Scripts.Space aSpace, Consumer<Void> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (onSuccess != null) {
            if (aModulesNames != null && aModulesNames.length > 0) {
                RequireProcess process = new RequireProcess(aModulesNames.length, onSuccess, onFailure);
                for (String moduleName : aModulesNames) {
                    app.getServerModules().getServerModuleStructure(moduleName, aSpace, (ServerModuleInfo info) -> {
                        process.complete(moduleName, null);
                    }, (Exception ex) -> {
                        process.complete(moduleName, ex);
                    });
                }
            } else {
                aSpace.process(()->{
                    onSuccess.accept(null);
                });
            }
        } else {
            for (String moduleName : aModulesNames) {
                app.getServerModules().getServerModuleStructure(moduleName, null, null, null);
            }
        }
    }
}
