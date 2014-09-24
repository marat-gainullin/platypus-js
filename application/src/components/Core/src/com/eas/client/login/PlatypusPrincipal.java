/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.login;

import com.eas.script.AlreadyPublishedException;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import java.security.Principal;
import java.util.Collections;
import java.util.Set;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author pk, mg, bl, vv
 */
public class PlatypusPrincipal implements Principal, HasPublished {

    protected Object published;

    private final String context;
    private final String email;
    private final String phone;
    private final String startAppElement;
    private final Set<String> roles;
    private final String name;

    public PlatypusPrincipal(String aUserName, String aContext, String aEmail, String aPhone, String aStartAppElement, Set<String> aRoles) {
        super();
        name = aUserName;
        context = aContext;
        email = aEmail;
        phone = aPhone;
        startAppElement = aStartAppElement;
        roles = aRoles;
    }

    private static final String NAME_JS_DOC = "/**\n"
            + "* The username..\n"
            + "*/";

    @ScriptFunction(jsDoc = NAME_JS_DOC)
    @Override
    public String getName() {
        return name;
    }

    private static final String HAS_ROLE_JS_DOC = ""
            + "/**\n"
            + "* Checks if a user have a specified role.\n"
            + "* @param role a role's name to test.\n"
            + "* @param onSuccess A success callback. Optional."
            + "* @param onFailure A failure callback. Optional."
            + "* @return <code>true</code> if the user has the provided role\n"
            + "*/";

    @ScriptFunction(jsDoc = HAS_ROLE_JS_DOC)
    public boolean hasRole(String aRole) {
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


    public boolean hasAnyRole(Set<String> aRoles) {
        if (aRoles != null && !aRoles.isEmpty()) {
            return aRoles.stream().anyMatch((role) -> (hasRole(role)));
        } else {
            return true;
        }
    }

    @Override
    public String toString() {
        return super.toString() + "{username: \"" + name + "\"}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlatypusPrincipal)) {
            return false;
        }
        PlatypusPrincipal that = (PlatypusPrincipal) o;
        if (this.getName().equals(that.getName())) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public void setPublished(Object aValue) {
        if (published != null) {
            throw new AlreadyPublishedException();
        }
        published = aValue;
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
