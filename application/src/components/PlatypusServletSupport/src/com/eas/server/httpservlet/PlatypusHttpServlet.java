package com.eas.server.httpservlet;

import com.bearsoft.rowset.Rowset;
import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.Client;
import com.eas.client.ClientConstants;
import com.eas.client.DatabasesClient;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.metadata.ApplicationElement;
import com.eas.client.model.script.ScriptableRowset;
import com.eas.client.queries.Query;
import com.eas.client.scripts.ScriptRunner;
import com.eas.client.settings.SettingsConstants;
import com.eas.client.threetier.ErrorResponse;
import com.eas.client.threetier.HelloRequest;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.binary.PlatypusResponseWriter;
import com.eas.client.threetier.http.PlatypusHttpConstants;
import com.eas.client.threetier.http.PlatypusHttpRequestParams;
import com.eas.client.threetier.requests.*;
import com.eas.debugger.jmx.server.Breakpoints;
import com.eas.debugger.jmx.server.Debugger;
import com.eas.debugger.jmx.server.DebuggerMBean;
import com.eas.debugger.jmx.server.Settings;
import com.eas.proto.ProtoWriter;
import com.eas.script.ScriptUtils;
import com.eas.server.*;
import com.eas.server.filter.AppElementsFilter;
import com.eas.server.handlers.ExecuteServerModuleMethodRequestHandler;
import com.eas.server.httpservlet.serial.query.QueryJsonWriter;
import com.eas.server.httpservlet.serial.rowset.RowsetJsonConstants;
import com.eas.server.httpservlet.serial.rowset.RowsetJsonWriter;
import com.eas.util.StringUtils;
import com.eas.util.logging.PlatypusFormatter;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.URLConnection;
import java.util.Set;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.xml.XMLObject;

/**
 * Platypus HTTP servlet implementation
 *
 * @author ml, kl, mg refactoring
 */
public class PlatypusHttpServlet extends HttpServlet {

    public static final String CORE_MISSING_MSG = "Application core havn't been initialized";
    public static final String SESSION_MISSING_MSG = "Session %s missing";
    public static final String ERRORRESPONSE_ERROR_MSG = "Error while sending ErrorResponse";
    public static final String PLATYPUS_SERVER_CORE_ATTR_NAME = "PLATYPUS_SERVER_CORE_ATTR_NAME";
    public static final String PLATYPUS_SESSION_ATTR_NAME = "PLATYPUS_SESSION_ATTR_NAME";
    public static final String REQUEST_NOT_CORRECT_MSG = "Your request is incorrect. Please, download and use Platypus Client";
    public static final String REQUEST_PARAMETER_MISSING_MSG = "Http request parameter '{0}' not found.";
    public static final String UNKNOWN_REQUEST_MSG = "Unknown http request has arrived. It's type is %d";
    public static final String REQUEST_PROCESSSING_ERROR_MSG = "Request processsing error";
    public static final String SUCCESS_JSON = "{\"success\": \"true\"}";
    //public static final String ID = "id";
    //public static final String JACC_ERROR_MSG = "JACC exception when getting current subject";
    //public static final String QUERY_PARAMS = "queryParams";
    //public static final String ERROR_JSON = "{\"success\": \"false\", \"error\": \"%s\", \"accessControl\": \"%s\", \"sqlstate\": \"%s\", \"sqlcode\": \"%s\"}";
    public static final String SUBJECT_CONTEXT_KEY = "javax.security.auth.Subject.container";
    public static final String HTTP_HOST_OBJECT_NAME = "http";
    public static final String EXCEL_CONTENT_TYPE = "application/xls";
    public static final String HTML_CONTENTTYPE = "text/html";
    public static final String TEXT_CONTENTTYPE = "text/plain";
    public static final String CREATE_MODULE_RESPONSE_FUNCTIONS_PROP = "functions";
    public static final String CREATE_MODULE_RESPONSE_IS_REPORT_PROP = "isReport";
    private PlatypusServerCore serverCore;
    private ThreadLocal<HttpServletRequest> currentRequest = new ThreadLocal<>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            super.init(config);
            ServerConfig scp = ServerConfig.parse(config);
            Handler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new PlatypusFormatter());
            Logger logger = Logger.getLogger(Client.APPLICATION_LOGGER_NAME);
            logger.addHandler(consoleHandler);
            logger.setUseParentHandlers(false);

            DatabasesClient serverCoreDbClient = new DatabasesClient(scp.getDbSettings(), true);
            serverCore = new PlatypusServerCore(serverCoreDbClient, scp.getTasks(), scp.getAppElementId());
            serverCoreDbClient.setContextHost(serverCore);
            serverCoreDbClient.setPrincipalHost(serverCore);
            ScriptRunner.PlatypusScriptedResource.init(serverCoreDbClient, serverCore, serverCore);
            ScriptUtils.getScope().defineProperty(ServerScriptRunner.MODULES_SCRIPT_NAME, serverCore.getScriptsCache(), ScriptableObject.READONLY);

            if (System.getProperty(ScriptRunner.DEBUG_PROPERTY) != null) {
                Debugger debugger;
                debugger = Debugger.initialize(false);
                unRegisterMBean(DebuggerMBean.DEBUGGER_MBEAN_NAME);
                registerMBean(DebuggerMBean.DEBUGGER_MBEAN_NAME, debugger);
                unRegisterMBean(Breakpoints.BREAKPOINTS_MBEAN_NAME);
                registerMBean(Breakpoints.BREAKPOINTS_MBEAN_NAME, Breakpoints.getInstance());
                unRegisterMBean(Settings.SETTINGS_MBEAN_NAME);
                registerMBean(Settings.SETTINGS_MBEAN_NAME, new Settings(serverCoreDbClient));
            }
            serverCore.startBackgroundTasks();
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    public void destroy() {
        if (serverCore != null) {
            serverCore.getDatabasesClient().shutdown();
            serverCore = null;
        }
        super.destroy();
    }
    protected static final Pattern fileNamePattern = Pattern.compile(".*filename=.*\"(.+)\".*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    protected static final String PUB_CONTEXT = "/pub/";

    protected boolean checkUpload(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        StringBuilder uploadedLocations = new StringBuilder();
        if (request.getContentType() != null && request.getContentType().contains("multipart/form-data")) {
            for (Part part : request.getParts()) {
                String dispositionHeader = part.getHeader("content-disposition");
                if (dispositionHeader != null) {
                    Matcher m = fileNamePattern.matcher(dispositionHeader);
                    String fileName = null;
                    if (m.matches()) {
                        fileName = m.group(1);
                    }
                    if (fileName != null && !fileName.isEmpty()) {
                        StringBuilder uploadedFileName = new StringBuilder();
                        uploadedFileName.append(IDGenerator.genID()).append("-").append(fileName);
                        try {
                            try {
                                part.write(uploadedFileName.toString());
                            } catch (IOException ex) {
                                Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, "Falling back to copy implementation", ex);
                                String realPath = request.getServletContext().getRealPath(PUB_CONTEXT + uploadedFileName.toString());
                                try (InputStream fin = part.getInputStream(); FileOutputStream fout = new FileOutputStream(realPath)) {
                                    byte[] buffer = new byte[1024 * 16];
                                    int read = fin.read(buffer);
                                    while (read >= 0) {
                                        fout.write(buffer, 0, read);
                                        read = fin.read(buffer);
                                    }
                                }
                            }
                        } finally {
                            part.delete();
                        }
                        uploadedLocations.append(uploadedLocations.length() > 0 ? "\n" : "").append("http://").append(request.getHeader("host")).append(request.getServletContext().getContextPath()).append(PUB_CONTEXT).append(uploadedFileName);
                    }
                }
            }
            if (uploadedLocations.length() > 0) {
                writeJsonResponse(uploadedLocations.toString(), response);
                return true;
            }
        }
        return false;
    }

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        currentRequest.set(request);
        try {
            if (!checkUpload(request, response)) {
                if (serverCore != null) {
                    HttpSession httpSession = request.getSession(true);
                    if (httpSession != null) {
                        SessionManager sessionManager = serverCore.getSessionManager();
                        Session session;
                        synchronized (sessionManager) {// Note: Internal sessionManager's synchronization is on the same point
                            String platypusSessionId = (String) httpSession.getAttribute(PLATYPUS_SESSION_ATTR_NAME);
                            if (platypusSessionId == null) {
                                platypusSessionId = httpSession.getId();
                            }
                            session = sessionManager.getOrCreateSession(getPrincipal(request), platypusSessionId);
                            httpSession.setAttribute(PLATYPUS_SESSION_ATTR_NAME, platypusSessionId);
                            httpSession.setAttribute(PLATYPUS_SERVER_CORE_ATTR_NAME, serverCore);
                        }
                        assert session != null : "Platypus session missing";
                        session.setPrincipal(getPrincipal(request));
                        // Thread-local current session setting. 
                        // Thus, all code, executed under current principal (java or js code) can be authorized.
                        sessionManager.setCurrentSession(session);
                        try {
                            processPlatypusRequest(request, response, session, httpSession);
                        } finally {
                            // Revoke current session to avoid ANY session/memory leak.
                            sessionManager.setCurrentSession(null);
                        }
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, CORE_MISSING_MSG);
                }
            }
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
            throw new ServletException(ex);
        } finally {
            currentRequest.set(null);
        }
    }

    private PlatypusPrincipal getPrincipal(final HttpServletRequest aRequest) {
        if (aRequest.getUserPrincipal() != null) {
            return new WebPlatypusPrincipal(aRequest.getUserPrincipal().getName(), this);
        } else {
            return new AnonymousPlatypusPrincipal(aRequest.getSession().getId());
        }
    }

    public HttpServletRequest getCurrentRequest() {
        return currentRequest.get();
    }

    /**
     * Precesses request for both PlatypusAPI requests and "Platypus protocol
     * over http" requests.
     *
     * @param aHttpRequest
     * @param aPlatypusSession
     * @param aHttpResponse
     * @param aHttpSession
     * @throws Exception
     */
    private void processPlatypusRequest(final HttpServletRequest aHttpRequest, final HttpServletResponse aHttpResponse, Session aPlatypusSession, HttpSession aHttpSession) throws Exception {
        Request platypusRequest = readPlatypusRequest(aHttpRequest, aHttpResponse, aPlatypusSession);
        try {
            RequestHandler<?> handler = findPlatypusHandler(platypusRequest, aPlatypusSession, aHttpRequest, aHttpResponse);
            handler.run();
            Response response = handler.getResponse();
            platypusResponse(aHttpRequest, platypusRequest, response, aHttpResponse);
            if (platypusRequest.getType() == Requests.rqLogout) {
                aHttpRequest.logout();
                aHttpSession.invalidate();
            }
        } catch (Exception ex) {
            Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, REQUEST_PROCESSSING_ERROR_MSG, ex);
            ErrorResponse er = new ErrorResponse(platypusRequest.getID(), ex.getMessage());
            try {
                sendJ2SEResponse(er, aHttpResponse);
            } catch (Exception e) {
                Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, ERRORRESPONSE_ERROR_MSG, e);
            }
        }
    }

    private static String moduleResponseToJson(Set<String> functionsNames, boolean isReport) {
        return (new StringBuilder())
                .append("{")
                .append("\"").append(CREATE_MODULE_RESPONSE_FUNCTIONS_PROP).append("\"")
                .append(":")
                .append("[").append(functionsNames.isEmpty() ? "" : "\"").append(StringUtils.join("\", \"", functionsNames.toArray(new String[]{}))).append(functionsNames.isEmpty() ? "" : "\"").append("]")
                .append(",")
                .append("\"").append(CREATE_MODULE_RESPONSE_IS_REPORT_PROP).append("\"")
                .append(":")
                .append(Boolean.valueOf(isReport).toString())
                .append("}")
                .toString();
    }

    private void writeResponse(Rowset aRowset, HttpServletResponse response, HttpServletRequest request) throws Exception {
        RowsetJsonWriter writer = new RowsetJsonWriter(aRowset);
        String json = writer.write();
        writeJsonResponse(json, response);
    }

    private void writeResponse(Query<?> query, HttpServletResponse aHttpResponse) throws Exception {
        QueryJsonWriter writer = new QueryJsonWriter(query);
        writeJsonResponse(writer.write(), aHttpResponse);
    }

    private void writeResponse(String aResponse, HttpServletResponse aHttpResponse, String aContentType) throws UnsupportedEncodingException, IOException {
        byte[] bytes = aResponse.getBytes(SettingsConstants.COMMON_ENCODING);
        aHttpResponse.setCharacterEncoding(SettingsConstants.COMMON_ENCODING);
        if (aHttpResponse.getContentType() == null) {
            aHttpResponse.setContentType(aContentType);
        }
        aHttpResponse.setContentLength(bytes.length);
        aHttpResponse.getOutputStream().write(bytes);
    }

    private void writeJsonResponse(String aResponse, HttpServletResponse response) throws UnsupportedEncodingException, IOException {
        writeResponse(aResponse, response, RowsetJsonConstants.JSON_CONTENTTYPE);
    }

    private void writeExcelResponse(byte[] aResponse, HttpServletResponse response) throws UnsupportedEncodingException, IOException {
        response.setCharacterEncoding(SettingsConstants.COMMON_ENCODING);
        response.setContentType(EXCEL_CONTENT_TYPE);
        response.addHeader("Content-Disposition", "attachment; filename=\"report.xls\"");
        response.setContentLength(aResponse.length);
        response.getOutputStream().write(aResponse);
    }

    private void writeResponse(byte[] aResponse, HttpServletResponse aHttpResponse, String aContentType) throws UnsupportedEncodingException, IOException {
        if (aContentType != null) {
            aHttpResponse.setContentType(aContentType);
        }
        aHttpResponse.setContentLength(aResponse.length);
        aHttpResponse.getOutputStream().write(aResponse);
    }

    private void sendJ2SEResponse(Response aPlatypusResponse, HttpServletResponse aResponse) throws Exception {
        ByteArrayOutputStream baOut = new ByteArrayOutputStream();
        ProtoWriter pw = new ProtoWriter(baOut);
        PlatypusResponseWriter.write(aPlatypusResponse, pw);
        pw.flush();
        byte[] rsData = baOut.toByteArray();
        aResponse.setContentType(PlatypusHttpConstants.CONTENT_TYPE);
        aResponse.setContentLength(rsData.length);
        aResponse.getOutputStream().write(rsData);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Platypus servlet provides platypus http clients communication with java ee servers";
    }// </editor-fold>

    protected Request readPlatypusRequest(HttpServletRequest aRequest, HttpServletResponse aResponse, Session aSession) throws Exception {
        if (!isApiRequest(aRequest) && !isResourceRequest(aRequest) && !isJ2SERequest(aRequest)) {
            Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, REQUEST_PARAMETER_MISSING_MSG, PlatypusHttpRequestParams.ID);
            throw new Exception(REQUEST_NOT_CORRECT_MSG + " -1- ");
        }
        int rqType;
        String sType = aRequest.getParameter(PlatypusHttpRequestParams.TYPE);
        if (sType != null) {
            rqType = Integer.valueOf(sType);
        } else {
            if (isResourceRequest(aRequest)) {
                rqType = Requests.rqAppElement;
            } else {
                Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, REQUEST_PARAMETER_MISSING_MSG, PlatypusHttpRequestParams.TYPE);
                throw new Exception(REQUEST_NOT_CORRECT_MSG + " -0- ");
            }
        }
        Request rq = PlatypusRequestsFactory.create(IDGenerator.genID(), rqType);
        PlatypusRequestHttpReader reader = new PlatypusRequestHttpReader(serverCore, rq.getID(), aRequest, extractURI(aRequest));
        rq.accept(reader);
        if (rq instanceof AppElementRequest && !isJ2SERequest(aRequest)) {
            rq = new FilteredAppElementRequest(((AppElementRequest) rq).getAppElementId());
        }
        if (rq == null) {
            Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, String.format(UNKNOWN_REQUEST_MSG, rqType));
            throw new Exception(REQUEST_NOT_CORRECT_MSG + " -3- ");
        }
        return rq;
    }

    private String extractURI(HttpServletRequest aRequest) {
        return aRequest.getPathInfo();
    }

    private boolean isApiRequest(HttpServletRequest aRequest) {
        return PlatypusRequestHttpReader.isApiUri(extractURI(aRequest));
    }

    private boolean isResourceRequest(HttpServletRequest aRequest) {
        return PlatypusRequestHttpReader.isResourceUri(extractURI(aRequest));
    }

    private boolean isJ2SERequest(HttpServletRequest aRequest) {
        return PlatypusHttpConstants.AGENT_NAME.equals(aRequest.getHeader(PlatypusHttpConstants.HEADER_USER_AGENT));
    }

    private static void registerMBean(String aName, Object aBean) throws Exception {
        // Get the platform MBeanServer
        // Uniquely identify the MBeans and register them with the platform MBeanServer
        ManagementFactory.getPlatformMBeanServer().registerMBean(aBean, new ObjectName(aName));
    }

    private static void unRegisterMBean(String aName) throws MBeanRegistrationException, MalformedObjectNameException {
        try {
            ManagementFactory.getPlatformMBeanServer().unregisterMBean(new ObjectName(aName));
        } catch (InstanceNotFoundException ex) {
            //no-op
        }
    }

    private void platypusResponse(final HttpServletRequest aHttpRequest, Request aPlatypusRequest, Response aPlatypusResponse, final HttpServletResponse aHttpResponse) throws Exception {
        if (isJ2SERequest(aHttpRequest)) {// platypus http client
            sendJ2SEResponse(aPlatypusResponse, aHttpResponse);
        } else { // browsers
            assert isApiRequest(aHttpRequest) || isResourceRequest(aHttpRequest) : "Unknown request uri. Requests uri should be /api or /resources";
            if (aPlatypusResponse instanceof ErrorResponse) {
                ErrorResponse er = (ErrorResponse) aPlatypusResponse;
                if (er.isAccessControl()) {
                    if (aPlatypusRequest instanceof FilteredAppElementRequest) {
                        String moduleId = ((FilteredAppElementRequest) aPlatypusRequest).getAppElementId();
                        if (moduleId != null && !moduleId.isEmpty()) {
                            String userName = getPrincipal(aHttpRequest).getName();
                            if (Character.isDigit(moduleId.charAt(0))) {
                                String moduleConstructor = AppElementsFilter.checkModuleName("Module", moduleId);
                                String formConstructor = AppElementsFilter.checkModuleName("Form", moduleId);
                                writeJsonResponse(
                                        String.format(AppElementsFilter.SECURITY_VIOLATION_TEMPLATE, moduleConstructor, moduleId, userName) + "\n"
                                        + String.format(AppElementsFilter.SECURITY_VIOLATION_TEMPLATE, formConstructor, moduleId, userName), aHttpResponse);
                            } else {
                                writeJsonResponse(String.format(AppElementsFilter.SECURITY_VIOLATION_TEMPLATE, moduleId, moduleId, userName), aHttpResponse);
                            }
                        } else {
                            aHttpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, er.getError());
                        }
                    } else {
                        aHttpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, er.getError());
                    }
                } else {
                    aHttpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, er.getError());
                }
            } else if (aPlatypusResponse instanceof RowsetResponse) {
                writeResponse(((RowsetResponse) aPlatypusResponse).getRowset(), aHttpResponse, aHttpRequest);
            } else if (aPlatypusResponse instanceof CreateServerModuleResponse) {
                CreateServerModuleResponse csmr = (CreateServerModuleResponse) aPlatypusResponse;
                writeJsonResponse(moduleResponseToJson(csmr.getFunctionsNames(), csmr.isReport()), aHttpResponse);
            } else if (aPlatypusResponse instanceof ExecuteServerModuleMethodRequest.Response) {
                Object result = ((ExecuteServerModuleMethodRequest.Response) aPlatypusResponse).getResult();
                if (result instanceof ScriptableRowset) {
                    writeResponse(((ScriptableRowset) result).unwrap(), aHttpResponse, aHttpRequest);
                } else if (result instanceof String) {
                    writeResponse((String) result, aHttpResponse, TEXT_CONTENTTYPE);
                } else if (result instanceof NativeObject) {
                    writeJsonResponse(ScriptUtils.toJson(result), aHttpResponse);
                } else if (result instanceof XMLObject) {
                    writeResponse(ScriptUtils.toXMLString((XMLObject) result), aHttpResponse, HTML_CONTENTTYPE);
                } else {
                    ScriptUtils.enterContext();
                    try {
                        writeJsonResponse(ScriptUtils.toJson(ScriptUtils.javaToJS(result, ScriptUtils.getScope())), aHttpResponse);
                    } finally {
                        Context.exit();
                    }
                }
            } else if (aPlatypusResponse instanceof AppQueryResponse) {
                Query<?> query = ((AppQueryResponse) aPlatypusResponse).getAppQuery();
                writeResponse(query, aHttpResponse);
            } else if (aPlatypusResponse instanceof FilteredAppElementRequest.FilteredResponse) {
                if (isResourceRequest(aHttpRequest) && aHttpRequest.getParameter(PlatypusHttpRequestParams.TYPE) == null) {// pure resource request
                    writeJsonResponse(((FilteredAppElementRequest.FilteredResponse) aPlatypusResponse).getFilteredScript(), aHttpResponse);
                } else {
                    writeJsonResponse(((FilteredAppElementRequest.FilteredResponse) aPlatypusResponse).getFilteredContent(), aHttpResponse);
                }
            } else if (aPlatypusResponse instanceof StartAppElementRequest.Response) {
                writeJsonResponse(String.valueOf(((StartAppElementRequest.Response) aPlatypusResponse).getAppElementId()), aHttpResponse);
            } else if (aPlatypusResponse instanceof ExecuteServerReportRequest.Response) {
                writeExcelResponse(((ExecuteServerReportRequest.Response) aPlatypusResponse).getResult(), aHttpResponse);
            } else if (aPlatypusResponse instanceof AppElementRequest.Response) {
                if (isResourceRequest(aHttpRequest) && aHttpRequest.getParameter(PlatypusHttpRequestParams.TYPE) == null) {
                    AppElementRequest.Response appElementResponse = (AppElementRequest.Response) aPlatypusResponse;
                    if (appElementResponse.getAppElement() != null) {
                        if (appElementResponse.getAppElement().getType() == ClientConstants.ET_RESOURCE) {
                            ApplicationElement appElement = ((AppElementRequest.Response) aPlatypusResponse).getAppElement();
                            String mimeType = URLConnection.getFileNameMap().getContentTypeFor(appElement.getName());
                            writeResponse(appElement.getBinaryContent(), aHttpResponse, mimeType);
                            if (mimeType != null && mimeType.contains("text")) {
                                aHttpResponse.setCharacterEncoding(SettingsConstants.COMMON_ENCODING);
                            }
                        } else {
                            aHttpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.format("Application element [%s] should be platypus application resource, but it is not!", ((AppElementRequest) aPlatypusRequest).getAppElementId()));
                        }
                    } else {
                        aHttpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.format("Application element [%s] is absent!", ((AppElementRequest) aPlatypusRequest).getAppElementId()));
                    }
                } else {
                    writeJsonResponse("", aHttpResponse);// Plain scripts have no model and other related resources
                }
            } else if (aPlatypusResponse instanceof CommitRequest.Response) {
                // simple OK response is needed
            } else if (aPlatypusResponse instanceof LogoutRequest.Response) {
                // logout is processed out of this method
            } else if (aPlatypusResponse instanceof HelloRequest.Response) {
                // simple OK response is needed
            } else if (aPlatypusResponse instanceof DisposeServerModuleRequest.Response) {
                // simple OK response is needed
            } else if (aPlatypusResponse instanceof OutHashRequest.Response) {
                // don't know how to deal with
                aHttpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.format("Out hash is not supported now. Response class name: %s", aPlatypusResponse != null ? aPlatypusResponse.getClass().getName() : "null"));
            } else {
                aHttpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.format("Unknown response. Don't know how to deal with it. Response class name: %s", aPlatypusResponse != null ? aPlatypusResponse.getClass().getName() : "null"));
            }
        }
    }

    private RequestHandler<?> findPlatypusHandler(Request rq, Session aSession, final HttpServletRequest aHttpRequest, final HttpServletResponse aHttpResponse) throws Exception {
        RequestHandler<?> handler;
        if (rq instanceof FilteredAppElementRequest) {
            handler = new FilteredAppElementRequestHandler(serverCore, aSession, (FilteredAppElementRequest) rq);
        } else {
            handler = RequestHandlerFactory.getHandler(serverCore, aSession, rq);
        }
        if (handler instanceof ExecuteServerModuleMethodRequestHandler) {
            ((ExecuteServerModuleMethodRequestHandler) handler).setExecuteEventsCallback(new ExecuteServerModuleMethodRequestHandler.ExecuteEventsCallback() {
                @Override
                public void beforeExecute(ServerScriptRunner ssr) {
                    ssr.defineProperty(HTTP_HOST_OBJECT_NAME, HttpScriptContext.getInstance(ssr, aHttpRequest, aHttpResponse), ScriptableObject.READONLY);
                }

                @Override
                public void afterExecute(ServerScriptRunner ssr) {
                    ssr.delete(HTTP_HOST_OBJECT_NAME);
                }
            });
        }
        return handler;
    }
}
