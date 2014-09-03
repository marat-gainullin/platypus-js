/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.threetier.requests.HelloRequest;
import com.eas.server.PlatypusServerCore;
import java.util.function.Consumer;

/**
 *
 * @author pk
 */
public class HelloRequestHandler extends CommonRequestHandler<HelloRequest, HelloRequest.Response> {

    public HelloRequestHandler(PlatypusServerCore aServerCore, HelloRequest aRequest) {
        super(aServerCore, aRequest);
    }

    @Override
    public void handle(Consumer<HelloRequest.Response> onSuccess, Consumer<Exception> onFailure) {
        if (onSuccess != null) {
            onSuccess.accept(new HelloRequest.Response());
        }
    }

}
