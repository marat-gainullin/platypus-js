package com.eas.server.httpservlet;

import com.bearsoft.rowset.utils.IDGenerator;
import com.eas.client.login.AnonymousPlatypusPrincipal;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.http.PlatypusHttpRequestParams;
import com.eas.client.threetier.requests.*;
import com.eas.script.ScriptUtils;
import com.eas.server.*;
import com.eas.server.handlers.CommonRequestHandler;
import com.eas.server.SessionRequestHandler;
import java.io.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

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
    public static final String SUBJECT_CONTEXT_KEY = "javax.security.auth.Subject.container";
    public static final String HTTP_HOST_OBJECT_NAME = "http";
    public static final String EXCEL_CONTENT_TYPE = "application/xls";
    public static final String EXCELX_CONTENT_TYPE = "application/xlsx";
    public static final String HTML_CONTENTTYPE = "text/html";
    public static final String TEXT_CONTENTTYPE = "text/plain";
    private static final ThreadLocal<HttpServletRequest> currentRequest = new ThreadLocal<>();
    private static final ThreadLocal<HttpServletResponse> currentResponse = new ThreadLocal<>();
    private PlatypusServerCore serverCore;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            super.init(config);
            ServerConfig scp = ServerConfig.parse(config);
            serverCore = PlatypusServerCore.getInstance(scp.getUrl(), scp.getDefaultDatasourceName(), scp.getAppElementName());
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    public void destroy() {
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
                PlatypusResponseHttpWriter.writeJsonResponse(uploadedLocations.toString(), response);
                return true;
            }
        }
        return false;
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!checkUpload(request, response)) {
            if (serverCore != null) {
                HttpSession httpSession = request.getSession(true);
                if (httpSession != null) {
                    SessionManager sessionManager = serverCore.getSessionManager();
                    Session session;
                    PlatypusPrincipal principal = servletRequestPrincipal(request);
                    synchronized (sessionManager) {// Note: Internal sessionManager's synchronization is on the same point
                        String platypusSessionId = (String) httpSession.getAttribute(PLATYPUS_SESSION_ATTR_NAME);
                        if (platypusSessionId == null) {
                            platypusSessionId = httpSession.getId();
                        }
                        session = sessionManager.getOrCreateSession(principal, platypusSessionId);
                        httpSession.setAttribute(PLATYPUS_SESSION_ATTR_NAME, platypusSessionId);
                        httpSession.setAttribute(PLATYPUS_SERVER_CORE_ATTR_NAME, serverCore);
                    }
                    assert session != null : "Platypus session missing";
                    session.setPrincipal(principal);

                    PlatypusPrincipal.setInstance(session.getPrincipal());
                    ScriptUtils.setRequest(request);
                    ScriptUtils.setResponse(response);
                    try {
                        processPlatypusRequest(request, response, session, httpSession);
                    } finally {
                        ScriptUtils.setRequest(null);
                        ScriptUtils.setResponse(null);
                        PlatypusPrincipal.setInstance(null);
                    }
                }
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, CORE_MISSING_MSG);
            }
        }
    }

    private static PlatypusPrincipal servletRequestPrincipal(final HttpServletRequest aRequest) {
        if (aRequest.getUserPrincipal() != null) {
            return new WebPlatypusPrincipal(aRequest.getUserPrincipal().getName(), aRequest);
        } else {
            return new AnonymousPlatypusPrincipal(aRequest.getSession().getId());
        }
    }

    public PlatypusServerCore getServerCore() {
        return serverCore;
    }

    /**
     * @return The currentRequest
     */
    public static HttpServletRequest getCurrentRequest() {
        return currentRequest.get();
    }

    /**
     * @return The currentResponse
     */
    public static HttpServletResponse getCurrentResponse() {
        return currentResponse.get();
    }

    /**
     * Precesses request for PlatypusAPI requests.
     *
     * @param aHttpRequest
     * @param aPlatypusSession
     * @param aHttpResponse
     * @param aHttpSession
     * @throws Exception
     */
    private void processPlatypusRequest(final HttpServletRequest aHttpRequest, final HttpServletResponse aHttpResponse, Session aPlatypusSession, HttpSession aHttpSession) throws Exception {
        Request platypusRequest = readPlatypusRequest(aHttpRequest, aHttpResponse, aPlatypusSession);
        if (platypusRequest.getType() == Requests.rqLogout) {
            aHttpRequest.logout();
            aHttpSession.invalidate();
        } else {
            RequestHandler<?, ?> handler = RequestHandlerFactory.getHandler(serverCore, platypusRequest);
            if (handler != null) {
                Consumer<Exception> onFailure = (Exception ex) -> {
                    try {
                        aHttpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
                    } catch (IOException ex1) {
                        Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                };
                Consumer<Response> onSuccess = (Response resp) -> {
                    try {
                        PlatypusResponseHttpWriter writer = new PlatypusResponseHttpWriter(aHttpResponse, aHttpRequest);
                        resp.accept(writer);
                    } catch (Exception ex) {
                        Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                };
                if (handler instanceof CommonRequestHandler<?, ?>) {
                    AsyncContext async = aHttpRequest.startAsync(aHttpRequest, aHttpResponse);
                    CommonRequestHandler<?, Response> crh = (CommonRequestHandler<?, Response>) handler;
                    crh.handle((Response resp) -> {
                        async.complete();
                        onSuccess.accept(resp);
                    }, (Exception ex) -> {
                        async.complete();
                        onFailure.accept(ex);
                    });
                } else if (handler instanceof SessionRequestHandler<?, ?>) {
                    AsyncContext async = aHttpRequest.startAsync(aHttpRequest, aHttpResponse);
                    SessionRequestHandler<?, Response> srh = (SessionRequestHandler<?, Response>) handler;
                    srh.handle(aPlatypusSession, (Response resp) -> {
                        async.complete();
                        onSuccess.accept(resp);
                    }, (Exception ex) -> {
                        async.complete();
                        onFailure.accept(ex);
                    });
                } else {
                    throw new IllegalStateException("Bad request handler detected");
                }
            } else {
                throw new IllegalStateException("No request handler found");
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Platypus servlet provides platypus http clients communication with J2EE/Servlet containers";
    }// </editor-fold>

    protected Request readPlatypusRequest(HttpServletRequest aHttpRequest, HttpServletResponse aResponse, Session aSession) throws Exception {
        String sType = aHttpRequest.getParameter(PlatypusHttpRequestParams.TYPE);
        if (sType != null) {
            int rqType = Integer.valueOf(sType);
            Request rq = PlatypusRequestsFactory.create(rqType);
            if (rq != null) {
                PlatypusRequestHttpReader reader = new PlatypusRequestHttpReader(serverCore, aHttpRequest);
                rq.accept(reader);
                return rq;
            } else {
                throw new Exception(String.format(UNKNOWN_REQUEST_MSG, rqType));
            }
        } else {
            Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, REQUEST_PARAMETER_MISSING_MSG, PlatypusHttpRequestParams.TYPE);
            throw new Exception(REQUEST_NOT_CORRECT_MSG + " -0- ");
        }
    }
}
