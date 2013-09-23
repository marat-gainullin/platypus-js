/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.eas.client.login.PlatypusPrincipal;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author mg
 */
public class WebPlatypusPrincipal extends PlatypusPrincipal {

    protected PlatypusHttpServlet servlet;

    public WebPlatypusPrincipal(String aUserName, PlatypusHttpServlet aServlet) {
        super(aUserName);
        servlet = aServlet;
    }

    @Override
    public boolean hasRole(String aRole) throws Exception {
        HttpServletRequest currentRequest = servlet.getCurrentRequest();
        assert currentRequest != null : "Current request is null"; //NOI18N
        return currentRequest.isUserInRole(aRole) || servlet.getServerCore().isUserInApplicationRole(getName(), aRole);
    }
}
