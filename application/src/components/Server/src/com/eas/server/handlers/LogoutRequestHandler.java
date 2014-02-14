/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import com.eas.server.SessionRequestHandler;

/**
 *
 * @author pk
 */
public class LogoutRequestHandler extends SessionRequestHandler<LogoutRequest> {

    public LogoutRequestHandler(PlatypusServerCore server, Session session, LogoutRequest rq) {
        super(server, session, rq);
    }

    @Override
    public Response handle2() throws Exception {
        getServerCore().getSessionManager().remove(getSession().getId());
        return new LogoutRequest.Response(getRequest().getID());
    }
}
