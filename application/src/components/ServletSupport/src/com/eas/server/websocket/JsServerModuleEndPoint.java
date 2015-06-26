/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.websocket;

import com.eas.client.ClientConstants;
import com.eas.client.DatabasesClient;
import com.eas.client.login.AnonymousPlatypusPrincipal;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.script.Scripts;
import com.eas.server.PlatypusServerCore;
import com.eas.server.SessionManager;
import com.eas.server.httpservlet.PlatypusHttpServlet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
@ServerEndpoint(value = "/{module-name}", configurator = JsServerModuleEndPointConfigurator.class)
public class JsServerModuleEndPoint {

    //
    private static final String WS_ON_OPEN_METHOD_NAME = "onopen";
    private static final String WS_ON_MESSAGE = "onmessage";
    private static final String WS_ON_CLOSE = "onclose";
    private static final String WS_ON_ERROR = "onerror";

    protected com.eas.server.Session session;
    protected WebSocketServerSession facade;

    public JsServerModuleEndPoint() {
        super();
    }

    private static void inContext(Runnable aTask, com.eas.server.Session aSession) {
        Scripts.LocalContext context = Scripts.createContext(aSession.getSpace());
        context.setAsync(null);
        context.setPrincipal(aSession.getPrincipal());
        context.setRequest(null);
        context.setResponse(null);
        Scripts.setContext(context);
        try {
            aTask.run();
        } finally {
            Scripts.setContext(null);
        }
    }

    @OnOpen
    public void sessionOpened(Session websocketSession, @PathParam("module-name") String aModuleName) throws Exception {
        PlatypusServerCore platypusCore = lookupPlaypusServerCore();
        HandshakeRequest handshake = (HandshakeRequest) websocketSession.getUserProperties().get(JsServerModuleEndPointConfigurator.HANDSHAKE_REQUEST);
        SessionManager sessionManager = platypusCore.getSessionManager();
        String userName = websocketSession.getUserPrincipal() != null ? websocketSession.getUserPrincipal().getName() : null;
        session = sessionManager.create(websocketSession.getId());
        Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.INFO, "WebSocket platypus session opened. Session id: {0}", session.getId());
        Scripts.LocalContext queueSpaceContext = Scripts.createContext(platypusCore.getQueueSpace());
        Scripts.setContext(queueSpaceContext);
        try {
            DatabasesClient.getUserProperties(platypusCore.getDatabasesClient(), userName, platypusCore.getQueueSpace(), (Map<String, String> aUserProps) -> {
                String usrContext = aUserProps.get(ClientConstants.F_USR_CONTEXT);
                PlatypusPrincipal principal;
                if (handshake.getUserPrincipal() != null) {
                    principal = new WebSocketPlatypusPrincipal(handshake.getUserPrincipal().getName(), usrContext, handshake);
                } else {
                    principal = new AnonymousPlatypusPrincipal(websocketSession.getId());
                }
                facade = new WebSocketServerSession(websocketSession);
                session.setPrincipal(principal);
                inContext(() -> {
                    platypusCore.executeMethod(aModuleName, WS_ON_OPEN_METHOD_NAME, new Object[]{facade.getPublished()}, true, (Object aResult) -> {
                        Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.FINE, "{0} method of {1} module called successfully.", new Object[]{WS_ON_OPEN_METHOD_NAME, aModuleName});
                    }, (Exception ex) -> {
                        Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                    });
                }, session);
            }, (Exception ex) -> {
                Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.WARNING, "Could not get user {0} properties (USR_CONTEXT, etc).", userName);
            });
        } finally {
            Scripts.setContext(null);
        }
    }

    @OnMessage
    public void messageRecieved(Session websocketSession, String aData) throws Exception {
        inContext(() -> {
            Scripts.getSpace().process(() -> {
                JSObject messageEvent = Scripts.getSpace().makeObj();
                messageEvent.setMember("data", aData);
                executeSessionFacadeMethod(WS_ON_MESSAGE, new Object[]{messageEvent});
            });
        }, session);
    }

    @OnError
    public void errorInSession(Session websocketSession, Throwable aError) {
        inContext(() -> {
            Scripts.getSpace().process(() -> {
                JSObject errorEvent = Scripts.getSpace().makeObj();
                errorEvent.setMember("message", aError.getMessage());
                executeSessionFacadeMethod(WS_ON_ERROR, new Object[]{errorEvent});
            });
        }, session);
    }

    @OnClose
    public void sessionClosed(Session websocketSession) throws Exception {
        PlatypusServerCore serverCore = lookupPlaypusServerCore();
        inContext(() -> {
            Scripts.getSpace().process(() -> {
                JSObject closeEvent = Scripts.getSpace().makeObj();
                closeEvent.setMember("wasClean", true);
                closeEvent.setMember("code", CloseReason.CloseCodes.NORMAL_CLOSURE.getCode());
                closeEvent.setMember("reason", "");
                executeSessionFacadeMethod(WS_ON_CLOSE, new Object[]{closeEvent});

                serverCore.getSessionManager().remove(session.getId());
                Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.INFO, "WebSocket platypus session closed. Session id: {0}", session.getId());
                session = null;
                facade = null;
            });
        }, session);
    }

    protected void executeSessionFacadeMethod(String methodName, Object[] args) {
        if (facade != null) {
            try {
                Object oFun = facade.getPublished().getMember(methodName);
                if (oFun instanceof JSObject && ((JSObject) oFun).isFunction()) {
                    JSObject callable = (JSObject) oFun;
                    callable.call(facade, args);
                } else {
                    Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.WARNING, "No method {0} found in {1}", new Object[]{methodName, WebSocketServerSession.class.getSimpleName()});
                }
            } catch (Exception ex) {
                Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }

    protected PlatypusServerCore lookupPlaypusServerCore() throws IllegalStateException, Exception {
        PlatypusServerCore serverCore = PlatypusHttpServlet.getCore();
        if (serverCore == null) {
            throw new IllegalStateException("Platypus server core is not initialized");
        }
        return serverCore;
    }
}
