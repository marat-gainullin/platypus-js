/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import com.eas.client.settings.SettingsConstants;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.requests.AccessControlExceptionResponse;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.client.threetier.requests.CommitRequest;
import com.eas.client.threetier.requests.ServerModuleStructureRequest;
import com.eas.client.threetier.requests.DisposeServerModuleRequest;
import com.eas.client.threetier.requests.ExceptionResponse;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import com.eas.client.threetier.requests.RPCRequest;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.client.threetier.requests.ModuleStructureRequest;
import com.eas.client.threetier.requests.PlatypusResponseVisitor;
import com.eas.client.threetier.requests.ResourceRequest;
import com.eas.client.threetier.requests.CredentialRequest;
import com.eas.client.threetier.requests.JsonExceptionResponse;
import com.eas.client.threetier.requests.SqlExceptionResponse;
import com.eas.util.BinaryUtils;
import com.eas.util.JsonUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mg
 */
public class PlatypusHttpResponseReader implements PlatypusResponseVisitor {

    public static final String REPORT_LOCATION_CONTENT_TYPE = "text/platypus-report-location";

    protected HttpURLConnection conn;
    protected int responseCode;
    protected Request request;
    private final byte[] bodyContent;

    public PlatypusHttpResponseReader(Request aRequest, HttpURLConnection aConn, byte[] aBodyContent) throws IOException {
        super();
        request = aRequest;
        conn = aConn;
        responseCode = conn.getResponseCode();
        bodyContent = aBodyContent;
    }

    protected String extractText() throws IOException {
        if (bodyContent != null) {
            String contentType = conn.getContentType();
            String[] contentTypeCharset = contentType != null ? contentType.split(";") : null;
            if (contentTypeCharset == null || contentTypeCharset.length == 0) {
                throw new IOException("Response must contain ContentType header with charset");
            }
            if (!contentTypeCharset[0].toLowerCase().startsWith("text/") && !contentTypeCharset[0].toLowerCase().startsWith("application/json")) {
                throw new IOException("Response header 'ContentType' must be text/... or application/json");
            }
            if (contentTypeCharset.length > 1) {
                String[] charsetNameValue = contentTypeCharset[1].split("=");
                if (charsetNameValue == null || charsetNameValue.length != 2) {
                    throw new IOException("Response must contain ContentType header with charset=... clause");
                }
                if (!charsetNameValue[0].equalsIgnoreCase("charset")) {
                    throw new IOException("Response ContentType must be formatted as following: text/...;charset=...");
                }
                return new String(bodyContent, charsetNameValue[1].trim());
            } else {
                return new String(bodyContent, SettingsConstants.COMMON_ENCODING);
            }
        } else {
            return null;
        }
    }

    @Override
    public void visit(ExceptionResponse rsp) throws Exception {
        // Error response has no body
    }

    @Override
    public void visit(AccessControlExceptionResponse rsp) throws Exception {
        // Error response has no body
    }

    @Override
    public void visit(JsonExceptionResponse rsp) throws Exception {
        String json = extractText();
        rsp.setJsonContent(json);
    }

    @Override
    public void visit(SqlExceptionResponse rsp) throws Exception {
        // Error response has no body
    }

    @Override
    public void visit(CredentialRequest.Response rsp) throws Exception {
        String text = extractText();
        Pattern userNamePattern = Pattern.compile("\\{\\s*userName\\s*:(.+)\\}");
        Matcher m = userNamePattern.matcher(text);
        if (m.find()) {
            String userName = m.group(1);
            rsp.setName(JsonUtils.jsonUnescape(userName));
        }
    }

    @Override
    public void visit(ExecuteQueryRequest.Response rsp) throws Exception {
        String json = extractText();
        rsp.setJson(json);
    }

    @Override
    public void visit(LogoutRequest.Response rsp) throws Exception {
        // response is simple OK
    }

    @Override
    public void visit(RPCRequest.Response rsp) throws Exception {
        if (conn.getContentType() != null && conn.getContentType().toLowerCase().startsWith(REPORT_LOCATION_CONTENT_TYPE)) {
            String reportLocation = extractText();
            URL currentUrl = conn.getURL();
            URL reportUrl = new URL(currentUrl.getProtocol(), currentUrl.getHost(), currentUrl.getPort(), reportLocation);
            rsp.setResult(reportUrl);
        } else {
            String text = extractText();
            rsp.setResult(text);
        }
    }

    @Override
    public void visit(DisposeServerModuleRequest.Response rsp) throws Exception {
        // response is simple OK
    }

    @Override
    public void visit(ServerModuleStructureRequest.Response rsp) throws Exception {
        if (responseCode == HttpURLConnection.HTTP_OK) {
            long timeStamp = conn.getLastModified();
            rsp.setTimeStamp(new Date(timeStamp));
            rsp.setInfoJson(extractText());
        } else if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
            rsp.setInfoJson(null);
            rsp.setTimeStamp(null);
        }
    }

    @Override
    public void visit(ResourceRequest.Response rsp) throws Exception {
        if (responseCode == HttpURLConnection.HTTP_OK) {
            long timeStamp = conn.getLastModified();
            rsp.setTimeStamp(new Date(timeStamp));
            if (bodyContent != null) {
                rsp.setContent(bodyContent);
            } else {
                try (InputStream is = conn.getInputStream()) {
                    byte[] content = BinaryUtils.readStream(is, -1);
                    rsp.setContent(content);
                }
            }
        } else if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
            rsp.setContent(null);
            rsp.setTimeStamp(null);
        }
    }

    @Override
    public void visit(AppQueryRequest.Response rsp) throws Exception {
        if (responseCode == HttpURLConnection.HTTP_OK) {
            long timeStamp = conn.getLastModified();
            rsp.setTimeStamp(new Date(timeStamp));
            rsp.setAppQueryJson(extractText());
        } else if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
            rsp.setAppQueryJson(null);
            rsp.setTimeStamp(null);
        }
    }

    @Override
    public void visit(CommitRequest.Response rsp) throws Exception {
        String text = extractText();
        int updated = Double.valueOf(text).intValue();
        rsp.setUpdated(updated);
    }

    @Override
    public void visit(ModuleStructureRequest.Response rsp) throws Exception {
        rsp.setJson(extractText());
    }
}
