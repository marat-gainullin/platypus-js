/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.model.application.ApplicationDbEntity;
import com.eas.client.queries.Query;
import com.eas.client.report.Report;
import com.eas.client.settings.SettingsConstants;
import com.eas.client.threetier.RowsetJsonConstants;
import com.eas.client.threetier.RowsetJsonWriter;
import com.eas.client.threetier.http.PlatypusHttpConstants;
import com.eas.client.threetier.http.PlatypusHttpResponseReader;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.client.threetier.requests.CommitRequest;
import com.eas.client.threetier.requests.CreateServerModuleRequest;
import com.eas.client.threetier.requests.CredentialRequest;
import com.eas.client.threetier.requests.DisposeServerModuleRequest;
import com.eas.client.threetier.requests.ErrorResponse;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import com.eas.client.threetier.requests.ExecuteServerModuleMethodRequest;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.client.threetier.requests.ModuleStructureRequest;
import com.eas.client.threetier.requests.PlatypusResponseVisitor;
import com.eas.client.threetier.requests.ResourceRequest;
import com.eas.script.ScriptUtils;
import com.eas.server.httpservlet.serial.query.QueryJsonWriter;
import com.eas.util.JSONUtils;
import com.eas.util.StringUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class PlatypusHttpResponseWriter implements PlatypusResponseVisitor {

    protected HttpServletResponse servletResponse;
    protected HttpServletRequest servletRequest;

    public PlatypusHttpResponseWriter(HttpServletResponse aServletResponse, HttpServletRequest aServletRequest) {
        super();
        servletRequest = aServletRequest;
        servletResponse = aServletResponse;
    }

    @Override
    public void visit(ErrorResponse resp) throws Exception {
        if (resp.isAccessControl()) {
            servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, resp.getErrorMessage());
        } else {
            servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp.getErrorMessage());
        }
    }

    @Override
    public void visit(CredentialRequest.Response resp) throws Exception {
        String name = ((CredentialRequest.Response) resp).getName();
        StringBuilder content = JSONUtils.o(new StringBuilder("userName"), JSONUtils.s(name));
        writeJsonResponse(content.toString(), servletResponse);
    }

    @Override
    public void visit(ExecuteQueryRequest.Response rsp) throws Exception {
        makeResponseNotCacheable(servletResponse);
        ExecuteQueryRequest.Response resp = (ExecuteQueryRequest.Response) rsp;
        if (resp.getRowset() != null) {
            writeResponse(resp.getRowset(), servletResponse);
        } else {
            writeJsonResponse(resp.getUpdateCount() + "", servletResponse);
        }
    }

    @Override
    public void visit(LogoutRequest.Response resp) throws Exception {
        // logout is processed out of this class
    }

    @Override
    public void visit(ExecuteServerModuleMethodRequest.Response resp) throws Exception {
        final Object result = ((ExecuteServerModuleMethodRequest.Response) resp).getResult();
        makeResponseNotCacheable(servletResponse);
//        if (result instanceof Rowset) {
//           
//        } else 
        if (result instanceof String) {
            writeJsonResponse(ScriptUtils.toJson(result), servletResponse);
        } else if (result instanceof JSObject) {
            JSObject jsResult = (JSObject) result;
            JSObject p = ScriptUtils.lookupInGlobal("P");
            if (p != null) {
                Object reportClass = p.getMember("Report");
                Object dbEntityClass = p.getMember("ApplicationDbEntity");
                if (jsResult.isInstanceOf(reportClass)) {
                    Report report = (Report) ((JSObject) jsResult.getMember("unwrap")).call(null, new Object[]{});
                    String docsRoot = servletRequest.getServletContext().getRealPath("/");
                    String userName = servletRequest.getUserPrincipal() != null ? servletRequest.getUserPrincipal().getName() : servletRequest.getSession().getId();
                    String userHomeInApplication = "/reports/" + userName + "/";
                    File userDir = new File(docsRoot + userHomeInApplication);
                    if (!userDir.exists()) {
                        userDir.mkdirs();
                    }
                    String reportName = report.getName() + IDGenerator.genID() + "." + report.getFormat();
                    File rep = new File(docsRoot + userHomeInApplication + reportName);
                    try (FileOutputStream out = new FileOutputStream(rep)) {
                        out.write(report.getReport());
                        out.flush();
                    }
                    String reportLocation = userHomeInApplication + reportName;
                    if (!"/".equals(servletRequest.getContextPath())) {
                        reportLocation = servletRequest.getContextPath() + reportLocation;
                    }
                    reportLocation = new URI(null, null, reportLocation, null).toASCIIString();
                    writeResponse(reportLocation, servletResponse, PlatypusHttpResponseReader.REPORT_LOCATION_CONTENT_TYPE);
                } else if (jsResult.isInstanceOf(dbEntityClass)) {
                    ApplicationDbEntity entity = (ApplicationDbEntity) ((JSObject) jsResult.getMember("unwrap")).call(null, new Object[]{});
                    writeResponse(entity.getRowset(), servletResponse);
                } else {
                    writeJsonResponse(ScriptUtils.toJson(result), servletResponse);
                }
            } else {
                writeJsonResponse(ScriptUtils.toJson(result), servletResponse);
            }
        } else {// including null result
            writeJsonResponse(ScriptUtils.toJson(ScriptUtils.toJs(result)), servletResponse);
        }
    }

    @Override
    public void visit(DisposeServerModuleRequest.Response resp) throws Exception {
        // simple OK response is needed
    }

    @Override
    public void visit(CreateServerModuleRequest.Response resp) throws Exception {
        CreateServerModuleRequest.Response csmr = (CreateServerModuleRequest.Response) resp;
        if (csmr.getInfo() != null) {
            assert resp.getTimeStamp() != null;
            servletResponse.setDateHeader(PlatypusHttpConstants.HEADER_LAST_MODIFIED, resp.getTimeStamp().getTime());
            writeJsonResponse(moduleResponseToJson(csmr.getInfo().getFunctionsNames(), csmr.getInfo().isPermitted()), servletResponse);
        } else {
            servletResponse.sendError(HttpURLConnection.HTTP_NOT_MODIFIED);
        }
    }

    @Override
    public void visit(ResourceRequest.Response resp) throws Exception {
        // resources are processed out of this class by servlet container
    }

    @Override
    public void visit(AppQueryRequest.Response resp) throws Exception {
        AppQueryRequest.Response aqr = ((AppQueryRequest.Response) resp);
        if (aqr.getAppQuery() != null) {
            assert resp.getTimeStamp() != null;
            servletResponse.setDateHeader(PlatypusHttpConstants.HEADER_LAST_MODIFIED, resp.getTimeStamp().getTime());
            writeResponse(aqr.getAppQuery(), servletResponse);
        } else {
            servletResponse.sendError(HttpURLConnection.HTTP_NOT_MODIFIED);
        }
    }

    @Override
    public void visit(CommitRequest.Response resp) throws Exception {
        writeJsonResponse(resp.getUpdated() + "", servletResponse);
    }

    @Override
    public void visit(ModuleStructureRequest.Response resp) throws Exception {
        StringBuilder content = JSONUtils.o(new StringBuilder(PlatypusHttpResponseReader.STRUCTURE_PROP_NAME), JSONUtils.as(resp.getStructure().toArray(new String[]{})),
                new StringBuilder(PlatypusHttpResponseReader.CLIENT_DEPENDENCIES_PROP_NAME), JSONUtils.as(resp.getClientDependencies().toArray(new String[]{})),
                new StringBuilder(PlatypusHttpResponseReader.QUERY_DEPENDENCIES_PROP_NAME), JSONUtils.as(resp.getQueryDependencies().toArray(new String[]{})),
                new StringBuilder(PlatypusHttpResponseReader.SERVER_DEPENDENCIES_PROP_NAME), JSONUtils.as(resp.getServerDependencies().toArray(new String[]{}))
        );
        makeResponseNotCacheable(servletResponse);
        writeJsonResponse(content.toString(), servletResponse);
    }

    private static void makeResponseNotCacheable(HttpServletResponse aHttpResponse) {
        aHttpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        aHttpResponse.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        aHttpResponse.setDateHeader("Expires", 0);// Proxies
    }

    private static String moduleResponseToJson(Set<String> functionsNames, boolean isPermitted) {
        return (new StringBuilder())
                .append("{")
                .append("\"").append(PlatypusHttpResponseReader.CREATE_MODULE_RESPONSE_FUNCTIONS_PROP).append("\"")
                .append(":")
                .append("[").append(functionsNames.isEmpty() ? "" : "\"").append(StringUtils.join("\", \"", functionsNames.toArray(new String[]{}))).append(functionsNames.isEmpty() ? "" : "\"").append("]")
                .append(",")
                .append("\"").append(PlatypusHttpResponseReader.CREATE_MODULE_RESPONSE_IS_PERMITTED_PROP).append("\"")
                .append(":")
                .append(isPermitted)
                .append("}")
                .toString();
    }

    private static void writeResponse(Rowset aRowset, HttpServletResponse aResponse) throws Exception {
        RowsetJsonWriter writer = new RowsetJsonWriter(aRowset);
        String json = writer.write();
        writeJsonResponse(json, aResponse);
    }

    private static void writeResponse(Query query, HttpServletResponse aHttpResponse) throws Exception {
        QueryJsonWriter writer = new QueryJsonWriter(query);
        writeJsonResponse(writer.write(), aHttpResponse);
    }

    private static void writeResponse(String aResponse, HttpServletResponse aHttpResponse, String aContentType) throws UnsupportedEncodingException, IOException {
        byte[] bytes = aResponse.getBytes(SettingsConstants.COMMON_ENCODING);
        aHttpResponse.setCharacterEncoding(SettingsConstants.COMMON_ENCODING);
        if (aHttpResponse.getContentType() == null) {
            aHttpResponse.setContentType(aContentType);
        }
        aHttpResponse.setContentLength(bytes.length);
        aHttpResponse.getOutputStream().write(bytes);
        aHttpResponse.getOutputStream().flush();
    }

    public static void writeJsonResponse(String aResponse, HttpServletResponse aHttpResponse) throws UnsupportedEncodingException, IOException {
        writeResponse(aResponse, aHttpResponse, RowsetJsonConstants.JSON_CONTENTTYPE);
    }
}
