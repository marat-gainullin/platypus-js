/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.websocket;

import com.eas.client.login.AnonymousPlatypusPrincipal;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.script.ScriptUtils;
import com.eas.server.PlatypusServerCore;
import com.eas.server.SessionManager;
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
@ServerEndpoint(value = "/{module-name}", configurator = JsModuleEndPointConfigurator.class)
public class JsModuleEndPoint {

    private static final String PLATYPUS_SESSION_ATTR_NAME = "platypus-session";
    private static final String WS_SCRIPT_FACADE = "ws-session-script-facade";
    //
    private static final String WS_ON_OPEN_METHOD_NAME = "onopen";
    private static final String WS_ON_MESSAGE = "onmessage";
    private static final String WS_ON_CLOSE = "onclose";
    private static final String WS_ON_ERROR = "onerror";

    public JsModuleEndPoint() {
        super();
    }

    @OnOpen
    public void sessionOpened(Session websocketSession, @PathParam("module-name") String aModuleName) throws Exception {
        PlatypusServerCore serverCore = lookupPlaypusServerCore();
        com.eas.server.Session session = lookupPlatypusSession(websocketSession, serverCore);
        WebSocketSession facade = new WebSocketSession(websocketSession);
        
        websocketSession.getUserProperties().put(WS_SCRIPT_FACADE, facade);
        serverCore.executeMethod(aModuleName, WS_ON_OPEN_METHOD_NAME, new Object[]{facade.getPublished()}, session, (Object aResult) -> {
            Logger.getLogger(JsModuleEndPoint.class.getName()).log(Level.FINE, "{0} method of {1} module called successfully.", new Object[]{WS_ON_OPEN_METHOD_NAME, aModuleName});
        }, (Exception ex) -> {
            Logger.getLogger(JsModuleEndPoint.class.getName()).log(Level.SEVERE, null, ex);
        });
    }

    @OnMessage
    public void messageRecieved(Session websocketSession, String aData) throws Exception {
        JSObject messageEvent = ScriptUtils.makeObj();
        messageEvent.setMember("data", aData);
        executeSessionFacadeMethod(websocketSession, WS_ON_MESSAGE, new Object[]{messageEvent});
    }

    @OnError
    public void errorInSession(Session websocketSession, Throwable aError) {
        JSObject errorEvent = ScriptUtils.makeObj();
        errorEvent.setMember("data", aError.getMessage());
        executeSessionFacadeMethod(websocketSession, WS_ON_ERROR, new Object[]{errorEvent});
    }

    @OnClose
    public void sessionClosed(Session websocketSession) {
        JSObject closeEvent = ScriptUtils.makeObj();
        closeEvent.setMember("wasClean", true);
        closeEvent.setMember("code", CloseReason.CloseCodes.NORMAL_CLOSURE.getCode());
        closeEvent.setMember("reason", "");
        executeSessionFacadeMethod(websocketSession, WS_ON_CLOSE, new Object[]{closeEvent});
        websocketSession.getUserProperties().remove(PLATYPUS_SESSION_ATTR_NAME);
        websocketSession.getUserProperties().remove(WS_SCRIPT_FACADE);
        websocketSession.getUserProperties().remove(JsModuleEndPointConfigurator.HANDSHAKE_REQUEST);
    }

    protected void executeSessionFacadeMethod(Session websocketSession, String methodName, Object[] args) {
        try {
            JSObject facade = (JSObject) websocketSession.getUserProperties().get(WS_SCRIPT_FACADE);
            Object oFun = facade.getMember(methodName);
            if (oFun instanceof JSObject && ((JSObject) oFun).isFunction()) {
                JSObject callable = (JSObject) oFun;
                callable.call(facade, args);
            } else {
                Logger.getLogger(JsModuleEndPoint.class.getName()).log(Level.WARNING, "No method {0} found in {1}", new Object[]{methodName, WebSocketSession.class.getSimpleName()});
            }
        } catch (Exception ex) {
            Logger.getLogger(JsModuleEndPoint.class.getName()).log(Level.WARNING, ex.getMessage());
        }
    }

    protected PlatypusServerCore lookupPlaypusServerCore() throws IllegalStateException, Exception {
        PlatypusServerCore serverCore = PlatypusServerCore.getInstance();
        if (serverCore == null) {
            throw new IllegalStateException("Platypus server core is not initialized");
        }
        return serverCore;
    }

    protected com.eas.server.Session lookupPlatypusSession(Session websocketSession, PlatypusServerCore serverCore) throws Exception, IllegalStateException {
        HandshakeRequest handshake = (HandshakeRequest) websocketSession.getUserProperties().get(JsModuleEndPointConfigurator.HANDSHAKE_REQUEST);
        PlatypusPrincipal principal = endpointRequestPrincipal(handshake, websocketSession);
        com.eas.server.Session session = platypusSessionByWebSocketSession(websocketSession, serverCore, principal);
        assert session != null : "Platypus session missing";
        session.setPrincipal(principal);
        return session;
    }

    public static com.eas.server.Session platypusSessionByWebSocketSession(Session websocketSession, PlatypusServerCore aCore, PlatypusPrincipal principal) {
        com.eas.server.Session session;
        SessionManager sessionManager = aCore.getSessionManager();
        synchronized (sessionManager) {// Note: Internal sessionManager's synchronization is done on the same point.
            String platypusSessionId = (String) websocketSession.getUserProperties().get(PLATYPUS_SESSION_ATTR_NAME);
            if (platypusSessionId == null) {
                platypusSessionId = websocketSession.getId();
            }
            session = sessionManager.getOrCreateSession(principal, platypusSessionId);
            websocketSession.getUserProperties().put(PLATYPUS_SESSION_ATTR_NAME, platypusSessionId);
        }
        return session;
    }

    private static PlatypusPrincipal endpointRequestPrincipal(HandshakeRequest aRequest, Session websocketSession) {
        if (aRequest.getUserPrincipal() != null) {
            return new WebSocketPlatypusPrincipal(aRequest.getUserPrincipal().getName(), aRequest);
        } else {
            return new AnonymousPlatypusPrincipal(websocketSession.getId());
        }
    }
}
