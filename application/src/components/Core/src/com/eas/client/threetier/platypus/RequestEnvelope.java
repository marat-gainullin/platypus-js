/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.platypus;

import com.eas.client.threetier.Request;

/**
 *
 * @author mg
 */
public class RequestEnvelope {

    public final Request request;
    public String userName;
    public String password;
    public String ticket;

    public RequestEnvelope(Request aRequest, String aUserName, String aPassword, String aTicket) {
        super();
        request = aRequest;
        userName = aUserName;
        password = aPassword;
        ticket = aTicket;
    }
}
