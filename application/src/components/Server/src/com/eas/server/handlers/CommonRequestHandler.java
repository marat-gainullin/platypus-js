/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.eas.server.handlers;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import com.eas.server.PlatypusServerCore;
import com.eas.server.RequestHandler;
import java.util.function.Consumer;

/**
 *
 * @author mg
 * @param <T>
 * @param <R>
 */
public abstract class CommonRequestHandler<T extends Request, R extends Response> extends RequestHandler<T, R> {

    public CommonRequestHandler(PlatypusServerCore aServerCore, T aRequest) {
        super(aServerCore, aRequest);
    }

    public abstract void handle(Consumer<R> onSuccess, Consumer<Exception> onFailure);
}
