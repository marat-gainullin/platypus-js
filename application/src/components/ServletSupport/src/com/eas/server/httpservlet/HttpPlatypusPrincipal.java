/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.eas.client.login.PlatypusPrincipal;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class HttpPlatypusPrincipal extends PlatypusPrincipal {

    protected HttpServletRequest servletRequest;

    public HttpPlatypusPrincipal(String aUserName, HttpServletRequest aServletRequest) {
        super(aUserName, null, Collections.emptySet(), null);
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
            aOnSuccess.call(null, new Object[]{});
        }
        // sync style
    }
}
