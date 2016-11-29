/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.eas.client.settings.SettingsConstants;
import com.eas.client.threetier.http.PlatypusHttpRequestParams;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.client.threetier.requests.CommitRequest;
import com.eas.client.threetier.requests.ServerModuleStructureRequest;
import com.eas.client.threetier.requests.DisposeServerModuleRequest;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import com.eas.client.threetier.requests.RPCRequest;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.client.threetier.requests.PlatypusRequestVisitor;
import com.eas.server.PlatypusServerCore;
import com.eas.client.threetier.requests.CredentialRequest;
import com.eas.client.threetier.requests.ModuleStructureRequest;
import com.eas.client.threetier.requests.ResourceRequest;
import com.eas.util.BinaryUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
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
    public static final String MUST_BE_RESOURCE_MSG = "Application element requests must be resource requests.";
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
        rq.setChangesJson(jsonText);
    }

    @Override
    public void visit(ServerModuleStructureRequest rq) throws Exception {
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
        String param = httpRequest.getParameter(PlatypusHttpRequestParams.PARAMS_ARRAY.substring(0, PlatypusHttpRequestParams.PARAMS_ARRAY.length() - 2));
        if (param != null) {
            rq.setArgumentsJsons(new String[]{param});
        } else {
            String[] params = httpRequest.getParameterValues(PlatypusHttpRequestParams.PARAMS_ARRAY);
            if (params != null) {
                rq.setArgumentsJsons(params);
            } else {
                rq.setArgumentsJsons(new String[]{});
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
        rq.setModuleName(moduleName);
    }

    @Override
    public void visit(ExecuteQueryRequest rq) throws Exception {
        String queryName = httpRequest.getParameter(PlatypusHttpRequestParams.QUERY_ID);
        rq.setQueryName(queryName);
        Map<String, String> res = new HashMap<>();
        httpRequest.getParameterMap().entrySet().forEach((Map.Entry<String, String[]> pEntry) -> {
            if (!PlatypusHttpRequestParams.isSystemParameter(pEntry.getKey())) {
                String[] pValues = pEntry.getValue();
                if (pValues != null && pValues.length == 1) {
                    res.put(pEntry.getKey(), pValues[0]);
                }
            }
        });
        rq.setParamsJsons(res);
    }

    private static byte[] getRequestContent(HttpServletRequest aRequest) throws IOException {
        try (InputStream is = aRequest.getInputStream()) {
            return BinaryUtils.readStream(is, aRequest.getContentLength() >= 0 ? aRequest.getContentLength() : 0);
        }
    }

    private static String getRequestText(HttpServletRequest aRequest) throws IOException {
        return new String(getRequestContent(aRequest), aRequest.getCharacterEncoding() != null ? aRequest.getCharacterEncoding() : SettingsConstants.COMMON_ENCODING);
    }
}
