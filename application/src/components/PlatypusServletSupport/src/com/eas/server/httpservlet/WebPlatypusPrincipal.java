/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.server.httpservlet;

import com.eas.client.login.PlatypusPrincipal;
import com.eas.script.NoPublisherException;
import com.eas.server.PlatypusServerCore;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class WebPlatypusPrincipal extends PlatypusPrincipal {

    protected PlatypusServerCore core;

    public WebPlatypusPrincipal(String aUserName, PlatypusServerCore aServerCore) {
        super(aUserName);
        core = aServerCore;
    }

    @Override
    public boolean hasRole(String aRole, Consumer<Boolean> onSuccess, Consumer<Exception> onFailure) throws Exception {
        Object request = core.getCurrentRequest().get();
        boolean res;
        if (request instanceof HttpServletRequest) {
            HttpServletRequest currentRequest = (HttpServletRequest) request;
            assert currentRequest != null : "Current request is null"; //NOI18N
            res = currentRequest.isUserInRole(aRole) || core.isUserInApplicationRole(getName(), aRole);
        } else {
            Logger.getLogger(WebPlatypusPrincipal.class.getName()).log(Level.WARNING, "Bad request type.");
            res = false;
        }
        if(onSuccess != null){
            onSuccess.accept(res);
            return false;
        }else{
            return res;
        }
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

    @Override
    public String getStartAppElement(Consumer<String> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if(onSuccess != null){
            onSuccess.accept(null);
        }
        return null;
    }
}
