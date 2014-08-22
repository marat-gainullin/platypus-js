/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.mina.platypus;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.ErrorResponse;
import com.eas.client.threetier.requests.LoginRequest;
import com.eas.server.*;
import com.eas.server.handlers.LoginRequestHandler;
import com.eas.server.handlers.LogoutRequestHandler;
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
    public static final String BAD_SESSION_ID_MSG = "Bad session id, login first.";
    public static final String GOT_SIGNATURE_MSG = "Got signature from client.";
    public static final String SESSION_ID = "sessionID";
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
        if (msg instanceof RequestDecoder.RequestEnvelope) {
            try {
                final Request request = ((RequestDecoder.RequestEnvelope) msg).request;
                String sessionTicket = ((RequestDecoder.RequestEnvelope) msg).ticket;
                Logger.getLogger(PlatypusRequestsHandler.class.getName()).log(Level.FINE, "Request {0}", request.toString());
                if (sessionTicket != null) {
                    assert request.getType() != Requests.rqLogin;
                    Session session = server.getSessionManager().get(sessionTicket);
                    assert session != null;
                    final RequestHandler<?> handler = RequestHandlerFactory.getHandler(server, session, request);
                    if (handler != null) {
                        handler.call((Response aResponse) -> {
                            if (!(handler instanceof LogoutRequestHandler)) {
                                ioSession.write(aResponse);
                            }
                        });
                    } else {
                        throw new IllegalStateException("Unknown request type " + request.getType());
                    }
                } else {
                    assert request instanceof LoginRequest : "Unauthorized. Login first.";
                    LoginRequestHandler loginHandler = new LoginRequestHandler(server, (LoginRequest) request);
                    LoginRequest.Response response = (LoginRequest.Response) loginHandler.call(null);
                    Session session = server.getSessionManager().get(response.getSessionId());
                    assert session != null : "Session had to be created by login request handler.";
                    ioSession.setAttribute(SESSION_ID, response.getSessionId());
                    ioSession.write(response);
                }
            } catch (Exception ex) {
                ioSession.write(new ErrorResponse(ex.getMessage()));
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
