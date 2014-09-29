/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.server.SessionRequestHandler;
import com.eas.client.threetier.requests.DisposeServerModuleRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pk
 */
public class DisposeServerModuleRequestHandler extends SessionRequestHandler<DisposeServerModuleRequest, DisposeServerModuleRequest.Response> {

    public DisposeServerModuleRequestHandler(PlatypusServerCore aServerCore, DisposeServerModuleRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    protected void handle2(Session aSession, Consumer<DisposeServerModuleRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        Logger.getLogger(DisposeServerModuleRequestHandler.class.getName()).log(Level.FINE, "Disposing server module {0}", getRequest().getModuleName());
        try {
            aSession.unregisterModule(getRequest().getModuleName());
            if (onSuccess != null) {
                onSuccess.accept(new DisposeServerModuleRequest.Response());
            }
        } catch (Exception ex) {
            if (onFailure != null) {
                onFailure.accept(ex);
            }
        }
    }
}
