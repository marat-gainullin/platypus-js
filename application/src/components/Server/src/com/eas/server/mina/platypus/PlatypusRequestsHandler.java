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
import com.eas.server.*;
import com.eas.server.handlers.CommonRequestHandler;
import com.eas.server.SessionRequestHandler;
import com.eas.server.DatabaseAuthorizer;
import java.security.AccessControlException;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 *
 * @author pk
 */
public class PlatypusRequestsHandler extends IoHandlerAdapter {

    public static final String BAD_PROTOCOL_MSG = "The message is not Platypus protocol request.";
    public static final String GOT_SIGNATURE_MSG = "Got signature from client.";
    public static final String SESSION_ID = "sessionID";
    private static final String GENERAL_EXCEPTION_MESSAGE = "Exception on request of type %d | %s.";
    private static final String ACCESS_CONTROL_EXCEPTION_MESSAGE = "AccessControlException on request of type %d | %s. Message: %s.";
    private static final String SQL_EXCEPTION_MESSAGE = "SQLException on request of type %d | %s. Message: %s. sqlState: %s, errorCode: %d";
    public static final int IDLE_TIME_EVENT = 5 * 60; // 5 minutes
    public static final int SESSION_TIME_OUT = 60 * 60; // 1 hour
    protected int sessionIdleCheckInterval = IDLE_TIME_EVENT;
    protected int sessionIdleTime = SESSION_TIME_OUT;
    private final PlatypusServerCore server;

    public PlatypusRequestsHandler(PlatypusServerCore aServer) {
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
                        Logger.getLogger(PlatypusRequestsHandler.class.getName()).log(Level.SEVERE, String.format(SQL_EXCEPTION_MESSAGE, requestEnv.request.getType(), requestEnv.request.getClass().getSimpleName(), sex.getMessage(), sex.getSQLState(), sex.getErrorCode()), sex);
                        ioSession.write(new ResponseEnvelope(new ErrorResponse(sex), requestEnv.ticket));
                    } else if (ex instanceof AccessControlException) {
                        AccessControlException aex = (AccessControlException) ex;
                        Logger.getLogger(PlatypusRequestsHandler.class.getName()).log(Level.SEVERE, String.format(ACCESS_CONTROL_EXCEPTION_MESSAGE, requestEnv.request.getType(), requestEnv.request.getClass().getSimpleName(), aex.getMessage()), aex);
                        ioSession.write(new ResponseEnvelope(new ErrorResponse(aex), requestEnv.ticket));
                    } else {
                        Logger.getLogger(PlatypusRequestsHandler.class.getName()).log(Level.SEVERE, String.format(GENERAL_EXCEPTION_MESSAGE, requestEnv.request.getType(), requestEnv.request.getClass().getSimpleName()), ex);
                        ioSession.write(new ResponseEnvelope(new ErrorResponse(ex.getMessage() != null && !ex.getMessage().isEmpty() ? ex.getMessage() : ex.getClass().getSimpleName()), requestEnv.ticket));
                    }
                };
                Logger.getLogger(PlatypusRequestsHandler.class.getName()).log(Level.FINE, "Request {0}", requestEnv.request.toString());
                final RequestHandler<?, ?> handler = RequestHandlerFactory.getHandler(server, requestEnv.request);
                if (handler != null) {
                    Consumer<Response> onSuccess = (Response aResponse) -> {
                        ioSession.write(new ResponseEnvelope(aResponse, requestEnv.ticket));
                    };
                    if (handler instanceof SessionRequestHandler<?, ?>) {
                        if (requestEnv.ticket == null) {
                            DatabaseAuthorizer.authorize(server, requestEnv.userName, requestEnv.password, (Session session) -> {
                                requestEnv.ticket = session.getId();
                                PlatypusPrincipal.setInstance(session.getPrincipal());
                                try {
                                    ((SessionRequestHandler<Request, Response>) handler).handle(session, onSuccess, onError);
                                } finally {
                                    PlatypusPrincipal.setInstance(null);
                                }
                            }, onError);
                        } else {
                            Session session = server.getSessionManager().get(requestEnv.ticket);
                            if (session != null) {
                                PlatypusPrincipal.setInstance(session.getPrincipal());
                                try {
                                    ((SessionRequestHandler<Request, Response>) handler).handle(session, onSuccess, onError);
                                } finally {
                                    PlatypusPrincipal.setInstance(null);
                                }
                            } else {
                                onError.accept(new AccessControlException("Bad session ticket."));
                            }
                        }
                    } else {
                        ((CommonRequestHandler<Request, Response>) handler).handle(onSuccess, onError);
                    }
                } else {
                    throw new IllegalStateException("Unknown request type " + requestEnv.request.getType());
                }
            } catch (Exception ex) {
                ioSession.write(new ResponseEnvelope(new ErrorResponse(ex.getMessage()), requestEnv.ticket));
            }
        } else {
            throw new IllegalArgumentException(BAD_PROTOCOL_MSG);
        }
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        Logger.getLogger(PlatypusRequestsHandler.class.getName()).log(Level.SEVERE, null, cause);
    }

    @Override
    public void messageSent(IoSession ioSession, Object message) throws Exception {
    }

}
