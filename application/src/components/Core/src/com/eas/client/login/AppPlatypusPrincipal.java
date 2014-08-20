/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.login;

import com.eas.client.AppClient;
import com.eas.script.NoPublisherException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class AppPlatypusPrincipal extends PlatypusPrincipal {

    protected AppClient client;
    protected Set<String> allowedRoles = new HashSet<>();

    public AppPlatypusPrincipal(String aUserName, AppClient aClient) {
        super(aUserName);
        client = aClient;
    }

    @Override
    public boolean hasRole(String aRole, Consumer<Boolean> onSuccess, Consumer<Exception> onFailure) throws Exception {
        if (onSuccess != null) {
            if (allowedRoles.contains(aRole)) {
                onSuccess.accept(true);
            } else {
                client.isUserInRole(aRole, (Boolean res) -> {
                    if (res) {
                        allowedRoles.add(aRole);
                    }
                    onSuccess.accept(res);
                }, onFailure);
            }
            return false;
        } else {
            if (allowedRoles.contains(aRole)) {
                return true;
            } else {
                boolean res = client.isUserInRole(aRole, null, null);
                if (res) {
                    allowedRoles.add(aRole);
                }
                return res;
            }
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
}
