/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.eas.client.login.PlatypusPrincipal;
import com.eas.script.Scripts;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class HttpPlatypusPrincipal extends PlatypusPrincipal {

    protected HttpServletRequest servletRequest;

    public HttpPlatypusPrincipal(String aUserName, String aDataContext, HttpServletRequest aServletRequest) {
        super(aUserName, aDataContext, Collections.emptySet(), null);
        servletRequest = aServletRequest;
    }

    @Override
    public boolean hasRole(String aRole) {
        return servletRequest.isUserInRole(aRole);
    }

    @Override
    public void logout(JSObject aOnSuccess, JSObject aOnFailure) throws Exception {
        servletRequest.logout();
        servletRequest.getSession().invalidate();
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
