/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.eas.client.report.Report;
import com.eas.client.settings.SettingsConstants;
import com.eas.client.threetier.http.PlatypusHttpConstants;
import com.eas.client.threetier.http.PlatypusHttpResponseReader;
import com.eas.client.threetier.requests.AccessControlExceptionResponse;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.client.threetier.requests.CommitRequest;
import com.eas.client.threetier.requests.ServerModuleStructureRequest;
import com.eas.client.threetier.requests.CredentialRequest;
import com.eas.client.threetier.requests.DisposeServerModuleRequest;
import com.eas.client.threetier.requests.ExceptionResponse;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import com.eas.client.threetier.requests.JsonExceptionResponse;
import com.eas.client.threetier.requests.RPCRequest;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.client.threetier.requests.ModuleStructureRequest;
import com.eas.client.threetier.requests.PlatypusResponseVisitor;
import com.eas.client.threetier.requests.ResourceRequest;
import com.eas.client.threetier.requests.SqlExceptionResponse;
import com.eas.script.Scripts;
import com.eas.util.IdGenerator;
import com.eas.util.JsonUtils;
import com.eas.util.RowsetJsonConstants;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.AsyncContext;
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
    protected AsyncContext async;
    protected Scripts.Space space;
    protected String userName;

    public PlatypusHttpResponseWriter(HttpServletResponse aServletResponse, HttpServletRequest aServletRequest, Scripts.Space aSpace, String aUserName, AsyncContext aAsync) {
        super();
        servletRequest = aServletRequest;
        servletResponse = aServletResponse;
        space = aSpace;
        userName = aUserName;
        async = aAsync;
    }

    @Override
    public void visit(ExceptionResponse resp) throws Exception {
        servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, resp.getErrorMessage());
        async.complete();
    }

    @Override
    public void visit(AccessControlExceptionResponse acer) throws Exception {
        /*
             // we can't send HttpServletResponse.SC_UNAUTHORIZED without knowlege about login mechanisms
             // of Sevlet/J2EE container.
             if (resp.isNotLoggedIn()) {
             servletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, resp.getErrorMessage());
             } else {
             servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, resp.getErrorMessage());
             }
         */
        servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN, acer.getErrorMessage());
        async.complete();
    }

    @Override
    public void visit(JsonExceptionResponse jer) throws Exception {
        servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, jer.getErrorMessage());
        async.complete();
    }

    @Override
    public void visit(SqlExceptionResponse ser) throws Exception {
        servletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ser.getErrorMessage());
        async.complete();
    }

    @Override
    public void visit(CredentialRequest.Response resp) throws Exception {
        String name = ((CredentialRequest.Response) resp).getName();
        StringBuilder content = JsonUtils.o(new StringBuilder("userName"), JsonUtils.s(name));
        writeJsonResponse(content.toString(), servletResponse, async);
    }

    @Override
    public void visit(ExecuteQueryRequest.Response rsp) throws Exception {
        makeResponseNotCacheable(servletResponse);
        ExecuteQueryRequest.Response resp = (ExecuteQueryRequest.Response) rsp;
        writeJsonResponse(resp.getJson(), servletResponse, async);
    }

    @Override
    public void visit(LogoutRequest.Response resp) throws Exception {
        // logout is processed out of this class
    }

    @Override
    public void visit(RPCRequest.Response resp) throws Exception {
        final Object result = ((RPCRequest.Response) resp).getResult();
        makeResponseNotCacheable(servletResponse);
        if (result instanceof String) {
            writeJsonResponse((String) result, servletResponse, async);
        } else if (result instanceof Report) {
            writeReportResponse((Report) result);
        } else if (result instanceof JSObject) {
            /*
            JSObject jsResult = (JSObject) result;
            JSObject p = space.lookupInGlobal("P");
            if (p != null) {
                Object reportClass = p.getMember("Report");
                if (jsResult.isInstanceOf(reportClass)) {
                    Report report = (Report) ((JSObject) jsResult.getMember("unwrap")).call(null, new Object[]{});
                    writeReportResponse(report);
                } else {
                    writeJsonResponse(space.toJson(result), servletResponse, async);
                }
            } else {
                writeJsonResponse(space.toJson(result), servletResponse, async);
            }
             */
            writeJsonResponse(space.toJson(result), servletResponse, async);
        } else {// including null result
            writeJsonResponse(space.toJson(space.toJs(result)), servletResponse, async);
        }
    }

    protected void writeReportResponse(Report report) throws URISyntaxException, IOException {
        String docsRoot = servletRequest.getServletContext().getRealPath("/");
        String userHomeInApplication = "/reports/" + userName + "/";
        File userDir = new File(docsRoot + userHomeInApplication);
        if (!userDir.exists()) {
            userDir.mkdirs();
        }
        String suffix = "." + report.getFormat();
        String reportName = report.getName();
        if (reportName.toLowerCase().endsWith(suffix.toLowerCase())) {
            reportName = reportName.substring(0, reportName.length() - suffix.length());
        }
        reportName += "-" + IdGenerator.genId() + suffix;
        File rep = new File(docsRoot + userHomeInApplication + reportName);
        try (FileOutputStream out = new FileOutputStream(rep)) {
            out.write(report.getBody());
            out.flush();
        }
        String reportLocation = userHomeInApplication + reportName;
        if (!"/".equals(servletRequest.getContextPath())) {
            reportLocation = servletRequest.getContextPath() + reportLocation;
        }
        reportLocation = new URI(null, null, reportLocation, null).toASCIIString();
        writeResponse(reportLocation, servletResponse, PlatypusHttpResponseReader.REPORT_LOCATION_CONTENT_TYPE, async);
    }

    @Override
    public void visit(DisposeServerModuleRequest.Response resp) throws Exception {
        // simple OK response is needed
        async.complete();
    }

    @Override
    public void visit(ServerModuleStructureRequest.Response resp) throws Exception {
        ServerModuleStructureRequest.Response csmr = (ServerModuleStructureRequest.Response) resp;
        if (csmr.getInfoJson() != null) {
            assert resp.getTimeStamp() != null;
            servletResponse.setDateHeader(PlatypusHttpConstants.HEADER_LAST_MODIFIED, resp.getTimeStamp().getTime());
            writeJsonResponse(resp.getInfoJson(), servletResponse, async);
        } else {
            servletResponse.sendError(HttpURLConnection.HTTP_NOT_MODIFIED);
            async.complete();
        }
    }

    @Override
    public void visit(ResourceRequest.Response resp) throws Exception {
        // resources are processed out of this class by servlet container
    }

    @Override
    public void visit(AppQueryRequest.Response resp) throws Exception {
        AppQueryRequest.Response aqr = ((AppQueryRequest.Response) resp);
        if (aqr.getAppQueryJson() != null) {
            assert resp.getTimeStamp() != null;
            servletResponse.setDateHeader(PlatypusHttpConstants.HEADER_LAST_MODIFIED, resp.getTimeStamp().getTime());
            writeJsonResponse(aqr.getAppQueryJson(), servletResponse, async);
        } else {
            servletResponse.sendError(HttpURLConnection.HTTP_NOT_MODIFIED);
            async.complete();
        }
    }

    @Override
    public void visit(CommitRequest.Response resp) throws Exception {
        writeJsonResponse(resp.getUpdated() + "", servletResponse, async);
    }

    @Override
    public void visit(ModuleStructureRequest.Response resp) throws Exception {
        makeResponseNotCacheable(servletResponse);
        writeJsonResponse(resp.getJson(), servletResponse, async);
    }

    private static void makeResponseNotCacheable(HttpServletResponse aHttpResponse) {
        aHttpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
        aHttpResponse.setHeader("Pragma", "no-cache"); // HTTP 1.0.
        aHttpResponse.setDateHeader("Expires", 0);// Proxies
    }

    private static void writeResponse(String aResponse, HttpServletResponse aHttpResponse, String aContentType, AsyncContext aAsync) throws UnsupportedEncodingException, IOException {
        byte[] bytes = aResponse.getBytes(SettingsConstants.COMMON_ENCODING);
        aHttpResponse.setCharacterEncoding(SettingsConstants.COMMON_ENCODING);
        if (aHttpResponse.getContentType() == null) {
            aHttpResponse.setContentType(aContentType);
        }
        aHttpResponse.setContentLength(bytes.length);
        aHttpResponse.getOutputStream().write(bytes);
        aHttpResponse.getOutputStream().flush();
        if (aAsync != null) {
            aAsync.complete();
        }
    }

    public static void writeJsonResponse(String aResponse, HttpServletResponse aHttpResponse, AsyncContext aAsync) throws UnsupportedEncodingException, IOException {
        writeResponse(aResponse, aHttpResponse, RowsetJsonConstants.JSON_CONTENTTYPE, aAsync);
    }
}
