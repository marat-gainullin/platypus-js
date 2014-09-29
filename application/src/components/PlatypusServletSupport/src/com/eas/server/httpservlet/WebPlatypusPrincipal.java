/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.eas.client.login.PlatypusPrincipal;
import com.eas.script.NoPublisherException;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class WebPlatypusPrincipal extends PlatypusPrincipal {

    protected HttpServletRequest servletRequest;

    public WebPlatypusPrincipal(String aUserName, HttpServletRequest aServletRequest) {
        super(aUserName, null, Collections.emptySet());
        servletRequest = aServletRequest;
    }

    @Override
    public boolean hasRole(String aRole) {
        return servletRequest.isUserInRole(aRole);
    }

    @Override
    public Object getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = publisher.call(null, new Object[]{this});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
}
