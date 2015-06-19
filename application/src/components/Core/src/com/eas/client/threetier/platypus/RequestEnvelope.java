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

    public Request request;
    public String userName;
    public String password;
    public volatile String ticket;
    public Response response;
    public final Consumer<Response> onComplete;

    public RequestEnvelope(Request aRequest, String aUserName, String aPassword, String aTicket, Consumer<Response> aOnComplete) {
        super();
        request = aRequest;
        userName = aUserName;
        password = aPassword;
        ticket = aTicket;
        onComplete = aOnComplete;
    }
}
