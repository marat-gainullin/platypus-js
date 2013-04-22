/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.login.DbPlatypusPrincipal;
import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.StartAppElementRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import com.eas.server.SessionRequestHandler;

/**
 *
 * @author mg
 */
public class StartAppElementRequestHandler extends SessionRequestHandler<StartAppElementRequest> {

    public StartAppElementRequestHandler(PlatypusServerCore aServerCore, Session aSession, StartAppElementRequest aRequest) {
        super(aServerCore, aSession, aRequest);
    }

    @Override
    protected Response handle2() throws Exception {
        String startAppElement = null;
        PlatypusPrincipal principal = getSession().getPrincipal();
        if (principal instanceof DbPlatypusPrincipal) {
            startAppElement = ((DbPlatypusPrincipal) principal).getStartAppElement();
        }
        if (startAppElement == null) {
            startAppElement = getServerCore().getDefaultAppElement();
        }
        return new StartAppElementRequest.Response(getRequest().getID(), startAppElement);
    }
}
