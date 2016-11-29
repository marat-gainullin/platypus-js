/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import java.util.function.Consumer;

/**
 *
 * @param <T>
 * @author pk
 * @param <R>
 */
public abstract class RequestHandler<T extends Request, R extends Response> {

    private final T request;
    protected final PlatypusServerCore serverCore;

    public RequestHandler(PlatypusServerCore aServerCore, T aRequest) {
        super();
        serverCore = aServerCore;
        request = aRequest;
    }

    public T getRequest() {
        return request;
    }

    public PlatypusServerCore getServerCore() {
        return serverCore;
    }

    public abstract void handle(Session aSession, Consumer<R> onSuccess, Consumer<Exception> onFailure);

}
