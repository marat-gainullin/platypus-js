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

    //
    private static final String WS_ON_OPEN_METHOD_NAME = "onopen";
    private static final String WS_ON_MESSAGE = "onmessage";
    private static final String WS_ON_CLOSE = "onclose";
    private static final String WS_ON_ERROR = "onerror";

    protected com.eas.server.Session session;
    protected WebSocketServerSession facade;
    protected Scripts.Space space;

    public JsServerModuleEndPoint() {
        super();
    }

    @OnOpen
    public void sessionOpened(Session websocketSession, @PathParam("module-name") String aModuleName) throws Exception {
        PlatypusServerCore serverCore = lookupPlaypusServerCore();
        HandshakeRequest handshake = (HandshakeRequest) websocketSession.getUserProperties().get(JsServerModuleEndPointConfigurator.HANDSHAKE_REQUEST);
        SessionManager sessionManager = serverCore.getSessionManager();
        String userName = websocketSession.getUserPrincipal() != null ? websocketSession.getUserPrincipal().getName() : null;
        session = sessionManager.create(websocketSession.getId());
        Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.INFO, "WebSocket platypus session opened. Session id: {0}", session.getId());
        DatabasesClient.getUserProperties(serverCore.getDatabasesClient(), userName, session.getSpace(), (Map<String, String> aUserProps) -> {
            String usrContext = aUserProps.get(ClientConstants.F_USR_CONTEXT);
            PlatypusPrincipal principal;
            if (handshake.getUserPrincipal() != null) {
                principal = new WebSocketPlatypusPrincipal(handshake.getUserPrincipal().getName(), usrContext, handshake);
            } else {
                principal = new AnonymousPlatypusPrincipal(websocketSession.getId());
            }
            facade = new WebSocketServerSession(websocketSession);
            space = session.getSpace();
            space.setPrincipal(principal);
            serverCore.executeMethod(aModuleName, WS_ON_OPEN_METHOD_NAME, new Object[]{facade.getPublished()}, session, true, (Object aResult) -> {
                Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.FINE, "{0} method of {1} module called successfully.", new Object[]{WS_ON_OPEN_METHOD_NAME, aModuleName});
            }, (Exception ex) -> {
                Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.SEVERE, null, ex);
            });
        }, (Exception ex) -> {
            Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.WARNING, "Could not get user {0} properties (USR_CONTEXT, etc).", userName);
        });
    }

    @OnMessage
    public void messageRecieved(Session websocketSession, String aData) throws Exception {
        space.process(() -> {
            JSObject messageEvent = space.makeObj();
            messageEvent.setMember("data", aData);
            executeSessionFacadeMethod(WS_ON_MESSAGE, new Object[]{messageEvent});
        });
    }

    @OnError
    public void errorInSession(Session websocketSession, Throwable aError) {
        space.process(() -> {
            JSObject errorEvent = space.makeObj();
            errorEvent.setMember("message", aError.getMessage());
            executeSessionFacadeMethod(WS_ON_ERROR, new Object[]{errorEvent});
        });
    }

    @OnClose
    public void sessionClosed(Session websocketSession) throws Exception {
        PlatypusServerCore serverCore = lookupPlaypusServerCore();
        space.process(() -> {
            JSObject closeEvent = space.makeObj();
            closeEvent.setMember("wasClean", true);
            closeEvent.setMember("code", CloseReason.CloseCodes.NORMAL_CLOSURE.getCode());
            closeEvent.setMember("reason", "");
            executeSessionFacadeMethod(WS_ON_CLOSE, new Object[]{closeEvent});

            serverCore.getSessionManager().remove(session.getId());
            Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.INFO, "WebSocket platypus session closed. Session id: {0}", session.getId());
            session = null;
            space = null;
            facade = null;
        });
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
        PlatypusServerCore serverCore = PlatypusServerCore.getInstance();
        if (serverCore == null) {
            throw new IllegalStateException("Platypus server core is not initialized");
        }
        return serverCore;
    }
}
