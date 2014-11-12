package com.eas.client.scripts;

import com.eas.client.AppElementFiles;
import com.eas.client.Application;
import com.eas.client.AsyncProcess;
import com.eas.client.ModuleStructure;
import com.eas.client.ServerModuleInfo;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.queries.Query;
import com.eas.client.settings.SettingsConstants;
import com.eas.script.ScriptUtils;
import com.eas.util.BinaryUtils;
import com.eas.util.FileUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.IDN;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author vv
 */
public class ScriptedResource {

    private static final Pattern httpPattern = Pattern.compile("https?://.*");
    protected static Application<Query> app;

    /**
     * Initializes a static fields.
     *
     * @param aApp
     * @throws Exception If something goes wrong
     */
    public static void init(Application aApp) throws Exception {
        assert app == null : "Platypus application resource may be initialized only once.";
        app = aApp;
    }

    public static Application<Query> getApp() {
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
        return load(aResourceName, null, null);
    }

    public static Object load(final String aResourceName, JSObject onSuccess) throws Exception {
        return load(aResourceName, null, null);
    }

    public static Object load(final String aResourceName, JSObject onSuccess, JSObject onFailure) throws Exception {
        if (onSuccess != null) {
            ScriptUtils.submitTask(() -> {
                try {
                    Object loaded = loadSync(aResourceName);
                    ScriptUtils.acceptTaskResult(() -> {
                        onSuccess.call(null, new Object[]{ScriptUtils.toJs(loaded)});
                    });
                } catch (Exception ex) {
                    Logger.getLogger(ScriptedResource.class.getName()).log(Level.SEVERE, null, ex);
                    if (onFailure != null) {
                        ScriptUtils.acceptTaskResult(() -> {
                            onFailure.call(null, new Object[]{ScriptUtils.toJs(ex.getMessage())});
                        });
                    }
                }
            });
            return null;
        } else {
            return loadSync(aResourceName);
        }
    }

    /**
     * Loads a resource as text for UTF-8 encoding.
     *
     * @param aResourceName An relative path to the resource
     * @return Resource's text
     * @throws Exception If some error occurs when reading the resource
     */
    protected static Object loadSync(String aResourceName) throws Exception {
        byte[] data = null;
        String encoding;
        Matcher htppMatcher = httpPattern.matcher(aResourceName);
        if (htppMatcher.matches()) {
            return requestViaHttpSync(aResourceName, null, null);
        } else {
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

    public static Object requestViaHttpSync(String aUrl, String aMethodName, String aRequestBody) throws URISyntaxException, IOException, MalformedURLException, UnsupportedEncodingException {
        byte[] data;
        String encoding;
        URL url = new URL(aUrl);
        HttpURLConnection conn;
        InputStream is;
        try {
            conn = (HttpURLConnection) url.openConnection();
        } catch (IOException ex) {
            url = encodeUrl(url);
            conn = (HttpURLConnection) url.openConnection();
        }
        if (aMethodName != null && !aMethodName.isEmpty()) {
            conn.setRequestMethod(aMethodName);
        }
        conn.setDoInput(true);
        if (aRequestBody != null && !aRequestBody.isEmpty()) {
            conn.setDoOutput(true);
            conn.setRequestProperty("content-type", "text/plain;charset=" + SettingsConstants.COMMON_ENCODING);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] body = aRequestBody.getBytes(SettingsConstants.COMMON_ENCODING);
                os.write(body);
                conn.setRequestProperty("content-length", ""+body.length);
            }
        }
        conn.setRequestProperty("accept-encoding", "deflate");
        conn.getResponseCode();
        is = conn.getInputStream();
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
                if (contentType.startsWith("text/") || contentType.contains("charset")) {
                    if (contentType.contains(";charset=")) {
                        String[] typeCharset = contentType.split(";charset=");
                        if (typeCharset.length == 2 && typeCharset[1] != null) {
                            encoding = typeCharset[1];
                        } else {
                            Logger.getLogger(ScriptedResource.class.getName()).log(Level.WARNING, ENCODING_MISSING_MSG);
                            encoding = SettingsConstants.COMMON_ENCODING;
                        }
                    } else {
                        Logger.getLogger(ScriptedResource.class.getName()).log(Level.WARNING, ENCODING_MISSING_MSG);
                        encoding = SettingsConstants.COMMON_ENCODING;
                    }
                } else {
                    encoding = null;// assume binary response
                }
            } else {
                Logger.getLogger(ScriptedResource.class.getName()).log(Level.WARNING, ENCODING_MISSING_MSG);
                encoding = SettingsConstants.COMMON_ENCODING;
            }
        }
        return encoding != null ? new String(data, encoding) : data;
    }
    public static final String ENCODING_MISSING_MSG = "Encoding missing in http response. Falling back to " + SettingsConstants.COMMON_ENCODING;

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

    protected static class RequireProcess extends AsyncProcess<Void, Void> {

        public RequireProcess(int aExpected, Consumer<Void> aOnSuccess, Consumer<Exception> aOnFailure) {
            super(aExpected, aOnSuccess, aOnFailure);
        }

        @Override
        public synchronized void complete(Void aValue, Exception aFailureCause) {
            if (aFailureCause != null) {
                exceptions.add(aFailureCause);
            }
            if (++completed == expected) {
                doComplete(null);
            }
        }

    }

    public static void require(String[] aScriptsNames, JSObject onSuccess, JSObject onFailure) throws Exception {
        _require(aScriptsNames, new ConcurrentSkipListSet<>(), (Void v) -> {
            if (onSuccess != null) {
                onSuccess.call(null, new Object[]{});
            }
        }, (Exception ex) -> {
            if (onFailure != null) {
                onFailure.call(null, new Object[]{ex.getMessage()});
            }
        });
    }

    public static void _require(String[] aScriptsNames, Set<String> required, Consumer<Void> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (aScriptsNames != null && aScriptsNames.length > 0) {
            aScriptsNames = Arrays.asList(aScriptsNames).stream().filter((String aScriptName) -> {
                return !required.contains(aScriptName);
            }).toArray((int aLength) -> {
                return new String[aLength];
            });
            RequireProcess scriptsProceses = new RequireProcess(aScriptsNames.length, onSuccess, onFailure);
            for (String scriptOrModuleName : aScriptsNames) {
                required.add(scriptOrModuleName);
                app.getModules().getModule(scriptOrModuleName, (ModuleStructure structure) -> {
                    if (structure != null) {
                        AppElementFiles files = structure.getParts();
                        File sourceFile = files.findFileByExtension(PlatypusFiles.JAVASCRIPT_EXTENSION);

                        RequireProcess scriptProcess = new RequireProcess(3, (Void v) -> {
                            try {
                                URL sourceUrl = sourceFile.toURI().toURL();
                                ScriptUtils.exec(sourceUrl);
                                try {
                                    scriptsProceses.complete(null, null);
                                } catch (Exception ex) {
                                    Logger.getLogger(ScriptedResource.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } catch (Exception ex) {
                                scriptsProceses.complete(null, ex);
                            }
                        }, (Exception ex) -> {
                            scriptsProceses.complete(null, ex);
                        });
                        if (files.isModule()) {
                            try {
                                qRequire(structure.getQueryDependencies().toArray(new String[]{}), (Void v) -> {
                                    scriptProcess.complete(null, null);
                                }, (Exception ex) -> {
                                    scriptProcess.complete(null, ex);
                                });
                            } catch (Exception ex) {
                                scriptProcess.complete(null, ex);
                            }
                            try {
                                sRequire(structure.getServerDependencies().toArray(new String[]{}), (Void v) -> {
                                    scriptProcess.complete(null, null);
                                }, (Exception ex) -> {
                                    scriptProcess.complete(null, ex);
                                });
                            } catch (Exception ex) {
                                scriptProcess.complete(null, ex);
                            }
                        } else {
                            scriptProcess.complete(null, null);// instead of qRequire
                            scriptProcess.complete(null, null);// instead of sRequire
                        }
                        try {
                            _require(structure.getClientDependencies().toArray(new String[]{}), required, (Void v) -> {
                                scriptProcess.complete(null, null);
                            }, (Exception ex) -> {
                                scriptProcess.complete(null, ex);
                            });
                        } catch (Exception ex) {
                            scriptProcess.complete(null, ex);
                        }
                    } else {
                        scriptsProceses.complete(null, new FileNotFoundException(scriptOrModuleName));
                    }
                }, (Exception ex) -> {
                    scriptsProceses.complete(null, ex);
                });
            }
        } else {
            onSuccess.accept(null);
        }
    }

    public static void require(String[] aScriptsNames) throws Exception {
        _require(aScriptsNames, new ConcurrentSkipListSet<>());
    }

    public static void _require(String[] aScriptsNames, Set<String> required) throws Exception {
        for (String aScriptName : aScriptsNames) {
            if (!required.contains(aScriptName)) {
                required.add(aScriptName);
                ModuleStructure structure = app.getModules().getModule(aScriptName, null, null);
                AppElementFiles files = structure.getParts();
                File sourceFile = files.findFileByExtension(PlatypusFiles.JAVASCRIPT_EXTENSION);
                URL sourceUrl = sourceFile.toURI().toURL();
                if (files.isModule()) {
                    qRequire(structure.getQueryDependencies().toArray(new String[]{}), null, null);
                    sRequire(structure.getServerDependencies().toArray(new String[]{}), null, null);
                }
                _require(structure.getClientDependencies().toArray(new String[]{}), required);
                ScriptUtils.exec(sourceUrl);
            }
        }
    }

    protected static void qRequire(String[] aQueriesNames, Consumer<Void> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (onSuccess != null) {
            if (aQueriesNames != null && aQueriesNames.length > 0) {
                RequireProcess process = new RequireProcess(aQueriesNames.length, onSuccess, onFailure);
                for (String queryName : aQueriesNames) {
                    app.getQueries().getQuery(queryName, (Query query) -> {
                        process.complete(null, null);
                    }, (Exception ex) -> {
                        process.complete(null, ex);
                    });
                }
            } else {
                onSuccess.accept(null);
            }
        } else {
            for (String queryName : aQueriesNames) {
                app.getQueries().getQuery(queryName, null, null);
            }
        }
    }

    protected static void sRequire(String[] aModulesNames, Consumer<Void> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (onSuccess != null) {
            if (aModulesNames != null && aModulesNames.length > 0) {
                RequireProcess process = new RequireProcess(aModulesNames.length, onSuccess, onFailure);
                for (String moduleName : aModulesNames) {
                    app.getServerModules().getServerModuleStructure(moduleName, (ServerModuleInfo info) -> {
                        process.complete(null, null);
                    }, (Exception ex) -> {
                        process.complete(null, ex);
                    });
                }
            } else {
                onSuccess.accept(null);
            }
        } else {
            for (String moduleName : aModulesNames) {
                app.getServerModules().getServerModuleStructure(moduleName, null, null);
            }
        }
    }

    /**
     * Executes a plain js resource.
     *
     * @param aResourceName
     * @throws Exception
     */
    /*
     public static void executeScriptResource(final String aResourceName) throws Exception {
     final String resourceId = ScriptedResource.normalizeResourcePath(aResourceName);
     String sourcesPath = app.getModules().getLocalPath();
     String sourcePath = sourcesPath + File.separator + resourceId;
     URL sourceUrl;
     File test = new File(sourcePath);
     if (test.exists()) {
     sourceUrl = test.toURI().toURL();
     } else {
     try {
     sourceUrl = new URL(sourcePath);
     } catch (MalformedURLException ex) {
     throw new FileNotFoundException(sourcePath);
     }
     }
     URLReader reader = new URLReader(sourceUrl, SettingsConstants.COMMON_ENCODING);
     StringBuilder read = new StringBuilder();
     char[] buffer = new char[1024 * 4];// 4kb
     int readCount = 0;
     while (readCount != -1) {
     readCount = reader.read(buffer);
     if (readCount != -1) {
     read.append(buffer, 0, readCount);
     }
     }
     ScriptUtils.exec(sourceUrl);
     ApplicationElement appElement = cache.get(resourceId);
     if (appElement != null && appElement.isModule()) {
     ScriptDocument doc = Dom2ScriptDocument.transform(appElement.getContent());
     JSObject nativeConstr = ScriptUtils.lookupInGlobal(appElement.getId());
     SecuredJSConstructor securedContr = new SecuredJSConstructor(nativeConstr, appElement.getId(), appElement.getTxtContentLength(), appElement.getTxtCrc32(), cache, getPrincipalHost(), doc);
     ScriptUtils.putInGlobal(appElement.getId(), securedContr);
     }
     }
     */
}
