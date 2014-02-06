/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.mina.platypus;

import com.eas.client.threetier.ErrorResponse;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.Requests;
import com.eas.client.threetier.Response;
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
        if (msg instanceof Signature) {
            Logger.getLogger(PlatypusRequestsHandler.class.getName()).finest(GOT_SIGNATURE_MSG);
            ioSession.write(new Signature());
        } else if (msg instanceof Request) {
            final Request rq = (Request) msg;
            Logger.getLogger(PlatypusRequestsHandler.class.getName()).log(Level.FINE, "Request {0}", rq.toString());
            String sessionId = (String) ioSession.getAttribute(SESSION_ID);
            if (sessionId != null || rq.getType() == Requests.rqLogin) {
                Session session = server.getSessionManager().get(sessionId);
                if (session != null) {
                    final Response pendingResponse = session.getPendingResponse(rq.getID());
                    if (pendingResponse != null) {
                        ioSession.write(pendingResponse);
                        return;
                    }
                }
                try {
                    final RequestHandler<?> handler = RequestHandlerFactory.getHandler(server, session, rq);
                    if (handler == null) {
                        ioSession.write(new ErrorResponse(rq.getID(), "Unknown request type " + rq.getType()));
                    } else {
                        handler.run();
                        final Response response = handler.getResponse();
                        if (handler instanceof LoginRequestHandler && response instanceof LoginRequest.Response) {
                            session = server.getSessionManager().get(((LoginRequest.Response) handler.getResponse()).getSessionId());
                            ioSession.setAttribute(SESSION_ID, session.getId());
                            ioSession.write(response);
                            //session.processPendingResponses(new PendingResponseSender(ioSession, session));
                        } else {
                            if (!(handler instanceof LogoutRequestHandler)) {
                                if (session != null) {
                                    session.addPendingResponse(response);
                                }
                                ioSession.write(response);
                            }
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(PlatypusRequestsHandler.class.getName()).log(Level.SEVERE, null, ex);
                    Response r = new ErrorResponse(rq.getID(), ex.getMessage());
                    if (session != null) {
                        session.addPendingResponse(r);
                    }
                    ioSession.write(r);
                }
            } else {
                throw new IllegalArgumentException(BAD_SESSION_ID_MSG);
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
        String sessionId = (String) ioSession.getAttribute(SESSION_ID);
        Session session = server.getSessionManager().get(sessionId);
        if (session != null && message instanceof Response) {
            session.removePendingResponse((Response) message);
        }
    }

    private static class PendingResponseSender implements ResponseProcessor {

        private final Session session;
        private final IoSession ioSession;

        public PendingResponseSender(IoSession aIoSession, Session aSession) {
            ioSession = aIoSession;
            session = aSession;
        }

        @Override
        public void processResponse(Response aResponse) {
            Logger.getLogger(PendingResponseSender.class.getName()).log(Level.FINE, "Sending pending response {0}", aResponse);
            ioSession.write(aResponse);
            session.removePendingResponse(aResponse);
        }
    }
}
