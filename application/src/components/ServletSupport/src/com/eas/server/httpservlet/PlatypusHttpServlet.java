package com.eas.server.httpservlet;

import com.eas.client.ClientConstants;
import com.eas.client.DatabasesClient;
import com.eas.client.LocalModulesProxy;
import com.eas.client.ScriptedDatabasesClient;
import com.eas.client.SqlQuery;
import com.eas.client.cache.ApplicationSourceIndexer;
import com.eas.client.cache.ModelsDocuments;
import com.eas.client.cache.ScriptsConfigs;
import com.eas.client.login.AnonymousPlatypusPrincipal;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.queries.LocalQueriesProxy;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.scripts.ScriptedResource;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.http.PlatypusHttpRequestParams;
import com.eas.client.threetier.requests.*;
import com.eas.concurrent.DeamonThreadFactory;
import com.eas.script.Scripts;
import com.eas.server.*;
import com.eas.util.IDGenerator;
import com.eas.util.JSONUtils;
import java.io.*;
import java.net.URI;
import java.security.AccessControlException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.InitialContext;
import javax.naming.NamingException;
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
    public static final String HTTP_SESSION_MISSING_MSG = "Container's session missing";
    public static final String PLATYPUS_SESSION_MISSING_MSG = "Platypus session missing";
    public static final String ERRORRESPONSE_ERROR_MSG = "Error while sending ErrorResponse";
    public static final String REQUEST_PARAMETER_MISSING_MSG = "Http request parameter {0} not found.";
    public static final String UNKNOWN_REQUEST_MSG = "Unknown http request has arrived. It's type is %d";
    public static final String REQUEST_PROCESSSING_ERROR_MSG = "Request processsing error";
    public static final String SUBJECT_CONTEXT_KEY = "javax.security.auth.Subject.container";
    public static final String HTTP_HOST_OBJECT_NAME = "http";
    public static final String EXCEL_CONTENT_TYPE = "application/xls";
    public static final String EXCELX_CONTENT_TYPE = "application/xlsx";
    public static final String HTML_CONTENT_TYPE = "text/html";
    public static final String TEXT_CONTENT_TYPE = "text/plain";
    public static final String PLATYPUS_SESSION_ID_ATTR_NAME = "platypus-session-id";
    public static final String PLATYPUS_USER_CONTEXT_ATTR_NAME = "platypus-user-context";

    private static volatile PlatypusServerCore platypusCore;
    private String realRootPath;
    private PlatypusServerConfig platypusConfig;
    private ExecutorService containerExecutor;
    private ThreadPoolExecutor selfExecutor;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            super.init(config);
            try {
                containerExecutor = (ExecutorService) InitialContext.doLookup("java:comp/DefaultManagedExecutorService");
            } catch (NamingException ex) {
                try {
                    containerExecutor = (ExecutorService) InitialContext.doLookup("java:comp/env/concurrent/ThreadPool");
                } catch (NamingException ex1) {
                    int maxSelfThreads = Runtime.getRuntime().availableProcessors() + 1;
                    selfExecutor = new ThreadPoolExecutor(maxSelfThreads, maxSelfThreads,
                            1L, TimeUnit.SECONDS,
                            new LinkedBlockingQueue<>(),
                            new DeamonThreadFactory("platypus-worker-", false));
                    selfExecutor.allowCoreThreadTimeOut(true);
                }
            }
            realRootPath = config.getServletContext().getRealPath("/");
            platypusConfig = PlatypusServerConfig.parse(config);
            File realRoot = new File(realRootPath);
            String realRootUrl = realRoot.toURI().toURL().toString();
            if (realRootUrl.toLowerCase().startsWith("file")) {
                File f = new File(new URI(realRootUrl));
                if (f.exists() && f.isDirectory()) {
                    ScriptsConfigs lsecurityConfigs = new ScriptsConfigs();
                    ServerTasksScanner tasksScanner = new ServerTasksScanner(lsecurityConfigs);
                    ApplicationSourceIndexer indexer = new ApplicationSourceIndexer(f.getPath(), tasksScanner);
                    ScriptedDatabasesClient basesProxy = new ScriptedDatabasesClient(platypusConfig.getDefaultDatasourceName(), indexer, true, tasksScanner.getValidators(), platypusConfig.getMaximumJdbcThreads());
                    QueriesProxy<SqlQuery> queries = new LocalQueriesProxy(basesProxy, indexer);
                    basesProxy.setQueries(queries);
                    platypusCore = new PlatypusServerCore(indexer, new LocalModulesProxy(indexer, new ModelsDocuments(), platypusConfig.getAppElementName()), queries, basesProxy, lsecurityConfigs, platypusConfig.getAppElementName(), SessionManager.Singleton.instance);
                    basesProxy.setContextHost(platypusCore);
                    Scripts.initBIO(platypusConfig.getMaximumBIOTreads());
                    ScriptedResource.init(platypusCore);
                    Scripts.initTasks((Runnable aTask) -> {
                        if (containerExecutor != null) {// J2EE 7+
                            containerExecutor.submit(aTask);
                        } else if (Scripts.getContext().getAsync() != null) {// Servlet 3+
                            ((AsyncContext) Scripts.getContext().getAsync()).start(aTask);
                        } else {
                            // Other environment
                            selfExecutor.submit(aTask);
                        }
                    });
                    platypusCore.startResidents(tasksScanner.getResidents());
                    if (platypusConfig.isWatch()) {
                        indexer.watch();
                    }
                } else {
                    throw new IllegalArgumentException("applicationUrl: " + realRootUrl + " doesn't point to existent directory.");
                }
            } else {
                throw new Exception("Unknown protocol in url: " + realRootUrl);
            }
        } catch (Throwable ex) {
            throw new ServletException(ex);
        }
    }

    public static PlatypusServerCore getCore() {
        return platypusCore;
    }

    @Override
    public void destroy() {
        if (platypusConfig.isWatch()) {
            try {
                platypusCore.getIndexer().unwatch();
            } catch (Exception ex) {
                Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        Scripts.shutdown();
        if (platypusCore.getDatabasesClient() != null) {
            platypusCore.getDatabasesClient().shutdown();
        }
        if (selfExecutor != null) {
            selfExecutor.shutdown();
            try {
                selfExecutor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        super.destroy();
    }

    protected static final String PUB_CONTEXT = "/pub/";

    protected boolean checkUpload(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (request.getContentType() != null && request.getContentType().contains("multipart/form-data")) {
            List<StringBuilder> uploadedLocations = new ArrayList();
            for (Part part : request.getParts()) {
                String dispositionHeader = part.getHeader("content-disposition");
                if (dispositionHeader != null) {
                    Pattern fileNamePattern = Pattern.compile(".*filename=.*\"(.+)\".*", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
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
                        StringBuilder uploadedLocation = new StringBuilder();
                        uploadedLocation.append("http://").append(request.getHeader("host")).append(request.getServletContext().getContextPath()).append(PUB_CONTEXT).append(uploadedFileName);
                        uploadedLocations.add(uploadedLocation);
                    }
                }
            }
            PlatypusHttpResponseWriter.writeJsonResponse(JSONUtils.as(uploadedLocations.toArray(new StringBuilder[]{})).toString(), response, null);
            return true;
        } else {
            return false;
        }
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
            if (platypusCore != null) {
                HttpSession httpSession = request.getSession();
                if (httpSession != null) {
                    AsyncContext async = request.startAsync();
                    async.setTimeout(-1);
                    String userName = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : null;
                    Consumer<Session> withPlatypusSession = (Session aSession) -> {
                        // http executor thread or sessions accounting thread
                        Scripts.LocalContext context = Scripts.createContext(aSession.getSpace());
                        context.setAsync(async);
                        context.setRequest(request);
                        context.setResponse(response);
                        context.setPrincipal(httpRequestPrincipal(request));
                        Scripts.setContext(context);
                        try {
                            aSession.getSpace().process(() -> {
                                // temporarily session thread 
                                try {
                                    handlePlatypusRequest(request, response, httpSession, aSession, async);
                                } catch (Exception ex) {
                                    Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, null, ex);
                                    try {
                                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.toString());
                                        async.complete();
                                    } catch (IOException | IllegalStateException ex1) {
                                        Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, null, ex1);
                                    }
                                }
                            });
                        } finally {
                            Scripts.setContext(null);
                        }
                    };
                    Session lookedup0 = platypusSessionByHttpSession(httpSession);
                    if (lookedup0 == null) {// Zombie check
                        Scripts.LocalContext queueSpaceContext = Scripts.createContext(platypusCore.getQueueSpace());
                        Scripts.setContext(queueSpaceContext);
                        try {
                            platypusCore.getQueueSpace().process(() -> {
                                // sessions accounting thread
                                Session lookedup1 = platypusSessionByHttpSession(httpSession);
                                if (lookedup1 == null) {
                                    try {
                                        Consumer<String> withDataContext = (String dataContext) -> {
                                            String platypusSessionId = (String) httpSession.getAttribute(PLATYPUS_SESSION_ID_ATTR_NAME);
                                            // platypusSessionId may be replicated from another instance in cluster
                                            Session lookedup2 = platypusSessionId != null ? SessionManager.Singleton.instance.get(platypusSessionId) : null;
                                            if (lookedup2 == null) {
                                                try {
                                                    // preserve replicated session id
                                                    Session created = SessionManager.Singleton.instance.create(platypusSessionId == null ? IDGenerator.genID() + "" : platypusSessionId);
                                                    if (dataContext == null) {
                                                        httpSession.removeAttribute(PLATYPUS_USER_CONTEXT_ATTR_NAME);
                                                    } else {
                                                        httpSession.setAttribute(PLATYPUS_USER_CONTEXT_ATTR_NAME, dataContext);
                                                    }
                                                    // publishing a session
                                                    httpSession.setAttribute(PLATYPUS_SESSION_ID_ATTR_NAME, created.getId());
                                                    // a session has been published
                                                    Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.INFO, "Platypus session opened. Session id: {0}", created.getId());
                                                    withPlatypusSession.accept(created);
                                                } catch (Exception ex) {
                                                    Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, null, ex);
                                                    try {
                                                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.toString());
                                                        async.complete();
                                                    } catch (IOException | IllegalStateException ex1) {
                                                        Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, null, ex1);
                                                    }
                                                }
                                            } else {
                                                withPlatypusSession.accept(lookedup2);
                                            }
                                        };
                                        if (request.getUserPrincipal() != null) {// Additional properties can be obtained only for authorized users
                                            DatabasesClient.getUserProperties(platypusCore.getDatabasesClient(), userName, platypusCore.getQueueSpace(), (Map<String, String> aUserProps) -> {
                                                // still sessions accounting thread
                                                String dataContext = aUserProps.get(ClientConstants.F_USR_CONTEXT);
                                                withDataContext.accept(dataContext);
                                            }, (Exception ex) -> {
                                                // still sessions accounting thread
                                                Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.FINE, "Unable to obtain properties of user {0} due to an error: {1}", new Object[]{userName, ex.toString()});
                                                withDataContext.accept(null);
                                            });
                                        } else {
                                            withDataContext.accept(null);
                                        }
                                    } catch (Exception ex) {
                                        Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, null, ex);
                                        try {
                                            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.toString());
                                            async.complete();
                                        } catch (IOException | IllegalStateException ex1) {
                                            Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, null, ex1);
                                        }
                                    }
                                } else {
                                    withPlatypusSession.accept(lookedup1);
                                }
                            });
                        } finally {
                            Scripts.setContext(null);
                        }
                    } else {
                        // http executor thread
                        withPlatypusSession.accept(lookedup0);
                    }
                } else {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, HTTP_SESSION_MISSING_MSG);
                }
            } else {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, CORE_MISSING_MSG);
            }
        }
    }

    public Session platypusSessionByHttpSession(HttpSession httpSession) {
        SessionManager sessionManager = platypusCore.getSessionManager();
        String platypusSessionId = (String) httpSession.getAttribute(PLATYPUS_SESSION_ID_ATTR_NAME);
        Session session = platypusSessionId != null ? sessionManager.get(platypusSessionId) : null;
        return session;
    }

    private static PlatypusPrincipal httpRequestPrincipal(final HttpServletRequest aRequest) {
        HttpSession httpSession = aRequest.getSession(false);
        if (aRequest.getUserPrincipal() != null) {
            return new HttpPlatypusPrincipal(aRequest.getUserPrincipal().getName(), (String) httpSession.getAttribute(PLATYPUS_USER_CONTEXT_ATTR_NAME), aRequest);
        } else {
            return httpSession != null ? new AnonymousPlatypusPrincipal("anonymous-" + httpSession.getId()) : new AnonymousPlatypusPrincipal();
        }
    }

    public PlatypusServerCore getServerCore() {
        return platypusCore;
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
    private void handlePlatypusRequest(final HttpServletRequest aHttpRequest, final HttpServletResponse aHttpResponse, HttpSession aHttpSession, Session aPlatypusSession, AsyncContext aAsync) throws Exception {
        // temporarily session thread 
        Request platypusRequest = readPlatypusRequest(aHttpRequest, aHttpResponse);
        if (platypusRequest.getType() == Requests.rqLogout) {
            aHttpRequest.logout();
            aHttpSession.invalidate();
            aHttpResponse.setStatus(HttpServletResponse.SC_OK);
            aAsync.complete();
        } else {
            RequestHandler<Request, Response> handler = (RequestHandler<Request, Response>) RequestHandlerFactory.getHandler(platypusCore, platypusRequest);
            if (handler != null) {
                Consumer<Exception> onFailure = (Exception ex) -> {
                    Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, ex.getMessage());
                    try {
                        if (ex instanceof AccessControlException) {
                            /*
                             // We can't send HttpServletResponse.SC_UNAUTHORIZED without knowlege about login mechanisms
                             // of J2EE container.
                             AccessControlException accEx = (AccessControlException)ex;
                             aHttpResponse.sendError(accEx.getPermission() instanceof AuthPermission ? HttpServletResponse.SC_UNAUTHORIZED : HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
                             */
                            aHttpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
                            aAsync.complete();
                        } else if (ex instanceof FileNotFoundException) {
                            aHttpResponse.sendError(HttpServletResponse.SC_NOT_FOUND, ex.getMessage());
                            aAsync.complete();
                        } else {
                            aHttpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
                            aAsync.complete();
                        }
                    } catch (IOException ex1) {
                        Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                };
                aPlatypusSession.accessed();
                handler.handle(aPlatypusSession, (Response resp) -> {
                    PlatypusHttpResponseWriter writer = new PlatypusHttpResponseWriter(aHttpResponse, aHttpRequest, Scripts.getSpace(), ((Principal) Scripts.getContext().getPrincipal()).getName(), aAsync);
                    try {
                        resp.accept(writer);
                    } catch (Exception ex) {
                        Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, ex.getMessage());
                    }
                }, (Exception ex) -> {
                    onFailure.accept(ex);
                });
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
        return "Platypus servlet provides platypus server functionality within J2EE/Servlet containers";
    }// </editor-fold>

    protected Request readPlatypusRequest(HttpServletRequest aHttpRequest, HttpServletResponse aResponse) throws Exception {
        String sType = aHttpRequest.getParameter(PlatypusHttpRequestParams.TYPE);
        if (sType != null) {
            int rqType = Integer.valueOf(sType);
            Request rq = PlatypusRequestsFactory.create(rqType);
            if (rq != null) {
                PlatypusHttpRequestReader reader = new PlatypusHttpRequestReader(platypusCore, aHttpRequest);
                rq.accept(reader);
                return rq;
            } else {
                throw new Exception(String.format(UNKNOWN_REQUEST_MSG, rqType));
            }
        } else {
            Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, REQUEST_PARAMETER_MISSING_MSG, PlatypusHttpRequestParams.TYPE);
            throw new Exception(String.format("Platypus http requset parameter '%s' is missing", PlatypusHttpRequestParams.TYPE));
        }
    }
}
