package com.eas.client.scripts;

import com.eas.client.AppCache;
import com.eas.client.Client;
import com.eas.client.ClientConstants;
import com.eas.client.login.PrincipalHost;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.settings.SettingsConstants;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptObj;
import com.eas.script.ScriptUtils;
import com.eas.util.BinaryUtils;
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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

/**
 *
 * @author vv
 */
@ScriptObj(name = "Resource", jsDoc = "/**\n"
        + "* Support object for resources loading.\n"
        + "*/")
public class PlatypusScriptedResource {

    private static final Pattern pattern = Pattern.compile("https?://.*");
    protected static Client client;
    protected static AppCache cache;
    protected static PrincipalHost principalHost;
    protected static ScriptDocumentsHost scriptDocumentsHost;

    /**
     * Initializes a static fields.
     *
     * @param aClient Client instance
     * @param aPrincipalHost Login support
     * @param aScriptDocumentsHost Scripts host
     * @throws Exception If something goes wrong
     */
    public static void init(Client aClient, PrincipalHost aPrincipalHost, ScriptDocumentsHost aScriptDocumentsHost) throws Exception {
        assert cache == null : "Platypus application resources may be initialized only once.";
        client = aClient;
        cache = client.getAppCache();
        principalHost = aPrincipalHost;
        scriptDocumentsHost = aScriptDocumentsHost;
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
     * Gets script documents host.
     *
     * @return Script documents host instance
     */
    public static ScriptDocumentsHost getScriptDocumentsHost() {
        return scriptDocumentsHost;
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
    @ScriptFunction(params = {"path"}, jsDoc = "/**\n"
            + "* Loads a resource's bytes either from disk or from datatbase.\n"
            + "* @param path a relative path to the resource\n"
            + "* @return the resource as a bytes array\n"
            + "*/")
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
    @ScriptFunction(params = {"path", "encoding"}, jsDoc = ""
            + "/**\n"
            + "* Loads a resource as text.\n"
            + "* @param path an relative path to the resource\n"
            + "* @param encoding an name of the specific encoding, UTF-8 by default (optional). Note: If a resource is loaded via http, http response content type header's charset have a priority.\n"
            + "* @return the resource as a <code>string</code>\n"
            + "*/")
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

    @ScriptFunction(params = {"aResourceName"}, jsDoc = ""
            + "/**\n"
            + "* Translates an application element name into local path name.\n"
            + "* Takes into account file cache in case of in-database application storage."
            + "* Bypasses http[s] urls."
            + "* Extension is omitted to give client code a chance to load various parts of an"
            + "application element (e.g. js source file or model definition or form/report template)."
            + "* @param aResourceName an relative path to the resource in an application.\n"
            + "* @return The local path name to the application element files without extension.\n"
            + "*/")
    public static String translateScriptPath(String aResourceId) throws Exception {
        if (aResourceId != null && !aResourceId.isEmpty()) {
            Matcher htppMatcher = pattern.matcher(aResourceId);
            if (htppMatcher.matches()) {
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

    protected static Client getClient() {
        return client;
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
        final String resourceId = PlatypusScriptedResource.normalizeResourcePath(aResourceId);
        if (!executedScriptResources.contains(resourceId)) {
            try {
                String sourcePath = PlatypusScriptedResource.translateScriptPath(resourceId);
                if (sourcePath != null) {
                    URL sourceUrl;
                    File test = new File(sourcePath);
                    if (test.exists()) {
                        sourceUrl = test.toURI().toURL();
                    } else {
                        sourceUrl = new URL(sourcePath);
                    }
                    ScriptUtils.exec(sourceUrl);
                } else {
                    throw new IllegalArgumentException("Script resource not found: " + resourceId + ". Hint: Regular platypus modules can't be used as resources.");
                }
            } catch (NotResourceException ex) {
                // Silently return.
                // There are cases, when require is called with regular platypus module id.
                // In such case, we have to ignore require call in SE client, server and servlet (because of automatic dependecies resolution),
                // and perform standard actions for html5 browser client.
                return;
            }
            executedScriptResources.add(resourceId);
        }
    }
}
