/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.bearsoft.rowset.Converter;
import com.bearsoft.rowset.changes.Change;
import com.bearsoft.rowset.changes.EntitiesHost;
import com.bearsoft.rowset.exceptions.RowsetException;
import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.queries.SqlQuery;
import com.eas.client.settings.SettingsConstants;
import com.eas.client.threetier.HelloRequest;
import com.eas.client.threetier.binary.PlatypusRequestReader;
import com.eas.client.threetier.http.PlatypusHttpRequestParams;
import com.eas.client.threetier.requests.AppElementChangedRequest;
import com.eas.client.threetier.requests.AppElementRequest;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.client.threetier.requests.CommitRequest;
import com.eas.client.threetier.requests.CreateServerModuleRequest;
import com.eas.client.threetier.requests.DbTableChangedRequest;
import com.eas.client.threetier.requests.DisposeServerModuleRequest;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import com.eas.client.threetier.requests.ExecuteServerModuleMethodRequest;
import com.eas.client.threetier.requests.ExecuteServerReportRequest;
import com.eas.client.threetier.requests.IsAppElementActualRequest;
import com.eas.client.threetier.requests.IsUserInRoleRequest;
import com.eas.client.threetier.requests.KeepAliveRequest;
import com.eas.client.threetier.requests.LoginRequest;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.client.threetier.requests.OutHashRequest;
import com.eas.client.threetier.requests.PlatypusRequestVisitor;
import com.eas.client.threetier.requests.StartAppElementRequest;
import com.eas.script.ScriptUtils;
import com.eas.server.PlatypusServerCore;
import com.eas.server.httpservlet.serial.ChangeJsonReader;
import com.eas.server.httpservlet.serial.rowset.RowsetJsonConstants;
import com.eas.util.BinaryUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author mg
 */
public class PlatypusRequestHttpReader implements PlatypusRequestVisitor {

    public static final String API_URI = "/api";
    public static final String SCRIPTS_URI = "/scripts";
    private static final String ARGUMENTS_ARRAY_PARAM_SUFFIX = "[]";
    public static final String MODULE_NAME_PARAMETER_MISSING_MSG = "Module name parameter missing";
    public static final String METHOD_NAME_PARAMETER_MISSING = "Method name parameter missing";
    public static final String PROPERTY_NAME_PARAMETER_MISSING_MSG = "Property name parameter missing";
    public static final String PROPERTY_VALUE_PARAMETER_MISSING_MSG = "Property value parameter missing.";
    protected PlatypusServerCore serverCore;
    protected long rqId;
    protected String rqUri;
    protected HttpServletRequest httpRequest;

    public PlatypusRequestHttpReader(PlatypusServerCore aServerCore, long aRequestId, HttpServletRequest aHttpRequest, String aRequestUri) {
        super();
        serverCore = aServerCore;
        rqId = aRequestId;
        httpRequest = aHttpRequest;
        rqUri = aRequestUri;
    }

    @Override
    public void visit(AppQueryRequest rq) throws Exception {
        String queryId = httpRequest.getParameter(PlatypusHttpRequestParams.QUERY_ID);
        rq.setQueryId(queryId);
    }

    @Override
    public void visit(LoginRequest rq) throws Exception {
        String login = httpRequest.getParameter(PlatypusHttpRequestParams.LOGIN);
        String psw = httpRequest.getParameter(PlatypusHttpRequestParams.PASSWD);
        rq.setLogin(login);
        rq.setLogin(psw);
    }

    @Override
    public void visit(LogoutRequest rq) throws Exception {
    }

    @Override
    public void visit(CommitRequest rq) throws Exception {
        if (isApiUri(rqUri)) {
            String jsonText = getRequestText(httpRequest);
            List<Change> changes = ChangeJsonReader.parse(jsonText, new EntitiesHost() {
                @Override
                public Field resolveField(String aEntityId, String aFieldName) throws Exception {
                    SqlQuery query = serverCore.getDatabasesClient().getQueryFactory().getQuery(aEntityId, false);
                    if (query != null) {
                        if (!query.getFields().isEmpty()) {
                            return query.getFields().get(aFieldName);
                        } else {
                            return query.getParameters().get(aFieldName);
                        }
                    } else {
                        Logger.getLogger(PlatypusRequestHttpReader.class.getName()).log(Level.SEVERE, String.format("Entity not found %s.", aEntityId));
                        return null;
                    }
                }

                @Override
                public void checkRights(String aEntityId) throws Exception {
                }
            });
            rq.setChanges(changes);
        } else {
            PlatypusRequestReader bodyReader = new PlatypusRequestReader(getRequestContent(httpRequest));
            rq.accept(bodyReader);
        }
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
    public void visit(ExecuteServerModuleMethodRequest rq) throws Exception {
        if (isApiUri(rqUri)) {
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
                rq.setArguments(new Object[]{tryParseJson(param)});
            } else {
                String[] params = httpRequest.getParameterValues(PlatypusHttpRequestParams.PARAMETER + ARGUMENTS_ARRAY_PARAM_SUFFIX);
                if (params != null) {
                    List<Object> paramsList = new ArrayList<>();
                    for (int i = 0; i < params.length; i++) {
                        paramsList.add(tryParseJson(params[i]));
                    }
                    rq.setArguments(paramsList.toArray());
                } else {
                    rq.setArguments(new Object[]{});
                }
            }
        } else {
            // binary
            PlatypusRequestReader bodyReader = new PlatypusRequestReader(getRequestContent(httpRequest));
            rq.accept(bodyReader);
        }
    }

    @Override
    public void visit(AppElementChangedRequest rq) throws Exception {
        String sDbId = httpRequest.getParameter(PlatypusHttpRequestParams.DATABASE_ID);
        String sEntityId = httpRequest.getParameter(PlatypusHttpRequestParams.ENTITY_ID);
        rq.setDatabaseId(sDbId);
        rq.setEntityId(sEntityId);
    }

    @Override
    public void visit(DbTableChangedRequest rq) throws Exception {
        String sDbId = httpRequest.getParameter(PlatypusHttpRequestParams.DATABASE_ID);
        String schema = httpRequest.getParameter(PlatypusHttpRequestParams.SCHEMA);
        String table = httpRequest.getParameter(PlatypusHttpRequestParams.TABLE);
        rq.setDatabaseId(sDbId);
        rq.setSchema(schema);
        rq.setTable(table);
    }

    @Override
    public void visit(HelloRequest hr) throws Exception {
    }

    @Override
    public void visit(ExecuteQueryRequest rq) throws Exception {
        if (isApiUri(rqUri)) {
            String queryId = httpRequest.getParameter(PlatypusHttpRequestParams.QUERY_ID);
            rq.setQueryId(queryId);
            rq.setParams(decodeQueryParams(queryId, httpRequest));
        } else {
            // binary
            PlatypusRequestReader bodyReader = new PlatypusRequestReader(getRequestContent(httpRequest));
            rq.accept(bodyReader);
        }
    }

    @Override
    public void visit(KeepAliveRequest rq) throws Exception {
    }

    @Override
    public void visit(OutHashRequest rq) throws Exception {
        String sUserName = httpRequest.getParameter(PlatypusHttpRequestParams.LOGIN);
        rq.setUserName(sUserName);
    }

    @Override
    public void visit(StartAppElementRequest saer) throws Exception {
    }

    @Override
    public void visit(IsUserInRoleRequest rq) throws Exception {
        String roleName = httpRequest.getParameter(PlatypusHttpRequestParams.ROLE_NAME);
        rq.setRoleName(roleName);
    }

    @Override
    public void visit(IsAppElementActualRequest rq) throws Exception {
        String sAppElementId = httpRequest.getParameter(PlatypusHttpRequestParams.ENTITY_ID);
        String sTxtSize = httpRequest.getParameter(PlatypusHttpRequestParams.TEXT_CONTENT_SIZE);
        Integer txtSize = Integer.valueOf(sTxtSize);
        String sTxtCrc32 = httpRequest.getParameter(PlatypusHttpRequestParams.TEXT_CONTENT_CRC32);
        Long txtCrc32 = Long.valueOf(sTxtCrc32);
        rq.setAppElementId(sAppElementId);
        rq.setTxtContentSize(txtSize);
        rq.setTxtContentCrc32(txtCrc32);
    }

    @Override
    public void visit(AppElementRequest rq) throws Exception {
        if (isScriptUri(rqUri)) {
            rq.setAppElementId(rqUri.substring(rqUri.lastIndexOf('/') + 1, rqUri.length() - 3));
        } else {
            rq.setAppElementId(httpRequest.getParameter(PlatypusHttpRequestParams.ENTITY_ID));
        }
    }
   
    @Override
    public void visit(ExecuteServerReportRequest rq) throws Exception {
        if (isApiUri(rqUri)) {
            String moduleName = httpRequest.getParameter(PlatypusHttpRequestParams.MODULE_NAME);
            if (httpRequest.getParameterMap().size() > 1) {
                List<ExecuteServerReportRequest.NamedArgument> args = new ArrayList<>();
                for (Entry<String, String[]> ent : httpRequest.getParameterMap().entrySet()) {
                    if (!ent.getKey().equals(PlatypusHttpRequestParams.MODULE_NAME)) {
                        Object value = null;
                        if (ent.getValue().length > 0) {
                            value = tryParseJson(ent.getValue()[0]);
                        }
                        args.add(new ExecuteServerReportRequest.NamedArgument(ent.getKey(), value));
                    }
                }
                rq.setArguments(args.toArray(new ExecuteServerReportRequest.NamedArgument[]{}));
            }
            rq.setModuleName(moduleName);
        } else {
            // binary
            PlatypusRequestReader bodyReader = new PlatypusRequestReader(getRequestContent(httpRequest));
            rq.accept(bodyReader);
        }
    }

    private Parameters decodeQueryParams(String aQueryId, HttpServletRequest aRequest) throws RowsetException, IOException, UnsupportedEncodingException, Exception {
        SqlQuery query = serverCore.getDatabasesClient().getAppQuery(aQueryId);
        Parameters params = query.getParameters();
        Converter converter = serverCore.getDatabasesClient().getDbMetadataCache(query.getDbId()).getConnectionDriver().getConverter();
        for (int i = 1; i <= params.getParametersCount(); i++) {
            Parameter param = params.get(i);
            String paramValue = aRequest.getParameter(param.getName());
            if (paramValue != null) {
                if ("null".equals(paramValue.toLowerCase()) || paramValue.isEmpty()) {
                    paramValue = null;
                }
                Object convertedParamValue;
                if (param.getTypeInfo().getSqlType() == Types.DATE || param.getTypeInfo().getSqlType() == Types.TIMESTAMP || param.getTypeInfo().getSqlType() == Types.TIME) {
                    SimpleDateFormat sdf = new SimpleDateFormat(RowsetJsonConstants.DATE_FORMAT);
                    convertedParamValue = converter.convert2RowsetCompatible(sdf.parse(paramValue), param.getTypeInfo());
                } else {
                    convertedParamValue = converter.convert2RowsetCompatible(paramValue, param.getTypeInfo());
                }
                param.setValue(convertedParamValue);
            }
        }
        return params;
    }

    public static boolean isApiUri(String reqUri) {
        return reqUri != null && reqUri.startsWith(API_URI);
    }

    public static boolean isScriptUri(String reqUri) {
        return reqUri != null && reqUri.startsWith(SCRIPTS_URI) && reqUri.endsWith(".js");
    }

    private static byte[] getRequestContent(HttpServletRequest aRequest) throws IOException {
        try (InputStream is = aRequest.getInputStream()) {
            return BinaryUtils.readStream(is, aRequest.getContentLength() >= 0 ? aRequest.getContentLength() : 0);
        }
    }

    private static String getRequestText(HttpServletRequest aRequest) throws IOException {
        return new String(getRequestContent(aRequest), aRequest.getCharacterEncoding() != null ? aRequest.getCharacterEncoding() : SettingsConstants.COMMON_ENCODING);
    }

    private static Object tryParseJson(String str) {
        try {
            return ScriptUtils.parseJson(str);
        } catch (Exception ex) {
            return str;
        }
    }
}
