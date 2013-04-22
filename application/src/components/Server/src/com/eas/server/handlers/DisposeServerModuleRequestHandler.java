/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.DisposeServerModuleRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import com.eas.server.SessionRequestHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pk
 */
public class DisposeServerModuleRequestHandler extends SessionRequestHandler<DisposeServerModuleRequest> {

    public DisposeServerModuleRequestHandler(PlatypusServerCore server, Session session, DisposeServerModuleRequest rq) {
        super(server, session, rq);
    }

    @Override
    public Response handle2() throws Exception {
        Logger.getLogger(DisposeServerModuleRequestHandler.class.getName()).log(Level.FINE, "Disposing server module {0}", getRequest().getModuleName());
        getSession().unregisterModule(getRequest().getModuleName());
        return new DisposeServerModuleRequest.Response(getRequest().getID());
    }
}
