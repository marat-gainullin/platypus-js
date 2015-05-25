/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.platypus;

import com.eas.client.threetier.Request;
import com.eas.client.threetier.Response;
import java.util.function.Consumer;

/**
 *
 * @author mg
 */
public class RequestEnvelope {

    public final Request request;
    public String userName;
    public String password;
    public String ticket;
    public Response response;
    public Consumer<Response> onComplete;

    public RequestEnvelope(Request aRequest, String aUserName, String aPassword, String aTicket, Consumer<Response> aOnComplete) {
        super();
        request = aRequest;
        userName = aUserName;
        password = aPassword;
        ticket = aTicket;
        onComplete = aOnComplete;
    }

    public void waitRequestCompletion() throws InterruptedException {
        synchronized (request) {// synchronized due to J2SE javadoc on wait()/notify() methods
            while (!request.isDone()) {
                request.wait();
            }
        }
    }

    public void notifyDone() {
        synchronized (request) {// synchronized due to J2SE javadoc on wait()/notify() methods
            request.setDone(true);
            request.notifyAll();
        }
    }
}
