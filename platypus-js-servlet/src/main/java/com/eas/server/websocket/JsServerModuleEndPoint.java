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
import com.eas.util.IdGenerator;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptException;
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
    private static final String WS_ON_OPEN = "onopen";
    private static final String WS_ON_MESSAGE = "onmessage";
    private static final String WS_ON_CLOSE = "onclose";
    private static final String WS_ON_ERROR = "onerror";

    protected volatile String wasPlatypusSessionId;
    protected volatile String moduleName;

    public JsServerModuleEndPoint() {
        super();
    }

    protected void in(PlatypusServerCore platypusCore, Session websocketSession, Consumer<com.eas.server.Session> aHandler) throws Exception {
        HandshakeRequest handshake = (HandshakeRequest) websocketSession.getUserProperties().get(JsServerModuleEndPointConfigurator.HANDSHAKE_REQUEST);
        String userName = websocketSession.getUserPrincipal() != null ? websocketSession.getUserPrincipal().getName() : null;
        Consumer<com.eas.server.Session> withPlatypusSession = (com.eas.server.Session aSession) -> {
            // Websocket executor thread or sessions accounting thread
            Scripts.LocalContext context = new Scripts.LocalContext(platypusPrincipal(handshake, websocketSession), aSession);
            aSession.getSpace().process(context, () -> {
                // temporarily session thread 
                try {
                    aHandler.accept(aSession);
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusHttpServlet.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        };
        com.eas.server.Session lookedup0 = platypusSessionByWebSocketSession(websocketSession);
        if (lookedup0 == null) {// Zombie check
            platypusCore.getQueueSpace().process(() -> {
                // sessions accounting thread
                com.eas.server.Session lookedup1 = platypusSessionByWebSocketSession(websocketSession);
                if (lookedup1 == null) {// Zombie check
                    try {
                        Consumer<String> withDataContext = (String dataContext) -> {
                            // still sessions accounting thread
                            String platypusSessionId = (String) websocketSession.getUserProperties().get(PlatypusHttpServlet.PLATYPUS_SESSION_ID_ATTR_NAME);
                            // platypusSessionId may be replicated from another instance in cluster
                            com.eas.server.Session lookedup2 = platypusSessionId != null ? SessionManager.Singleton.instance.get(platypusSessionId) : null;
                            if (lookedup2 == null) {// Non zombie check
                                try {
                                    // preserve replicated session id
                                    com.eas.server.Session created = SessionManager.Singleton.instance.create(platypusSessionId == null ? IdGenerator.genId() + "" : platypusSessionId);
                                    if (dataContext == null) {
                                        websocketSession.getUserProperties().remove(PlatypusHttpServlet.PLATYPUS_USER_CONTEXT_ATTR_NAME);
                                    } else {
                                        websocketSession.getUserProperties().put(PlatypusHttpServlet.PLATYPUS_USER_CONTEXT_ATTR_NAME, dataContext);
                                    }
                                    // publishing a session
                                    wasPlatypusSessionId = created.getId();
                                    websocketSession.getUserProperties().put(PlatypusHttpServlet.PLATYPUS_SESSION_ID_ATTR_NAME, created.getId());
                                    // a session has been published
                                    Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.INFO, "WebSocket platypus session opened. Session id: {0}", created.getId());
                                    withPlatypusSession.accept(created);
                                } catch (ScriptException ex) {
                                    Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            } else {
                                withPlatypusSession.accept(lookedup2);
                            }
                        };
                        if (websocketSession.getUserPrincipal() != null) {// Additional properties can be obtained only for authorized users
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
                    }
                } else {
                    withPlatypusSession.accept(lookedup1);
                }
            });
        } else {
            withPlatypusSession.accept(lookedup0);
        }
    }

    public PlatypusPrincipal platypusPrincipal(HandshakeRequest handshake, Session websocketSession) {
        PlatypusPrincipal principal;
        if (handshake.getUserPrincipal() != null) {
            principal = new WebSocketPlatypusPrincipal(handshake.getUserPrincipal().getName(), (String) websocketSession.getUserProperties().get(PlatypusHttpServlet.PLATYPUS_USER_CONTEXT_ATTR_NAME), handshake);
        } else {
            principal = new AnonymousPlatypusPrincipal(websocketSession.getId());
        }
        return principal;
    }

    public com.eas.server.Session platypusSessionByWebSocketSession(Session websocketSession) {
        String platypusSessionId = (String) websocketSession.getUserProperties().get(PlatypusHttpServlet.PLATYPUS_SESSION_ID_ATTR_NAME);
        return platypusSessionId != null ? SessionManager.Singleton.instance.get(platypusSessionId) : null;
    }

    @OnOpen
    public void sessionOpened(Session websocketSession, @PathParam("module-name") String aModuleName) throws Exception {
        moduleName = aModuleName;
        PlatypusServerCore platypusCore = lookupPlaypusServerCore();
        in(platypusCore, websocketSession, (com.eas.server.Session aSession) -> {
            try {
                Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.FINE, "WebSocket container OnOpen {0}.", aSession.getId());
                platypusCore.executeMethod(moduleName, WS_ON_OPEN, new Object[]{new WebSocketServerSession(websocketSession)}, true, (Object aResult) -> {
                    Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.FINE, "{0} method of {1} module called successfully.", new Object[]{WS_ON_OPEN, aModuleName});
                }, (Exception ex) -> {
                    Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.SEVERE, null, ex);
                });
            } catch (Exception ex) {
                Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    @OnMessage
    public void messageRecieved(Session websocketSession, String aData) throws Exception {
        PlatypusServerCore platypusCore = lookupPlaypusServerCore();
        in(platypusCore, websocketSession, (com.eas.server.Session aSession) -> {
            Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.FINE, "WebSocket container OnMessage {0}.", aSession.getId());
            JSObject messageEvent = Scripts.getSpace().makeObj();
            messageEvent.setMember("data", aData);
            messageEvent.setMember("id", websocketSession.getId());
            platypusCore.executeMethod(moduleName, WS_ON_MESSAGE, new Object[]{messageEvent}, true, (Object aResult) -> {
                Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.FINE, "{0} method of {1} module called successfully.", new Object[]{WS_ON_MESSAGE, moduleName});
            }, (Exception ex) -> {
                Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.SEVERE, null, ex);
            });
        });
    }

    @OnError
    public void errorInSession(Session websocketSession, Throwable aError) throws Exception {
        PlatypusServerCore platypusCore = lookupPlaypusServerCore();
        in(platypusCore, websocketSession, (com.eas.server.Session aSession) -> {
            Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.FINE, "WebSocket container OnError {0}.", aSession.getId());
            JSObject errorEvent = Scripts.getSpace().makeObj();
            errorEvent.setMember("message", aError.getMessage());
            errorEvent.setMember("id", websocketSession.getId());
            Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.SEVERE, null, aError);
            platypusCore.executeMethod(moduleName, WS_ON_ERROR, new Object[]{errorEvent}, true, (Object aResult) -> {
                Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.FINE, "{0} method of {1} module called successfully.", new Object[]{WS_ON_ERROR, moduleName});
            }, (Exception ex) -> {
                Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.SEVERE, null, ex);
            });
        });
    }

    @OnClose
    public void sessionClosed(Session websocketSession) throws Exception {
        PlatypusServerCore platypusCore = lookupPlaypusServerCore();
        in(platypusCore, websocketSession, (com.eas.server.Session aSession) -> {
            Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.FINE, "WebSocket container OnClose {0}.", aSession.getId());
            JSObject closeEvent = Scripts.getSpace().makeObj();
            closeEvent.setMember("wasClean", true);
            closeEvent.setMember("code", CloseReason.CloseCodes.NORMAL_CLOSURE.getCode());
            closeEvent.setMember("reason", "");
            closeEvent.setMember("id", websocketSession.getId());
            platypusCore.executeMethod(moduleName, WS_ON_CLOSE, new Object[]{closeEvent}, true, (Object aResult) -> {
                com.eas.server.Session session = SessionManager.Singleton.instance.remove(wasPlatypusSessionId);
                Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.INFO, "WebSocket platypus session closed. Session id: {0}", session.getId());
                Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.FINE, "{0} method of {1} module called successfully.", new Object[]{WS_ON_CLOSE, moduleName});
            }, (Exception ex) -> {
                com.eas.server.Session session = SessionManager.Singleton.instance.remove(wasPlatypusSessionId);
                Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.INFO, "WebSocket platypus session closed. Session id: {0}", session.getId());
                Logger.getLogger(JsServerModuleEndPoint.class.getName()).log(Level.SEVERE, null, ex);
            });
        });
    }

    protected PlatypusServerCore lookupPlaypusServerCore() throws IllegalStateException, Exception {
        PlatypusServerCore serverCore = PlatypusHttpServlet.getCore();
        if (serverCore == null) {
            throw new IllegalStateException("Platypus server core is not initialized");
        }
        return serverCore;
    }
}
