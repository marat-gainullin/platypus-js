/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.queries.Query;
import com.eas.client.report.Report;
import com.eas.client.settings.SettingsConstants;
import com.eas.client.threetier.RowsetJsonConstants;
import com.eas.client.threetier.RowsetJsonWriter;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.client.threetier.requests.CommitRequest;
import com.eas.client.threetier.requests.CreateServerModuleRequest;
import com.eas.client.threetier.requests.CredentialRequest;
import com.eas.client.threetier.requests.DisposeServerModuleRequest;
import com.eas.client.threetier.requests.ErrorResponse;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import com.eas.client.threetier.requests.ExecuteServerModuleMethodRequest;
import com.eas.client.threetier.requests.HelloRequest;
import com.eas.client.threetier.requests.IsUserInRoleRequest;
import com.eas.client.threetier.requests.KeepAliveRequest;
import com.eas.client.threetier.requests.LoginRequest;
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
import java.net.URI;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class PlatypusResponseHttpWriter implements PlatypusResponseVisitor {

    public static final String CREATE_MODULE_RESPONSE_FUNCTIONS_PROP = "functions";
    public static final String CREATE_MODULE_RESPONSE_IS_PERMITTED_PROP = "isPermitted";
    public static final String REPORT_LOCATION_CONTENT_TYPE = "text/platypus-report-location";
    
    protected HttpServletResponse servletResponse;
    protected HttpServletRequest servletRequest;

    public PlatypusResponseHttpWriter(HttpServletResponse aServletResponse, HttpServletRequest aServletRequest) {
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
    public void visit(HelloRequest.Response resp) throws Exception {
        // simple OK response is needed
    }

    @Override
    public void visit(CredentialRequest.Response resp) throws Exception {
        String appElementName = ((CredentialRequest.Response) resp).getAppElementName();
        String name = ((CredentialRequest.Response) resp).getName();
        StringBuilder sb = new StringBuilder();
        JSONUtils.o(sb,
                "appElementName", JSONUtils.s(appElementName),
                "userName", JSONUtils.s(name));
        writeJsonResponse(sb.toString(), servletResponse);
    }

    @Override
    public void visit(ExecuteQueryRequest.Response resp) throws Exception {
        makeResponseNotCacheable(servletResponse);
        writeResponse(((ExecuteQueryRequest.Response) resp).getRowset(), servletResponse);
    }

    @Override
    public void visit(LogoutRequest.Response resp) throws Exception {
        // logout is processed out of this class
    }

    @Override
    public void visit(LoginRequest.Response resp) throws Exception {
        // login is processed out of this class by servlet container
    }

    @Override
    public void visit(KeepAliveRequest.Response resp) throws Exception {
        // http keep alive is processed out of this class by servlet container
    }

    @Override
    public void visit(IsUserInRoleRequest.Response resp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(ExecuteServerModuleMethodRequest.Response resp) throws Exception {
        final Object result = ((ExecuteServerModuleMethodRequest.Response) resp).getResult();
        makeResponseNotCacheable(servletResponse);
        if (result instanceof Rowset) {
            writeResponse((Rowset) result, servletResponse);
        } else if (result instanceof String) {
            writeJsonResponse(ScriptUtils.toJson(result), servletResponse);
        } else if (result instanceof JSObject) {
            writeJsonResponse(ScriptUtils.toJson(result), servletResponse);
        } else if (result instanceof Report) {
            Report report = (Report) result;
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
            writeResponse(reportLocation, servletResponse, REPORT_LOCATION_CONTENT_TYPE);
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
        makeResponseNotCacheable(servletResponse);
        writeJsonResponse(moduleResponseToJson(csmr.getInfo().getFunctionsNames(), csmr.getInfo().isPermitted()), servletResponse);
    }

    @Override
    public void visit(CommitRequest.Response resp) throws Exception {
        // simple OK response is needed
    }

    @Override
    public void visit(ModuleStructureRequest.Response resp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void visit(ResourceRequest.Response resp) throws Exception {
        // resources are processed out of this class by servlet container
    }

    @Override
    public void visit(AppQueryRequest.Response resp) throws Exception {
        Query query = ((AppQueryRequest.Response) resp).getAppQuery();
        makeResponseNotCacheable(servletResponse);
        writeResponse(query, servletResponse);
    }

    private void writeResponse(byte[] aResponse, HttpServletResponse aHttpResponse, String aContentType) throws UnsupportedEncodingException, IOException {
        if (aContentType != null) {
            aHttpResponse.setContentType(aContentType);
        }
        aHttpResponse.setContentLength(aResponse.length);
        aHttpResponse.getOutputStream().write(aResponse);
    }

    private void makeResponseNotCacheable(HttpServletResponse aHttpResponse) {
        aHttpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        aHttpResponse.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        aHttpResponse.setDateHeader("Expires", 0);// Proxies
    }
    private static String moduleResponseToJson(Set<String> functionsNames, boolean isPermitted) {
        return (new StringBuilder())
                .append("{")
                .append("\"").append(CREATE_MODULE_RESPONSE_FUNCTIONS_PROP).append("\"")
                .append(":")
                .append("[").append(functionsNames.isEmpty() ? "" : "\"").append(StringUtils.join("\", \"", functionsNames.toArray(new String[]{}))).append(functionsNames.isEmpty() ? "" : "\"").append("]")
                .append(",")
                .append("\"").append(CREATE_MODULE_RESPONSE_IS_PERMITTED_PROP).append("\"")
                .append(":")
                .append(isPermitted)
                .append("}")
                .toString();
    }

    private void writeResponse(Rowset aRowset, HttpServletResponse aResponse) throws Exception {
        RowsetJsonWriter writer = new RowsetJsonWriter(aRowset);
        String json = writer.write();
        writeJsonResponse(json, aResponse);
    }

    private void writeResponse(Query query, HttpServletResponse aHttpResponse) throws Exception {
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
