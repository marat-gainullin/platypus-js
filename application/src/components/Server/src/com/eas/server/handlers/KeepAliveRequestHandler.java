/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.server.handlers;

import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.KeepAliveRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import com.eas.server.SessionRequestHandler;
import java.util.logging.Logger;

/**
 *
 * @author pk
 */
public class KeepAliveRequestHandler extends SessionRequestHandler<KeepAliveRequest> {

    public KeepAliveRequestHandler(PlatypusServerCore server, Session session, KeepAliveRequest rq)
    {
        super(server, session, rq);
    }

    @Override
    public Response handle2() throws Exception
    {
        Logger.getLogger(KeepAliveRequestHandler.class.getName()).finest("keeping alive.");
        return new KeepAliveRequest.Response(getRequest().getID());
    }

}
