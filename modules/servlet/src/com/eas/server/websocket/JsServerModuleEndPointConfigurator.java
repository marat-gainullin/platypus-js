/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.websocket;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 *
 * @author mg
 */
public class JsServerModuleEndPointConfigurator extends ServerEndpointConfig.Configurator {

    public static final String HANDSHAKE_REQUEST = "handshake-request";
    
    public JsServerModuleEndPointConfigurator() {
        super();
    }

    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        sec.getUserProperties().put(HANDSHAKE_REQUEST, request);
        super.modifyHandshake(sec, request, response);      
    }
    
}
