/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.handlers;

import com.eas.client.threetier.Response;
import com.eas.client.threetier.requests.HelloRequest;
import com.eas.server.PlatypusServerCore;
import com.eas.server.RequestHandler;

/**
 *
 * @author pk
 */
public class HelloRequestHandler extends RequestHandler<HelloRequest> {

    public HelloRequestHandler(PlatypusServerCore server, HelloRequest rq) {
        super(server, rq);
    }

    @Override
    protected Response handle() throws Exception {
        return new HelloRequest.Response();
    }

}
