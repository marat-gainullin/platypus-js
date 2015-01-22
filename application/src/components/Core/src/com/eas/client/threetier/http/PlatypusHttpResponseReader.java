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
import com.eas.client.ServerModuleInfo;
import com.eas.client.queries.PlatypusQuery;
import com.eas.client.report.Report;
import com.eas.client.settings.SettingsConstants;
import com.eas.client.threetier.Request;
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
import java.util.HashSet;
import java.util.Set;
import jdk.nashorn.api.scripting.JSObject;
import jdk.nashorn.internal.runtime.JSType;

/**
 *
 * @author mg
 */
public class PlatypusHttpResponseReader implements PlatypusResponseVisitor {

    public static final String SERVER_DEPENDENCIES_PROP_NAME = "serverDependencies";
    public static final String QUERY_DEPENDENCIES_PROP_NAME = "queryDependencies";
    public static final String CLIENT_DEPENDENCIES_PROP_NAME = "clientDependencies";
    public static final String STRUCTURE_PROP_NAME = "structure";
    //
    public static final String CREATE_MODULE_RESPONSE_FUNCTIONS_PROP = "functions";
    public static final String CREATE_MODULE_RESPONSE_IS_PERMITTED_PROP = "isPermitted";
    //
    public static final String REPORT_LOCATION_CONTENT_TYPE = "text/platypus-report-location";

    protected PlatypusHttpConnection pConn;
    protected HttpURLConnection conn;
    protected int responseCode;
    protected Converter converter;
    protected Request request;
    private byte[] bodyContent;

    public PlatypusHttpResponseReader(Request aRequest, HttpURLConnection aConn, Converter aConverter, PlatypusHttpConnection aPConn) throws IOException {
        super();
        request = aRequest;
        conn = aConn;
        converter = aConverter;
        responseCode = conn.getResponseCode();
        pConn = aPConn;
    }

    public boolean checkIfSecirutyForm() throws IOException {
        String contentType = conn.getContentType();
        if ("text/html".equalsIgnoreCase(contentType)) {
            String formContent = extractText();
            return formContent.toLowerCase().contains(PlatypusHttpRequestWriter.J_SECURITY_CHECK_ACTION_NAME);
        } else {
            return false;
        }
    }

    protected Object extractJSON() throws IOException {
        String contentText = extractText();
        return ScriptUtils.parseJson(contentText);
    }

    protected String extractText() throws IOException {
        try (InputStream in = conn.getInputStream()) {
            bodyContent = BinaryUtils.readStream(in, -1);
            String contentType = conn.getContentType();
            String[] contentTypeCharset = contentType.split(";");
            if (contentTypeCharset == null || contentTypeCharset.length == 0) {
                throw new IOException("Response must contain ContentType header with charset");
            }
            if (!contentTypeCharset[0].toLowerCase().startsWith("text/")) {
                throw new IOException("Response ContentType must be text/...");
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
        }
    }

    @Override
    public void visit(ErrorResponse rsp) throws Exception {
        // Error response has no body
    }

    @Override
    public void visit(CredentialRequest.Response rsp) throws Exception {
        JSObject jsCredential = (JSObject) extractJSON();
        String userName = JSType.toString(jsCredential.getMember("userName"));
        rsp.setName(userName);
    }

    @Override
    public void visit(ExecuteQueryRequest.Response rsp) throws Exception {
        Object oData = ScriptUtils.parseDates(extractJSON());
        if (oData instanceof JSObject && ((JSObject) oData).isArray()) {
            Rowset rowset = new Rowset(rsp.getExpectedFields());
            rowset.setConverter(converter);
            Fields fields = rowset.getFields();
            JSObject jsData = (JSObject) oData;
            int length = ((Number) jsData.getMember(LENGTH_PROP_NAME)).intValue();
            for (int i = 0; i < length; i++) {
                JSObject oRow = (JSObject) jsData.getSlot(i);
                Row row = new Row("", fields);
                rowset.insert(row, false);
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
            int updated = JSType.toInteger(oData);
            rsp.setUpdateCount(updated);
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
            URL reportUrl = new URL(currentUrl.getProtocol(), currentUrl.getHost(), currentUrl.getPort(), reportLocation);
            HttpURLConnection reportConn = (HttpURLConnection) reportUrl.openConnection();
            reportConn.setDoInput(true);
            pConn.addCookies(reportConn);
            pConn.checkedAddBasicAuthentication(reportConn);
            try (InputStream in = reportConn.getInputStream()) {
                byte[] content = BinaryUtils.readStream(in, -1);
                int slashIdx = reportLocation.lastIndexOf("/");
                String fileName = reportLocation.substring(slashIdx + 1);
                if (fileName.contains(".")) {
                    String[] nameFormat = fileName.split("\\.");
                    Report report = new Report(content, nameFormat[nameFormat.length - 1], nameFormat[0]);
                    rsp.setResult(report);
                } else {
                    Report report = new Report(content, "unknown", "unknown");
                    rsp.setResult(report);
                }
            }
            pConn.acceptCookies(reportConn);
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
        if (responseCode == HttpURLConnection.HTTP_OK) {
            long timeStamp = conn.getLastModified();
            rsp.setTimeStamp(new Date(timeStamp));
            Set<String> functions = new HashSet<>();
            JSObject jsProxy = (JSObject) extractJSON();
            JSObject jsFunctions = (JSObject) jsProxy.getMember(CREATE_MODULE_RESPONSE_FUNCTIONS_PROP);
            int length = JSType.toInteger(jsFunctions.getMember(LENGTH_PROP_NAME));
            for (int i = 0; i < length; i++) {
                String fName = JSType.toString(jsFunctions.getSlot(i));
                functions.add(fName);
            }
            boolean permitted = JSType.toBoolean(jsProxy.getMember(CREATE_MODULE_RESPONSE_IS_PERMITTED_PROP));
            rsp.setInfo(new ServerModuleInfo(((CreateServerModuleRequest) request).getModuleName(), functions, permitted));
        } else if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
            rsp.setInfo(null);
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
            JSObject jsQuery = (JSObject) extractJSON();
            PlatypusQuery query = QueryJSONReader.read(jsQuery);
            rsp.setAppQuery(query);
        } else if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
            rsp.setAppQuery(null);
            rsp.setTimeStamp(null);
        }
    }

    @Override
    public void visit(CommitRequest.Response rsp) throws Exception {
        Object oData = extractJSON();
        int updated = JSType.toInteger(oData);
        rsp.setUpdated(updated);
    }

    @Override
    public void visit(ModuleStructureRequest.Response rsp) throws Exception {
        JSObject jsStructure = (JSObject) extractJSON();
        JSObject jsParts = (JSObject) jsStructure.getMember(STRUCTURE_PROP_NAME);
        int partsLength = JSType.toInteger(jsParts.getMember(LENGTH_PROP_NAME));
        for (int i = 0; i < partsLength; i++) {
            String part = JSType.toString(jsParts.getSlot(i));
            rsp.getStructure().add(part);
        }
        JSObject jsClientDependencies = (JSObject) jsStructure.getMember(CLIENT_DEPENDENCIES_PROP_NAME);
        int clientDepsLength = JSType.toInteger(jsClientDependencies.getMember(LENGTH_PROP_NAME));
        for (int i = 0; i < clientDepsLength; i++) {
            String dep = JSType.toString(jsClientDependencies.getSlot(i));
            rsp.getClientDependencies().add(dep);
        }
        JSObject jsQueryDependencies = (JSObject) jsStructure.getMember(QUERY_DEPENDENCIES_PROP_NAME);
        int queryDepsLength = JSType.toInteger(jsQueryDependencies.getMember(LENGTH_PROP_NAME));
        for (int i = 0; i < queryDepsLength; i++) {
            String dep = JSType.toString(jsQueryDependencies.getSlot(i));
            rsp.getQueryDependencies().add(dep);
        }
        JSObject jsServerDependencies = (JSObject) jsStructure.getMember(SERVER_DEPENDENCIES_PROP_NAME);
        int serverDepsLength = JSType.toInteger(jsServerDependencies.getMember(LENGTH_PROP_NAME));
        for (int i = 0; i < serverDepsLength; i++) {
            String dep = JSType.toString(jsServerDependencies.getSlot(i));
            rsp.getServerDependencies().add(dep);
        }
    }
    protected static final String LENGTH_PROP_NAME = "length";
}
