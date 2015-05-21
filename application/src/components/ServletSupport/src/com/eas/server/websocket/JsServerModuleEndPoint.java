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

    public static class SessionPrincipal {

        private final com.eas.server.Session session;
        private final PlatypusPrincipal principal;

        public SessionPrincipal(com.eas.server.Session aSession, PlatypusPrincipal aPrincipal) {
            super();
            session = aSession;
            principal = aPrincipal;
        }

        public com.eas.server.Session getSession() {
            return session;
        }

        public PlatypusPrincipal getPrincipal() {
            return principal;
        }

    }

    //
    private static final String WS_ON_OPEN_METHOD_NAME = "onopen";
    private static final String WS_ON_MESSAGE = "onmessage";
    private static final String WS_ON_CLOSE = "onclose";
    private static final String WS_ON_ERROR = "onerror";

    protected WebSocketServerSession facade;
    protected SessionPrincipal sessionPrincipal;

    public JsServerModuleEndPoint() {
        super();
    }

    @OnOpen
    public void sessionOpened(Session websocketSession, @PathParam("module-name") String aModuleName) throws Exception {
        PlatypusServerCore serverCore = lookupPlaypusServerCore();
        HandshakeRequest handshake = (HandshakeRequest) websocketSession.getUserProperties().get(JsServerModuleEndPointConfigurator.HANDSHAKE_REQUEST);
        com.eas.server.Session session;
        String usrContext = null;
        SessionManager sessionManager = serverCore.getSessionManager();
        synchronized (sessionManager) {// Note: Internal sessionManager's synchronization is done on the same point.
            String platypusSessionId = websocketSession.getId();
            session = sessionManager.get(platypusSessionId);
            if (session == null) {
                session = sessionManager.createSession(platypusSessionId);
                String userName = websocketSession.getUserPrincipal() != null ? websocketSession.getUserPrincipal().getName() : null;
                if (userName != null && serverCore.getDatabasesClient() != null) {
                    try {
                        Map<String, String> userProps = DatabasesClient.getUserProperties(serverCore.getDatabasesClient(), userName, null, null);
                        usrContext = userProps.get(ClientConstants.F_USR_CONTEXT);
                    } catch (Exception ex) {
                        Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.WARNING, "Could not get user {0} properties (USR_CONTEXT, etc).", userName);
                    }
                }
            }
        }
        PlatypusPrincipal principal;
        if (handshake.getUserPrincipal() != null) {
            principal = new WebSocketPlatypusPrincipal(handshake.getUserPrincipal().getName(), usrContext, handshake);
        } else {
            principal = new AnonymousPlatypusPrincipal(websocketSession.getId());
        }
        
        assert session != null : "Platypus session missing while opening WebSocket session";
        sessionPrincipal = new SessionPrincipal(session, principal);
        
        facade = new WebSocketServerSession(websocketSession, sessionPrincipal.getSession(), sessionPrincipal.getPrincipal());
        PlatypusPrincipal.setInstance(sessionPrincipal.getPrincipal());
        Scripts.setSession(sessionPrincipal.getSession());
        try {
            serverCore.executeMethod(aModuleName, WS_ON_OPEN_METHOD_NAME, new Object[]{facade.getPublished()}, sessionPrincipal.getSession(), (Object aResult) -> {
                Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.FINE, "{0} method of {1} module called successfully.", new Object[]{WS_ON_OPEN_METHOD_NAME, aModuleName});
            }, (Exception ex) -> {
                Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.SEVERE, null, ex);
            }, (Object aLock) -> {
                facade.setLock(aLock);
            });
        } finally {
            PlatypusPrincipal.setInstance(null);
            Scripts.setSession(null);
        }
    }

    @OnMessage
    public void messageRecieved(Session websocketSession, String aData) throws Exception {
        JSObject messageEvent = Scripts.makeObj();
        messageEvent.setMember("data", aData);
        executeSessionFacadeMethod(WS_ON_MESSAGE, new Object[]{messageEvent});
    }

    @OnError
    public void errorInSession(Session websocketSession, Throwable aError) {
        JSObject errorEvent = Scripts.makeObj();
        errorEvent.setMember("message", aError.getMessage());
        executeSessionFacadeMethod(WS_ON_ERROR, new Object[]{errorEvent});
    }

    @OnClose
    public void sessionClosed(Session websocketSession) throws Exception {
        JSObject closeEvent = Scripts.makeObj();
        closeEvent.setMember("wasClean", true);
        closeEvent.setMember("code", CloseReason.CloseCodes.NORMAL_CLOSURE.getCode());
        closeEvent.setMember("reason", "");
        executeSessionFacadeMethod(WS_ON_CLOSE, new Object[]{closeEvent});

        PlatypusServerCore serverCore = lookupPlaypusServerCore();
        serverCore.getSessionManager().remove(sessionPrincipal.getSession().getId());
        Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.INFO, "Platypus session closed id: {0}", sessionPrincipal.getSession().getId());

        facade = null;
    }

    protected void executeSessionFacadeMethod(String methodName, Object[] args) {
        if (facade != null) {
            try {
                Object oFun = facade.getPublished().getMember(methodName);
                if (oFun instanceof JSObject && ((JSObject) oFun).isFunction()) {
                    JSObject callable = (JSObject) oFun;
                    final Object lock = facade.getLock();
                    if (Scripts.getLock() == null) {
                        Scripts.setLock(lock);
                        Scripts.setSession(facade.getPlatypusSession());
                        PlatypusPrincipal.setInstance(facade.getPrincipal());
                        Scripts.setRequest(facade.getRequest());
                        Scripts.setResponse(facade.getResponse());
                        try {
                            if (lock != null) {
                                synchronized (lock) {
                                    callable.call(facade, args);
                                }
                            } else {
                                throw new IllegalStateException("Can't obtain a lock for web socket session callback: " + methodName);
                            }
                        } finally {
                            Scripts.setLock(null);
                            Scripts.setSession(null);
                            PlatypusPrincipal.setInstance(null);
                            Scripts.setRequest(null);
                            Scripts.setResponse(null);
                        }
                    } else {
                        callable.call(facade, args);
                    }
                } else {
                    Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.WARNING, "No method {0} found in {1}", new Object[]{methodName, WebSocketServerSession.class.getSimpleName()});
                }
            } catch (Exception ex) {
                Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.WARNING, null, ex);
            }
        }
    }

    protected PlatypusServerCore lookupPlaypusServerCore() throws IllegalStateException, Exception {
        PlatypusServerCore serverCore = PlatypusServerCore.getInstance();
        if (serverCore == null) {
            throw new IllegalStateException("Platypus server core is not initialized");
        }
        return serverCore;
    }
}
