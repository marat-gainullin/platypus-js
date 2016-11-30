/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.mina.platypus;

import com.eas.client.threetier.Response;

/**
 *
 * @author mg
 */
public class ResponseEnvelope {

    public Response response;
    public String ticket;

    public ResponseEnvelope(Response aResponse, String aTicket) {
        super();
        response = aResponse;
        ticket = aTicket;
    }

}
