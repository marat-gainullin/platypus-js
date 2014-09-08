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
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.IDN;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
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

    private static final Pattern pattern = Pattern.compile("https?://.*");
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
    /*
     public static String getApplicationPath() throws Exception {
     return client.getAppCache().getApplicationPath();
     }
     */
    /**
     * Loads a resource's bytes either from disk or from datatbase.
     *
     * @param aResourceName An relative path to the resource
     * @return Bytes for resource
     * @throws Exception If some error occurs when reading the resource
     */
    public static byte[] load(String aResourceName) throws Exception {
        if (aResourceName != null && !aResourceName.isEmpty()) {
            Matcher htppMatcher = pattern.matcher(aResourceName);
            if (htppMatcher.matches()) {
                URL url = new URL(aResourceName);
                try {
                    try (InputStream is = url.openStream()) {
                        return BinaryUtils.readStream(is, -1);
                    }
                } catch (IOException ex) {
                    url = encodeUrl(url);
                    try (InputStream is = url.openStream()) {
                        return BinaryUtils.readStream(is, -1);
                    }
                }
            } else {
                String resourceId = normalizeResourcePath(aResourceName);
                String sourcesPath = app.getModules().getLocalPath();
                File resourceFile = new File(sourcesPath + File.separator + resourceId);
                if (resourceFile.exists() && !resourceFile.isDirectory()) {
                    return FileUtils.readBytes(resourceFile);
                } else {
                    throw new IllegalArgumentException(String.format("Resource %s not found", aResourceName));
                }
                //}
            }
        } else {
            return null;
        }
    }

    /**
     * Loads a resource as text for UTF-8 encoding.
     *
     * @param aResourceId An relative path to the resource
     * @return Resource's text
     * @throws Exception If some error occurs when reading the resource
     */
    public static String loadText(String aResourceId) throws Exception {
        return loadText(aResourceId, SettingsConstants.COMMON_ENCODING);
    }

    /**
     * Loads a resource as text.
     *
     * @param aResourceId An relative path to the resource
     * @param aEncodingName Encoding name
     * @return Resource's text
     * @throws Exception If some error occurs when reading the resource
     */
    public static String loadText(String aResourceId, String aEncodingName) throws Exception {
        if (aEncodingName == null) {
            aEncodingName = SettingsConstants.COMMON_ENCODING;
        }
        byte[] data = null;
        Matcher htppMatcher = pattern.matcher(aResourceId);
        if (htppMatcher.matches()) {
            URL url = new URL(aResourceId);
            URLConnection conn = null;
            InputStream is = null;
            try {
                conn = url.openConnection();
                conn.setRequestProperty("accept-encoding", "deflate");
                ((HttpURLConnection) conn).getResponseCode();
                is = conn.getInputStream();
            } catch (IOException ex) {
                url = encodeUrl(url);
                conn = url.openConnection();
                ((HttpURLConnection) conn).getResponseCode();
                is = conn.getInputStream();
            }
            String contentEncoding = conn.getContentEncoding();
            if (contentEncoding != null) {
                if (contentEncoding.contains("gzip") || contentEncoding.contains("zip")) {
                    is = new GZIPInputStream(is);
                } else if (contentEncoding.contains("deflate")) {
                    is = new InflaterInputStream(conn.getInputStream());
                }
            }
            try (InputStream _is = is) {
                data = BinaryUtils.readStream(_is, -1);
                String contentType = conn.getContentType();
                if (contentType != null) {
                    contentType = contentType.replaceAll("\\s+", "").toLowerCase();
                    if (contentType.contains(";charset=")) {
                        String[] typeCharset = contentType.split(";charset=");
                        if (typeCharset.length == 2 && typeCharset[1] != null) {
                            aEncodingName = typeCharset[1];
                        } else {
                            Logger.getLogger(ScriptedResource.class.getName()).log(Level.WARNING, ENCODING_MISSING_MSG, aEncodingName);
                        }
                    } else {
                        Logger.getLogger(ScriptedResource.class.getName()).log(Level.WARNING, ENCODING_MISSING_MSG, aEncodingName);
                    }
                } else {
                    Logger.getLogger(ScriptedResource.class.getName()).log(Level.WARNING, ENCODING_MISSING_MSG, aEncodingName);
                }
            }
        } else {
            data = load(aResourceId);
        }
        if (!Charset.isSupported(aEncodingName)) {
            throw new IllegalStateException("Encoding: " + aEncodingName + " is not supported.");
        }
        return data != null ? new String(data, aEncodingName) : null;
    }
    public static final String ENCODING_MISSING_MSG = "Encoding missing in http response. Falling back to {0}";

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

    protected static class RequireProcess extends AsyncProcess<Void> {

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

    public static void require(String[] aScriptsNames, Consumer<Void> onSuccess, Consumer<Exception> onFailure) throws Exception {
        jsRequire(aScriptsNames, new ConcurrentSkipListSet<>(), onSuccess, onFailure);
    }

    public static void jsRequire(String[] aScriptsNames, Set<String> required, Consumer<Void> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (aScriptsNames != null && aScriptsNames.length > 0) {
            aScriptsNames = Arrays.asList(aScriptsNames).stream().filter((String aScriptName) -> {
                return !required.contains(aScriptName);
            }).toArray((int aLength) -> {
                return new String[aLength];
            });
            RequireProcess scriptsProceses = new RequireProcess(aScriptsNames.length, onSuccess, onFailure);
            for (String scriptName : aScriptsNames) {
                required.add(scriptName);
                app.getModules().getModule(scriptName, (ModuleStructure structure) -> {
                    AppElementFiles files = structure.getParts();
                    File sourceFile = files.findFileByExtension(PlatypusFiles.JAVASCRIPT_EXTENSION);

                    RequireProcess scriptProcess = new RequireProcess(3, (Void v) -> {
                        try {
                            URL sourceUrl = sourceFile.toURI().toURL();
                            ScriptUtils.exec(sourceUrl);
                            String source = FileUtils.readString(sourceFile, SettingsConstants.COMMON_ENCODING);
                            ScriptDocument config = ScriptDocument.parse(source);
                            JSObject nativeConstr = ScriptUtils.lookupInGlobal(scriptName);
                            if (nativeConstr instanceof JSObjectFacade) {
                                nativeConstr = ((JSObjectFacade) nativeConstr).getDelegate();
                            }
                            SecuredJSConstructor securedConstr = new SecuredJSConstructor(nativeConstr, scriptName, null, config);
                            ScriptUtils.putInGlobal(scriptName, securedConstr);
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
                        jsRequire(structure.getClientDependencies().toArray(new String[]{}), required, (Void v) -> {
                            scriptProcess.complete(null, null);
                        }, (Exception ex) -> {
                            scriptProcess.complete(null, ex);
                        });
                    } catch (Exception ex) {
                        scriptProcess.complete(null, ex);
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
        jsRequire(aScriptsNames, new ConcurrentSkipListSet<>());
    }

    public static void jsRequire(String[] aScriptsNames, Set<String> required) throws Exception {
        for (String aScriptName : aScriptsNames) {
            if (!required.contains(aScriptName)) {
                required.add(aScriptName);
                ModuleStructure structure = app.getModules().getModule(aScriptName, null, null);
                AppElementFiles files = structure.getParts();
                File sourceFile = files.findFileByExtension(PlatypusFiles.JAVASCRIPT_EXTENSION);
                URL sourceUrl = sourceFile.toURI().toURL();
                if (files.isModule()) {
                    String source = FileUtils.readString(sourceFile, SettingsConstants.COMMON_ENCODING);
                    ScriptDocument config = ScriptDocument.parse(source);
                    JSObject nativeConstr = ScriptUtils.lookupInGlobal(aScriptName);
                    if (nativeConstr instanceof JSObjectFacade) {
                        nativeConstr = ((JSObjectFacade) nativeConstr).getDelegate();
                    }
                    SecuredJSConstructor securedContr = new SecuredJSConstructor(nativeConstr, aScriptName, null, config);
                    ScriptUtils.putInGlobal(aScriptName, securedContr);
                    qRequire(structure.getQueryDependencies().toArray(new String[]{}), null, null);
                    sRequire(structure.getServerDependencies().toArray(new String[]{}), null, null);
                }
                jsRequire(structure.getClientDependencies().toArray(new String[]{}), required, null, null);
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
