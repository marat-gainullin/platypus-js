/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.threetier.requests.IsUserInRoleRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import java.util.function.Consumer;

/**
 *
 * @author mg
 */
public class IsUserInRoleRequestHandler extends SessionRequestHandler<IsUserInRoleRequest, IsUserInRoleRequest.Response> {

    public IsUserInRoleRequestHandler(PlatypusServerCore aServerCore, IsUserInRoleRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    protected void handle2(Session aSession, Consumer<IsUserInRoleRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        onSuccess.accept(new IsUserInRoleRequest.Response(aSession.getPrincipal().hasRole(getRequest().getRoleName())));
    }
}
