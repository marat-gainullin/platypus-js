/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.KeepAliveRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.Session;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 *
 * @author pk
 */
public class KeepAliveRequestHandler extends CommonRequestHandler<KeepAliveRequest, KeepAliveRequest.Response> {

    public KeepAliveRequestHandler(PlatypusServerCore aServerCore, KeepAliveRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    public void handle(Consumer<KeepAliveRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        Logger.getLogger(KeepAliveRequestHandler.class.getName()).finest("keeping alive.");
        if (onSuccess != null) {
            onSuccess.accept(new KeepAliveRequest.Response());
        }
    }
}
