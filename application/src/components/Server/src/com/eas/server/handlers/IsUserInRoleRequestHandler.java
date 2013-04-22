/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.IsUserInRoleRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import com.eas.server.SessionRequestHandler;

/**
 *
 * @author mg
 */
public class IsUserInRoleRequestHandler extends SessionRequestHandler<IsUserInRoleRequest> {

    public IsUserInRoleRequestHandler(PlatypusServerCore aServerCore, Session aSession, IsUserInRoleRequest aRequest) {
        super(aServerCore, aSession, aRequest);
    }

    @Override
    protected Response handle2() throws Exception {
        IsUserInRoleRequest.Response response = new IsUserInRoleRequest.Response(getRequest().getID(),
                getSession().getPrincipal().hasRole(getRequest().getRoleName()));
        return response;
    }
}
