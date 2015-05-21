/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import com.eas.script.Scripts;
import java.util.function.Consumer;

/**
 *
 * @author kl
 */
public interface AppConnection {

    public <R extends Response> void enqueueRequest(Request aRequest, Scripts.Space aSpace, Consumer<R> onSuccess, Consumer<Exception> onFailure) throws Exception;

    public <R extends Response> R executeRequest(Request aRequest) throws Exception;

    public void shutdown();
}
