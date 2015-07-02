/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.mina.platypus;

import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.platypus.RequestEnvelope;
import com.eas.client.threetier.requests.ErrorResponse;
import com.eas.script.Scripts;
import com.eas.server.*;
import com.eas.server.DatabaseAuthorizer;
import com.eas.util.IDGenerator;
import java.net.NetPermission;
import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.script.ScriptException;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 *
 * @author pk, mg, ab
 */
public class PlatypusRequestsHandler extends IoHandlerAdapter {

    public static final String BAD_PROTOCOL_MSG = "The message is not Platypus protocol request.";
    public static final String GOT_SIGNATURE_MSG = "Got signature from client.";
    public static final String SESSION_ID = "platypus-session-id";
    private static final String GENERAL_EXCEPTION_MESSAGE = "Exception on request of type %d | %s. %s";
    private static final String ACCESS_CONTROL_EXCEPTION_MESSAGE = "AccessControlException on request of type %d | %s. Message: %s.";
    private static final String SQL_EXCEPTION_MESSAGE = "SQLException on request of type %d | %s. Message: %s. sqlState: %s, errorCode: %d";
    public static final int IDLE_TIME_EVENT = 5 * 60; // 5 minutes
    public static final int SESSION_TIME_OUT = 60 * 60; // 1 hour
    protected int sessionIdleCheckInterval = IDLE_TIME_EVENT;
    protected int sessionIdleTime = SESSION_TIME_OUT;
    private final PlatypusServer server;

    public PlatypusRequestsHandler(PlatypusServer aServer) {
        super();
        server = aServer;
    }

    public int getSessionIdleCheckInterval() {
        return sessionIdleCheckInterval;
    }

    public void setSessionIdleCheckInterval(int aValue) {
        sessionIdleCheckInterval = aValue;
    }

    public int getSessionIdleTime() {
        return sessionIdleTime;
    }

    public void setSessionIdleTime(int aValue) {
        sessionIdleTime = aValue;
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
        session.getConfig().setBothIdleTime(sessionIdleCheckInterval);
    }

    @Override
    public void sessionIdle(IoSession ioSession, IdleStatus status) throws Exception {
        super.sessionIdle(ioSession, status);
        if (ioSession.getBothIdleCount() * sessionIdleCheckInterval >= sessionIdleTime) {
            ioSession.close(false);
            String sessionId = (String) ioSession.getAttribute(SESSION_ID);
            if (sessionId != null) {
                // A bit hacky, due to sessions nature. ioSession is NOT an equivalent of sessionManager.session,
                // but we use idle events as source of information about idle sessions
                server.getSessionManager().remove(sessionId);
            } else {
                Logger.getLogger(PlatypusRequestsHandler.class.getName()).severe("An attempt to remove system (null) session occured.");
            }
        }
    }

    /**
     *
     * @param ioSession
     * @param msg
     * @throws Exception
     */
    @Override
    public void messageReceived(IoSession ioSession, Object msg) throws Exception {
        if (msg instanceof RequestEnvelope) {
            RequestEnvelope requestEnv = (RequestEnvelope) msg;
            try {
                Consumer<Exception> onError = (Exception ex) -> {
                    if (ex instanceof SQLException) {
                        SQLException sex = (SQLException) ex;
                        Logger.getLogger(PlatypusRequestsHandler.class.getName()).log(Level.SEVERE, String.format(SQL_EXCEPTION_MESSAGE, requestEnv.request.getType(), requestEnv.request.getClass().getSimpleName(), sex.getMessage(), sex.getSQLState(), sex.getErrorCode()));
                        ioSession.write(new ErrorResponse(sex));
                    } else if (ex instanceof AccessControlException) {
                        AccessControlException aex = (AccessControlException) ex;
                        Logger.getLogger(PlatypusRequestsHandler.class.getName()).log(Level.SEVERE, String.format(ACCESS_CONTROL_EXCEPTION_MESSAGE, requestEnv.request.getType(), requestEnv.request.getClass().getSimpleName(), aex.getMessage()));
                        ioSession.write(new ErrorResponse(aex));
                    } else {
                        Logger.getLogger(PlatypusRequestsHandler.class.getName()).log(Level.SEVERE, String.format(GENERAL_EXCEPTION_MESSAGE, requestEnv.request.getType(), requestEnv.request.getClass().getSimpleName(), ex.toString()));
                        ioSession.write(new ErrorResponse(ex.getMessage() != null && !ex.getMessage().isEmpty() ? ex.getMessage() : ex.toString()));
                    }
                };
                Logger.getLogger(PlatypusRequestsHandler.class.getName()).log(Level.FINE, "Request {0}", requestEnv.request.toString());
                final RequestHandler<Request, Response> handler = (RequestHandler<Request, Response>) RequestHandlerFactory.getHandler(server, requestEnv.request);
                if (handler != null) {
                    if (requestEnv.ticket == null) {
                        try {
                            Session session = server.getSessionManager().create(String.valueOf(IDGenerator.genID()));
                            requestEnv.ticket = session.getId();
                            // It is safe to put SESSION_ID attribute here because of request-response
                            // protocol nature.
                            // Another thread needs access to the same ioSession or same ticket to
                            // get access to partially initialized platypus session.
                            // So, the following call to DatabaseAuthorizer.authorize and further initialization
                            // of platypus session in callback body is safe.
                            ioSession.setAttribute(SESSION_ID, session.getId());
                            Scripts.LocalContext context = Scripts.createContext(session.getSpace());
                            Scripts.setContext(context);
                            try {
                                DatabaseAuthorizer.authorize(server, requestEnv.userName, requestEnv.password, Scripts.getSpace(), (PlatypusPrincipal aPrincipal) -> {
                                    session.setPrincipal(aPrincipal);
                                    // The only place to use this getter.
                                    // See its javadoc please.
                                    context.setPrincipal(session.getPrincipal());
                                    Scripts.getSpace().process(() -> {
                                        handler.handle(session, (Response aResponse) -> {
                                            ioSession.write(aResponse);
                                        }, onError);
                                    });
                                }, onError);
                            } finally {
                                Scripts.setContext(null);
                            }
                        } catch (ScriptException ex) {
                            Logger.getLogger(PlatypusRequestsHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        Session session = server.getSessionManager().get(requestEnv.ticket);
                        if (session != null) {
                            ioSession.setAttribute(SESSION_ID, session.getId());
                            Scripts.LocalContext context = Scripts.createContext(session.getSpace());
                            // The only place to use this getter.
                            // See its javadoc please.
                            context.setPrincipal(session.getPrincipal());
                            Scripts.setContext(context);
                            try {
                                Scripts.getSpace().process(() -> {
                                    handler.handle(session, (Response aResponse) -> {
                                        ioSession.write(aResponse);
                                    }, onError);
                                });
                            } finally {
                                Scripts.setContext(null);
                            }
                        } else {
                            throw new AccessControlException("Bad session ticket.", new NetPermission("*"));
                        }
                    }
                } else {
                    throw new IllegalStateException("Unknown request type " + requestEnv.request.getType());
                }
            } catch (Throwable ex) {
                ioSession.write(new ErrorResponse(ex.getMessage()));
                Logger.getLogger(PlatypusRequestsHandler.class.getName()).log(Level.SEVERE, ex.toString());
            }
        } else {
            throw new IllegalArgumentException(BAD_PROTOCOL_MSG);
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        Logger.getLogger(PlatypusRequestsHandler.class.getName()).log(Level.SEVERE, cause.toString());
    }

    @Override
    public void messageSent(IoSession ioSession, Object message) throws Exception {
    }

}
