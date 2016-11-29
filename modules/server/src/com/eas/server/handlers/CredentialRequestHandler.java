/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.login.PlatypusPrincipal;
import com.eas.client.threetier.requests.CredentialRequest;
import com.eas.script.Scripts;
import com.eas.server.PlatypusServerCore;
import com.eas.server.RequestHandler;
import com.eas.server.Session;
import java.util.function.Consumer;

/**
 *
 * @author mg
 */
public class CredentialRequestHandler extends RequestHandler<CredentialRequest, CredentialRequest.Response> {

    public CredentialRequestHandler(PlatypusServerCore aServerCore, CredentialRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    public void handle(Session aSession, Consumer<CredentialRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        if (onSuccess != null) {
            onSuccess.accept(new CredentialRequest.Response(((PlatypusPrincipal) Scripts.getContext().getPrincipal()).getName()));
        }
    }
}
