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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.NashornException;

/**
 *
 * @author vv
 */
public class ScriptedResource {

    private static final Pattern HTTP_PATTERN = Pattern.compile("https?://.*");
    protected static volatile Application<?> app;

    /**
     * Initializes a static fields.
     *
     * @param aApp
     * @param aAbsoluteApiPath
     * @param aGlobalAPI
     * @throws Exception If something goes wrong
     */
    public static void init(Application<?> aApp, Path aAbsoluteApiPath, boolean aGlobalAPI) throws Exception {
        assert app == null : "Platypus application resource may be initialized only once.";
        app = aApp;
        Scripts.init(aAbsoluteApiPath, aGlobalAPI);
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
        return app.getModules().getLocalPath().toString();
    }

    /**
     * Part of manual dependencies resolving process
     *
     * @param aRemotesNames
     * @param aOnSuccess
     * @param aOnFailure
     * @throws java.lang.Exception
     */
    public static void loadRemotes(String[] aRemotesNames, JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        sRequire(aRemotesNames, Scripts.getSpace(), aOnSuccess != null ? (Void v) -> {
            aOnSuccess.call(null, new Object[]{});
        } : null, aOnFailure != null ? (Exception aReason) -> {
            aOnFailure.call(null, new Object[]{aReason.getMessage()});
        } : null);
    }

    /**
     * Part of manual dependencies resolving process
     *
     * @param aQueriesNames
     * @param aOnSuccess
     * @param aOnFailure
     * @throws java.lang.Exception
     */
    public static void loadEntities(String[] aQueriesNames, JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        qRequire(aQueriesNames, Scripts.getSpace(), aOnSuccess != null ? (Void v) -> {
            aOnSuccess.call(null, new Object[]{});
        } : null, aOnFailure != null ? (Exception aReason) -> {
            aOnFailure.call(null, new Object[]{aReason.getMessage()});
        } : null);
    }

    public static Object load(final String aResourceName, String aCalledFromFile) throws Exception {
        return load(aResourceName, aCalledFromFile, (JSObject) null, (JSObject) null);
    }

    public static Object load(final String aResourceName, String aCalledFromFile, JSObject aOnSuccess) throws Exception {
        return load(aResourceName, aCalledFromFile, aOnSuccess, (JSObject) null);
    }

    public static Object load(final String aResourceName, String aCalledFromFile, JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        Scripts.Space space = Scripts.getSpace();
        return _load(aResourceName, aCalledFromFile, space, aOnSuccess != null ? (Object aLoaded) -> {
            aOnSuccess.call(null, new Object[]{space.toJs(aLoaded)});
        } : null, aOnFailure != null ? (Exception ex) -> {
            aOnFailure.call(null, new Object[]{space.toJs(ex.getMessage())});
        } : null);
    }

    public static Object _load(final String aResourceName, String aCalledFromFile, Scripts.Space aSpace) throws Exception {
        return _load(aResourceName, aCalledFromFile, aSpace, null, null);
    }

    public static Object _load(final String aResourceName, String aCalledFromFile, Scripts.Space aSpace, Consumer<Object> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (onSuccess != null) {
            Matcher htppMatcher = HTTP_PATTERN.matcher(aResourceName);
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
                Path apiPath = Scripts.getAbsoluteApiPath();
                Path appPath = getAbsoluteAppPath();
                Path calledFromFile = aCalledFromFile != null ? resolveApiApp(aCalledFromFile, apiPath, appPath) : null;
                String resourceName = calledFromFile != null ? relativizeApiApp(aResourceName, calledFromFile, apiPath, calledFromFile.getParent(), appPath) : aResourceName;
                app.getModules().getResource(resourceName, aSpace, (File resourceFile) -> {
                    try {
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
                            Exception ex = new IllegalArgumentException(String.format("Resource %s not found", resourceName));
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
            return loadSync(aResourceName, aCalledFromFile, aSpace);
        }
    }

    /**
     * Loads a resource as text for UTF-8 encoding.
     *
     * @param aResourceName An relative path to the resource
     * @param aCalledFromFile .js file, the code is invoked from
     * @param aSpace
     * @return Resource's text
     * @throws Exception If some error occurs when reading the resource
     */
    protected static Object loadSync(String aResourceName, String aCalledFromFile, Scripts.Space aSpace) throws Exception {
        byte[] data = null;
        String encoding;
        Matcher htppMatcher = HTTP_PATTERN.matcher(aResourceName);
        if (htppMatcher.matches()) {
            SEHttpResponse httpResponse = requestHttpResource(aResourceName, null, null, null);
            return httpResponse.getBody() != null ? httpResponse.getBody() : httpResponse.getBodyBuffer();
        } else {
            Path apiPath = Scripts.getAbsoluteApiPath();
            Path appPath = getAbsoluteAppPath();
            Path calledFromFile = aCalledFromFile != null ? resolveApiApp(aCalledFromFile, apiPath, appPath) : null;
            String resourceName = calledFromFile != null ? relativizeApiApp(aResourceName, calledFromFile, apiPath, calledFromFile.getParent(), appPath) : aResourceName;

            File resourceFile = app.getModules().getResource(resourceName, aSpace, null, null);
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
                throw new IllegalArgumentException(String.format("Resource %s not found", resourceName));
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

    public static String toModuleId(Path apiPath, Path appPath, String aScriptName, String aCalledFromFile) throws URISyntaxException {
        if (aScriptName != null && !aScriptName.isEmpty()) {
            Path calledFromFile = resolveApiApp(aCalledFromFile, apiPath, appPath);
            Path calledFromDir = calledFromFile.getParent();
            return relativizeApiApp(aScriptName, calledFromFile, apiPath, calledFromDir, appPath);
        } else {
            return aScriptName;
        }
    }

    protected static String relativizeApiApp(String relative, Path calledFromFile, Path apiPath, Path calledFromDir, Path appPath) {
        String absolute;
        if (relative.startsWith("./") || relative.startsWith("../")) {
            if (calledFromFile.startsWith(apiPath)) {// api relative
                Path apiFile = calledFromDir.resolve(relative).normalize();
                absolute = apiPath.relativize(apiFile).toString().replace(File.separator, "/");
            } else if (calledFromFile.startsWith(appPath)) {// application relative
                Path appFile = calledFromDir.resolve(relative).normalize();
                absolute = appPath.relativize(appFile).toString().replace(File.separator, "/");
            } else {
                absolute = relative;
            }
        } else {
            absolute = relative;
        }
        return absolute;
    }

    protected static Path resolveApiApp(String aCalledFromFile, Path apiPath, Path appPath) {
        Path calledFromFile = Paths.get(aCalledFromFile.replace("/", File.separator));
        Path apiResolvedCallPoint = apiPath.resolve(calledFromFile);
        Path appResolvedCallPoint = appPath.resolve(calledFromFile);
        if (apiResolvedCallPoint.toFile().exists()) {
            calledFromFile = apiResolvedCallPoint.normalize();
        } else if (appResolvedCallPoint.toFile().exists()) {
            calledFromFile = appResolvedCallPoint.normalize();
        }
        return calledFromFile;
    }

    public static Path lookupPlatypusJs() throws URISyntaxException {
        URL platypusURL = Thread.currentThread().getContextClassLoader().getResource(Scripts.INTERNALS_JS_FILENAME);
        if (platypusURL != null) {
            Path apiPath = Paths.get(platypusURL.toURI());
            apiPath = apiPath.getParent();
            return apiPath;
        } else {
            throw new IllegalStateException("Couldn't find Platypus.js API script " + Scripts.INTERNALS_JS_FILENAME);
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
                    df.setTimeZone(TimeZone.getTimeZone("UTC"));
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

    private static final Map<String, ModuleStructure> scriptsOfModulesStructures = new ConcurrentHashMap<>();

    private static void loadScriptOfModule(String aModuleName, String aCalledFromFile, Scripts.Space aSpace, Set<String> aCyclic, Path apiPath, Consumer<Path> onSuccess, Consumer<Exception> onFailure) {
        // API content is not compressible into bundles and so API module name is transformed into a script file name directly
        // Also API files can't have global dependencies and can't have prefetched resources
        Path apiLocalPath = apiPath.resolve(aModuleName + PlatypusFiles.JAVASCRIPT_FILE_END);
        if (apiLocalPath != null && apiLocalPath.toFile().exists() && !apiLocalPath.toFile().isDirectory()) {
            // network activity simulation
            aSpace.process(() -> {
                onSuccess.accept(apiLocalPath.normalize());
            });
        } else {// Module is application module, so let's discover what file contains the module.
            Consumer<ModuleStructure> withModuleStructure = (ModuleStructure structure) -> {
                if (structure != null) {
                    scriptsOfModulesStructures.put(aModuleName, structure);
                    try {
                        AppElementFiles files = structure.getParts();
                        File sourceFile = files.findFileByExtension(PlatypusFiles.JAVASCRIPT_EXTENSION);

                        RequireProcess moduleProcess = new RequireProcess(3, (Void v) -> {
                            Path appLocalPath = Paths.get(sourceFile.toURI());
                            onSuccess.accept(appLocalPath);
                        }, (Exception ex) -> {
                            onFailure.accept(ex);
                        });
                        if (files.isModule()) {
                            try {
                                // 1
                                qRequire(structure.getQueryDependencies().toArray(new String[]{}), aSpace, (Void v) -> {
                                    moduleProcess.complete(aModuleName + ".q", null);
                                }, (Exception ex) -> {
                                    moduleProcess.complete(aModuleName + ".q", ex);
                                });
                                // 2
                                sRequire(structure.getServerDependencies().toArray(new String[]{}), aSpace, (Void v) -> {
                                    moduleProcess.complete(aModuleName + ".s", null);
                                }, (Exception ex) -> {
                                    moduleProcess.complete(aModuleName + ".s", ex);
                                });
                            } catch (Exception ex) {
                                Logger.getLogger(ScriptedResource.class.getName()).log(Level.INFO, "{0} - Failed {1}", new Object[]{checkedModuleName(aModuleName), ex.toString()});
                            }
                        } else {
                            // 1
                            moduleProcess.complete(aModuleName + ".q", null);// instead of qRequire
                            // 2
                            moduleProcess.complete(aModuleName + ".s", null);// instead of sRequire
                        }
                        // 3
                        _require(structure.getClientDependencies().toArray(new String[]{}), aCalledFromFile, aSpace, aCyclic, (Void v) -> {
                            moduleProcess.complete(null, null);
                        }, (Exception ex) -> {
                            moduleProcess.complete(null, ex);
                        });
                    } catch (Exception ex) {
                        Logger.getLogger(ScriptedResource.class.getName()).log(Level.INFO, "{0} - Failed {1}", new Object[]{checkedModuleName(aModuleName), ex.toString()});
                    }
                } else {
                    Exception ex = new FileNotFoundException(aModuleName);
                    onFailure.accept(ex);
                }
            };
            ModuleStructure cached = scriptsOfModulesStructures.get(aModuleName);
            if (cached != null) {
                // network activity simulation
                aSpace.process(() -> {
                    withModuleStructure.accept(cached);
                });
            } else {
                try {
                    app.getModules().getModule(aModuleName, aSpace, withModuleStructure, (Exception ex) -> {
                        onFailure.accept(ex);
                    });
                } catch (Exception ex) {
                    Logger.getLogger(ScriptedResource.class.getName()).log(Level.INFO, "{0} - Failed {1}", new Object[]{aModuleName, ex.toString()});
                }
            }
        }
    }

    private static Object checkedModuleName(String aScriptName) {
        return aScriptName != null && !aScriptName.isEmpty() ? aScriptName : "[start]";
    }

    public static void require(String[] aModulesNames, String aCalledFromFile, JSObject onSuccess, JSObject onFailure) throws Exception {
        Scripts.Space space = Scripts.getSpace();
        _require(aModulesNames, aCalledFromFile, space, new HashSet<>(), (Void v) -> {
            if (onSuccess != null) {
                onSuccess.call(null, new Object[]{});
            }
        }, (Exception ex) -> {
            if (onFailure != null) {
                onFailure.call(null, new Object[]{ex.getMessage()});
            }
        });
    }

    public static void _require(String[] aModulesNames, String aCalledFromFile, Scripts.Space aSpace, Set<String> aCyclic, Consumer<Void> onSuccess, Consumer<Exception> onFailure) throws Exception {
        String[] modulesNames = aModulesNames != null ? new HashSet<>(Arrays.asList(aModulesNames)).toArray(new String[]{}) : null;
        if (modulesNames != null && modulesNames.length > 0) {
            Path apiPath = Scripts.getAbsoluteApiPath();
            Path appPath = getAbsoluteAppPath();
            RequireProcess process = new RequireProcess(modulesNames.length, (Void v) -> {
                aSpace.process(() -> {
                    onSuccess.accept(v);
                });
            }, (Exception ex) -> {
                aSpace.process(() -> {
                    onFailure.accept(ex);
                });
            });
            for (String moduleName : modulesNames) {
                if (aSpace.getDefined().containsKey(moduleName)) {
                    process.complete(moduleName, null);
                } else if (aCyclic.contains(moduleName)) {
                    Logger.getLogger(ScriptedResource.class.getName()).log(Level.WARNING, "Cyclic dependency detected: {0}", checkedModuleName(moduleName));
                    process.complete(moduleName, null);
                } else {
                    aCyclic.add(moduleName);
                    // add callbacks to pendings
                    aSpace.pendOn(moduleName, new Scripts.Pending((Void v) -> {
                        process.complete(moduleName, null);
                    }, (Exception ex) -> {
                        process.complete(moduleName, ex);
                    }));
                    Logger.getLogger(ScriptedResource.class.getName()).log(Level.INFO, "Loading {0} ...", checkedModuleName(moduleName));
                    loadScriptOfModule(moduleName, aCalledFromFile, aSpace, aCyclic, apiPath, (Path aScriptFile) -> {
                        try {
                            // sync require may occur while pending
                            if (!aSpace.getDefined().containsKey(moduleName)) {
                                URL scriptURL = aScriptFile.toUri().toURL();
                                Set<String> amdNames;
                                if (!aSpace.getExecuted().containsKey(scriptURL)) {
                                    Path relativeLocalPath;
                                    if (aScriptFile.startsWith(apiPath)) {
                                        relativeLocalPath = apiPath.relativize(aScriptFile);
                                    } else if (aScriptFile.startsWith(appPath)) {
                                        relativeLocalPath = appPath.relativize(aScriptFile);
                                    } else {
                                        relativeLocalPath = aScriptFile;
                                    }
                                    aSpace.exec(relativeLocalPath.toString().replace(File.separator, "/"), scriptURL);
                                    amdNames = aSpace.getExecuted().get(scriptURL);
                                    Collection<Scripts.AmdDefine> amdDefines = aSpace.consumeAmdDefines();
                                    // Amd in action...
                                    for (Scripts.AmdDefine amdDefine : amdDefines) {
                                        assert amdDefine.getModuleName() != null : DEFAULT_MODULE_NAME_ASSERT_MSG;
                                        amdNames.add(amdDefine.getModuleName());
                                        final String amdModuleName = amdDefine.getModuleName();
                                        String[] amdDependencies = amdDefine.getAmdDependencies();
                                        JSObject amdModuleDefiner = amdDefine.getModuleDefiner();
                                        _require(amdDependencies, null, aSpace, new HashSet<>(), (Void v) -> {
                                            try {
                                                amdModuleDefiner.call(null, new Object[]{amdModuleName});
                                                // If module is still not defined because of buggy definer in script,
                                                // we have to put it definition as undefined by hand.
                                                if (!aSpace.getDefined().containsKey(amdModuleName)) {
                                                    aSpace.getDefined().put(amdModuleName, null);
                                                }
                                                Logger.getLogger(ScriptedResource.class.getName()).log(Level.INFO, "{0} - Loaded", checkedModuleName(amdModuleName));
                                                aSpace.notifyLoaded(amdModuleName);
                                            } catch (NashornException ex) {
                                                Logger.getLogger(ScriptedResource.class.getName()).log(Level.WARNING, "{0} - Failed {1}", new Object[]{checkedModuleName(amdModuleName), ex.toString()});
                                                aSpace.notifyFailed(amdModuleName, ex);
                                            }
                                        }, (Exception ex) -> {
                                            Logger.getLogger(ScriptedResource.class.getName()).log(Level.WARNING, "{0} - Failed {1}", new Object[]{checkedModuleName(amdModuleName), ex.toString()});
                                            aSpace.notifyFailed(amdModuleName, ex);
                                        });
                                    }
                                } else {
                                    amdNames = aSpace.getExecuted().get(scriptURL);
                                }
                                if (!amdNames.contains(moduleName)) {
                                    Logger.getLogger(ScriptedResource.class.getName()).log(Level.INFO, "{0} - Loaded", checkedModuleName(moduleName));
                                    aSpace.notifyLoaded(moduleName);
                                    // If module is global or it is a plain *.js file, we have to put its definition as undefined
                                    // in AMD structure.
                                    if (!aSpace.getDefined().containsKey(moduleName)) {
                                        aSpace.getDefined().put(moduleName, null);
                                    } else {
                                        Logger.getLogger(ScriptedResource.class.getName()).log(Level.WARNING, "Module {0} is defined multiple times. May be it exists both as AMD module and as a global function.", checkedModuleName(moduleName));
                                    }
                                }
                            } else {
                                aSpace.notifyLoaded(moduleName);
                            }
                        } catch (NashornException | ScriptException ex) {
                            Logger.getLogger(ScriptedResource.class.getName()).log(Level.WARNING, "{0} - Failed {1}", new Object[]{checkedModuleName(moduleName), ex.toString()});
                            aSpace.notifyFailed(moduleName, ex);
                        } catch (Exception ex) {
                            Logger.getLogger(ScriptedResource.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }, (Exception ex) -> {
                        Logger.getLogger(ScriptedResource.class.getName()).log(Level.WARNING, "{0} - Failed {1}", new Object[]{checkedModuleName(moduleName), ex.toString()});
                        aSpace.notifyFailed(moduleName, ex);
                    });
                }
            }
        } else {
            aSpace.process(() -> {
                onSuccess.accept(null);
            });
        }
    }
    private static final String DEFAULT_MODULE_NAME_ASSERT_MSG = "Default module name assumption failed";

    public static void require(String[] aModulesNames, String aCalledFromFile) throws Exception {
        Scripts.Space space = Scripts.getSpace();
        _require(aModulesNames, aCalledFromFile, space, new HashSet<>());
    }

    public static void _require(String[] aModulesNames, String aCalledFromFile, Scripts.Space aSpace, Set<String> aCyclic) throws Exception {
        if (aModulesNames != null && aModulesNames.length > 0) {
            Path apiPath = Scripts.getAbsoluteApiPath();
            Path appPath = getAbsoluteAppPath();
            for (String moduleName : aModulesNames) {
                if (!aSpace.getDefined().containsKey(moduleName)) {
                    if (aCyclic.contains(moduleName)) {
                        Logger.getLogger(ScriptedResource.class.getName()).log(Level.WARNING, "Cyclic dependency detected: {0}", checkedModuleName(moduleName));
                    } else {
                        aCyclic.add(moduleName);
                        Path apiLocalPath = apiPath.resolve(moduleName + PlatypusFiles.JAVASCRIPT_FILE_END);
                        if (apiLocalPath != null && apiLocalPath.toFile().exists() && !apiLocalPath.toFile().isDirectory()) {
                            URL scriptURL = apiLocalPath.toUri().toURL();
                            aSpace.exec(moduleName, scriptURL);
                        } else {
                            ModuleStructure structure = app.getModules().getModule(moduleName, null, null, null);
                            if (structure != null) {
                                AppElementFiles files = structure.getParts();
                                File sourceFile = files.findFileByExtension(PlatypusFiles.JAVASCRIPT_EXTENSION);
                                URL scriptURL = sourceFile.toURI().toURL();
                                if (!aSpace.getExecuted().containsKey(scriptURL)) {
                                    if (files.isModule()) {
                                        qRequire(structure.getQueryDependencies().toArray(new String[]{}), null, null, null);
                                        sRequire(structure.getServerDependencies().toArray(new String[]{}), null, null, null);
                                    }
                                    String[] autoDiscoveredDependencies = structure.getClientDependencies().toArray(new String[]{});
                                    _require(autoDiscoveredDependencies, null, aSpace, aCyclic);
                                    Path fileToLoad = Paths.get(scriptURL.toURI());
                                    Path appRelative = appPath.relativize(fileToLoad);
                                    aSpace.exec(appRelative.toString().replace(File.separator, "/"), scriptURL);
                                }
                            } else {
                                throw new FileNotFoundException(moduleName);
                            }
                        }
                        Collection<Scripts.AmdDefine> amdDefines = aSpace.consumeAmdDefines();
                        Collection<String> amdNames = new HashSet<>();
                        for (Scripts.AmdDefine amdDefine : amdDefines) {
                            assert amdDefine.getModuleName() != null : DEFAULT_MODULE_NAME_ASSERT_MSG;
                            amdNames.add(amdDefine.getModuleName());
                            final String amdModuleName = amdDefine.getModuleName();
                            final String[] amdDependencies = amdDefine.getAmdDependencies();
                            final JSObject amdModuleDefiner = amdDefine.getModuleDefiner();
                            _require(amdDependencies, null, aSpace, aCyclic);
                            amdModuleDefiner.call(null, new Object[]{amdModuleName});
                            // If module is still not defined (buggy definer in script, etc.)
                            // we have to put it definition as undefined by hand.
                            if (!aSpace.getDefined().containsKey(amdModuleName)) {
                                aSpace.getDefined().put(amdModuleName, null);
                            }
                        }
                        // If module is global or it is a plain *.js file, we have to
                        // put it as undefined in AMD structure.
                        if (!amdNames.contains(moduleName)) {
                            if (!aSpace.getDefined().containsKey(moduleName)) {
                                aSpace.getDefined().put(moduleName, null);
                            } else {
                                Logger.getLogger(ScriptedResource.class.getName()).log(Level.WARNING, "Module {0} is defined multiple times. May be it exists both as AMD module and as a global function", checkedModuleName(moduleName));
                            }
                        }
                    }
                }
            }
        }
    }

    public static Path getAbsoluteAppPath() {
        return app.getModules().getLocalPath();
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
                aSpace.process(() -> {
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
                aSpace.process(() -> {
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
