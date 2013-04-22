/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.threetier.ErrorResponse;
import com.eas.client.threetier.Request;

/**
 *
 * @author kl
 */
public interface AppConnection {
            
    /**
     * Processes error response when an error occurs on the server side.
     * @param aResponse An ErrorResponse instance, recieved from the server
     * @see ErrorResponse
     */
    public void handleErrorResponse(ErrorResponse aResponse) throws Exception;
    
    public void connect() throws Exception;
    
    public void executeRequest(Request rq) throws Exception;
    
    public void disconnect();
    
}
