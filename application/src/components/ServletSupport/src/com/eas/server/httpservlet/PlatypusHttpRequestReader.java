/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.eas.client.DatabaseMdCache;
import com.eas.client.SQLUtils;
import com.eas.client.SQLUtils.TypesGroup;
import com.eas.client.SqlQuery;
import com.eas.client.changes.Change;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.client.settings.SettingsConstants;
import com.eas.client.threetier.http.PlatypusHttpRequestParams;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.client.threetier.requests.CommitRequest;
import com.eas.client.threetier.requests.CreateServerModuleRequest;
import com.eas.client.threetier.requests.DisposeServerModuleRequest;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import com.eas.client.threetier.requests.RPCRequest;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.client.threetier.requests.PlatypusRequestVisitor;
import com.eas.script.ScriptUtils;
import com.eas.server.PlatypusServerCore;
import com.eas.server.httpservlet.serial.ChangeJsonReader;
import com.eas.client.threetier.RowsetJsonConstants;
import com.eas.client.threetier.requests.CredentialRequest;
import com.eas.client.threetier.requests.ModuleStructureRequest;
import com.eas.client.threetier.requests.ResourceRequest;
import com.eas.util.BinaryUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author mg
 */
public class PlatypusHttpRequestReader implements PlatypusRequestVisitor {

    public static final String API_URI = "/api";
    public static final String RESOURCES_URI = "/resources";
    private static final String ARGUMENTS_ARRAY_PARAM_SUFFIX = "[]";
    public static final String MODULE_NAME_PARAMETER_MISSING_MSG = "Module name parameter missing";
    public static final String METHOD_NAME_PARAMETER_MISSING = "Method name parameter missing";
    public static final String PROPERTY_NAME_PARAMETER_MISSING_MSG = "Property name parameter missing";
    public static final String PROPERTY_VALUE_PARAMETER_MISSING_MSG = "Property value parameter missing.";
    public static final String MUST_BE_RESOURCE_MSG = "Application element requests nust be resource requests.";
    protected PlatypusServerCore serverCore;
    protected HttpServletRequest httpRequest;

    public PlatypusHttpRequestReader(PlatypusServerCore aServerCore, HttpServletRequest aHttpRequest) {
        super();
        serverCore = aServerCore;
        httpRequest = aHttpRequest;
    }

    @Override
    public void visit(AppQueryRequest rq) throws Exception {
        String queryName = httpRequest.getParameter(PlatypusHttpRequestParams.QUERY_ID);
        rq.setQueryName(queryName);
    }

    @Override
    public void visit(LogoutRequest rq) throws Exception {
    }

    @Override
    public void visit(CommitRequest rq) throws Exception {
        String jsonText = getRequestText(httpRequest);
        List<Change> changes = ChangeJsonReader.parse(jsonText, (String aEntityName, String aFieldName) -> {
            SqlQuery query = serverCore.getQueries().getQuery(aEntityName, null, null);
            if (query != null) {
                if (!query.getFields().isEmpty()) {
                    return query.getFields().get(aFieldName);
                } else {
                    return query.getParameters().get(aFieldName);
                }
            } else {
                Logger.getLogger(PlatypusHttpRequestReader.class.getName()).log(Level.SEVERE, String.format("Entity not found %s.", aEntityName));
                return null;
            }
        });
        rq.setChanges(changes);
    }

    @Override
    public void visit(CreateServerModuleRequest rq) throws Exception {
        String moduleName = httpRequest.getParameter(PlatypusHttpRequestParams.MODULE_NAME);
        rq.setModuleName(moduleName);
    }

    @Override
    public void visit(DisposeServerModuleRequest rq) throws Exception {
        String moduleName = httpRequest.getParameter(PlatypusHttpRequestParams.MODULE_NAME);
        rq.setModuleName(moduleName);
    }

    @Override
    public void visit(RPCRequest rq) throws Exception {
        // pure http
        String moduleName = httpRequest.getParameter(PlatypusHttpRequestParams.MODULE_NAME);
        if (moduleName == null || moduleName.isEmpty()) {
            throw new IllegalArgumentException(MODULE_NAME_PARAMETER_MISSING_MSG);
        }
        String methodName = httpRequest.getParameter(PlatypusHttpRequestParams.METHOD_NAME);
        if (methodName == null || methodName.isEmpty()) {
            throw new IllegalArgumentException(METHOD_NAME_PARAMETER_MISSING);
        }
        rq.setModuleName(moduleName);
        rq.setMethodName(methodName);
        String param = httpRequest.getParameter(PlatypusHttpRequestParams.PARAMETER);
        if (param != null) {
            rq.setArguments(new Object[]{ScriptUtils.parseDates(tryParseJson(param))});
        } else {
            String[] params = httpRequest.getParameterValues(PlatypusHttpRequestParams.PARAMETER + ARGUMENTS_ARRAY_PARAM_SUFFIX);
            if (params != null) {
                List<Object> argsList = new ArrayList<>();
                for (String arg : params) {
                    argsList.add(ScriptUtils.parseDates(tryParseJson(arg)));
                }
                rq.setArguments(argsList.toArray());
            } else {
                rq.setArguments(new Object[]{});
            }
        }
    }

    @Override
    public void visit(ResourceRequest rq) throws Exception {
    }

    @Override
    public void visit(CredentialRequest rq) throws Exception {
    }

    @Override
    public void visit(ModuleStructureRequest rq) throws Exception {
        String moduleName = httpRequest.getParameter(PlatypusHttpRequestParams.MODULE_NAME);
        rq.setModuleOrResourceName(moduleName);
    }

    @Override
    public void visit(ExecuteQueryRequest rq) throws Exception {
        String queryName = httpRequest.getParameter(PlatypusHttpRequestParams.QUERY_ID);
        rq.setQueryName(queryName);
        rq.setParams(decodeQueryParams(queryName, httpRequest));
    }

    private Parameters decodeQueryParams(String aQueryName, HttpServletRequest aRequest) throws IOException, UnsupportedEncodingException, Exception {
        SqlQuery query = serverCore.getQueries().getQuery(aQueryName, null, null);
        Parameters params = query.getParameters().copy();
        for (int i = 1; i <= params.getParametersCount(); i++) {
            Parameter param = params.get(i);
            String paramValue = aRequest.getParameter(param.getName());
            if (paramValue != null && !"null".equals(paramValue.toLowerCase()) && !paramValue.isEmpty()) {
                if (SQLUtils.getTypeGroup(param.getTypeInfo().getSqlType()) == TypesGroup.DATES) {
                    SimpleDateFormat sdf = new SimpleDateFormat(RowsetJsonConstants.DATE_FORMAT);
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                    param.setValue(sdf.parse(paramValue));
                } else if (SQLUtils.getTypeGroup(param.getTypeInfo().getSqlType()) == TypesGroup.LOGICAL) {
                    param.setValue("true".equalsIgnoreCase(paramValue));
                } else if (SQLUtils.getTypeGroup(param.getTypeInfo().getSqlType()) == TypesGroup.NUMBERS) {
                    param.setValue(Double.valueOf(paramValue));
                } else {
                    param.setValue(paramValue);
                }
            } else {
                param.setValue(null);
            }
        }
        return params;
    }

    private static byte[] getRequestContent(HttpServletRequest aRequest) throws IOException {
        try (InputStream is = aRequest.getInputStream()) {
            return BinaryUtils.readStream(is, aRequest.getContentLength() >= 0 ? aRequest.getContentLength() : 0);
        }
    }

    private static String getRequestText(HttpServletRequest aRequest) throws IOException {
        return new String(getRequestContent(aRequest), aRequest.getCharacterEncoding() != null ? aRequest.getCharacterEncoding() : SettingsConstants.COMMON_ENCODING);
    }

    private static Object tryParseJson(String aText) {
        try {
            return ScriptUtils.parseJson(aText);
        } catch (Exception ex) {
            return aText;
        }
    }
}
