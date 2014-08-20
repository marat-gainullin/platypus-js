/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.login;

import com.eas.script.NoPublisherException;
import java.util.Collections;
import java.util.Set;
import java.util.function.Consumer;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class DbPlatypusPrincipal extends PlatypusPrincipal {

    private final String context;
    private final String email;
    private final String phone;
    private final String startAppElement;
    private final Set<String> roles;

    public DbPlatypusPrincipal(String aUsername, String aContext, String aEmail, String aPhone, String aStartAppElement, Set<String> aRoles) {
        super(aUsername);
        context = aContext;
        email = aEmail;
        phone = aPhone;
        startAppElement = aStartAppElement;
        roles = aRoles;
    }

    @Override
    public boolean hasRole(String aRole, Consumer<Boolean> onSuccess, Consumer<Exception> onFailure) {
        if (onSuccess != null) {
            onSuccess.accept(roles != null ? roles.contains(aRole) : false);
        }
        return roles != null ? roles.contains(aRole) : false;
    }

    public Set<String> getRoles() {
        return roles != null ? Collections.unmodifiableSet(roles) : null;
    }

    public String getContext() {
        return context;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getStartAppElement() {
        return startAppElement;
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
