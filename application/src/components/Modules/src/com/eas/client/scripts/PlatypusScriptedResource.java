package com.eas.client.scripts;

import com.eas.client.AppCache;
import com.eas.client.Client;
import com.eas.client.ClientConstants;
import com.eas.client.login.PrincipalHost;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.scripts.store.Dom2ScriptDocument;
import com.eas.client.settings.SettingsConstants;
import com.eas.client.threetier.PlatypusClient;
import com.eas.script.ScriptUtils;
import com.eas.util.BinaryUtils;
import java.io.File;
import java.io.FileNotFoundException;
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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.api.scripting.URLReader;

/**
 *
 * @author vv
 */
public class PlatypusScriptedResource {

    private static final Pattern pattern = Pattern.compile("https?://.*");
    protected static Client client;
    protected static AppCache cache;
    protected static PrincipalHost principalHost;

    /**
     * Initializes a static fields.
     *
     * @param aClient Client instance
     * @param aPrincipalHost Login support
     * @throws Exception If something goes wrong
     */
    public static void init(Client aClient, PrincipalHost aPrincipalHost) throws Exception {
        assert cache == null : "Platypus application resources may be initialized only once.";
        client = aClient;
        cache = client.getAppCache();
        principalHost = aPrincipalHost;
    }

    /**
     * Do not use. Only for tests.
     * @param aClient
     * @param aPrincipalHost
     * @throws Exception 
     */
    public static void initForTests(Client aClient, PrincipalHost aPrincipalHost) throws Exception { 
        client = aClient;
        cache = client.getAppCache();
        principalHost = aPrincipalHost;
    }
    
    /**
     * Gets an principal provider.
     *
     * @return Principal host instance
     */
    public static PrincipalHost getPrincipalHost() {
        return principalHost;
    }

    /**
     * Gets an absolute path to the application's directory.
     *
     * @return Application's directory full path or null if not path is not
     * avaliable
     * @throws java.lang.Exception
     */
    public static String getApplicationPath() throws Exception {
        return client.getAppCache().getApplicationPath();
    }

    /**
     * Loads a resource's bytes either from disk or from datatbase.
     *
     * @param aResourceId An relative path to the resource
     * @return Bytes for resource
     * @throws Exception If some error occurs when reading the resource
     */
    public static byte[] load(String aResourceId) throws Exception {
        if (aResourceId != null && !aResourceId.isEmpty()) {
            Matcher htppMatcher = pattern.matcher(aResourceId);
            if (htppMatcher.matches()) {
                URL url = new URL(aResourceId);
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
                if (cache == null) {
                    throw new IllegalStateException("Platypus application resources have to be initialized first.");
                }
                String resourceId = normalizeResourcePath(aResourceId);
                ApplicationElement appElement = cache.get(resourceId);
                if (appElement != null) {
                    if (appElement.getType() == ClientConstants.ET_RESOURCE) {
                        // let's check actuality
                        if (!cache.isActual(appElement.getId(), appElement.getTxtContentLength(), appElement.getTxtCrc32())) {
                            cache.remove(appElement.getId());
                            appElement = cache.get(resourceId);
                        }
                    } else {
                        throw new NotResourceException(resourceId);
                    }
                }
                if (appElement != null && appElement.getType() == ClientConstants.ET_RESOURCE) {
                    return appElement.getBinaryContent();
                } else {
                    throw new IllegalArgumentException(String.format("Resource %s not found", aResourceId));
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
                            Logger.getLogger(PlatypusScriptedResource.class.getName()).log(Level.WARNING, ENCODING_MISSING_MSG, aEncodingName);
                        }
                    } else {
                        Logger.getLogger(PlatypusScriptedResource.class.getName()).log(Level.WARNING, ENCODING_MISSING_MSG, aEncodingName);
                    }
                } else {
                    Logger.getLogger(PlatypusScriptedResource.class.getName()).log(Level.WARNING, ENCODING_MISSING_MSG, aEncodingName);
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

    public static String translateScriptPath(String aResourceId) throws Exception {
        if (aResourceId != null && !aResourceId.isEmpty()) {
            Matcher httpMatcher = pattern.matcher(aResourceId);
            if (httpMatcher.matches()) {
                return aResourceId;
            } else {
                if (cache == null) {
                    throw new IllegalStateException("Platypus application resources have to be initialized first.");
                }
                String resourceId = normalizeResourcePath(aResourceId);
                String appElementCachedPath = cache.translateScriptPath(resourceId);
                if (appElementCachedPath != null) {
                    return appElementCachedPath;
                } else {
                    throw new IllegalArgumentException(String.format("Resource %s not found", aResourceId));
                }
            }
        } else {
            return null;
        }
    }

    public static Client getClient() {
        return client;
    }

    public static PlatypusClient getPlatypusClient() {
        if (client instanceof PlatypusClient) {
            return (PlatypusClient)client;
        }
        return null;
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

    protected static class NotResourceException extends Exception {

        protected String resourceId;

        public NotResourceException() {
            super();
        }

        public NotResourceException(String aResourceId) {
            super(aResourceId + " is not a platypus resource. Hint: may be it is regular platypus module.");
            resourceId = aResourceId;
        }

        public String getResourceId() {
            return resourceId;
        }
    }

    /**
     * Accounting of already executed scripts. Allows to avoid reexecution.
     */
    protected static Set<String> executedScriptResources = new HashSet<>();

    /**
     * Executes a plain js resource.
     *
     * @param aResourceId
     * @throws Exception
     */
    public static void executeScriptResource(final String aResourceId) throws Exception {
        executeScriptResource(aResourceId, null);
    }

    public static void executeScriptResource(final String aResourceId, SecuredJSConstructor aPrevConstrVersion) throws Exception {
        final String resourceId = PlatypusScriptedResource.normalizeResourcePath(aResourceId);
        if (!executedScriptResources.contains(resourceId) || aPrevConstrVersion != null) {
            executedScriptResources.add(resourceId);
            String sourcePath = PlatypusScriptedResource.translateScriptPath(resourceId);
            if (sourcePath != null) {
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
                DependenciesWalker walker = new DependenciesWalker(read.toString(), cache);
                walker.walk();
                for (String aDependence : walker.getDependencies()) {
                    executeScriptResource(aDependence);
                }
                ScriptUtils.exec(sourceUrl);
                ApplicationElement appElement = cache.get(resourceId);
                if (appElement != null && appElement.isModule()) {
                    ScriptDocument doc = Dom2ScriptDocument.transform(appElement.getContent());
                    JSObject nativeConstr = ScriptUtils.lookupInGlobal(appElement.getId());
                    SecuredJSConstructor securedContr;
                    if (aPrevConstrVersion != null) {
                        securedContr = aPrevConstrVersion;
                        aPrevConstrVersion.replace(nativeConstr, appElement.getTxtContentLength(), appElement.getTxtCrc32(), doc);
                    } else {
                        securedContr = new SecuredJSConstructor(nativeConstr, appElement.getId(), appElement.getTxtContentLength(), appElement.getTxtCrc32(), cache, getPrincipalHost(), doc);
                    }
                    ScriptUtils.putInGlobal(appElement.getId(), securedContr);
                }
            } else {
                throw new IllegalArgumentException("Script resource not found: " + resourceId + ". Hint: Regular platypus modules can't be used as resources.");
            }
        }
    }
}
