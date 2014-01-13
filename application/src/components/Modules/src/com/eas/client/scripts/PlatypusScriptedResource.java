/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.scripts;

import com.eas.client.AppCache;
import com.eas.client.Client;
import com.eas.client.ClientConstants;
import com.eas.client.login.PrincipalHost;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.client.settings.EasSettings;
import com.eas.client.settings.SettingsConstants;
import com.eas.script.ScriptFunction;
import com.eas.script.ScriptObj;
import com.eas.util.BinaryUtils;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    protected static CompiledScriptDocumentsHost scriptDocumentsHost;

    /**
     * Initializes a static fields.
     *
     * @param aClient Client instance
     * @param aPrincipalHost Login support
     * @param aScriptDocumentsHost Scripts host
     * @throws Exception If something goes wrong
     */
    public static void init(Client aClient, PrincipalHost aPrincipalHost, CompiledScriptDocumentsHost aScriptDocumentsHost) throws Exception {
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
    public static CompiledScriptDocumentsHost getScriptDocumentsHost() {
        return scriptDocumentsHost;
    }

    /**
     * Gets an absolute path to the application's directory.
     *
     * @return Application's directory full path or null if not path is not
     * avaliable
     */
    public static String getApplicationPath() {
        EasSettings settings = client.getSettings();
        if (settings instanceof DbConnectionSettings) {
            return ((DbConnectionSettings) settings).getApplicationPath();
        } else {
            return null;
        }
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

                String resourceId = translateResourcePath(aResourceId);
                /*
                 File test = new File(resourceId);
                 if (test.exists()) {
                 return FileUtils.readBytes(test);
                 } else {
                 */
                ApplicationElement appElement = cache.get(resourceId);
                if (appElement != null) {
                    if (appElement.getType() == ClientConstants.ET_RESOURCE) {
                        // let's check actuality
                        if (!cache.isActual(appElement.getId(), appElement.getTxtContentLength(), appElement.getTxtCrc32())) {
                            cache.remove(appElement.getId());
                            appElement = cache.get(resourceId);
                        }
                    } else {
                        throw new ScriptRunner.NotResourceException(resourceId);
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

    protected static String translateResourcePath(String aPath) throws Exception {
        /*
         File test = new File(aPath);
         if (test.exists()) {
         // it seems, that id is a real file path
         return test.getPath();
         } else {
         */
        if (aPath.startsWith("/")) {
            throw new IllegalStateException("Platypus resource path can't begin with /. Platypus resource paths must point somewhere in application, but not in filesystem.");
        }
        if (aPath.startsWith("..") || aPath.startsWith(".")) {
            /*
             EvaluatorException ex = Context.reportRuntimeError("_");
             ScriptStackElement[] stack = ex.getScriptStack();
             traverse stack to reach non platypusStandardLib script and use it as base path
             */
            throw new IllegalStateException("Platypus resource paths must be application-absolute. \"" + aPath + "\" is not application-absolute");
        }
        URI uri = new URI(null, null, aPath, null);
        return uri.normalize().getPath();
        //}
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
}
