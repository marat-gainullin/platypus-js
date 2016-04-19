/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.websocket;

import com.eas.client.login.PlatypusPrincipal;
import com.eas.script.Scripts;
import java.util.Collections;
import javax.websocket.server.HandshakeRequest;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class WebSocketPlatypusPrincipal extends PlatypusPrincipal {

    protected HandshakeRequest servletRequest;

    public WebSocketPlatypusPrincipal(String aUserName, String aDataContext, HandshakeRequest aServletRequest) {
        super(aUserName, aDataContext, Collections.emptySet(), null);
        servletRequest = aServletRequest;
    }

    @Override
    public boolean hasRole(String aRole) {
        return servletRequest.isUserInRole(aRole);
    }

    @Override
    public void logout(JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        if (aOnSuccess != null) {
            // async style
            Scripts.getSpace().process(() -> {
                aOnSuccess.call(null, new Object[]{});
            });
        } else {
            // sync style
        }
    }
}
