/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server;

import com.eas.client.threetier.Response;


/**
 *
 * @author pk
 */
public interface ResponseProcessor
{
    public void processResponse(Response response) throws Exception;
}
