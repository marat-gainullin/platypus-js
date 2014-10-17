/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import com.bearsoft.rowset.Converter;
import com.bearsoft.rowset.Row;
import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.report.Report;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.client.threetier.requests.CommitRequest;
import com.eas.client.threetier.requests.CreateServerModuleRequest;
import com.eas.client.threetier.requests.DisposeServerModuleRequest;
import com.eas.client.threetier.requests.ErrorResponse;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import com.eas.client.threetier.requests.ExecuteServerModuleMethodRequest;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.client.threetier.requests.ModuleStructureRequest;
import com.eas.client.threetier.requests.PlatypusResponseVisitor;
import com.eas.client.threetier.requests.ResourceRequest;
import com.eas.client.threetier.requests.CredentialRequest;
import com.eas.script.ScriptUtils;
import com.eas.util.BinaryUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class PlatypusHttpResponseReader implements PlatypusResponseVisitor {

    public static final String CREATE_MODULE_RESPONSE_FUNCTIONS_PROP = "functions";
    public static final String CREATE_MODULE_RESPONSE_IS_PERMITTED_PROP = "isPermitted";
    public static final String REPORT_LOCATION_CONTENT_TYPE = "text/platypus-report-location";

    protected HttpURLConnection conn;
    protected int responseCode;
    protected Converter converter;

    public PlatypusHttpResponseReader(HttpURLConnection aConn, Converter aConverter) throws IOException {
        super();
        conn = aConn;
        converter = aConverter;
        responseCode = conn.getResponseCode();
    }

    protected Object extractJSON() throws IOException {
        String contentText = extractText();
        return ScriptUtils.parseJson(contentText);
    }

    protected String extractText() throws IOException {
        try (InputStream in = conn.getInputStream()) {
            byte[] content = BinaryUtils.readStream(in, -1);
            String contentType = conn.getContentType();
            String[] contentTypeCharset = contentType.split(";");
            if (contentTypeCharset == null || contentTypeCharset.length != 2) {
                throw new IllegalStateException("CredentialRequest.Response must contain ContentType header with charset");
            }
            if (!contentTypeCharset[0].toLowerCase().startsWith("text")) {
                throw new IllegalStateException("CredentialRequest.Response ContentType must be text/...");
            }
            String[] charsetNameValue = contentTypeCharset[1].split("=");
            if (charsetNameValue == null || charsetNameValue.length != 2) {
                throw new IllegalStateException("CredentialRequest.Response must contain ContentType header with charset=... clause");
            }
            if (!contentTypeCharset[0].equalsIgnoreCase("charset")) {
                throw new IllegalStateException("CredentialRequest.Response ContentType must be formatted as following: text/...;charset=...");
            }
            return new String(content, contentTypeCharset[1].trim());
        }
    }

    @Override
    public void visit(ErrorResponse rsp) throws Exception {
        // Error response has no body
    }

    @Override
    public void visit(CredentialRequest.Response rsp) throws Exception {
        Object oCredential = extractJSON();
        if (oCredential instanceof JSObject) {
            JSObject jsCredential = (JSObject) oCredential;
            Object oUserName = ScriptUtils.toJava(jsCredential.getMember("userName"));
            rsp.setName((String) oUserName);
        }
    }

    @Override
    public void visit(ExecuteQueryRequest.Response rsp) throws Exception {
        Object oData = extractJSON();
        if (oData instanceof JSObject && ((JSObject) oData).isArray()) {
            Rowset rowset = new Rowset(rsp.getExpectedFields());
            rowset.setConverter(converter);
            Fields fields = rowset.getFields();
            JSObject jsData = (JSObject) oData;
            int length = ((Number) jsData.getMember("length")).intValue();
            for (int i = 0; i < length; i++) {
                JSObject oRow = (JSObject) jsData.getSlot(i);
                rowset.insert();
                Row row = rowset.getCurrentRow();
                for (String pName : oRow.keySet()) {
                    Field field = fields.get(pName);
                    if (field != null) {
                        Object oValue = ScriptUtils.toJava(oRow.getMember(pName));
                        row.setColumnObject(fields.find(pName), converter.convert2RowsetCompatible(oValue, field.getTypeInfo()));
                    }
                }
            }
            rowset.currentToOriginal();
            rsp.setRowset(rowset);
        } else {
            Object javaData = ScriptUtils.toJava(oData);
            if (javaData instanceof String) {
                Number updateCount = Double.valueOf((String) javaData);
                if (updateCount != null) {
                    rsp.setUpdateCount(updateCount.intValue());
                }
            }
        }
    }

    @Override
    public void visit(LogoutRequest.Response rsp) throws Exception {
        // response is simple OK
    }

    @Override
    public void visit(ExecuteServerModuleMethodRequest.Response rsp) throws Exception {
        if (conn.getContentType() != null && conn.getContentType().toLowerCase().startsWith(REPORT_LOCATION_CONTENT_TYPE)) {
            String reportLocation = extractText();
            URL currentUrl = conn.getURL();
            URL reportUrl = new URL(currentUrl.getProtocol(), currentUrl.getHost(), reportLocation);
            try (InputStream in = reportUrl.openStream()) {
                byte[] content = BinaryUtils.readStream(in, -1);
                int slashIdx = reportLocation.lastIndexOf("/");
                String fileName = reportLocation.substring(slashIdx + 1);
                if (fileName.contains(".")) {
                    String[] nameFormat = fileName.split(".");
                    Report report = new Report(content, nameFormat[1], nameFormat[0]);
                    rsp.setResult(report);
                } else {
                    Report report = new Report(content, "unknown", "unknown");
                    rsp.setResult(report);
                }
            }
        } else {
            Object oData = ScriptUtils.parseDates(extractJSON());
            rsp.setResult(oData);
        }
    }

    @Override
    public void visit(DisposeServerModuleRequest.Response rsp) throws Exception {
        // response is simple OK
    }

    @Override
    public void visit(CreateServerModuleRequest.Response rsp) throws Exception {
    }

    @Override
    public void visit(CommitRequest.Response rsp) throws Exception {
        Object oData = extractJSON();
        if (!(oData instanceof JSObject)) {
            Object javaData = ScriptUtils.toJava(oData);
            if (javaData instanceof String) {
                Number updateCount = Double.valueOf((String) javaData);
                if (updateCount != null) {
                    rsp.setUpdated(updateCount.intValue());
                }
            }
        }
    }

    @Override
    public void visit(ModuleStructureRequest.Response rsp) throws Exception {
    }

    @Override
    public void visit(ResourceRequest.Response rsp) throws Exception {
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (InputStream is = conn.getInputStream()) {
                byte[] content = BinaryUtils.readStream(is, -1);
                long stamp = conn.getLastModified();
                rsp.setContent(content);
                rsp.setTimeStamp(stamp != 0 ? new Date(stamp) : null);
            }
        } else if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
            rsp.setContent(null);
            rsp.setTimeStamp(null);
        }
    }

    @Override
    public void visit(AppQueryRequest.Response rsp) throws Exception {
    }

}
